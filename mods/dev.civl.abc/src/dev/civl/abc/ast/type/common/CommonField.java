package dev.civl.abc.ast.type.common;

import dev.civl.abc.ast.entity.IF.CommonEntity;
import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.node.IF.declaration.FieldDeclarationNode;
import dev.civl.abc.ast.type.IF.Field;
import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.ast.value.IF.Value;

public class CommonField extends CommonEntity implements Field {

	private Value bitWidth;

	private int memberIndex;

	public CommonField(FieldDeclarationNode declaration, ObjectType type,
			Value bitWidth) {
		super(EntityKind.FIELD, declaration.getName(),
				ProgramEntity.LinkageKind.NONE);
		addDeclaration(declaration);
		setDefinition(declaration);
		this.bitWidth = bitWidth;
		setType(type);
		this.memberIndex = declaration.childIndex();
	}

	@Override
	public FieldDeclarationNode getDefinition() {
		return (FieldDeclarationNode) super.getDefinition();
	}

	@Override
	public ObjectType getType() {
		return (ObjectType) super.getType();
	}

	@Override
	public Value getBitWidth() {
		return bitWidth;
	}

	@Override
	public int getMemberIndex() {
		return memberIndex;
	}

	@Override
	public boolean isAnonymous() {
		return getDefinition().getIdentifier() == null;
	}
}
