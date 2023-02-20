package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.NothingNode;
import dev.civl.abc.ast.node.common.expression.CommonExpressionNode;
import dev.civl.abc.token.IF.Source;

public class CommonNothingNode extends CommonExpressionNode
		implements
			NothingNode {

	public CommonNothingNode(Source source) {
		super(source);
	}

	@Override
	public NothingNode copy() {
		return new CommonNothingNode(this.getSource());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.println("NothingNode");
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.NOTHING;
	}

	@Override
	public boolean isConstantExpression() {
		return true;
	}

	@Override
	public boolean isSideEffectFree(boolean errorsAreSideEffects) {
		return true;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonNothingNode has no child, but saw index " + index);
	}
}
