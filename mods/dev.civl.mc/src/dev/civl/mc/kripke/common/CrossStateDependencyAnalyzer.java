package dev.civl.mc.kripke.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import dev.civl.gmc.dpor.DependencyAnalyzer;
import dev.civl.gmc.dpor.DporSearchStack;
import dev.civl.gmc.dpor.DporStackEntry;
import dev.civl.gmc.seq.StateManager;
import dev.civl.mc.kripke.IF.TraceStep;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.SeqSet;

public class CrossStateDependencyAnalyzer
		implements
			DependencyAnalyzer<State, Transition> {
	
	private StateManager<State, Transition> manager;
	private StateFactory stateFactory;
	private SimpleEnabler enabler;
	
	private int numCrossTransitions = 0;
	private int numCrossTraceSteps = 0;

	public CrossStateDependencyAnalyzer(StateManager<State, Transition> manager, StateFactory stateFactory, SimpleEnabler enabler) {
		this.manager = manager;
		this.stateFactory = stateFactory;
		this.enabler = enabler;
	}
	
	@Override
	public int numCrossTransitions() {
		return numCrossTransitions;
	}
	
	@Override
	public int numCrossTraceSteps() {
		return numCrossTraceSteps;
	}
	
	private void collectTraceStepStats(TraceStep traceStep) {
		numCrossTraceSteps++;
		numCrossTransitions += traceStep.getNumAtomicSteps();
	}
	
	@Override
	public boolean checkDependent(DporSearchStack<State, Transition> stack,
			int stackIndex, int pid) {
		DporStackEntry<State, Transition> inEntry, topEntry;
		inEntry = stack.get(stackIndex);
		topEntry = stack.top();
		int inPid = inEntry.getPid();
		State inState = inEntry.getState(), topState = topEntry.getState();

		SeqSet inLocal = computeLocalMem(inEntry, inPid);
		SeqSet topLocal = computeLocalMem(topEntry, pid);
		
		try {
			State crossState = stateFactory.crossState(inState, inPid, inLocal, topState, pid, topLocal);
			/* Dependencies of inPid and topPid must be computed using
			 * crossState since the actions of other processes might affect what
			 * inPid or topPid depend on in their respective states (and
			 * crossState over-approximates these other actions)
			 */
			SeqSet inDep = new SeqSet(), inDepWrite = new SeqSet();
			enabler.computeDepends(crossState, inPid, inDep, inDepWrite);
			SeqSet topDep = new SeqSet(), topDepWrite = new SeqSet();
			Set<Integer> waitees = enabler.computeDepends(crossState, pid, topDep, topDepWrite);
			/* TODO: I think we want there to be a HB edge in this case but we
			 * don't actually want to add a backtrack point (unless there are
			 * other outgoing edges from outPid which aren't $wait and are
			 * dependent with inEntry).
			 */
			if (waitees != null && waitees.contains(inPid))
				return true;
			
			if (inDep.disjoint(topDepWrite)
					&& topDep.disjoint(inDepWrite))
				return false;
			
			TraceStep inTopStep = (TraceStep) manager.tryNextState(crossState, inEntry.currentTransition());
			if (inTopStep == null)
				return true;
			collectTraceStepStats(inTopStep);
			
			
			Collection<Transition> enabledBeforeIn = manager.getTransitions(crossState, pid);
			Collection<Transition> enabledAfterIn = manager.getTransitions(inTopStep.getFinalState(), pid);
			if (!transitionSetsEqual(enabledBeforeIn, enabledAfterIn))
				return true;
			
			Collection<Transition> enabledBeforeTop = manager.getTransitions(crossState, inPid);
			for (Transition tran : enabledBeforeIn) {
				TraceStep topInStep = (TraceStep) manager.tryNextState(crossState, tran);
				if (topInStep == null)
					return true;
				collectTraceStepStats(topInStep);
				
				Collection<Transition> enabledAfterTop = manager.getTransitions(topInStep.getFinalState(), inPid);
				if (!transitionSetsEqual(enabledBeforeTop, enabledAfterTop))
					return true;
				
				inTopStep = (TraceStep) manager.tryNextState(inTopStep.getFinalState(), tran);
				if (inTopStep == null)
					return true;
				collectTraceStepStats(inTopStep);
				topInStep = (TraceStep) manager.tryNextState(topInStep.getFinalState(), inEntry.currentTransition());
				if (topInStep == null)
					return true;
				collectTraceStepStats(topInStep);
				
				if (!inTopStep.getFinalState().equals(topInStep.getFinalState()))
					return true;
			}
			
			return false;
			
		} catch (UnsatisfiablePathConditionException e) {
			// edge doesn't exist so it can't be dependent right?
			return false;
		}
	}
	
	/**
	 * Tries to determinne of the two collections of transitions are equal.
	 * 
	 * @param tranSet1
	 *            A collection of transitions
	 * @param tranSet2
	 *            A collection of transitions
	 * @return Whether they contain the same set of transitions
	 */
	private boolean transitionSetsEqual(Collection<Transition> tranSet1, Collection<Transition> tranSet2) {
		if (tranSet1.size() != tranSet2.size())
			return false;
		
		List<Transition> unmatchedTransitions = new LinkedList<>(tranSet1);
		for (Transition tran : tranSet2) {
			boolean foundMatch = false;
			ListIterator<Transition> iter = unmatchedTransitions.listIterator();
			while (iter.hasNext()) {
				Transition unmatchedTran = iter.next();
				if (unmatchedTran.equals(tran)) {
					foundMatch = true;
					iter.remove();
					break;
				}
			}
			if (!foundMatch)
				return false;
		}
		
		return true;
	}

	/**
	 * Looks at the state contained by "entry" and computes the set of local
	 * memory locations to process pid. A memory location is local to pid if pid
	 * can reach it but no other enabled process can.
	 * 
	 * @param entry
	 * @param pid
	 * @return the set of memory locations local to pid at entry
	 */
	private SeqSet computeLocalMem(DporStackEntry<State, Transition> entry, int pid) {
		State state = entry.getState();
		SeqSet nonLocal = new SeqSet();
		for (int otherProc : entry.enabledProcs()) {
			if (otherProc == pid)
				continue;
			SeqSet otherReach = new SeqSet(), otherReachWrite = new SeqSet();
			enabler.computeReach(state, otherProc, otherReach,
					otherReachWrite);
			nonLocal.addAll(otherReachWrite);
		}

		SeqSet reach = new SeqSet(), reachWrite = new SeqSet();
		enabler.computeReach(state, pid, reach, reachWrite);
		SeqSet local = new SeqSet();
		for (int[] leaves : reach.getLeaves()) {
			if (!nonLocal.contains(leaves)) {
				local.add(leaves);
			}
		}
		
		return local;
	}

}
