package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * A {@link ProverHeuristic} which always attempts prover calls.
 * 
 * @author awilton
 *
 */
public class TotalProverHeuristic implements ProverHeuristic {

	@Override
	public boolean attemptValid(BooleanExpression predicate) {
		return true;
	}

	@Override
	public boolean attemptUnsat(BooleanExpression predicate) {
		return true;
	}

}
