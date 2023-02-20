package dev.civl.abc.ast.node.common.acsl;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.common.CommonASTNode;
import dev.civl.abc.token.IF.Source;

public abstract class CommonContractNode extends CommonASTNode implements
		ContractNode {

	public CommonContractNode(Source source, ASTNode child) {
		super(source, child);
	}

	public CommonContractNode(Source source, ASTNode child0, ASTNode child1) {
		super(source, child0, child1);
	}

	public CommonContractNode(Source source, ASTNode child0, ASTNode child1,
			ASTNode child2) {
		super(source, child0, child1, child2);
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.CONTRACT;
	}
}
