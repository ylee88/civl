import re
from dataclasses import dataclass

class LexicalStream:
    """
    invariants:
    + _in is a readable, unclosed text stream
    + _eof is a boolean which is true iff _in has reached the end of its stream
      (but we may still have unread text in _buffer)
    + _buffer is a list of strings satisfying:
      + len(_buffer) > 0
      + if _buffer[i] contains '\n' for some i then it is the last character of the string
      + if _buffer[i] does not contain '\n' then i ~= -1 and _eof.
      + if _peekStack is empty then len(_buffer) == 1
    + _peekStack is a list of SourceLoc satisfying:
      + for all 0 <= i <= j < len(_peekStack): SourceLoc(0,0) <= _peekStack(i) <= _peekStack(j) <= _headLoc
    + _headLoc is a SourceLoc satisfying:
      + 0 <= _headLoc.lineNum < len(_buffer)
      + 0 <= _headLoc.charPos <= len(_buffer[_headLoc.lineNum])
      + _headLoc.charPos == len(_buffer[_headLoc.lineNum]) iff all of the following hold:
        + _headLoc.lineNum ~= -1
        + _headLoc.charPos == 0 (i.e. the last string is just "")
        + _eof
    + _buffLoc is a SourceLoc that only can increase over time.

    (note that ~= means "equal as an index". So 3 ~= -1 wrt list l if l[3] refers to the same element as l[-1])
    """
    def __init__(self, rTxtFile):
        assert rTxtFile.readable() and not rTxtFile.closed
        self._in = rTxtFile
        self._buffer = [self._in.readline()]
        self._eof = self._buffer[0] == ""
        self._peekStack = []
        self._headLoc = SourceLoc(0,0)
        self._buffLoc = SourceLoc(0,0)

    def start(self):
        self._peekStack.append(self._headLoc)

    def complete(self):
        openLoc = self._peekStack.pop()
        closedLines = []

        if self._headLoc.lineNum == openLoc.lineNum:
            closedLines = [self._buffer[openLoc.lineNum][openLoc.charPos:self._headLoc.charPos]]
        else:
            closedLines = [self._buffer[openLoc.lineNum][openLoc.charPos:]]
            
            closedLines.extend(self._buffer[openLoc.lineNum+1:self._headLoc.lineNum])
            if self._headLoc.charPos > 0:
                closedLines.append(self._buffer[self._headLoc.lineNum][:self._headLoc.charPos])
        if len(self._peekStack) == 0:
            self._shrinkBufferToHead()

        return SourceRange(closedLines, self._getGlobalLoc(openLoc))

    def revert(self):
        self._headLoc = self._peekStack.pop()

    def eof(self):
        return self._eof and self._eob(self._headLoc)

    def readline(self):
        return self.readlines(1)[0]

    def readlines(self, n):
        if n <= 0:
            return None

        result = [self._restOfLine()]
        bufferTail = self._buffer[self._headLoc.lineNum+1:]
        tailBufferReads = min(n-1, self._bufferEnd().lineNum)
        
        result.extend(bufferTail[:tailBufferReads])
        pulledReads = n - len(bufferTail) - 1
        
        if pulledReads >= 0:
            pullStart = self._pull(pulledReads)
            
            result.extend(self._buffer[pullStart.lineNum:])
            self._headLoc = self._bufferEnd()
            self._headLoc = self._pull()
        else:
            self._headLoc = SourceLoc(self._headLoc.lineNum + 1 + tailBufferReads, 0)

        return result
            

    def read(self, n):
        if self.eof():
            return ""
        
        rest = self._restOfLine()
        if n < len(rest):
            self._headLoc = SourceLoc(self._headLoc.lineNum, self._headLoc.charPos + n)
            return rest[:n]
        else:
            rest = self.readline()
            return rest + self.read(n-len(rest))

    def match(self, string):
        self.start()
        if self.read(len(string)) == string:
            return self.complete()
        else:
            self.revert()
            return None

    """
    Note: Only performs regex matching on current line.
    """
    def regmatch(self, pattern):
        rest = self._restOfLine()
        match = re.match(pattern, rest)
        if match:
            self.read(match.end())
            return match
        else:
            return None

    def peek(self, pattern):
        self.start()
        isMatch = self.regmatch(pattern) != None
        self.revert()
        return isMatch

    def skipws(self):
        rest = self._restOfLine()
        while (nextNonWs := len(rest) - len(rest.lstrip())) == len(rest) and not self.eof():
            self.readline()
            rest = self._restOfLine()
        self._headLoc = SourceLoc(self._headLoc.lineNum, self._headLoc.charPos + nextNonWs)

    def pos(self):
        return self._getGlobalLoc(self._headLoc)

    # Private methods

    def _restOfLine(self):
        return self._buffer[self._headLoc.lineNum][self._headLoc.charPos:]

    def _getGlobalLoc(self, relLoc):
        return self._buffLoc.add(relLoc)

    def _bufferEnd(self):
        return SourceLoc.lineArrEnd(self._buffer)

    def _eob(self, loc):
        return loc >= self._bufferEnd()

    def _shrinkBufferToHead(self):
        if self._headLoc.lineNum > 0:
            self._buffer = self._buffer[self._headLoc.lineNum:]
            self._buffLoc = SourceLoc(self._buffLoc.lineNum + self._headLoc.lineNum, 0)
            self._headLoc = SourceLoc(0, self._headLoc.charPos)

    """
    Reads n newlines from _in and moves them into the buffer.
    Does not change _headLoc. Returns starting location of pulled lines.
    If _eof when called, then no change to _buffer and returns _bufferEnd()
    """
    def _pull(self, n=1):
        pulledLines = []
        while n > 0 and not self._eof:
            newLine = self._in.readline()
            
            if newLine == "":
                self._eof = True
            pulledLines.append(newLine)
            n -= 1
        
        if len(pulledLines) > 0 and self._eob(self._headLoc) and len(self._peekStack) == 0:
            # Condition implies we can throw out old _buffer
            # Also, invariant implies that len(_buffer) == 1 hence
            # we only need to shift _buffLoc forward one line
            self._buffLoc = SourceLoc(self._buffLoc.lineNum+1,0)
            self._buffer = pulledLines
            return SourceLoc(0,0)
        
        result = self._bufferEnd()
        
        self._buffer.extend(pulledLines)
        return result
            

class SourceRange:
    def __init__(self, lines, startLoc):
        self._lines = lines
        self._startLoc = startLoc
        self._endLoc = startLoc.add(SourceLoc(len(lines)-1, len(lines[-1])))

    def getStr(self):
        return ''.join(self._lines)

    def getLines(self):
        return self._lines

    def getStartLoc(self):
        return self._startLoc

    def getEndLoc(self):
        return self._endLoc

@dataclass(order=True, frozen=True)
class SourceLoc:
    lineNum: int
    charPos: int

    # Intentially not __add__ since it isn't associative
    def add(self, other):
        charPos = other.charPos
        if other.lineNum == 0:
            charPos += self.charPos
        return SourceLoc(self.lineNum + other.lineNum, charPos)
    
    def lineArrEnd(lines):
        return SourceLoc(len(lines) - 1 if len(lines[-1]) == 0 else len(lines), 0)
        
class ParseError(Exception):
    def __init__(self, lineNum):
        self.lineNum = lineNum
