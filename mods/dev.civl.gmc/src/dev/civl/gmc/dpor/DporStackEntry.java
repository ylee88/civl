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
	
	final private int pos;
	
	/**
	 * The stack index of the last transition from the same process as this
	 * entry. -1 if no such earlier transition exists or if this entry is
	 * on the top of the stack.
	 */
	private DporStackEntry<STATE, TRANSITION> lastEntry = null;
	
	/**
	 * Maps a process to a structure containing happens-before information
	 * for all outgoing transitions from this entry that belong to that
	 * process
	 */
	private Map<Integer, DporHbSet> procToHbSet = new HashMap<>();

	/**
	 * Index into backtrack of the process we are currently processing.
	 */
	private int current = 0;
	
	private Set<Integer> enabledProcs;

	/**
	 * The collection of transitions that the current
	 */
	private Collection<TRANSITION> transitions;

	/**
	 * The iterator to iterate either the ample set or the ample set complement
	 * of the {@link #sourceState}. This iterator will iterate over all the
	 * transitions after {@link #currentTran} transition.
	 */
	private Iterator<TRANSITION> transitionIterator;

	/**
	 * The index of the current transition. This is used to write the trace file
	 * which will be used later for replay.
	 */
	private int tid = -1;

	/**
	 * The current transition.
	 */
	private TRANSITION currentTran = null;

	/**
	 * @param node
	 *            The node that wraps the source state.
	 * @param dporSearchStack TODO
	 * @param transitions
	 *            The ample set or ample set complement of the source state.
	 * @param offset
	 *            the ID number that should be associated to the first
	 *            transition in the sequence.
	 */
	DporStackEntry(DporSearchStack<STATE, TRANSITION> dporSearchStack, DporNode<STATE, TRANSITION> node) {
		this.pos = dporSearchStack.size();
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
		// Initialize hb-relationships for all enabled processes
		for (int pid : enabledProcs) {
			DporHbSet hbSet = new DporHbSet();
			procToHbSet.put(pid, hbSet);
			// Add an edge to the last entry on the stack from this proc if one exists
			DporStackEntry<STATE, TRANSITION> lastEntry = dporSearchStack.lastEntry(pid);
			if (lastEntry != null) {
				hbSet.addEntry(lastEntry);
			}
		}
	}
	
	/**
	 * Initializes {@link DporStackEntry#transitionIterator}
	 */
	private void initializeTransitions(Collection<TRANSITION> newTransitions) {
		transitions = newTransitions;
		transitionIterator = transitions.iterator();
		nextTransition();
	}

	public TRANSITION currentTransition() {
		return currentTran;
	}

	public Collection<TRANSITION> getTransitions() {
		return transitions;
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
	 * @return the pid of the process currently being explored.
	 */
	public int getPid() {
		return backtrack.get(current);
	}
	
	public int getPos() {
		return pos;
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
	
	public DporStackEntry<STATE, TRANSITION> getLastEntry() {
		return lastEntry;
	}
	
	/**
	 * @return the current transition and also move the
	 *         {@link #transitionIterator}.
	 */
	TRANSITION nextTransition() {
		if (transitionIterator.hasNext()) {
			tid++;
			currentTran = transitionIterator.next();
		} else {
			currentTran = null;
		}
		return currentTran;
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
	int nextProc() {
		current++;
		if (current >= backtrack.size())
			return -1;
		
		int pid = backtrack.get(current);
		initializeTransitions(manager.getTransitions(node.getState(), pid));
		
		return pid;
	}
	
	/**
	 * Get the hb-relation for the specified process.
	 * 
	 * Precondition: pid is in this entry's enabled set.
	 */
	public DporHbSet getHbSet(int pid) {
		return procToHbSet.get(pid);
	}
	
	public DporHbSet getHbSet() {
		return getHbSet(getPid());
	}
	
	void addRace(DporStackEntry<STATE, TRANSITION> entry, int pid) {
		getHbSet(pid).addEntry(entry);
	}
	
	/**
	 * Sets the last interior entry with a transition from the process returned by {@link DporStackEntry#getPid()}
	 */
	void setLastEntry(DporStackEntry<STATE, TRANSITION> lastEntry) {
		this.lastEntry = lastEntry;
	}
}