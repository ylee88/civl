package dev.civl.gmc.dpor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

import dev.civl.gmc.TraceStepIF;
import dev.civl.gmc.seq.StateManager;
import dev.civl.gmc.util.Pair;

/**
 * This class represents the search stack used in the DPOR algorithm. It
 * maintains a stack of entries each of which represents a state in the state
 * space. If an entry in the stack is not the top entry, then it also represents
 * the transition from its own state to the next state on the stack.
 * 
 * This class also manages a happens-before relation which is built up using the
 * {@link addRace} method.
 * 
 * Races can only be added between some inner stack entry
 * and the outermost/top stack entry. Therefore, the user is responsible for adding
 * all races for the top stack entry before pushing to the stack.
 * 
 * @author Alex Wilton
 */
public class DporSearchStack<STATE, TRANSITION> {
	StateManager<STATE, TRANSITION> manager;
	private DporNodeFactory<STATE, TRANSITION> nodeFactory;
	
	private Stack<DporStackEntry<STATE, TRANSITION>> stack = new Stack<>();
	
	/**
	 * A map from a proc ID to that proc's local DPOR info object.
	 */
	Map<Integer, StackProcessInfo> stackProcInfoMap = new HashMap<>();
	
	/** Statistic variables **/
	private int numStatesSeen = 0;
	private int numStatesMatched = 0;
	private int numTraceSteps = 0;
	private int numTraceStepsMatched = 0;

	DporSearchStack(StateManager<STATE, TRANSITION> manager,
			DporNodeFactory<STATE, TRANSITION> nodeFactory,
			STATE initialState) {
		this.manager = manager;
		this.nodeFactory = nodeFactory;
		DporNode<STATE, TRANSITION> initialNode = nodeFactory
				.getInitialNode(initialState);
		DporStackEntry<STATE, TRANSITION> initialEntry = new DporStackEntry<STATE, TRANSITION>(this, initialNode);
		
		
		for (int pid : manager.getEnabledProcesses(initialState)) {
			StackProcessInfo stackProcInfo = new StackProcessInfo();
			stackProcInfoMap.put(pid, stackProcInfo);
		}
		
		stack.push(initialEntry);
		initialNode.setStackPosition(0);
		numStatesSeen++;
	}
	
	/**
	 * @return is the stack empty ?
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	/**
	 * @return the number of entries on the stack
	 */
	public int size() {
		return stack.size();
	}

	/**
	 * @return the most recent entry on the stack. It is the only entry that has
	 *         no transition.
	 * @throws {@link EmptyStackException} if the stack is empty
	 */
	public DporStackEntry<STATE, TRANSITION> top() {
		return stack.peek();
	}
	
	/**
	 * @param index
	 * @return the entry on the stack with the specified index
	 */
	public DporStackEntry<STATE, TRANSITION> get(int index) {
		return stack.get(index);
	}
	
	/**
	 * @return Return the last entry on the stack with a transition from pid.
	 *         null if pid has no transitions on stack
	 */
	public DporStackEntry<STATE, TRANSITION> lastEntry(int pid) {
		StackProcessInfo stackProcInfo = stackProcInfoMap.getOrDefault(pid, null);
		return stackProcInfo == null ? null : get(stackProcInfo.lastEntry);
	}
	
	/**
	 * @return the state held by the top entry of our stack.
	 * @throws {@link EmptyStackException} if the stack is empty
	 */
	public STATE currentState() {
		return top().getState();
	}
	
	/**
	 * @return the number of unique states explored
	 */
	public int numStatesSeen() {
		return numStatesSeen;
	}
	
	/**
	 * @return the number of times a state was re-explored
	 */
	public int numStatesMatched() {
		return numStatesMatched;
	}
	
	/**
	 * @return the number of unique trace steps executed
	 */
	public int numTraceSteps() {
		return numTraceSteps;
	}
	
	/**
	 * @return the number of times a trace step was re-explored using the cache
	 *         to avoid a repeat execution
	 */
	public int numTraceStepsMatched() {
		return numTraceStepsMatched;
	}
	
	/**
	 * @return the next transition to be executed. null if the top entry is done
	 *         exploring transitions.
	 * @throws {@link EmptyStackException} if the stack is empty
	 */
	public TRANSITION currentTransition() {
		return top().getCurrentTransition();
	}
	
	/**
	 * Precondition: currentTransition() == null
	 * 
	 * Traverses to the next available transition to explore, popping from the
	 * stack as necessary.
	 * 
	 * @return whether a next transition was found. Will be false iff the stack
	 *         is empty after searching
	 */
	public boolean searchForTransition() {
		while (!stack.isEmpty() && !top().nextTransition()) {
			popTransition();
		}
		return !isEmpty();
	}
	
	/**
	 * Requires currentTransition() != null;
	 * 
	 * Explores currentTransition() and pushes resulting state onto the stack
	 * 
	 * Returns whether the new stack entry contains a node that has been seen before
	 */
	public boolean pushTransition() {
		DporStackEntry<STATE, TRANSITION> topEntry = top();
		
		topEntry.setStackProcInfo(stackProcInfoMap.get(topEntry.getPid()));
		
		StackProcessInfo newStackProcInfo = new StackProcessInfo();
		newStackProcInfo.lastEntry = topEntry.getStackPosition();
		stackProcInfoMap.put(topEntry.getPid(), newStackProcInfo);
		
		DporNode<STATE, TRANSITION> topNode = topEntry.getNode();
		STATE topState = topNode.getState();
		TRANSITION currentTran = topEntry.getCurrentTransition();
		TraceStepIF<STATE> traceStep = topNode.getTraceStepCache(currentTran);
		if (traceStep == null) {
			traceStep = manager.nextState(topState, currentTran);
			topNode.cacheTraceStep(currentTran, traceStep);
			numTraceSteps++;
		} else {
			numTraceStepsMatched++;
		}
		
		manager.printTraceStep(topState, traceStep);
		Pair<DporNode<STATE, TRANSITION>, Boolean> nodeResult = nodeFactory
				.getNode(traceStep);
		DporNode<STATE, TRANSITION> newNode = nodeResult.left;
		manager.printTraceStepFinalState(newNode.getState(), newNode.getId());
		if (!nodeResult.right) {
			numStatesSeen++;
		} else {
			numStatesMatched++;
		}
		newNode.setStackPosition(stack.size());
		stack.push(new DporStackEntry<STATE, TRANSITION>(this, newNode));
		
		return nodeResult.right;
	}
	
	public void popTransition() {
		DporStackEntry<STATE, TRANSITION> topEntry = top();
		manager.debug(topEntry.getState(), topEntry.backtrack);
		topEntry.getNode().setStackPosition(-1);
		stack.pop();
		if (!stack.isEmpty()) {
			DporStackEntry<STATE, TRANSITION> newTopEntry = top();
			stackProcInfoMap.put(newTopEntry.getPid(), newTopEntry.getStackProcInfo());
			newTopEntry.setStackProcInfo(null);
		}
	}
	
	public void addRace(DporStackEntry<STATE, TRANSITION> entry, int pid) {
		StackProcessInfo stackProcInfo = stackProcInfoMap.get(pid);
		stackProcInfo.hbSet.addEntry(entry);
	}
	
	public StackTraversal makeStackTraversal(int proc) {
		return new StackTraversal(proc);
	}

	/**
	 * A structure for efficiently traversing the stack in reverse order,
	 * skipping entries which happen before the specified process of the top
	 * stack entry. Adding races to the stack while traversing is permitted and
	 * such changes are reflected in the traversal.
	 */
	public class StackTraversal {
		private PriorityQueue<Integer> entryQueue;
		private DporHbSet topHbSet;

		private StackTraversal(int proc) {
			topHbSet = stackProcInfoMap.get(proc).hbSet;

			entryQueue = new PriorityQueue<Integer>(stackProcInfoMap.size(),
					Collections.reverseOrder());
			for (StackProcessInfo stackProcInfo : stackProcInfoMap.values()) {
				if (stackProcInfo.lastEntry >= 0) {
					DporStackEntry<STATE, TRANSITION> lastEntry = get(
							stackProcInfo.lastEntry);
					if (!topHbSet.contains(lastEntry)) {
						entryQueue.add(lastEntry.getStackPosition());
					}
				}
			}
		}

		public DporStackEntry<STATE, TRANSITION> next() {
			while (!entryQueue.isEmpty()) {
				DporStackEntry<STATE, TRANSITION> lastEntry = get(entryQueue.poll());
				if (!topHbSet.contains(lastEntry)) {
					
					int nextToLastEntryPosition = lastEntry.getLastEntryPosition();
					if (nextToLastEntryPosition >= 0)
						entryQueue.add(get(nextToLastEntryPosition).getStackPosition());
					return lastEntry;
				}
			}
			return null;
		}
	}
}
