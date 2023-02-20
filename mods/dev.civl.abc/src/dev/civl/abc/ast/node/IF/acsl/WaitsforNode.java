package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

public interface WaitsforNode extends ContractNode {
	/**
	 * Returns all arguments of the "waitsfor" clause as a {@link SequenceNode}
	 * 
	 * @return
	 */
	SequenceNode<ExpressionNode> getArguments();

	@Override
	WaitsforNode copy();
}
