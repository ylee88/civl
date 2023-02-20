package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.WildcardNode;
import dev.civl.abc.token.IF.Source;

public class CommonWildcardNode extends CommonExpressionNode
		implements
			WildcardNode {

	public CommonWildcardNode(Source source) {
		super(source);
	}

	@Override
	public WildcardNode copy() {
		return new CommonWildcardNode(this.getSource());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("...");
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.WILDCARD;
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
				"CommonWildcardNode has no child, but saw index " + index);
	}
}
