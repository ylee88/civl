package dev.civl.abc.ast.value.IF;

import java.math.BigInteger;

import dev.civl.abc.ast.type.IF.IntegerType;

public interface IntegerValue extends Value {

	@Override
	IntegerType getType();

	BigInteger getIntegerValue();

}
