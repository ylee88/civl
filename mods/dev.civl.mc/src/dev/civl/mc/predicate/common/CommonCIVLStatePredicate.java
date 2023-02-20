package dev.civl.mc.predicate.common;

import dev.civl.mc.log.IF.CIVLExecutionException;
import dev.civl.mc.predicate.IF.CIVLStatePredicate;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.sarl.IF.SymbolicUniverse;

public abstract class CommonCIVLStatePredicate implements CIVLStatePredicate {

	/**
	 * If violation is found it is cached here.
	 */
	protected CIVLExecutionException violation = null;

	protected SymbolicUniverse universe;

	/**
	 * The symbolic analyzer for operations on symbolic expressions and states,
	 * used in this class for printing states.
	 */
	protected SymbolicAnalyzer symbolicAnalyzer;

	@Override
	public CIVLExecutionException getViolation() {
		return this.violation;
	}

	@Override
	public CIVLExecutionException getUnreportedViolation() {
		if (this.violation != null && !this.violation.isReported())
			return this.violation;
		return null;
	}

	@Override
	public boolean isAndPredicate() {
		return false;
	}
}
