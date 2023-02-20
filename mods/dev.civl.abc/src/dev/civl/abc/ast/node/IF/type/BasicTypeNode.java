package dev.civl.abc.ast.node.IF.type;

import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;

public interface BasicTypeNode extends TypeNode {

	BasicTypeKind getBasicTypeKind();

	@Override
	BasicTypeNode copy();

}
