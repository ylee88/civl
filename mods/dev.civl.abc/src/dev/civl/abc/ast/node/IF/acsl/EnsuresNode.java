package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * An "ensures" clause in a procedure contract clause that represents a
 * post-condition.
 *
 * @author siegel
 */
public interface EnsuresNode extends ContractNode {

	/**
	 * An expression of boolean type which is the post-condition
	 *
	 * @return the boolean expression post-condition
	 */
	ExpressionNode getExpression();

	@Override
	EnsuresNode copy();
}
