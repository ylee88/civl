package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * Constant <code>$nothing</code>, argument of <code>$assigns / $reads</code>
 * contract clauses.
 * 
 * @author Manchun Zheng
 *
 */
public interface NothingNode extends ExpressionNode {
	@Override
	NothingNode copy();
}
