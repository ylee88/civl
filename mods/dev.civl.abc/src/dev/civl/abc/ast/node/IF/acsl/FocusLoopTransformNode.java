package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

public interface FocusLoopTransformNode extends FocusTransformNode {
	String getFocusTag();

	SequenceNode<ExpressionNode> getFocusWindow();
	
	SequenceNode<ExpressionNode> getMemoryList();
}
