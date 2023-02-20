package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.FloatingType;

public interface ComplexValue extends Value {

	@Override
	FloatingType getType();

	RealFloatingValue getRealPart();

	RealFloatingValue getImaginaryPart();

}
