package dev.civl.abc.ast.node.common.statement;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.RunNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

public class CommonRunNode extends CommonStatementNode implements RunNode {

	public CommonRunNode(Source source, StatementNode statement) {
		super(source, statement);
	}

	@Override
	public StatementNode getStatement() {
		return (StatementNode) child(0);
	}

	@Override
	public RunNode copy() {
		return new CommonRunNode(getSource(), duplicate(getStatement()));
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("RunNode");
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.RUN;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index != 0)
			throw new ASTException(
					"CommonRunNode has only one child, but saw index " + index);
		if (!(child == null || child instanceof StatementNode))
			throw new ASTException(
					"Child of CommonRunNode must be a StatementNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
