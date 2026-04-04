package dev.civl.sarl.IF;

import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.number.IF.Numbers;

public class SARLConstants {

	/**
	 * Used in a heuristic to determine when to use probabilistic methods to
	 * determine polynomial zero-ness. If the product of the number of variables and
	 * the total degree is greater than or equal to this number, the polynomial is
	 * considered too big to be expanded, and probabilistic techniques will be used
	 * instead (unless the probabilistic bound is 0).
	 */
	public static IntegerNumber polyProbThreshold = Numbers.REAL_FACTORY.integer(100);

	/**
	 * Shall this universe use backwards substitution to solve for certain numeric
	 * expressions in terms of others when simplifying? This is used in the Gaussian
	 * elimination simplification phase. If this option is {@code false}, the
	 * results of Gaussian elimination are used only to replace certain expressions
	 * with constants. If this option is {@code true}, the results are used more
	 * generally to replace certain expressions with linear combinations of other
	 * expressions. This can reduce the number of symbolic constants occurring in a
	 * context, but can be expensive.
	 */
	public static boolean useBackwardSubstitution = true;

	/**
	 * A technique for simplifying a context in CNF form that contains multiple
	 * or-clauses. If two or more or-clauses contain a common factor, a
	 * simplification may be possible. This technique looks for all opportunities of
	 * that kind. For example, if the context contains clauses p||q1 and p||q2 then
	 * it will replace those clauses with the result of p||simplify(q1&&q2), where
	 * the simplify occurs in the context obtained by removing the two original
	 * clauses from the original context.
	 */
	public static boolean useMultiOrReduction = true;

	/**
	 * Should an "OR" expression e be simplified as not(simplify(not(e))) ?
	 */
	public static boolean useDoubleOrNegation = false;

	/**
	 * Look for simplification cycles during simplification? If true, the simplifier
	 * will keep track of expressions seen during recursive calls and loops, and
	 * will return immediately as soon as an expression has been seen before. If
	 * false, this check will not take place and the simplifier may loop forever ---
	 * depending on the particular simplification rules used.
	 */
	// public static boolean cycleDetection = true;

	/**
	 * Simplifies an expression such as (a WITH i:=x)[j] to i==j?x:a[j]. This
	 * happens immediately in the universe, so the first expression is never even
	 * created.
	 */
	public static boolean arrayReadCondSimplify = true;
}
