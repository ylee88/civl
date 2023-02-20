package dev.civl.abc.analysis.entity;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.compound.LiteralObject;
import dev.civl.abc.ast.type.IF.Type;

public class CommonLiteralObject implements LiteralObject {

	private LiteralTypeNode typeNode;

	private ASTNode sourceNode;

	public CommonLiteralObject(LiteralTypeNode typeNode, ASTNode sourceNode) {
		this.typeNode = typeNode;
		this.sourceNode = sourceNode;
	}

	public LiteralTypeNode getTypeNode() {
		return typeNode;
	}

	public ASTNode getSourceNode() {
		return sourceNode;
	}

	@Override
	public Type getType() {
		return typeNode.getType();
	}

}
