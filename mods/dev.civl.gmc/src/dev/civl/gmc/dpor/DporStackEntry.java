package dev.civl.gmc.dpor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dev.civl.gmc.seq.StateManager;

/**
 * An element of the {@link DporSearchStack}. Wraps a {@link DporNode} with
 * transient data used in the DPOR search.
 * 
 * @author Alex Wilton
 */
public class DporStackEntry<STATE, TRANSITION> {
	/**
	 * The search node that wraps the source state
	 */
	private DporNode<STATE, TRANSITION> node;

	/**
	 * Collection of processes which need to be explored
	 */
	List<Integer> backtrack = new ArrayList<>();
	
	/**
	 * DPOR data for the current transition. Only relevant if this transition is
	 * actually "active," meaning if this entry is the top of the stack then
	 * this is {@code null}.
	 */
	private DporTransitionData transitionData = null;

	/**
	 * Index into {@link DporStackEntry#backtrack} of the process we are currently exploring.
	 */
	private int current = 0;

	/**
	 * The iterator for {@link DporStackEntry#transitions}.
	 */
	private Iterator<TRANSITION> transitionIterator;

	/**
	 * The current transition.
	 */
	private TRANSITION currentTransition = null;
	
	/**
	 * The index of the current transition. This is used to write the trace file
	 * for replay.
	 */
	private int tid = -1;

	/**
	 * @param manager
	 *            The search stack that this entry will belong to
	 * @param node
	 *            The node that wraps the source state.
	 */
	DporStackEntry(StateManager<STATE, TRANSITION> manager, DporNode<STATE, TRANSITION> node) {
		this.node = node;
		STATE state = node.getState();
		Set<Integer> enabledProcs = manager.getEnabledProcesses(state);
		if (enabledProcs.isEmpty()) {
			initializeTransitions(new ArrayList<TRANSITION>());
		} else {
			int pid = enabledProcs.iterator().next();
			this.backtrack.add(pid);
			initializeTransitions(manager.getTransitions(state, pid));
		}
	}
	
	private void initializeTransitions(Collection<TRANSITION> newTransitions) {
		transitionIterator = newTransitions.iterator();
		nextTransitionInProc();
	}

	/**
	 * @return the transition which is either being explored (if this an
	 *         "interior" entry) or is about to be explored (if this is the top
	 *         entry of the stack)
	 */
	public TRANSITION currentTransition() {
		return currentTransition;
	}
	
	/**
	 * @return the transition id used for replaying traces
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * @return The {@link DporNode} held by this entry
	 */
	public DporNode<STATE, TRANSITION> getNode() {
		return node;
	}

	/**
	 * @return The state held by this entry's node
	 */
	public STATE getState() {
		return node.getState();
	}
	
	/**
	 * Precondition: !{@link DporStackEntry#isDone()}
	 * 
	 * @return the id of the process currently being explored.
	 */
	public int getPid() {
		return backtrack.get(current);
	}
	
	/**
	 * @return the index this entry has on the stack
	 */
	public int getStackPosition() {
		return node.getStackPosition();
	}
	
	/**
	 * @return whether every enabled transition from every process in this
	 *         entry's backtrack set has been explored
	 */
	public boolean isDone() {
		return current >= backtrack.size();
	}
	
	/**
	 * @return whether pid is in this entry's backtrack set
	 */
	public boolean inBacktrack(int pid) {
		return backtrack.contains(pid);
	}
	
	/**
	 * Add process pid into this entry's backtrack set
	 * <p>
	 * Preconditions:
	 * <ul>
	 *  <li>pid is not in already backtrack set</li>
	 *  <li>pid is enabled at this entry's state</li>
	 * </ul>
	 */
	public void addToBacktrack(int pid) {
		backtrack.add(pid);
	}
	
	/**
	 * Adds all of the processes in procs which are not already in the backtrack set
	 * 
	 * Precondition: All processes in procs are enabled at this entry's state
	 * 
	 * @return the number of new processes added to the backtrack
	 */
	public int addAllToBacktrack(Collection<Integer> procs) {
		Set<Integer> remainingProcs = new HashSet<>(procs);
		remainingProcs.removeAll(backtrack);
		backtrack.addAll(remainingProcs);
		return remainingProcs.size();
	}

	/**
	 * Increments to the next outgoing transition to be explored from the
	 * current state, moving to the next process in the backtrack set if
	 * necessary.
	 * 
	 * @param manager
	 *            Used to obtain the transitions of a process
	 * 
	 * @return true iff a next transition exists
	 */
	public boolean nextTransition(StateManager<STATE, TRANSITION> manager) {
		if (nextTransitionInProc() == null) {
			return nextProc(manager) != -1;
		}
		return true;
	}
	
	/**
	 * Increments to the next transition of the current process. If no such
	 * transition exists then {@link DporStackEntry#currentTransition} becomes
	 * {@code null}.
	 * 
	 * @return the current transition after incrementing
	 */
	private TRANSITION nextTransitionInProc() {
		if (transitionIterator.hasNext()) {
			tid++;
			currentTransition = transitionIterator.next();
		} else {
			currentTransition = null;
		}
		return currentTransition;
	}
	
	/**
	 * Moves to the next process in the backtrack set, changing the set of
	 * transitions being explored to those of the new process.
	 * 
	 * @param stateManager
	 *            Used to obtain the transitions of the next process
	 * @return the id of the new process being explored. -1 if there is no next
	 *         process.
	 */
	private int nextProc(StateManager<STATE, TRANSITION> manager) {
		current++;
		if (current >= backtrack.size())
			return -1;
		
		int pid = backtrack.get(current);
		initializeTransitions(manager.getTransitions(node.getState(), pid));
		
		return pid;
	}
	
	/**
	 * Precondition: This entry is not the top of the stack.
	 * 
	 * @return The stack position of the last entry with a transition from the
	 *         same process as this entry's transition.
	 */
	public int getPrevStackPosition() {
		return transitionData.prevStackPosition;
	}
	
	/**
	 * Precondition: This entry is not on the top of the stack.
	 * 
	 * @return The {@link DporHbSet} representing the set of stack entries with
	 *         transitions that happen before this entry's transition
	 */
	public DporHbSet getHbSet() {
		return transitionData.hbSet;
	}
	
	/**
	 * Sets the {@link DporTransitionData} for this entry.
	 * @param transitionData
	 */
	void setTransitionData(DporTransitionData transitionData) {
		this.transitionData = transitionData;
	}
	
	/**
	 * @return this entry's {@link DporTransitionData}. Will be {@code null} iff this
	 *         entry is at the top of the stack.
	 */
	DporTransitionData getTransitionData() {
		return transitionData;
	}
}