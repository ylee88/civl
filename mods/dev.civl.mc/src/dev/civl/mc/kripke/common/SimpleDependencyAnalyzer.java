package dev.civl.mc.kripke.common;

import dev.civl.gmc.dpor.DependencyAnalyzer;
import dev.civl.gmc.dpor.DporSearchStack;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.State;

public class SimpleDependencyAnalyzer
		implements
			DependencyAnalyzer<State, Transition> {

	@Override
	public boolean checkDependent(DporSearchStack<State, Transition> stack,
			int stackIndex, int pid) {
		return true;
	}

}
