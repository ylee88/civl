package dev.civl.mc.semantics.common;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.LogicFunction;
import dev.civl.mc.semantics.IF.ArrayToolBox;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.object.StringObject;
/**
 * This evaluator focuses on evaluating logic function calls.
 * 
 * @author ziqing
 *
 */
public class ReservedLogicFunctionCallEvaluator {

	/**
	 * TODO: get rid of this function when Z3 and CVC provers can handle
	 * reserved logic functions well.
	 * 
	 * @return true iff the given expression involves a symbolic constant which
	 *         is a reserved logic function:
	 */
	public static boolean hasReservedLogicFunctionCalls(SymbolicUniverse su,
			SymbolicExpression expression) {
		Set<SymbolicConstant> freeConstants = su
				.getFreeSymbolicConstants(expression);
		Set<StringObject> freeConstantNames = new HashSet<>();

		for (SymbolicConstant freeConstant : freeConstants)
			freeConstantNames.add(freeConstant.name());

		StringObject[] reservedFuncs = {
				su.stringObject(LogicFunction.RESERVED_PERMUT)};

		for (StringObject func : reservedFuncs)
			if (freeConstantNames.contains(func))
				return true;
		return false;
	}

	/**
	 * <p>
	 * <b>pre-condition:</b> {@link LogicFunction#isReservedFunction()} returns
	 * true.
	 * </p>
	 * 
	 * <p>
	 * Applying actual arguments to a reserved logic function.
	 * </p>
	 * 
	 * @param Evaluator
	 *            a reference to a {@link Evaluator}
	 * @param function
	 *            the {@link LogicFunction} that gets called
	 * @param arguments
	 *            an array of actual parameters
	 * @param source
	 *            the {@link CIVLSource} that is associated with this function
	 *            call
	 * @return the logic function call expression
	 */
	static SymbolicExpression applyReservedFunction(Evaluator evaluator,
			LogicFunction function, SymbolicExpression arguments[],
			CIVLSource source) {
		switch (function.name().name()) {
			case LogicFunction.RESERVED_PERMUT :
				return applyPermut(evaluator, arguments, source);
			default :
				throw new CIVLInternalException(
						"unknown reserved function " + function.name().name(),
						source);
		}
	}

	static private SymbolicExpression applyPermut(Evaluator evaluator,
			SymbolicExpression arguments[], CIVLSource source) {
		SymbolicUniverse su = evaluator.universe();
		ArrayToolBox arrayToolBox = evaluator.newArrayToolBox(su);
		NumericExpression sliceLength0 = su.subtract(su.length(arguments[0]),
				(NumericExpression) arguments[1]);
		SymbolicExpression slice0 = arrayToolBox.arraySliceRead(arguments[0],
				new NumericExpression[]{(NumericExpression) arguments[1]},
				sliceLength0);
		NumericExpression sliceLength1 = su.subtract(su.length(arguments[2]),
				(NumericExpression) arguments[1]);
		SymbolicExpression slice1 = arrayToolBox.arraySliceRead(arguments[2],
				new NumericExpression[]{(NumericExpression) arguments[3]},
				sliceLength1);
		BooleanExpression sliceLengthEq = su.equals(sliceLength0, sliceLength1);
		BooleanExpression permut = su.permut(slice0, slice1,
				(NumericExpression) arguments[4],
				(NumericExpression) arguments[5]);

		return su.and(sliceLengthEq, permut);
	}
}
