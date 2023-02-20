package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.ArrayType;

public interface ArrayValue extends CompoundValue {

	Value getElement(int index);

	void setElement(int index, Value elementValue);

	@Override
	ArrayType getType();

}
