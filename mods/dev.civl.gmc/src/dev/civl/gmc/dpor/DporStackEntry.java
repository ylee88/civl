package dev.civl.gmc.dpor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.civl.gmc.seq.StateManager;

/**
 * An element of the {@link DporSearchStack}. Wraps a
 * {@link DporNode} with transient data for the DPOR search.
 */
public class DporStackEntry<STATE, TRANSITION> {
	private StateManager<STATE, TRANSITION> manager;
	
	/**
	 * The search node that wraps the source state
	 */
	private DporNode<STATE, TRANSITION> node;

	/**
	 * Collection of processes which need to be explored
	 */
	List<Integer> backtrack = new ArrayList<>();
	
	/**
	 * Maps a process to a {@link DporHbSet} which a structure containing happens-before information
	 * for all outgoing transitions from this entry that belong to that
	 * process
	 */
	//private Map<Integer, DporHbSet> procToHbSet = new HashMap<>();
	
	private StackProcessInfo stackProcInfo = null;

	/**
	 * Index into {@link DporStackEntry#backtrack} of the process we are currently exploring.
	 */
	private int current = 0;
	
	/**
	 * Set of all processes with an enabled outgoing transition at the current state
	 */
	private Set<Integer> enabledProcs;

	/**
	 * The collection of enabled transitions belonging to the current process ({@link DporStackEntry#getPid()})
	 */
	private Collection<TRANSITION> transitions;

	/**
	 * The iterator for {@link DporStackEntry#transitions}.
	 */
	private Iterator<TRANSITION> transitionIterator;

	/**
	 * The index of the current transition. This is used to write the trace file
	 * for replay.
	 */
	private int tid = -1;

	/**
	 * The current transition.
	 */
	private TRANSITION currentTransition = null;

	/**
	 * @param dporSearchStack
	 *            The search stack that this entry will belong to
	 * @param node
	 *            The node that wraps the source state.
	 */
	DporStackEntry(DporSearchStack<STATE, TRANSITION> dporSearchStack, DporNode<STATE, TRANSITION> node) {
		this.manager = dporSearchStack.manager;
		this.node = node;
		STATE state = node.getState();
		this.enabledProcs = manager.getEnabledProcesses(state);
		if (enabledProcs.isEmpty()) {
			initializeTransitions(new ArrayList<TRANSITION>());
		} else {
			int pid = enabledProcs.iterator().next();
			this.backtrack.add(pid);
			initializeTransitions(manager.getTransitions(state, pid));
		}
	}
	
	/**
	 * Initializes {@link DporStackEntry#transitionIterator}
	 */
	private void initializeTransitions(Collection<TRANSITION> newTransitions) {
		transitions = newTransitions;
		transitionIterator = transitions.iterator();
		nextTransitionInProc();
	}

	public TRANSITION getCurrentTransition() {
		return currentTransition;
	}
	
	public int getTid() {
		return tid;
	}

	public DporNode<STATE, TRANSITION> getNode() {
		return node;
	}

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
	 */
	public void addToBacktrack(int pid) {
		backtrack.add(pid);
	}
	
	/**
	 * Fills the backtrack with all remaining enabled processes.
	 * 
	 * @return the number of new processes added to the backtrack
	 */
	public int fullyEnable() {
		Set<Integer> remainingProcs = new HashSet<>(enabledProcs);
		remainingProcs.removeAll(backtrack);
		backtrack.addAll(remainingProcs);
		return remainingProcs.size();
	}
	
	public Collection<Integer> enabledProcs() {
		return enabledProcs;
	}
	
	/**
	 * Increments to the next outgoing transition to be explored from the
	 * current state. This may involve switching to the transitions of the next
	 * process in the backtrack set.
	 * 
	 * @return true iff a next transition exists
	 */
	public boolean nextTransition() {
		if (nextTransitionInProc() == null) {
			return nextProc() != -1;
		}
		return true;
	}
	
	/**
	 * @return the current transition and also move the
	 *         {@link #transitionIterator}.
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
	 *            Used for obtaining the set of enabled transitions for the next
	 *            process
	 * @return
	 */
	private int nextProc() {
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
	public int getLastEntryPosition() {
		return stackProcInfo.lastEntry;
	}
	
	/**
	 * Precondition: This entry is not on the top of the stack.
	 * 
	 * @return The {@link DporHbSet} representing the set of stack entries with
	 *         transitions that happen before this entry's transition
	 */
	public DporHbSet getHbSet() {
		return stackProcInfo.hbSet;
	}
	
	void setStackProcInfo(StackProcessInfo stackProcInfo) {
		this.stackProcInfo = stackProcInfo;
	}
	
	StackProcessInfo getStackProcInfo() {
		return stackProcInfo;
	}
}