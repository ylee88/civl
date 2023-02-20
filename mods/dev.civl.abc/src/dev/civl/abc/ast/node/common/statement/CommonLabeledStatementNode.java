package dev.civl.abc.ast.node.common.statement;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.label.LabelNode;
import dev.civl.abc.ast.node.IF.statement.LabeledStatementNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

public class CommonLabeledStatementNode extends CommonStatementNode
		implements
			LabeledStatementNode {

	public CommonLabeledStatementNode(Source source, LabelNode label,
			StatementNode statement) {
		super(source, label, statement);
	}

	@Override
	public LabelNode getLabel() {
		return (LabelNode) child(0);
	}

	@Override
	public StatementNode getStatement() {
		return (StatementNode) child(1);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("LabeledStatement");
	}

	@Override
	public LabeledStatementNode copy() {
		return new CommonLabeledStatementNode(getSource(),
				duplicate(getLabel()), duplicate(getStatement()));
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.LABELED;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 2)
			throw new ASTException(
					"CommonLabeledStatementNode has only two children, but saw index "
							+ index);
		if (index == 0 && (!(child == null || child instanceof LabelNode)))
			throw new ASTException(
					"Child of CommonLabeledStatementNode at index " + index
							+ "  must be a LabelNode, but saw " + child
							+ " with type " + child.nodeKind());
		if (index == 1 && (!(child == null || child instanceof StatementNode)))
			throw new ASTException(
					"Child of CommonLabeledStatementNode at index " + index
							+ " must be a StatementNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
