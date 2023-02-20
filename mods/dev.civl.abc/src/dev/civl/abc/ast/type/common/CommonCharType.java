package dev.civl.abc.ast.type.common;

import dev.civl.abc.ast.type.IF.IntegerType;

public class CommonCharType extends CommonBasicType implements IntegerType {

	public CommonCharType() {
		super(BasicTypeKind.CHAR);
	}

	@Override
	public String toString() {
		return "char";
	}

}
