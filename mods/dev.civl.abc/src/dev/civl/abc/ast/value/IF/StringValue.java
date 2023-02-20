package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.ArrayType;
import dev.civl.abc.token.IF.StringLiteral;

public interface StringValue extends Value {

	StringLiteral getLiteral();

	@Override
	ArrayType getType();

}
