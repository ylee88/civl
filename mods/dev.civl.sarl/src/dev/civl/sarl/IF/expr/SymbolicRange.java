package dev.civl.sarl.IF.expr;

/**
 * Represents a symbolic range of integer values.
 * 
 * @author awilton
 */
public interface SymbolicRange {
	static public enum RangeKind {
		/**
		 * A range consisting of a single value.
		 */
		SINGLETON,
		/**
		 * A contiguous range of values.
		 */
		INTERVAL,
		/**
		 * A range of values which are evenly spaced by a step value.
		 */
		REGULAR
	}

	/**
	 * @return the {@link RangeKind} of this object
	 */
	RangeKind getRangeKind();

	/**
	 * @return the (inclusive) lower bound expression
	 */
	NumericExpression getLower();

	/**
	 * @return the (exclusive) upper bound expression
	 */
	NumericExpression getUpper();

	/**
	 * @return the step expression
	 */
	NumericExpression getStep();
}
