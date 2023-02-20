package dev.civl.abc.analysis.entity;

import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.err.IF.ABCRuntimeException;

public class LiteralScalarTypeNode extends LiteralTypeNode {

	public LiteralScalarTypeNode(ObjectType type) {
		super(type);
	}

	@Override
	public boolean hasFixedLength() {
		return true;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public String toString() {
		return getType().toString();
	}

	@Override
	public LiteralTypeNode getChild(int index) {
		throw new ABCRuntimeException("should never be called");
	}

}
