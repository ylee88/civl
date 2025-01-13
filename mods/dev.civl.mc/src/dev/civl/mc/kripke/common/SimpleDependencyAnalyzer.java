package dev.civl.mc.kripke.common;

import dev.civl.gmc.dpor.DependencyAnalyzer;
import dev.civl.gmc.dpor.DporSearchStack;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.SeqSet;

public class SimpleDependencyAnalyzer
		implements
			DependencyAnalyzer<State, Transition> {
	
	private SimpleEnabler enabler;

	public SimpleDependencyAnalyzer(SimpleEnabler enabler) {
		this.enabler = enabler;
	}
	
	@Override
	public boolean checkDependent(DporSearchStack<State, Transition> stack,
			int stackIndex, int pid) {
		DporSearchStack<State, Transition>.Entry inEntry, topEntry;
		inEntry = stack.get(stackIndex);
		topEntry = stack.top();
		State inState = inEntry.getState(), topState = topEntry.getState();

		try {
			SeqSet inDep = new SeqSet(), inDepWrite = new SeqSet();
			enabler.computeDepends(inState, inEntry.getPid(), inDep,
					inDepWrite);
			for (int otherProc : inEntry.enabledProcs()) {
				if (otherProc == inEntry.getPid())
					continue;
				SeqSet otherReach = new SeqSet(),
						otherReachWrite = new SeqSet();
				enabler.computeReach(inState, otherProc, otherReach,
						otherReachWrite);
				if (!inDep.disjoint(otherReachWrite))
					return true;
			}

			SeqSet topDep = new SeqSet(), topDepWrite = new SeqSet();
			enabler.computeDepends(topState, pid, topDep, topDepWrite);
			for (int otherProc : topEntry.enabledProcs()) {
				if (otherProc == pid)
					continue;

				SeqSet otherReach = new SeqSet(),
						otherReachWrite = new SeqSet();
				enabler.computeReach(topState, otherProc, otherReach,
						otherReachWrite);
				if (!topDep.disjoint(otherReachWrite))
					return true;
			}

			return !(inDep.disjoint(topDepWrite)
					&& topDep.disjoint(inDepWrite));
		} catch (UnsatisfiablePathConditionException e) {
			// edge doesn't exist so it can't be dependent right?
			return false;
		}
	}

}
