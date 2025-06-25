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
 * the transition from its own state to the next state on the stack. Such
 * entries are referred to as "interior" entries.
 * 
 * @author Alex Wilton
 */
public class DporSearchStack<STATE, TRANSITION> {
	private StateManager<STATE, TRANSITION> manager;
	private DporNodeFactory<STATE, TRANSITION> nodeFactory;
	
	/**
	 * The stack of entries which represent the current execution being
	 * explored.
	 */
	private Stack<DporStackEntry<STATE, TRANSITION>> stack = new Stack<>();
	
	/**
	 * A map from each available process to a {@link DporTransitionData}
	 * structure that will be assigned to that process's next transition when it
	 * executes.
	 * 
	 * Transition data for a specific process held by this map may be built up
	 * and modified as pushes and pops of transitions from other transitions
	 * execute. For instance, {@link DporTransitionData#hbSet} will get new
	 * entries added as dependent transitions from other processes execute.
	 * 
	 * When a process has one of its transitions pushed on the stack, its
	 * transition data from this map gets assigned to the stack entry and the
	 * process gets a fresh object in this map.
	 * 
	 * When a process has one of its transitions popped from the stack, the
	 * inverse happens. The new top stack entry moves its transition data into
	 * this map, replacing whatever was there, and leaves itself with no
	 * transition data since it is no longer an "interior" entry.
	 */
	Map<Integer, DporTransitionData> nextTransitionDataMap = new HashMap<>();
	
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
		DporStackEntry<STATE, TRANSITION> initialEntry = new DporStackEntry<STATE, TRANSITION>(manager, initialNode);
		
		
		for (int pid : manager.getEnabledProcesses(initialState)) {
			DporTransitionData transitionData = new DporTransitionData();
			nextTransitionDataMap.put(pid, transitionData);
		}
		
		stack.push(initialEntry);
		initialNode.setStackPosition(0);
		numStatesSeen++;
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
	 * @return whether the stack is empty
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
	 * @return the state held by the top entry of our stack.
	 * @throws {@link EmptyStackException} if the stack is empty
	 */
	public STATE currentState() {
		return top().getState();
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
	 * Traverses to the next available transition to explore, popping from the
	 * stack as necessary.
	 * <p>
	 * Precondition: {@link DporSearchStack#currentTransition()} {@code == null}
	 * 
	 * @return whether a next transition was found. Will be false iff the stack
	 *         is empty after searching
	 */
	public boolean searchForTransition() {
		while (!stack.isEmpty() && !top().nextTransition(manager)) {
			popTransition();
		}
		return !isEmpty();
	}
	
	/**
	 * Explores the current transition and pushes resulting state as a
	 * {@link DporStackEntry} onto the stack
	 * <p>
	 * Precondition:
	 * <ul>
	 * <li>The stack is not empty</li>
	 * <li>{@link DporSearchStack#currentTransition()} != null;</li>
	 * </ul>
	 * 
	 * @return whether the new stack entry contains a node that has been seen
	 *         before
	 */
	public boolean pushTransition() {
		DporStackEntry<STATE, TRANSITION> oldTopEntry = top();
		DporNode<STATE, TRANSITION> oldTopNode = oldTopEntry.getNode();
		STATE topState = oldTopNode.getState();
		TRANSITION currentTran = oldTopEntry.currentTransition();
		TraceStepIF<STATE> traceStep = oldTopNode.getTraceStepCache(currentTran);
		if (traceStep == null) {
			traceStep = manager.nextState(topState, currentTran);
			oldTopNode.cacheTraceStep(currentTran, traceStep);
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
		stack.push(new DporStackEntry<STATE, TRANSITION>(manager, newNode));
		
		oldTopEntry.setTransitionData(nextTransitionDataMap.get(oldTopEntry.getPid()));
		
		DporTransitionData newTransitionData = new DporTransitionData();
		newTransitionData.prevStackPosition = oldTopEntry.getStackPosition();
		newTransitionData.hbSet.addEntry(oldTopEntry);
		nextTransitionDataMap.put(oldTopEntry.getPid(), newTransitionData);
		
		return nodeResult.right;
	}
	
	/**
	 * Pops the top entry from the stack.
	 * <p>
	 * This action also implicitly pops the last transition of the execution if
	 * one existed: the new top entry will no longer be "interior" and will thus
	 * no longer represent a transition.
	 * <p>
	 * Precondition: The stack is not empty.
	 */
	public void popTransition() {
		DporStackEntry<STATE, TRANSITION> topEntry = top();
		manager.debug(topEntry.getState(), topEntry.backtrack);
		topEntry.getNode().setStackPosition(-1);
		stack.pop();
		if (!stack.isEmpty()) {
			DporStackEntry<STATE, TRANSITION> newTopEntry = top();
			nextTransitionDataMap.put(newTopEntry.getPid(), newTopEntry.getTransitionData());
			newTopEntry.setTransitionData(null);
		}
	}
	
	/**
	 * Marks a race between the transition of {@code entry} and one of the next
	 * transitions at the current state from the process {@code pid}.
	 * 
	 * @param entry
	 *            An interior entry
	 * @param pid
	 *            a process id
	 */
	public void addRace(DporStackEntry<STATE, TRANSITION> entry, int pid) {
		DporTransitionData transitionData = nextTransitionDataMap.get(pid);
		transitionData.hbSet.addEntry(entry);
	}
	
	/**
	 * Constructs a new {@link StackTraversal} object for traversing the
	 * interior entries of this stack in reverse order, skipping entries whose
	 * transition happens before some next transition of proc at the stack's
	 * current state.
	 * <p>
	 * Adding races to this stack while traversing with this returned object is
	 * allowed and such additions are reflected in the traversal
	 * 
	 * @param proc
	 * @return a new {@link StackTraversal} object
	 */
	public StackTraversal makeStackTraversal(int proc) {
		return new StackTraversal(nextTransitionDataMap.get(proc).hbSet);
	}

	/**
	 * A structure for efficiently traversing the interior entries of this stack
	 * in reverse order, skipping entries whose transition occur in a
	 * {@link DporHbSet} supplied to this object when constructed.
	 * <p>
	 * Adding races to the stack while traversing is permitted and such changes
	 * are reflected in the traversal.
	 */
	public class StackTraversal {
		/**
		 * The priority queue of stack positions used for traversal.
		 */
		private PriorityQueue<Integer> entryQueue;
		/**
		 * A reference to the hb-set which will be used to skip transitions
		 * along the execution. We hold onto the reference after construction in
		 * case more entries get added to it while traversing.
		 */
		private DporHbSet hbSet;

		private StackTraversal(DporHbSet hbSet) {
			this.hbSet = hbSet;
			entryQueue = new PriorityQueue<Integer>(nextTransitionDataMap.size(),
					Collections.reverseOrder());
			
			// For each process, add its last entry if it isn't in hbSet
			for (DporTransitionData transitionData : nextTransitionDataMap
					.values()) {
				if (transitionData.prevStackPosition >= 0) {
					DporStackEntry<STATE, TRANSITION> lastEntry = get(
							transitionData.prevStackPosition);
					if (!hbSet.contains(lastEntry)) {
						entryQueue.add(lastEntry.getStackPosition());
					}
				}
			}
		}

		/**
		 * Traverses to the next stack entry and returns it.
		 * 
		 * @return the next stack entry, or {@code null} if there are no more
		 *         entries to traverse.
		 */
		public DporStackEntry<STATE, TRANSITION> next() {
			while (!entryQueue.isEmpty()) {
				DporStackEntry<STATE, TRANSITION> lastEntry = get(
						entryQueue.poll());
				// If lastEntry is in hbSet then we skip and do not continue
				// traversing edges from its process
				if (!hbSet.contains(lastEntry)) {
					int nextToLastEntryPosition = lastEntry.getPrevStackPosition();
					// Only add nextToLastEntryPosition if that entry isn't in the hbSet
					if (nextToLastEntryPosition > hbSet.lastEntryPos(lastEntry.getPid()))
						entryQueue.add(nextToLastEntryPosition);
					return lastEntry;
				}
			}
			return null;
		}
	}
}
