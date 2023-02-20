package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.RemoteOnExpressionNode;
import dev.civl.abc.token.IF.Source;

public class CommonRemoteExpressionNode extends CommonExpressionNode
		implements
			RemoteOnExpressionNode {

	public CommonRemoteExpressionNode(Source source,
			ExpressionNode processExpression, ExpressionNode foreignNode) {
		super(source, processExpression, foreignNode);

	}

	@Override
	public boolean isConstantExpression() {
		return false;
	}

	@Override
	public ExpressionNode getProcessExpression() {
		return (ExpressionNode) child(0);
	}

	@Override
	public ExpressionNode getForeignExpressionNode() {
		return (ExpressionNode) child(1);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("RemoteOnExpressionNode");
	}

	@Override
	public RemoteOnExpressionNode copy() {
		return new CommonRemoteExpressionNode(getSource(),
				duplicate(getProcessExpression()),
				duplicate(getForeignExpressionNode()));
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.REMOTE_REFERENCE;
	}

	@Override
	public boolean isSideEffectFree(boolean errorsAreSideEffects) {
		return getProcessExpression().isSideEffectFree(errorsAreSideEffects);
	}

	@Override
	public void setProcessExpression(ExpressionNode arg) {
		setChild(0, arg);
	}

	@Override
	public void setForeignExpressionNode(ExpressionNode arg) {
		setChild(1, arg);
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 2)
			throw new ASTException(
					"CommonRemoteExpressionNode has two children, but saw index "
							+ index);
		if ((child == null || child instanceof ExpressionNode))
			throw new ASTException(
					"Child of CommonRemoteExpressionNode at index " + index
							+ " must be a ExpressionNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
