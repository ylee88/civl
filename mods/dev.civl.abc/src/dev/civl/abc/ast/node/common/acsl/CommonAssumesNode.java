package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.AssumesNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.token.IF.Source;

public class CommonAssumesNode extends CommonContractNode
		implements
			AssumesNode {

	public CommonAssumesNode(Source source, ExpressionNode predicate) {
		super(source, predicate);
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.ASSUMES;
	}

	@Override
	public ExpressionNode getPredicate() {
		return (ExpressionNode) this.child(0);
	}

	@Override
	public AssumesNode copy() {
		return new CommonAssumesNode(getSource(), duplicate(getPredicate()));
	}

	@Override
	protected void printBody(PrintStream out) {
		out.println("AssumesNode");
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index != 0)
			throw new ASTException(
					"CommonAssumesNode has only one child, but saw index "
							+ index);
		if (!(child == null || child instanceof ExpressionNode))
			throw new ASTException(
					"Child of CommonAssumesNode must be an ExpressionNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
