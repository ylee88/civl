package dev.civl.sarl.simplify.simplification;

import java.util.Collections;
import java.util.Set;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;

/**
 * A {@link ProverHeuristic} which decides a prover call should be made iff the
 * free variables of a given predicate intersect a given set of {@link SymbolicConstant}s.
 * 
 * @author awilton
 *
 */
public class FreeVarProverHeuristic implements ProverHeuristic {
	private Set<SymbolicConstant> varSet;
	
	FreeVarProverHeuristic(Set<SymbolicConstant> varSet) {
		this.varSet = varSet;
	}
	
	@Override
	public boolean attemptValid(BooleanExpression predicate) {
		return !Collections.disjoint(varSet, predicate.getFreeVars());
	}

	@Override
	public boolean attemptUnsat(BooleanExpression predicate) {
		return !Collections.disjoint(varSet, predicate.getFreeVars());
	}

}
