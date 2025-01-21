package dev.civl.gmc.dpor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
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
 * Races can only be added by a user which are between some inner stack entry
 * and the outermost/top stack entry. Therefore, the user is responsible for adding
 * all races for the top stack entry before pushing to the stack.
 * 
 * @author Alex Wilton
 */
public class DporSearchStack<STATE, TRANSITION> {
	private StateManager<STATE, TRANSITION> manager;
	private DporNodeFactory<STATE, TRANSITION> nodeFactory;
	
	private Stack<Entry> stack = new Stack<>();
	
	private Map<Integer, Integer> procLastEntry = new HashMap<>();
	
	private int numStatesSeen = 0;

	DporSearchStack(StateManager<STATE, TRANSITION> manager,
			DporNodeFactory<STATE, TRANSITION> nodeFactory,
			STATE initialState) {
		this.manager = manager;
		this.nodeFactory = nodeFactory;
		DporNode<STATE, TRANSITION> initialNode = nodeFactory
				.getInitialNode(initialState);
		Entry initialEntry = new Entry(initialNode);
		
		stack.push(initialEntry);
		initialNode.setSeen(true);
		initialNode.setStackPosition(0);
		numStatesSeen++;
	}
	
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public int size() {
		return stack.size();
	}
	
	public Entry top() {
		return stack.peek();
	}
	
	public Entry get(int index) {
		return stack.get(index);
	}
	
	public Set<Integer> seenProcesses() {
		return procLastEntry.keySet();
	}
	
	public int lastEntryPos(int proc) {
		return procLastEntry.getOrDefault(proc, -1);
	}
	
	public STATE currentState() {
		return top().getState();
	}
	
	public int numStatesSeen() {
		return numStatesSeen;
	}
	
	public TRANSITION currentTransition() {
		return top().currentTransition();
	}
	
	/**
	 * Increments to the next outgoing transition to be explored from the
	 * current state. This may involve switching to the transitions of the next
	 * process in the backtrack set.
	 * 
	 * @return true iff a next transition exists
	 */
	public boolean nextTransition() {
		if (top().nextTransition() == null) {
			return top().nextProc() != -1;
		}
		return true;
	}
	
	/**
	 * Requires currentTransition() != null;
	 * 
	 * Executes currentTransition() and pushes resulting state onto the stack
	 * 
	 * Returns whether the attained state after pushing has been seen before
	 */
	public boolean pushTransition() {
		// Current top entry will now represent a transition and so "last entry"
		// info needs updating
		top().setLastEntry(procLastEntry.getOrDefault(top().getPid(), -1));
		procLastEntry.put(top().getPid(), stack.size() - 1);
		
		DporNode<STATE, TRANSITION> topNode = top().getNode();
		STATE topState = topNode.getState();
		TRANSITION currentTran = top().currentTransition();
		TraceStepIF<STATE> traceStep = topNode.getTraceStep(currentTran);
		if (traceStep == null) {
			traceStep = manager.nextState(topState, currentTran);
			topNode.setTraceStep(currentTran, traceStep);
		}
		
		manager.printTraceStep(topState, traceStep);
		DporNode<STATE, TRANSITION> newNode = nodeFactory
				.getNode(traceStep);
		manager.printTraceStepFinalState(newNode.getState(), newNode.getId());
		boolean seen = newNode.getSeen();
		newNode.setSeen(true);
		newNode.setStackPosition(stack.size());
		stack.push(new Entry(newNode));
		if (!seen)
			numStatesSeen++;
		
		return seen;
	}
	
	public void popTransition() {
		manager.debug(top().getState(), top().backtrack);
		stack.pop();
		// New top no longer represents a transition and so last entry info
		// needs updated
		if (!stack.isEmpty()) {
			if (top().getLastEntry() < 0)
				procLastEntry.remove(top().getPid());
			else
				procLastEntry.put(top().getPid(), top().getLastEntry());
			
			top().setLastEntry(-1);
		}
	}
	
	public void addRace(int outerPid, int pos) {
		top().addRace(outerPid, pos);
	}

	public boolean hb(int entryPos, int pid) {
		return top().getHbRel(pid).hb(entryPos);
	}
	
	public StackTraversal makeStackTraversal(int proc) {
		return new StackTraversal(proc);
	}

	public class Entry {
		/**
		 * The search node that wraps the source state with its search information
		 * like stack position or fullyExpanded flag.
		 */
		private DporNode<STATE, TRANSITION> node;

		/**
		 * Collection of processes which need to be explored
		 */
		private List<Integer> backtrack = new ArrayList<>();
		
		/**
		 * The stack index of the last transition from the same process as this
		 * entry. -1 if no such earlier transition exists.
		 */
		private int lastEntry = -1;
		
		/**
		 * Maps a process to a structure containing happens-before information
		 * for all outgoing transitions from this entry that belong to that
		 * process
		 */
		private Map<Integer, HbRelation> procToHbRel = new HashMap<>();
		

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
		 * @param transitions
		 *            The ample set or ample set complement of the source state.
		 * @param offset
		 *            the ID number that should be associated to the first
		 *            transition in the sequence.
		 */
		private Entry(DporNode<STATE, TRANSITION> node) {
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
			for (int proc : enabledProcs) {
				HbRelation hbRel = new HbRelation();
				procToHbRel.put(proc, hbRel);
				// Add an edge to the last entry on the stack from this proc if one exists
				int lastEntry = procLastEntry.getOrDefault(proc, -1);
				if (lastEntry >= 0) {
					hbRel.addEdge(lastEntry, false);
				}
			}
		}
		
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
		 * Only call this if isDone() is false.
		 * 
		 * @return the pid of the process currently being explored.
		 */
		public int getPid() {
			return backtrack.get(current);
		}
		
		public boolean isDone() {
			return current >= backtrack.size();
		}
		
		public boolean inBacktrack(int pid) {
			return backtrack.contains(pid);
		}
		
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
		
		public int getLastEntry() {
			return lastEntry;
		}
		
		/**
		 * @return the current transition and also move the
		 *         {@link #transitionIterator}.
		 */
		private TRANSITION nextTransition() {
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
		private int nextProc() {
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
		public HbRelation getHbRel(int pid) {
			return procToHbRel.get(pid);
		}
		
		public HbRelation getHbRel() {
			return getHbRel(getPid());
		}
		
		private void addRace(int pid, int entryPos) {
			getHbRel(pid).addRace(entryPos);
		}
		
		private void setLastEntry(int lastEntry) {
			this.lastEntry = lastEntry;
		}
	}
	
	public class HbRelation {
		private Map<Integer, HbEdge> map = new HashMap<>();
		
		public HbRelation() {}
		
		public void addRace(int entryPos) {
			addEdge(entryPos, true);
		}
		
		private void addEdge(int entryPos, boolean isRace) {
			Entry entry = get(entryPos);
			int pid = entry.getPid();
			for (Map.Entry<Integer, HbEdge> hbEdge : entry.getHbRel(pid).map.entrySet()) {
				int edgePos = hbEdge.getValue().entry;
				HbEdge newEdge = new HbEdge(edgePos, false);
				if (edgePos >= map.getOrDefault(hbEdge.getKey(), newEdge).entry) {
					map.put(hbEdge.getKey(), newEdge);
				}
			}
			map.put(pid, new HbEdge(entryPos, isRace));
		}
		
		public boolean hb(int entryPos) {
			Entry entry = get(entryPos);
			HbEdge edge = map.getOrDefault(entry.getPid(), null);
			return edge == null ? false : entryPos <= edge.entry;
		}
		
		public int lastHbEntry(int pid) {
			HbEdge lastEdge = map.getOrDefault(pid, null);
			return lastEdge == null ? -1 : lastEdge.entry;
		}
		
		public Set<Integer> hbProcSet() {
			return map.keySet();
		}
		
		static private class HbEdge {
			int entry;
			@SuppressWarnings("unused")
			boolean isRace;
			
			public HbEdge(int entry, boolean isRace) {
				this.entry = entry;
				this.isRace = isRace;
			}
		}
	}
	
	/**
	 * A structure for efficiently traversing the stack in reverse order,
	 * skipping entries which happen before the specified process of the top
	 * stack entry. Adding races to the stack while traversing is permitted and
	 * such changes are reflected in the traversal.
	 */
	public class StackTraversal {
		private PriorityQueue<Integer> entryQueue;
		private HbRelation topHbRel;
		
		private StackTraversal(int proc) {
			topHbRel = top().getHbRel(proc);
			Set<Integer> seenProcs = seenProcesses();
			
			if (seenProcs.isEmpty())
				entryQueue = new PriorityQueue<Integer>();
			else {
				entryQueue = new PriorityQueue<Integer>(seenProcs.size(),
						Collections.reverseOrder());
				for (int seenProc : seenProcs) {
					int lastEntry = procLastEntry.get(seenProc);
					if (!topHbRel.hb(lastEntry)) {
						entryQueue.add(lastEntry);
					}
				}
			}
		}
		
		public int next() {
			while (!entryQueue.isEmpty()) {
				int lastEntryPos = entryQueue.poll();
				if (!topHbRel.hb(lastEntryPos)) {
					Entry lastEntry = get(lastEntryPos);
					int nextLastEntryPos = lastEntry.getLastEntry();
					if (nextLastEntryPos >= 0)
						entryQueue.add(nextLastEntryPos);
					return lastEntryPos;
				}
			}
			return -1;
		}
	}
}
