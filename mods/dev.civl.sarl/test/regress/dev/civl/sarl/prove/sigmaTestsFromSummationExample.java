package dev.civl.sarl.prove;

import java.util.Arrays;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicFunctionType;

public class sigmaTestsFromSummationExample {
	private static SymbolicUniverse universe = SARL.newStandardUniverse();

	/*
	 * context: 0 == sigma(0,Y0,lambda t : int . (X_a[t] + X_b[t])) - 1*Y1 && 0
	 * <= X_N - 1*Y0 - 1 && 0 <= X_N - 1 && 0 <= Y0
	 */
	/*
	 * predicate: 0 == sigma(0,Y0 + 1,lambda t : int . (X_a[t] + X_b[t])) -
	 * 1*X_a[Y0] - 1*X_b[Y0] - 1*Y1 && 0 <= X_N - 1*Y0 - 1 && 0 <= Y0 + 1
	 */
	@Test
	public void agt_0_assert_4() {
		// This juint test is automatically generated.
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.integerType());
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		NumericExpression var_X_N = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X_N"),
						universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.integerType(), var_X_N);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression var_X_b = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_b"), tmpType_2);
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.arrayRead(var_X_b, var_t);
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		SymbolicExpression tmpVar_3 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"), tmpType_0);
		tmpVar_3 = universe.lambda((SymbolicConstant) var_t, tmpVar_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.integerType());
		tmpVar_4 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_3);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.integerType());
		NumericExpression tmpVar_5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.integerType());
		tmpVar_5 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y1));
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.integerType());
		tmpVar_6 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_4, tmpVar_5));
		BooleanExpression tmpVar_7 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.booleanType());
		tmpVar_7 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_6);
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.integerType());
		tmpVar_8 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y0));
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.integerType());
		tmpVar_9 = (NumericExpression) universe.add(Arrays.asList(var_X_N,
				tmpVar_8,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_10 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						universe.booleanType());
		tmpVar_10 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_9);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.integerType());
		tmpVar_11 = (NumericExpression) universe.add(Arrays.asList(var_X_N,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_12 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.booleanType());
		tmpVar_12 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_11);
		BooleanExpression tmpVar_13 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.booleanType());
		tmpVar_13 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y0);
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_7, tmpVar_10, tmpVar_12, tmpVar_13));
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_16 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						tmpType_0);
		tmpVar_16 = universe.lambda((SymbolicConstant) var_t, tmpVar_2);
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.integerType());
		tmpVar_17 = universe.sigma(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_15, tmpVar_16);
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.integerType());
		tmpVar_18 = (NumericExpression) universe.arrayRead(var_X_a, var_Y0);
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				tmpVar_18));
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.arrayRead(var_X_b, var_Y0);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				tmpVar_20));
		NumericExpression tmpVar_22 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.integerType());
		tmpVar_22 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_17, tmpVar_19, tmpVar_21, tmpVar_5));
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_22);
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_15);
		BooleanExpression tmpVar_25 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.booleanType());
		tmpVar_25 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_23, tmpVar_10, tmpVar_24));
		universe.setShowProverQueries(true);
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_14).valid(tmpVar_25).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

} // Class End
