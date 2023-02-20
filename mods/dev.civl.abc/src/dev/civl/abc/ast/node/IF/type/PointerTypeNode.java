package dev.civl.abc.ast.node.IF.type;

public interface PointerTypeNode extends TypeNode {

	TypeNode referencedType();

	@Override
	PointerTypeNode copy();
}
