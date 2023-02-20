package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.PointerBoolConversion;
import dev.civl.abc.ast.type.IF.PointerType;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType;

public class CommonPointerBoolConversion extends CommonConversion implements
		PointerBoolConversion {

	public CommonPointerBoolConversion(PointerType type1,
			StandardUnsignedIntegerType booleanType) {
		super(type1, booleanType);
	}

	@Override
	public PointerType getOldType() {
		return (PointerType) super.getOldType();
	}

	@Override
	public StandardUnsignedIntegerType getNewType() {
		return (StandardUnsignedIntegerType) super.getNewType();
	}

	@Override
	public String toString() {
		return "PointerBool" + super.toString();
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.POINTER_BOOL;
	}

}
