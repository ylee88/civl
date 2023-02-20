package dev.civl.mc.semantics.common;

import java.util.Arrays;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.model.IF.type.CIVLPointerType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;

/***
 * pointer2IntCaster = new UFExtender(this.universe, POINTER_TO_INT_FUNCTION,
 * this.pointerType, universe.integerType(), new Pointer2IntCaster(universe,
 * symbolicUtil, this.pointerType)); int2PointerCaster = new
 * UFExtender(this.universe, INT_TO_POINTER_FUNCTION, universe.integerType(),
 * this.pointerType, new Int2PointerCaster(universe, symbolicUtil,
 * this.pointerType));
 * 
 * @author zmanchun
 *
 */
// int to pointer
public class Int2PointerCaster
		implements
			CIVLUnaryOperator<SymbolicExpression> {
	private SymbolicUniverse universe;
	private SymbolicConstant int2PointerFunc;
	private SymbolicUtility symbolicUtil;

	public Int2PointerCaster(SymbolicUniverse universe,
			SymbolicUtility symbolicUtil, SymbolicType pointerType) {
		this.universe = universe;
		this.symbolicUtil = symbolicUtil;
		this.int2PointerFunc = universe.symbolicConstant(
				universe.stringObject(CommonEvaluator.INT_TO_POINTER_FUNCTION),
				universe.functionType(Arrays.asList(universe.integerType()),
						pointerType));
	}

	@Override
	public SymbolicExpression apply(BooleanExpression context,
			SymbolicExpression value, CIVLType castType) {
		// only good cast for:
		// 1. from 0 to null pointer
		// 2. pointer2Int(x) back to x;
		BooleanExpression claim = universe.equals(universe.zeroInt(), value);
		ResultType resultType = universe.reasoner(context).valid(claim)
				.getResultType();

		if (resultType != ResultType.YES) {
			SymbolicExpression castedValue = this.symbolicUtil
					.applyReverseFunction(
							CommonEvaluator.POINTER_TO_INT_FUNCTION, value);

			if (castedValue != null)
				value = castedValue;
			else {// if (!((CIVLPointerType) castType).baseType().isVoidType())
					// {
				value = universe.apply(this.int2PointerFunc,
						Arrays.asList(value));
				// state = errorLogger.logError(arg.getSource(), state,
				// process,
				// this.symbolicAnalyzer.stateInformation(state),
				// claim, resultType, ErrorKind.INVALID_CAST,
				// "Cast from non-zero integer to pointer");
				// eval.state = state;
			}
		} else {
			if (((CIVLPointerType) castType).baseType().isFunction())
				value = this.symbolicUtil.nullFunctionPointer();
			else
				value = this.symbolicUtil.nullPointer();
		}
		return value;
	}

	SymbolicExpression forceCast(SymbolicExpression intValue) {
		return universe.apply(this.int2PointerFunc, Arrays.asList(intValue));
	}
}
