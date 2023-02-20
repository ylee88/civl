package dev.civl.abc.ast.node.IF.statement;

import dev.civl.abc.ast.node.IF.label.LabelNode;

public interface LabeledStatementNode extends StatementNode {

	LabelNode getLabel();

	StatementNode getStatement();

	@Override
	LabeledStatementNode copy();

}
