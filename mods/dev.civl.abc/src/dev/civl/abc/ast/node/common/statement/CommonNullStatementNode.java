package dev.civl.abc.ast.node.common.statement;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.NullStatementNode;
import dev.civl.abc.token.IF.Source;

public class CommonNullStatementNode extends CommonStatementNode
		implements
			NullStatementNode {

	public CommonNullStatementNode(Source source) {
		super(source);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("NullStatement");
	}

	@Override
	public NullStatementNode copy() {
		return new CommonNullStatementNode(getSource());
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.NULL;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"Set child operation is not allowed at CommonNullStatementNode.");
	}
}
