package dev.civl.gmc.dpor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

import dev.civl.gmc.TraceStepIF;
import dev.civl.gmc.seq.StateManager;

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
	 * A map from a proc ID to that proc's last entry on the stack.
	 * Processes with no entry on the stack are not included in the map
	 */
	Map<Integer, DporStackEntry<STATE, TRANSITION>> lastEntries = new HashMap<>();
	
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
		
		stack.push(initialEntry);
		initialNode.setSeen(true);
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
		return lastEntries.getOrDefault(pid, null);
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
		return top().currentTransition();
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
	 * Returns the new stack entry pushed to the top
	 */
	public DporStackEntry<STATE, TRANSITION> pushTransition() {
		// Current top entry will now represent a transition and so "last entry"
		// info needs updating
		DporStackEntry<STATE, TRANSITION> topEntry = top();
		topEntry.setLastEntry(lastEntry(topEntry.getPid()));
		lastEntries.put(topEntry.getPid(), topEntry);
		
		DporNode<STATE, TRANSITION> topNode = topEntry.getNode();
		STATE topState = topNode.getState();
		TRANSITION currentTran = topEntry.currentTransition();
		TraceStepIF<STATE> traceStep = topNode.getTraceStepCache(currentTran);
		if (traceStep == null) {
			traceStep = manager.nextState(topState, currentTran);
			topNode.cacheTraceStep(currentTran, traceStep);
			numTraceSteps++;
		} else {
			numTraceStepsMatched++;
		}
		
		manager.printTraceStep(topState, traceStep);
		DporNode<STATE, TRANSITION> newNode = nodeFactory
				.getNode(traceStep);
		manager.printTraceStepFinalState(newNode.getState(), newNode.getId());
		if (!newNode.getSeen()) {
			numStatesSeen++;
		} else {
			numStatesMatched++;
		}
		newNode.setSeen(true);
		newNode.setStackPosition(stack.size());
		stack.push(new DporStackEntry<STATE, TRANSITION>(this, newNode));
		
		return top();
	}
	
	public void popTransition() {
		DporStackEntry<STATE, TRANSITION> topEntry = top();
		manager.debug(topEntry.getState(), topEntry.backtrack);
		topEntry.getNode().setStackPosition(-1);
		stack.pop();
		// New top no longer represents a transition and so last entry info
		// needs updated
		if (!stack.isEmpty()) {
			if (topEntry.getLastEntry() != null)
				lastEntries.put(topEntry.getPid(), topEntry.getLastEntry());
				
			else
				lastEntries.remove(topEntry.getPid());
			
			topEntry.setLastEntry(null);
		}
	}
	
	public void addRace(DporStackEntry<STATE, TRANSITION> entry, int pid) {
		top().addRace(entry, pid);
	}

	public boolean hb(int entryPos, int pid) {
		return top().getHbSet(pid).contains(get(entryPos));
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
			topHbSet = top().getHbSet(proc);

			if (lastEntries.isEmpty())
				entryQueue = new PriorityQueue<Integer>();
			else {
				entryQueue = new PriorityQueue<Integer>(lastEntries.size(),
						Collections.reverseOrder());
				for (DporStackEntry<STATE, TRANSITION> lastEntry : lastEntries.values()) {
					if (!topHbSet.contains(lastEntry)) {
						entryQueue.add(lastEntry.getPos());
					}
				}
			}
		}

		public DporStackEntry<STATE, TRANSITION> next() {
			while (!entryQueue.isEmpty()) {
				DporStackEntry<STATE, TRANSITION> lastEntry = get(entryQueue.poll());
				if (!topHbSet.contains(lastEntry)) {
					
					DporStackEntry<STATE, TRANSITION> nextToLastEntry = lastEntry.getLastEntry();
					if (nextToLastEntry != null)
						entryQueue.add(nextToLastEntry.getPos());
					return lastEntry;
				}
			}
			return null;
		}
	}
}
