package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.Integer2PointerConversion;
import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.PointerType;

public class CommonInteger2PointerConversion extends CommonConversion implements
		Integer2PointerConversion {

	public CommonInteger2PointerConversion(IntegerType oldType,
			PointerType newType) {
		super(oldType, newType);
	}

	@Override
	public IntegerType getOldType() {
		return (IntegerType) super.getOldType();
	}

	@Override
	public PointerType getNewType() {
		return (PointerType) super.getNewType();
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.INTEGER_POINTER;
	}

}
