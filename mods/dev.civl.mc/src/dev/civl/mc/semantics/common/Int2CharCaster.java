package dev.civl.mc.semantics.common;

import java.util.Arrays;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.number.IntegerNumber;

//int to char
public class Int2CharCaster implements CIVLUnaryOperator<SymbolicExpression> {
	private SymbolicUniverse universe;
	private SymbolicConstant int2CharFunc;
	private SymbolicUtility symbolicUtil;

	public Int2CharCaster(SymbolicUniverse universe,
			SymbolicUtility symbolicUtil) {
		this.universe = universe;
		this.symbolicUtil = symbolicUtil;
		this.int2CharFunc = universe.symbolicConstant(
				universe.stringObject(CommonEvaluator.INT_TO_CHAR_FUNCTION),
				universe.functionType(
						Arrays.asList(this.universe.integerType()),
						universe.characterType()));
	}

	@Override
	public SymbolicExpression apply(BooleanExpression context,
			SymbolicExpression value, CIVLType type) {
		NumericExpression integerValue = (NumericExpression) value;
		Number concreteValue = null;

		// If integerValue is concrete and is inside the range [0, 255],
		// return corresponding character by using java casting.
		// Else if it's only outside of the range [0, 255], return abstract
		// function.
		if (integerValue.operator() == SymbolicOperator.CONCRETE) {
			int int_value;

			concreteValue = symbolicUtil.extractInt(null, integerValue);
			assert (concreteValue != null) : "NumericExpression with concrete operator cannot "
					+ "provide concrete numeric value";
			assert (!(concreteValue instanceof IntegerNumber)) : "A Number object which suppose "
					+ "has integer type cannot cast to IntegerNumber type";
			int_value = concreteValue.intValue();
			if (int_value < 0 || int_value > 255) {
				throw new CIVLUnimplementedFeatureException(
						"Converting integer whose value is larger than UCHAR_MAX or is less than UCHAR_MIN to char type\n");
			} else
				value = universe.character((char) int_value);
		} else {
			// If not concrete, return abstract function.
			value = universe.apply(this.int2CharFunc,
					Arrays.asList(integerValue));
		}
		return value;
	}

}
