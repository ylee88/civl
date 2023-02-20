package dev.civl.abc.ast.node.common;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.IF.DifferenceObject.DiffKind;
import dev.civl.abc.ast.entity.IF.Entity;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.token.IF.Source;

public class CommonIdentifierNode extends CommonASTNode implements
		IdentifierNode {

	private String name;

	private Entity entity;

	public CommonIdentifierNode(Source source, String name) {
		super(source);
		this.name = name;
	}

	@Override
	public void printBody(PrintStream out) {
		out.print("Identifier[" + name + "]");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public IdentifierNode copy() {
		return new CommonIdentifierNode(getSource(), name);
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.IDENTIFIER;
	}

	@Override
	public DifferenceObject diffWork(ASTNode that) {
		if (that instanceof IdentifierNode)
			if (this.name.equals(((IdentifierNode) that).name()))
				return null;
			else
				return new DifferenceObject(this, that,
						DiffKind.IDENTIFIER_NAME);
		return new DifferenceObject(this, that);
	}

}
