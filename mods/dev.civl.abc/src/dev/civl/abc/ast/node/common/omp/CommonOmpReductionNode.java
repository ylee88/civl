package dev.civl.abc.ast.node.common.omp;

import dev.civl.abc.ast.node.IF.omp.OmpReductionNode;
import dev.civl.abc.ast.node.common.CommonASTNode;
import dev.civl.abc.token.IF.Source;

public abstract class CommonOmpReductionNode extends CommonASTNode
		implements OmpReductionNode {

	public CommonOmpReductionNode(Source source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.OMP_REDUCTION_OPERATOR;
	}
}
