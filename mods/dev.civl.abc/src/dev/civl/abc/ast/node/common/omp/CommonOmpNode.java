package dev.civl.abc.ast.node.common.omp;

import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.omp.OmpNode;
import dev.civl.abc.ast.node.common.CommonASTNode;
import dev.civl.abc.token.IF.Source;

public abstract class CommonOmpNode extends CommonASTNode implements OmpNode {

	public CommonOmpNode(Source source) {
		super(source);
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.OMP_NODE;
	}

	@Override
	protected DifferenceObject diffWork(ASTNode that) {
		if (that instanceof OmpNode)
			if (this.ompNodeKind() == ((OmpNode) that).ompNodeKind())
				return null;
		return new DifferenceObject(this, that);
	}

	// @Override
	// public StatementKind statementKind() {
	// return StatementKind.OMP;
	// }
}
