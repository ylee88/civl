package dev.civl.abc.ast.value.IF;

public interface ComplexFloatingValue extends FloatingValue {

	RealFloatingValue getRealPart();

	RealFloatingValue getImaginaryPart();

}
