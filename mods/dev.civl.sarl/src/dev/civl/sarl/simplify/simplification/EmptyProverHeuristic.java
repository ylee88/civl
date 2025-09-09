package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * A {@link ProverHeuristic} which simply never attempts prover calls.
 * 
 * @author awilton
 *
 */
public class EmptyProverHeuristic implements ProverHeuristic {

	@Override
	public boolean attemptValid(BooleanExpression predicate) {
		return false;
	}

	@Override
	public boolean attemptUnsat(BooleanExpression predicate) {
		return false;
	}

}
