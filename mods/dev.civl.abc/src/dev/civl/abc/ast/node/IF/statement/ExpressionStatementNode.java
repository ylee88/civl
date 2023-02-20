package dev.civl.abc.ast.node.IF.statement;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

public interface ExpressionStatementNode extends StatementNode {

	ExpressionNode getExpression();

	@Override
	ExpressionStatementNode copy();
}
