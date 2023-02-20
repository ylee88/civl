package dev.civl.abc.ast.value.common;

import dev.civl.abc.ast.type.IF.Type;
import dev.civl.abc.ast.value.IF.Value;

public abstract class CommonValue implements Value {

	private Type type;

	public CommonValue(Type type) {
		assert type != null;
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean isScalar() {
		return type.isScalar();
	}

}
