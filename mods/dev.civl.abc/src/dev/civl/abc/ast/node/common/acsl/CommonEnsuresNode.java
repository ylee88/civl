package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.EnsuresNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.token.IF.Source;

public class CommonEnsuresNode extends CommonContractNode implements EnsuresNode {

	public CommonEnsuresNode(Source source, ExpressionNode expression) {
		super(source, expression);
	}

	@Override
	public ExpressionNode getExpression() {
		return (ExpressionNode) child(0);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("Ensures");
	}

	@Override
	public EnsuresNode copy() {
		return new CommonEnsuresNode(getSource(), duplicate(getExpression()));
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.ENSURES;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index != 0)
			throw new ASTException("CommonEnsuresNode has only one child, but saw index " + index);
		if (!(child == null || child instanceof ExpressionNode))
			throw new ASTException("Child of CommonEnsuresNode must be an ExpressionNode, but saw " + child
					+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
