package dev.civl.abc.ast.node.common.acsl;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.token.IF.Source;

public abstract class CommonTransformNode extends CommonContractNode
		implements
			TransformNode {

	CommonTransformNode(Source source, Iterable<? extends ASTNode> children) {
		super(source, children);
	}

	CommonTransformNode(Source source) {
		super(source);
	}

	CommonTransformNode(Source source, ASTNode child0) {
		super(source, child0);
	}

	CommonTransformNode(Source source, ASTNode child0, ASTNode child1) {
		super(source, child0, child1);
	}

	CommonTransformNode(Source source, ASTNode child0, ASTNode child1,
			ASTNode child2) {
		super(source, child0, child1, child2);
	}
	
	CommonTransformNode(Source source, ASTNode child0, ASTNode child1,
			ASTNode child2, ASTNode child3) {
		super(source, child0, child1, child2, child3);
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.TRANSFORM;
	}

}
