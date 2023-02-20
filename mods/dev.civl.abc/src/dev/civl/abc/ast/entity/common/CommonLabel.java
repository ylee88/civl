package dev.civl.abc.ast.entity.common;

import dev.civl.abc.ast.entity.IF.CommonEntity;
import dev.civl.abc.ast.entity.IF.Label;
import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.node.IF.label.OrdinaryLabelNode;

public class CommonLabel extends CommonEntity implements Label {

	public CommonLabel(OrdinaryLabelNode declaration) {
		super(EntityKind.LABEL, declaration.getName(), ProgramEntity.LinkageKind.NONE);
		addDeclaration(declaration);
		setDefinition(declaration);
	}

	@Override
	public OrdinaryLabelNode getDefinition() {
		return (OrdinaryLabelNode) super.getDefinition();
	}

}
