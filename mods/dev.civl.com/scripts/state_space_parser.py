import sys
from dataclasses import dataclass

from state_space import *
from lexical_stream import *

def createTransition(stateSpace, contentStr, pid, fromState, toState):
    transition = Transition(contentStr, pid, fromState, toState)
    stateSpace.addTransition(transition)
    return transition

@dataclass(frozen=True)
class StartTransitionToken:
    pid: int
    fromStateID: StateID

@dataclass(frozen=True)
class SubTransitionToken:
    contentStr: str

@dataclass(frozen=True)
class StopTransitionToken:
    toStateID: StateID
        
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

    def parseFrameEntry(file):
        file.start()
        try:
            if not file.match("| | | | Frame[function="):
                file.revert()
                return None
            
            functionName = file.regmatch(r"[^\s,]+").group(0)
            file.match(", location=")
            
            location = int(file.regmatch(r"\d+").group(0))
            file.match(", ")

            match = file.regmatch(r"(.+), dyscope=d(\d+)\]\n$")
            source = match.group(1)
            dyscope = int(match.group(2))
            
            return file.complete(), FrameEntry(functionName, location, source, dyscope)
        except Exception as err:
            file.revert()
            print(err)
            raise ParseError(file.pos().lineNum)

    def parseProcessState(file):
        file.start()
        try:
            if not file.match("| | process "):
                file.revert()
                return None
            
            procMatch = file.regmatch(r"\d+")
            file.skipws()
            procId = int(procMatch.group(0))
            
            atomicCount = 0
            if file.match("| | | atomicCount="):
                atomicCount = int(file.regmatch(r"\d+"))
                file.skipws()

            if not file.match("| | | call stack"):
                raise Exception("Expected call stack")
            file.skipws()

            frames = []
            while (tuple := StateTerminal.parseFrameEntry(file)):
                _, frameEntry = tuple
                frames.append(frameEntry)

            return file.complete(), ProcessState(procId, atomicCount, frames)
        except Exception as err:
            file.revert()
            print(err)
            raise ParseError(file.pos().lineNum)

    def parse(file):
        headRange, sid = StateTerminal.parseHead(file)
        file.skipws()
        contentStr = ""
        reachedEmptySpace = False
        procStateLine = "| Process states\n"
        
        while not file.match(procStateLine) and not file.eof():
            contentStr += file.readline()
        contentStr += procStateLine
        
        procStates = []
        while (tuple := StateTerminal.parseProcessState(file)):
            procStateRange, procState = tuple
            contentStr += procStateRange.getStr()
            procStates.append(procState)

        return State(headRange.getStr().strip(), contentStr, sid, procStates)

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
        
    if not initState:
        print("No States found.")
        return

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
