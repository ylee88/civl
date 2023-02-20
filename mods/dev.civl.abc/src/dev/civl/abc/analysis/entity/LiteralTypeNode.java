package dev.civl.abc.analysis.entity;

import dev.civl.abc.ast.type.IF.ObjectType;

public abstract class LiteralTypeNode {

	private LiteralTypeNode parent;

	private ObjectType type;

	public LiteralTypeNode(ObjectType type) {
		this.type = type;
	}

	public abstract boolean hasFixedLength();

	public abstract int length();

	public abstract LiteralTypeNode getChild(int index);

	void setParent(LiteralTypeNode parent) {
		this.parent = parent;
	}

	public ObjectType getType() {
		return type;
	}

	public LiteralTypeNode parent() {
		return parent;
	}

}
