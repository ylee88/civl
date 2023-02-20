package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.ArrayConversion;
import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.ast.type.IF.PointerType;
import dev.civl.abc.ast.type.IF.Type;

public class CommonArrayConversion extends CommonConversion
		implements
			ArrayConversion {

	public CommonArrayConversion(Type oldType, PointerType newType) {
		super(oldType, newType);
	}

	@Override
	public ObjectType getOldType() {
		return (ObjectType) super.getOldType();
	}

	@Override
	public PointerType getNewType() {
		return (PointerType) super.getNewType();
	}

	@Override
	public String toString() {
		return "Array" + super.toString();
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.ARRAY;
	}
}
