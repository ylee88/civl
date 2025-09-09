package dev.civl.sarl.simplify.simplification;

import java.util.List;

import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * An interface for determining how {@link SymbolicExpression}s should be
 * simplified.
 * 
 * @author awilton
 *
 */
public interface SimplificationSelector {
	/**
	 * Given a {@link SymbolicExpression}, select a sequence of
	 * {@link Simplification}s to apply to the expression.
	 * 
	 * @param symbExpr
	 * @return A sequence of Simplifications that should be applied to the
	 *         expression
	 */
	List<Simplification> select(SymbolicExpression symbExpr);
}
