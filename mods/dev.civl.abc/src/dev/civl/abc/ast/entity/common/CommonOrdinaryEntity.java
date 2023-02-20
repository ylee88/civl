package dev.civl.abc.ast.entity.common;

import dev.civl.abc.ast.entity.IF.CommonEntity;
import dev.civl.abc.ast.entity.IF.OrdinaryEntity;
import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.type.IF.Type;

public class CommonOrdinaryEntity extends CommonEntity implements
		OrdinaryEntity {

	public CommonOrdinaryEntity(EntityKind kind, String name,
			ProgramEntity.LinkageKind linkage, Type type) {
		super(kind, name, linkage);
		setType(type);
	}

}
