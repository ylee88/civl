import sys
import re
from dataclasses import dataclass

class StateSpace:
    def __init__(self):
        self.uStateMap = {}
        self.pStateMap = {}
        self.outgoingMap = {}
        self.incomingMap = {}
        self.initStates = set()

    def addTransition(self, transition):
        fs = transition.fromState
        ts = transition.toState
        self._addState(fs)
        self._addState(ts)
        self.incomingMap[ts.uid()].append(transition)
        self.outgoingMap[fs.uid()].append(transition)

    def incoming(self, stateID):
        return self.incomingMap.get(stateID.unique, None)

    def outgoing(self, stateID):
        return self.outgoingMap.get(stateID.unique, None)

    def _addState(self, state):
        self.uStateMap.setdefault(state.uid(), state)
        self.pStateMap.setdefault(state.pairID(), state)
        self.incomingMap.setdefault(state.uid(), [])
        self.outgoingMap.setdefault(state.uid(), [])

    def addInitState(self, state):
        self._addState(state)
        self.initStates.add(state)

    def getState(self, stateID):
        if isinstance(stateID, StateID):
            stateID = stateID.unique
        if isinstance(stateID, tuple):
            assert len(stateID) == 2
            return self.pStateMap.get(stateID, None)
        else:
            assert isinstance(stateID, int)
            return self.uStateMap.get(stateID, None)

    def __str__(self):
        seenStates = set()
        outStr = ""
        for initState in self.initStates:
            if initState in seenStates:
                continue
            outStr += str(initState)
            seenStates.add(initState)
            openList = []
            openList.extend(reversed(self.outgoing(initState.stateID())))
            while len(openList) > 0:
                nextTrans = openList.pop()
                outStr += str(nextTrans) + "\n"
                if nextTrans.toState not in seenStates:
                    seenStates.add(nextTrans.toState)
                    outStr += str(nextTrans.toState)
                    openList.extend(reversed(self.outgoing(nextTrans.toState.stateID())))

        return outStr

    def reachableFrom(self, startStates):
        if not isinstance(startStates, set):
            startStates = {startStates}
        subSpace = StateSpace()
        for startState in startStates:
            if subSpace.getState(startState.stateID()):
                continue
            subSpace.addInitState(startState)
            openList = []
            openList.extend(self.outgoing(startState.stateID()))
            while len(openList) > 0:
                nextTrans = openList.pop()
                if not subSpace.getState(nextTrans.toState.stateID()):
                    openList.extend(self.outgoing(nextTrans.toState.stateID()))
                subSpace.addTransition(nextTrans)

        return subSpace

    def reachableTo(self, endStates):
        if not isinstance(endStates, set):
            endStates = {endStates}
        subSpace = StateSpace()
        for endState in endStates:
            if subSpace.getState(endState.stateID()):
                continue
            openList = []
            openList.extend(self.incoming(endState.stateID()))
            if len(openList) == 0:
                subSpace.addInitState(endState)
            while len(openList) > 0:
                prevTrans = openList.pop()
                if not subSpace.getState(prevTrans.fromState.stateID()):
                    incomingList = self.incoming(prevTrans.fromState.stateID())
                    if len(incomingList) == 0:
                        subSpace.addInitState(prevTrans.fromState)
                    else:
                        openList.extend(incomingList)
                else:
                    subSpace.addInitState(prevTrans.fromState)
                subSpace.addTransition(prevTrans)

        subSpace._collapseInitStates()
        return subSpace

    # Removes any redundant initStates
    def _collapseInitStates(self):
        possibleInitStates = self.initStates.copy()
        for initState in self.initStates:
            if initState not in possibleInitStates:
                continue
            seenStates = {initState}
            openList = []
            openList.extend(self.outgoing(initState.stateID()))
            while len(openList) > 0:
                nextTrans = openList.pop()
                if nextTrans.toState not in seenStates:
                    seenStates.add(nextTrans.toState)
                    openList.extend(self.outgoing(nextTrans.toState.stateID()))
            seenStates.remove(initState)
            possibleInitStates.difference_update(seenStates)

        self.initStates = possibleInitStates
            

##########
# Tokens #
##########

@dataclass
class StateID:
    left: int
    right: int
    unique: int
    
class State:
    def __init__(self, headStr, contentStr, sid):
        self.headStr = headStr
        self.contentStr = contentStr
        self.sid = sid

    def stateID(self):
        return self.sid

    def uid(self):
        return self.sid.unique

    def pairID(self):
        return (self.sid.left, self.sid.right)

    def __str__(self):
        return self.headStr + "\n" + self.contentStr + "\n"

@dataclass
class Transition:
    contentStr: str
    pid: int
    fromState: State
    toState: State

    def __str__(self):
        return "Executed by p"+str(self.pid)+" from "+self.fromState.headStr+"\n  "+self.contentStr+"--> "+self.toState.headStr+"\n"

def createTransition(stateSpace, contentStr, pid, fromState, toState):
    transition = Transition(contentStr, pid, fromState, toState)
    stateSpace.addTransition(transition)
    return transition

@dataclass
class StartTransitionToken:
    pid: int
    fromStateID: StateID

@dataclass
class SubTransitionToken:
    contentStr: str

@dataclass
class StopTransitionToken:
    toStateID: StateID

###########
# Parsing #
###########

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
            closedLines = [self._restOfLine()]
            
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
        while (nextNonWs := len(rest) - len(rest.lstrip())) == len(rest):
            self.readline()
            rest = self._restOfLine()
        self._headLoc = SourceLoc(self._headLoc.lineNum, self._headLoc.charPos + nextNonWs)

    def pos(self):
        return self._headLoc

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
                self._eof = true
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
        if other.lineNum > 0:
            charPos += self.charPos
        return SourceLoc(self.lineNum + other.lineNum, charPos)
    
    def lineArrEnd(lines):
        return SourceLoc(len(lines) - 1 if len(lines[-1]) == 0 else len(lines), 0)
        
class ParseError(Exception):
    def __init__(self, lineNum):
        self.lineNum = lineNum
        
class StateTerminal:
    first = "State"

    pattern = r"(\d+(?:\.\d+)?) \(id=(\d+)\)"

    def parseHead(file):
        file.start()
        try:
            file.match(StateTerminal.first)
            file.skipws()
            file.match("(")
            leftID = int(file.regmatch(r"\d+").group(0))
            if file.match("."):
                rightID = int(file.regmatch(r"\d+").group(0))
                file.match(")")
            else:
                rightID = 0
            file.skipws()
            file.match("(id=")
            uniqueID = int(file.regmatch(r"\d+").group(0))
            file.match(")")
        except Exception as err:
            file.revert()
            print(err)
            raise ParseError(file.pos().lineNum)

        return file.complete(), StateID(leftID, rightID, uniqueID)

    def parse(file):
        headRange, sid = StateTerminal.parseHead(file)
        file.skipws()
        contentStr = ""
        while not (line := file.readline()).isspace():
            contentStr += line

        return State(headRange.getStr().strip(), contentStr, sid)

class StartTransitionTerminal:
    first = "Executed"

    def parse(file):
        file.start()
        try:
            file.match(StartTransitionTerminal.first)
            file.skipws()
            file.match("by")
            file.skipws()
            file.match("p")
            procID = int(file.regmatch(r"\d+").group(0))
            file.skipws()
            file.match("from")
            file.skipws()
            _, fromStateID = StateTerminal.parseHead(file)
        except Exception:
            file.revert()
            raise ParseError(file.pos().lineNum)

        file.complete()
        return StartTransitionToken(procID, fromStateID)

class SubTransitionTerminal:
    first = r"\d+->(\d+|RET):"

    def parse(file):
        return SubTransitionToken(file.readline())

class StopTransitionTerminal:
    first = "-->"

    def parse(file):
        file.start()
        try:
            file.match(StopTransitionTerminal.first)
            file.skipws()
            _, toStateID = StateTerminal.parseHead(file)
        except Exception:
            file.revert()
            raise ParseError(file.pos().lineNum)

        file.complete()
        return StopTransitionToken(toStateID)

def parseTerminal(file, *potentialTerms):
    for terminal in potentialTerms:
        if file.peek(terminal.first):
            return terminal.parse(file)
    return None

def parseInitState(file, stateSpace):
    try:
        while not (initState := parseTerminal(file, StateTerminal)) and not file.eof():
            file.readline()
    except ParseError as err:
        print("Parsing error: Failed to parse initial state.", err)
        exit()
    if not initState:
        print("No States found.")
        exit()

    stateSpace.addInitState(initState)

def parseState(file, stateSpace):
    try:
        return parseTerminal(file, StateTerminal)
    except ParseError as err:
        print("Parsing error: Failed to parse initial state.", err)
        raise

def parseStartTransition(file):
    try:
        return parseTerminal(file, StartTransitionTerminal)
    except ParseError as err:
        print("Parsing error: Failed to parse transition", err)
        raise

def parseSubTransition(file):
    try:
        if not (subTransToken := parseTerminal(file, SubTransitionTerminal)):
            raise ParseError(file.pos().lineNum)
        return subTransToken
    except ParseError as err:
        print("Parsing error: Failed to parse sub transition", err)
        raise

def parseStopTransition(file):
    try:
        if not (stopToken := parseTerminal(file, StopTransitionTerminal)):
            raise ParseError(file.pos().lineNum)
        return stopToken
    except ParseError as err:
        print("Parsing error: Failed to parse sub transition", err)
        raise

def parseLargeTransitionStep(file, stateSpace):
    if not (startToken := parseStartTransition(file)):
        return False
    lastState = stateSpace.getState(startToken.fromStateID)

    file.skipws()
    subTransToken = parseSubTransition(file)

    file.skipws()
    while (nextState := parseState(file, stateSpace)):
        newTransition = createTransition(stateSpace, subTransToken.contentStr, startToken.pid, lastState, nextState)
        lastState = nextState
        
        file.skipws()
        subTransToken = parseSubTransition(file)
        
        file.skipws()
    stopToken = parseStopTransition(file)

    file.skipws()
    if not (endState := parseState(file, stateSpace)):
        endState = stateSpace.getState(stopToken.toStateID)
    lastTransition = createTransition(stateSpace, subTransToken.contentStr, startToken.pid, lastState, endState)
    
    return True

def parseStateSpace(fileName):
    stateSpace = StateSpace()

    with open(fileName, 'r', encoding="utf-8") as f:
        file = LexicalStream(f)
        try:
            parseInitState(file, stateSpace)
            file.skipws()
            while parseLargeTransitionStep(file, stateSpace):
                file.skipws()
        except ParseError:
            pass
        

    return stateSpace

if __name__ == "__main__":
    stateSpace = parseStateSpace(sys.argv[1])
    print("Done parsing")

    print(stateSpace)
        
