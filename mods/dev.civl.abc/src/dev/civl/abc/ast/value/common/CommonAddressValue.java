package dev.civl.abc.ast.value.common;

import dev.civl.abc.ast.type.IF.PointerType;
import dev.civl.abc.ast.value.IF.AddressValue;
import dev.civl.abc.ast.value.IF.ValueFactory.Answer;

public abstract class CommonAddressValue extends CommonValue implements
		AddressValue {

	CommonAddressValue(PointerType type) {
		super(type);
	}

	@Override
	public PointerType getType() {
		return (PointerType) super.getType();
	}

	@Override
	public Answer isZero() {
		return Answer.NO;
	}

}
