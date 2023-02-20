package dev.civl.mc.predicate.IF;

import java.util.Map;
import java.util.Set;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.predicate.common.CommonAndPredicate;
import dev.civl.mc.predicate.common.CommonDeadlock;
import dev.civl.mc.predicate.common.CommonFunctionalEquivalence;
import dev.civl.mc.predicate.common.CommonPotentialDeadlock;
import dev.civl.mc.predicate.common.CommonTrivialPredicate;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class Predicates {

	public static Deadlock newDeadlock(SymbolicUniverse universe,
			Enabler enabler, StateFactory stateFactory,
			SymbolicAnalyzer symbolicAnalyzer) {
		return new CommonDeadlock(universe, enabler, stateFactory,
				symbolicAnalyzer);
	}

	public static PotentialDeadlock newPotentialDeadlock(
			SymbolicUniverse universe, Enabler enabler,
			LibraryEnablerLoader loader, Evaluator evaluator,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer) {
		return new CommonPotentialDeadlock(universe, enabler, loader, evaluator,
				modelFactory, symbolicUtil, symbolicAnalyzer);
	}

	public static FunctionalEquivalence newFunctionalEquivalence(
			SymbolicUniverse universe, SymbolicAnalyzer symbolicAnalyzer,
			String[] outputNames,
			Map<BooleanExpression, Set<Pair<State, SymbolicExpression[]>>> specOutputs) {
		return new CommonFunctionalEquivalence(universe, symbolicAnalyzer,
				outputNames, specOutputs);
	}

	public static AndPredicate newAndPredicate(CIVLStatePredicate predicate) {
		return new CommonAndPredicate(predicate);
	}

	public static TrivialPredicate newTrivialPredicate() {
		return new CommonTrivialPredicate();
	}
}
