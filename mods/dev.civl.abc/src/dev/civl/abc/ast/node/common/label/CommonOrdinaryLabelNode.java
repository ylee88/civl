package dev.civl.abc.ast.node.common.label;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.entity.IF.Label;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.label.OrdinaryLabelNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.ast.node.common.declaration.CommonDeclarationNode;
import dev.civl.abc.token.IF.Source;

public class CommonOrdinaryLabelNode extends CommonDeclarationNode
		implements
			OrdinaryLabelNode {

	private Function function;

	private StatementNode statement;

	public CommonOrdinaryLabelNode(Source source, IdentifierNode name) {
		super(source, name);
	}

	@Override
	public Label getEntity() {
		return (Label) super.getEntity();
	}

	@Override
	public Function getFunction() {
		return function;
	}

	@Override
	public void setFunction(Function function) {
		this.function = function;
	}

	@Override
	public StatementNode getStatement() {
		return statement;
	}

	@Override
	public void setStatement(StatementNode statement) {
		this.statement = statement;
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("Label");
	}

	public String getName() {
		return getIdentifier().name();
	}

	@Override
	public OrdinaryLabelNode copy() {
		return new CommonOrdinaryLabelNode(getSource(),
				duplicate(getIdentifier()));
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.ORDINARY_LABEL;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 1)
			throw new ASTException(
					"CommonOrdinaryLabelNode has one child, but saw index "
							+ index);
		return super.setChild(index, child);
	}
}
