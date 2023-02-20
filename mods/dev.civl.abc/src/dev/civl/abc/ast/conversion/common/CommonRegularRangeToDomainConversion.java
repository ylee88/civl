package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.RegularRangeToDomainConversion;
import dev.civl.abc.ast.type.IF.DomainType;
import dev.civl.abc.ast.type.IF.ObjectType;

public class CommonRegularRangeToDomainConversion extends CommonConversion
		implements RegularRangeToDomainConversion {

	public CommonRegularRangeToDomainConversion(ObjectType oldType,
			DomainType newType) {
		super(oldType, newType);
	}

	@Override
	public ObjectType getOldType() {
		return (ObjectType) super.getOldType();
	}

	@Override
	public DomainType getNewType() {
		return (DomainType) super.getNewType();
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.REG_RANGE_DOMAIN;
	}
}
