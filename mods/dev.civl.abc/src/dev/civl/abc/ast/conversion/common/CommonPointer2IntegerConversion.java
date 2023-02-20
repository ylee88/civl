package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.Pointer2IntegerConversion;
import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.PointerType;

public class CommonPointer2IntegerConversion extends CommonConversion implements
		Pointer2IntegerConversion {

	public CommonPointer2IntegerConversion(PointerType oldType,
			IntegerType newType) {
		super(oldType, newType);
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.POINTER_INTEGER;
	}

	@Override
	public PointerType getOldType() {
		return (PointerType) super.getOldType();
	}

	@Override
	public IntegerType getNewType() {
		return (IntegerType) super.getNewType();
	}
}
