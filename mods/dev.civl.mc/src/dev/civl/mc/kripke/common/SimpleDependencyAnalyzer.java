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

public class SimpleDependencyAnalyzer
		implements
			DependencyAnalyzer<State, Transition> {
	
	private StateManager<State, Transition> manager;
	private StateFactory stateFactory;
	private SimpleEnabler enabler;

	public SimpleDependencyAnalyzer(StateManager<State, Transition> manager, StateFactory stateFactory, SimpleEnabler enabler) {
		this.manager = manager;
		this.stateFactory = stateFactory;
		this.enabler = enabler;
	}
	
	@Override
	public int numCrossTransitions() {
		return 0;
	}
	
	@Override
	public int numCrossTraceSteps() {
		return 0;
	}
	
	@Override
	public boolean checkDependent(DporSearchStack<State, Transition> stack,
			int stackIndex, int pid) {
		DporStackEntry<State, Transition> inEntry, topEntry;
		inEntry = stack.get(stackIndex);
		topEntry = stack.top();
		int inPid = inEntry.getPid();
		State inState = inEntry.getState(), topState = topEntry.getState();

		if (topState.getProcessState(pid).hasEmptyStack())
			return true;
		
		try {
			//SeqSet inReach = new SeqSet(), inReachWrite = new SeqSet();
			//enabler.computeReach(inState, inPid, inReach, inReachWrite);
			SeqSet inDep = new SeqSet(), inDepWrite = new SeqSet();
			enabler.computeDepends(inState, inPid, inDep, inDepWrite);
			
			//SeqSet topReach = new SeqSet(), topReachWrite = new SeqSet();
			//enabler.computeReach(topState, pid, topReach, topReachWrite);
			SeqSet topDep = new SeqSet(), topDepWrite = new SeqSet();
			Set<Integer> waitees = enabler.computeDepends(topState, pid, topDep, topDepWrite);
			
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
			
			return true;
			
		} catch (UnsatisfiablePathConditionException e) {
			// edge doesn't exist so it can't be dependent right?
			return false;
		}
	}
}
