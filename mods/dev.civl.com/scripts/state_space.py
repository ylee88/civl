import itertools
import dataclasses

class StateSpace:
    def __init__(self):
        self.uStateMap = {}
        self.pStateMap = {}
        self.outgoingMap = {}
        self.incomingMap = {}
        self.transitions = set()
        self.initStates = set()

    def addTransition(self, transition):
        if transition in self.transitions:
            return
        
        fs = transition.fromState
        ts = transition.toState
        self._addState(fs)
        self._addState(ts)
        self.incomingMap[ts.uid()].append(transition)
        self.outgoingMap[fs.uid()].append(transition)
        self.transitions.add(transition)

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
        outStr = ""
        seenStates = set()

        def initStateAction(state):
            nonlocal outStr, seenStates
            outStr += str(state)
            seenStates.add(state)

        def transitionAction(trace):
            nonlocal outStr, seenStates
            outStr += str(trace.top()) + "\n"
            if (nextState := trace.top().toState) not in seenStates:
                outStr += str(nextState)

        self.dfs(self.initStates, transitionAction, initStateAction)

        return outStr

    def printer(self):
        """
        Unfinished.

        Meant to provide an object which can be used to interactively move around the state space and print sections of it out.
        """
        seenStates = set()

        def initStateAction(startState):
            nonlocal seenStates
            
            seenStates.add(startState)
            print("Init:")
            print(startState)

        def transitionAction(trace):
            nonlocal seenStates
            print(trace.top()+"\n")
            nextState = trace.top().toState
            if nextState in seenStates:
                print("Seen before: "+nextState.headStr)
            else:
                seenStates.add(nextState)

        dfsIter = self.dfsIter(self.initStates, transitionAction, initStateAction)

    def dfs(self, startStates, transitionAction=lambda trace:None, initStateAction=lambda startState:None, postTransitionAction=lambda trace:None):
        dfsIter = self.dfsIter(startStates, transitionAction, initStateAction, postTransitionAction)

        try:
            return next(dfsIter)
        except Exception as e:
            return None

    def dfsIter(self, startStates, transitionAction=lambda trace:None, initStateAction=lambda startState:None, postTransitionAction=lambda trace:None):
        if not isinstance(startStates, set):
            startStates = {startStates}

        seenStates = set()
        for startState in startStates:
            if startState in seenStates:
                continue
            
            actionReturn = initStateAction(startState)
            if actionReturn:
                yield actionReturn

            currentTrace = Trace(startState)
            seenStates.add(startState.stateID())
            
            openList = []
            openList.extend(zip(reversed(self.outgoing(startState.stateID())),
                                (False for _ in itertools.count())))

            while len(openList) > 0:
                transition, inTrace = openList.pop()
                if inTrace:
                    actionReturn = postTransitionAction(currentTrace)
                    if actionReturn:
                        yield actionReturn
                    
                    currentTrace.pop()
                    continue

                nextStateID = transition.toState.stateID()

                openList.append((transition, True))
                currentTrace.push(transition)
                
                actionReturn = transitionAction(currentTrace)
                if actionReturn:
                    yield actionReturn
                
                if nextStateID not in seenStates:
                    seenStates.add(nextStateID)
                    openList.extend(zip(reversed(self.outgoing(nextStateID)),
                                        (False for _ in itertools.count())))
                    
    def maximalTraces(self):
        lastState = None

        def initStateAction(startState):
            if len(self.outgoing(startState.stateID())) == 0:
                return Trace(startState)

        def transitionAction(trace):
            nonlocal lastState
            lastState = trace.top().toState()

        def postTransitionAction(trace):
            nonlocal lastState
            if trace.top().toState() == lastState:
                return trace

        return self.dfsIter(self.initStates, transitionAction=transitionAction, postTransitionAction=postTransitionAction)

    """
    
    """
    def tracesWithExcessiveVisits(self, threshold):
        exVisitSpace = StateSpace()
        thresholdStates = set()
        locMap = {}
        locMaxMap = {}
        currThresholdState = None

        def transitionAction(trace):
            nonlocal exVisitSpace, locMap, locMaxMap, currThresholdState, thresholdStates
            
            nextState = trace.top().toState
            procState = nextState.processState(0)
            if currThresholdState:
                exVisitSpace.addTransition(trace.top())

            if len(procState.callStack) > 0:
                loc = procState.callStack[0].location
                val = locMap.get(loc, 0) + 1
                locMap[loc] = val
                locMaxMap[loc] = max(locMaxMap.get(loc, 0), val)

                if not currThresholdState and val >= threshold: 
                    currThresholdState = nextState
                    thresholdStates.add(nextState)
                
                    exVisitSpace.addInitState(trace.initState)
                    for transition in trace.transitions():
                        exVisitSpace.addTransition(transition)

        def initStateAction(state):
            nonlocal exVisitSpace, locMap, locMaxMap, currThresholdState

            loc = state.processState(0).callStack[0].location
            locMap[loc] = 1
            locMaxMap[loc] = max(locMaxMap.get(loc,0), 1)

        def postTransitionAction(trace):
            nonlocal exVisitSpace, locMap, locMaxMap, currThresholdState
            
            procState = trace.top().toState.processState(0)
            
            if len(procState.callStack) == 0:
                return

            loc = procState.callStack[0].location
            val = locMap[loc] - 1
            assert val >= 0
            locMap[loc] = val

            if trace.top().toState == currThresholdState:
                currThresholdState = None

        self.dfs(self.initStates, transitionAction, initStateAction, postTransitionAction)

        return exVisitSpace, thresholdStates, locMaxMap

    def reachableFrom(self, startStates):
        subSpace = StateSpace()

        def initAction(state):
            nonlocal subSpace
            
            subSpace.addInitState(state)

        def transitionAction(trace):
            nonlocal subSpace
            
            subSpace.addTransition(trace.top())

        self.dfs(startStates, transitionAction, initStateAction=initAction)

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
            
class Trace:
    """
    initState - initial state. Important for when there are no transitions yet.
    transStack - stack of Transitions
    stateMap - map from states to the index of the transition in transStack
    that has the state as its "fromState"

    Currently assume that we don't add to a Trace if it reaches its first cycle.
    """
    
    def __init__(self, initState):
        self.initState = initState
        self.transStack = []
        self.stateMap = {}

    def states(self):
        return self.stateMap.keys()

    def outgoingIndex(self, state):
        return self.stateMap.get(state)

    def transitions(self):
        return self.transStack

    def initState(self):
        return self.initState

    def top(self):
        return self.transStack[-1]

    def push(self, transition):
        self.stateMap[transition.fromState] = len(self.transStack)
        self.transStack.append(transition)

    def pop(self):
        poppedTransition = self.transStack.pop()
        del self.stateMap[poppedTransition.fromState]

        return poppedTransition

    def asSubSpace(self):
        subSpaceView = StateSpace()
        
        subSpaceView.addInitState(self.initState)
        
        for trans in self.transStack:
            subSpaceView.addTransition(trans)

        return subSpaceView

    def __str__(self):
        return str(self.asSubSpace())

class State:
    def __init__(self, headStr, contentStr, sid, procStates):
        self.headStr = headStr
        self.contentStr = contentStr
        self.sid = sid
        self.procStates = procStates

    def stateID(self):
        return self.sid

    def uid(self):
        return self.sid.unique

    def pairID(self):
        return (self.sid.left, self.sid.right)

    def processStates(self):
        return self.procStates

    def processState(self, i):
        return self.procStates[i]

    def isTerminated(self):
        for procState in procStates:
            if not procState.isTerminated():
                return False
        return True

    def __str__(self):
        return self.headStr + "\n" + self.contentStr + "\n"

@dataclasses.dataclass(frozen=True)
class StateID:
    """
    Class to hold the ID of a state.
    """
    left: int
    right: int
    unique: int

    def __hash__(self):
        return hash(self.unique)

class ProcessState:
    def __init__(self, pid, atomicCount, callStack):
        self.pid = pid
        self.atomicCount = atomicCount
        self.callStack = callStack

    def sameLocation(self, other):
        return self.callStack[0].location == other.callStack[0].location

    def isTerminated(self):
        return len(callStack) == 0

@dataclasses.dataclass(frozen=True)
class FrameEntry:
    function: str
    location: int
    source: str
    dyscope: int

@dataclasses.dataclass(frozen=True)
class Transition:
    contentStr: str
    pid: int
    fromState: State
    toState: State

    def __str__(self):
        return "Executed by p"+str(self.pid)+" from "+self.fromState.headStr+"\n  "+self.contentStr+"--> "+self.toState.headStr+"\n"
