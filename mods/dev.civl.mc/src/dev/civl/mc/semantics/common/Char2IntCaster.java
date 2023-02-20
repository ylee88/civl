package dev.civl.mc.semantics.common;

import java.util.Arrays;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.object.CharObject;

//char to int
public class Char2IntCaster implements CIVLUnaryOperator<SymbolicExpression> {
	private SymbolicUniverse universe;
	private SymbolicConstant char2IntFunc;
	private SymbolicUtility symbolicUtil;

	public Char2IntCaster(SymbolicUniverse universe,
			SymbolicUtility symbolicUtil) {
		this.universe = universe;
		this.symbolicUtil = symbolicUtil;
		this.char2IntFunc = universe.symbolicConstant(
				universe.stringObject(CommonEvaluator.CHAR_TO_INT_FUNCTION),
				universe.functionType(
						Arrays.asList(this.universe.characterType()),
						universe.integerType()));
	}

	@Override
	public SymbolicExpression apply(BooleanExpression context,
			SymbolicExpression value, CIVLType type) {
		if (value.operator() == SymbolicOperator.CONCRETE) {
			CharObject charObj = (CharObject) value.argument(0);

			return this.universe.integer((int) charObj.getChar());
		} else {
			SymbolicExpression castedValue = this.symbolicUtil
					.applyReverseFunction(CommonEvaluator.INT_TO_CHAR_FUNCTION,
							value);

			if (castedValue != null)
				value = castedValue;
			else
				value = universe.apply(this.char2IntFunc, Arrays.asList(value));
		}
		return value;
	}
}
