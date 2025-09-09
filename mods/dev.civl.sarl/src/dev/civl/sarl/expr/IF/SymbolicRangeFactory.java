package dev.civl.sarl.expr.IF;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicRange;

/**
 * Factory for creating and manipulating {@link SymbolicRange}'s.
 * 
 * @author awilton
 */
public interface SymbolicRangeFactory {
	/**
	 * Generates a new {@link SymbolicRange} which only includes "element".
	 * 
	 * @param element
	 *            The expression representing the single value of this range.
	 * @return the newly created {@link SymbolicRange}
	 */
	SymbolicRange symbolicRange(NumericExpression element);

	/**
	 * Generates a new {@link SymbolicRange} which ranges from "lower"
	 * (inclusive) to "upper" (exclusive).
	 * 
	 * @param lower
	 *            The expression representing the (inclusive) lower bound.
	 * @param upper
	 *            The expression representing the (exclusive) upper bound.
	 * @return the newly created {@link SymbolicRange}
	 */
	SymbolicRange symbolicRange(NumericExpression lower,
			NumericExpression upper);

	/**
	 * Generates a new {@link SymbolicRange} which ranges from "lower"
	 * (inclusive) to "upper" (exclusive) taking "step" number of steps.
	 * 
	 * @param lower
	 *            The expression representing the (inclusive) lower bound.
	 * @param upper
	 *            The expression representing the (exclusive) upper bound.
	 * @param step
	 *            The expression representing the step value.
	 * @return the newly created {@link SymbolicRange}
	 */
	SymbolicRange symbolicRange(NumericExpression lower,
			NumericExpression upper, NumericExpression step);

	/**
	 * NOTE: Currently only support ranges with a step value of 1.
	 * 
	 * @param range0
	 *            the first {@link SymbolicRange}
	 * @param range1
	 *            the second {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true if the values of range0
	 *         are all strictly below the values of range1.
	 */
	BooleanExpression strictlyBelow(SymbolicRange range0, SymbolicRange range1);

	/**
	 * NOTE: Currently only support ranges with a step value of 1.
	 * 
	 * @param range0
	 *            the first {@link SymbolicRange}
	 * @param range1
	 *            the second {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true whenever range0 and
	 *         range1 are disjoint.
	 */
	BooleanExpression disjoint(SymbolicRange range0, SymbolicRange range1);

	/**
	 * NOTE: Currently only support ranges with a step value of 1.
	 * 
	 * @param range0
	 *            the first {@link SymbolicRange}
	 * @param range1
	 *            the second {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true whenever range0 is a
	 *         subset of range1.
	 */
	BooleanExpression subset(SymbolicRange range0, SymbolicRange range1);

	SymbolicRange[] diff(SymbolicRange range0, SymbolicRange range1);
	
	/**
	 * NOTE: Currently only support ranges with a step value of 1.
	 * 
	 * @param range0
	 *            the first {@link SymbolicRange}
	 * @param range1
	 *            the second {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true whenever range0 is
	 *         equal to range1.
	 */
	BooleanExpression equals(SymbolicRange range0, SymbolicRange range1);

	/**
	 * NOTE: Currently only support ranges with a step value of 1.
	 * 
	 * @param range0
	 *            the first {@link SymbolicRange}
	 * @param range1
	 *            the second {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true whenever range0 is
	 *         not equal to range1.
	 */
	BooleanExpression neq(SymbolicRange range0, SymbolicRange range1);
	
	/**
	 * @param expr
	 *            a {@link NumericExpression}
	 * @param range
	 *            a {@link SymbolicRange}
	 * @return A {@link BooleanExpression} which is true whenever expr is an
	 *         element of r.
	 */
	BooleanExpression inRange(NumericExpression expr, SymbolicRange range);

	/**
	 * Attempts to create a single {@link SymbolicRange} which is equivalent to
	 * the union of the input {@link SymbolicRange}s range0 and range1. If no
	 * such {@link SymbolicRange} can be constructed then null is returned.
	 * 
	 * Optionally takes a {@link Reasoner} "reasoner" for determining potential
	 * relationships between range0 and range1. If reasoner is null then simple
	 * syntactic reasoning is used instead.
	 * 
	 * @param reasoner
	 *            the {@link Reasoner} to be used in the union process. May be
	 *            null in which case simple syntactic reasoning is used.
	 * @param range0
	 *            a {@link SymbolicRange}
	 * @param range1
	 *            a {@link SymbolicRange}
	 * @return either a single {@link SymbolicRange} equivalent to the union of
	 *         range0 and range1, or null if such a range could not be
	 *         constructed.
	 */
	SymbolicRange tryUnion(Reasoner reasoner, SymbolicRange range0,
			SymbolicRange range1);
}
