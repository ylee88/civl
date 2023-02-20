package dev.civl.abc.ast.entity.common;

import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.entity.IF.Typedef;
import dev.civl.abc.ast.node.IF.declaration.TypedefDeclarationNode;
import dev.civl.abc.ast.type.IF.Type;

public class CommonTypedef extends CommonOrdinaryEntity implements Typedef {

	public CommonTypedef(String name, Type type) {
		super(EntityKind.TYPEDEF, name, ProgramEntity.LinkageKind.NONE, type);
	}

	@Override
	public TypedefDeclarationNode getDefinition() {
		return (TypedefDeclarationNode) super.getDefinition();
	}

}
