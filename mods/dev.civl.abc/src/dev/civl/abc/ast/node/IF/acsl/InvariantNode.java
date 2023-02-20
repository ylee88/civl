package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

public interface InvariantNode extends ContractNode {
	/**
	 * Returns the expression of this invariant
	 * 
	 * @return
	 */
	ExpressionNode getExpression();

	/**
	 * is this a loop invariant?
	 * 
	 * @return
	 */
	boolean isLoopInvariant();

	@Override
	InvariantNode copy();
}
