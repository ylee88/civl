package dev.civl.abc.ast.value.IF;

import java.math.BigInteger;

public interface RealFloatingValue extends FloatingValue {

	int getRadix();

	double getDoubleValue();

	BigInteger getWholePartValue();

	BigInteger getFractionPartValue();

	int getFractionLength();

	BigInteger getExponentValue();

}
