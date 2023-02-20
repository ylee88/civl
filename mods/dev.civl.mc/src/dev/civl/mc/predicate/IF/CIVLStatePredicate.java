package dev.civl.mc.predicate.IF;

import dev.civl.mc.log.IF.CIVLExecutionException;
import dev.civl.mc.state.IF.State;
import dev.civl.gmc.StatePredicateIF;

public interface CIVLStatePredicate extends StatePredicateIF<State> {

	CIVLExecutionException getViolation();

	/**
	 * Returns the violation that has not yet been reported.
	 * 
	 * @return
	 */
	CIVLExecutionException getUnreportedViolation();

	/**
	 * Is this an And predicate?
	 * 
	 * @return
	 */
	boolean isAndPredicate();
}
