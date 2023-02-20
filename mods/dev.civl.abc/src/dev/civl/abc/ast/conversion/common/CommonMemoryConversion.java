package dev.civl.abc.ast.conversion.common;

import dev.civl.abc.ast.conversion.IF.MemConversion;
import dev.civl.abc.ast.type.IF.MemType;
import dev.civl.abc.ast.type.IF.Type;

public class CommonMemoryConversion extends CommonConversion
		implements
			MemConversion {

	public CommonMemoryConversion(Type oldType, MemType newType) {
		super(oldType, newType);
	}

	@Override
	public MemType getNewType() {
		return (MemType) super.getNewType();
	}

	@Override
	public String toString() {
		return "Memory" + super.toString();
	}

	@Override
	public ConversionKind conversionKind() {
		return ConversionKind.MEM;
	}
}
