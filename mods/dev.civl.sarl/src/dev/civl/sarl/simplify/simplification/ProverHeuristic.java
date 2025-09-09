package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.prove.IF.TheoremProver;

/**
 * An interface for heuristically determining whether to make validity or
 * unsatisfiability calls for a given {@link BooleanExpression} during
 * simplification.
 * 
 * @author awilton
 *
 */
public interface ProverHeuristic {
	/**
	 * @param predicate
	 * @return true iff this heuristic has determined that a validity call to a
	 *         {@link TheoremProver} should be attempted.
	 */
	boolean attemptValid(BooleanExpression predicate);

	/**
	 * @param predicate
	 * @return true iff this heuristic has determined that an unsatisfiability call to a
	 *         {@link TheoremProver} should be attempted.
	 */
	boolean attemptUnsat(BooleanExpression predicate);
}
