package dev.civl.mc.transform.common.contracts;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.mc.transform.SubstituteGuide;

public class CommonASTNodeSubstituteGuide extends SubstituteGuide {

	public CommonASTNodeSubstituteGuide(ExpressionNode newNode,
			ExpressionNode oldNode) {
		super(new ASTNode[]{newNode}, oldNode);
	}

	@Override
	protected ASTNode buildNewNode(NodeFactory nf) {
		assert super.newNodeComponents.length == 1;
		return super.newNodeComponents[0];
	}
}
