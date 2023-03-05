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
import dev.civl.sarl.IF.type.SymbolicTupleType;

public class SigmaFromAdderCompareExampleTest {

	private static SymbolicUniverse universe = SARL.newStandardUniverse();

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(0,Y0,lambda t : int . X_a[t])
	 * - 1*Y1 && 0 == sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t :
	 * dynamicType . (0 <= CIVL_SIZEOF(_t) - 1) && 0 <= (2*Y0) div 3 - 1*Y3 - 1
	 * && 0 <= SIZEOF_REAL - 1 && 0 <= X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0
	 * <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y3 + 1,lambda t : int . X_a[t]) - 1*X_a[Y3] && 0
	 * <= (2*Y0) div 3 - 1*Y3 - 1
	 */
	@Test
	public void agt_0_assert_17() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_5);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_10);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.integerType());
		tmpVar_16 = (NumericExpression) universe.add(Arrays.asList(tmpVar_15,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_17 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.booleanType());
		tmpVar_17 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_16);
		BooleanExpression tmpVar_18 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.booleanType());
		tmpVar_18 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_17);
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.divide(tmpVar_19,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_23 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.integerType());
		tmpVar_23 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_23);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25);
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_28 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.booleanType());
		tmpVar_28 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_27);
		BooleanExpression tmpVar_29 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.booleanType());
		tmpVar_29 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_3, tmpVar_9, tmpVar_14, tmpVar_18,
						tmpVar_22, tmpVar_24, tmpVar_26, tmpVar_28, tmpVar_29));
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_32 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						tmpType_0);
		tmpVar_32 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.realType());
		tmpVar_33 = universe.sigma(var_Y3, tmpVar_31, tmpVar_32);
		NumericExpression tmpVar_34 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.realType());
		tmpVar_34 = (NumericExpression) universe.arrayRead(var_X_a, var_Y3);
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.realType());
		tmpVar_35 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_34));
		NumericExpression tmpVar_36 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.realType());
		tmpVar_36 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_33, tmpVar_35));
		BooleanExpression tmpVar_37 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.booleanType());
		tmpVar_37 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_36);
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_37, tmpVar_22));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_30).valid(tmpVar_38).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y4 && 0 == (2*Y0) div 3 - 1*Y5 && 0 ==
	 * sigma(Y4,Y5,lambda t : int . X_a[t]) - 1*Y2 && 0 == sigma(0,Y0,lambda t :
	 * int . X_a[t]) - 1*Y1 && 0 == sigma(0,Y4,lambda t : int . X_a[t]) - 1*Y3
	 * && forall _t : dynamicType . (0 <= CIVL_SIZEOF(_t) - 1) && Y4 - 1*Y5 + 1
	 * <= 0 && 0 <= SIZEOF_REAL - 1 && 0 <= X__mpi_nprocs_hi - 3 && 0 <= Y0 -
	 * 1*Y5 - 1 && 0 <= Y0 - 3 && 0 <= Y4
	 */
	/*
	 * predicate: 0 == sigma(Y5,Y5 + 1,lambda t : int . X_a[t]) - 1*X_a[Y5] && 0
	 * <= Y0 - 1*Y5 - 1
	 */
	@Test
	public void agt_1_assert_27() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.integerType());
		tmpVar_4 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.integerType());
		tmpVar_5 = (NumericExpression) universe.divide(tmpVar_4,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.integerType());
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.integerType());
		tmpVar_6 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y5));
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.integerType());
		tmpVar_7 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_5, tmpVar_6));
		BooleanExpression tmpVar_8 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.booleanType());
		tmpVar_8 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_7);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.realType());
		tmpVar_9 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(var_Y4, var_Y5, tmpVar_10);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_15);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicExpression tmpVar_20 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						tmpType_0);
		tmpVar_20 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.realType());
		tmpVar_21 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y4,
				tmpVar_20);
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.realType());
		NumericExpression tmpVar_22 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.realType());
		tmpVar_22 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y3));
		NumericExpression tmpVar_23 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.realType());
		tmpVar_23 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_21, tmpVar_22));
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_23);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_26 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.integerType());
		tmpVar_26 = (NumericExpression) universe.add(Arrays.asList(tmpVar_25,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_27 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.booleanType());
		tmpVar_27 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_26);
		BooleanExpression tmpVar_28 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.booleanType());
		tmpVar_28 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_27);
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.integerType());
		tmpVar_29 = (NumericExpression) universe
				.add(Arrays.asList(var_Y4, tmpVar_6,
						universe.number(universe.numberFactory().number("1"))));
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe.lessThanEquals(tmpVar_29,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.integerType());
		tmpVar_33 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_34 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.booleanType());
		tmpVar_34 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_33);
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.integerType());
		tmpVar_35 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				tmpVar_6,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_36 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.booleanType());
		tmpVar_36 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_35);
		NumericExpression tmpVar_37 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.integerType());
		tmpVar_37 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_37);
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y4);
		BooleanExpression tmpVar_40 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.booleanType());
		tmpVar_40 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_8, tmpVar_14, tmpVar_19, tmpVar_24, tmpVar_28, tmpVar_30,
				tmpVar_32, tmpVar_34, tmpVar_36, tmpVar_38, tmpVar_39));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y5,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_42 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						tmpType_0);
		tmpVar_42 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.realType());
		tmpVar_43 = universe.sigma(var_Y5, tmpVar_41, tmpVar_42);
		NumericExpression tmpVar_44 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.realType());
		tmpVar_44 = (NumericExpression) universe.arrayRead(var_X_a, var_Y5);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.realType());
		tmpVar_45 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_44));
		NumericExpression tmpVar_46 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.realType());
		tmpVar_46 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_43, tmpVar_45));
		BooleanExpression tmpVar_47 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.booleanType());
		tmpVar_47 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_46);
		BooleanExpression tmpVar_48 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.booleanType());
		tmpVar_48 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_47, tmpVar_36));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_40).valid(tmpVar_48).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y4 && 0 == (2*Y0) div 3 - 1*Y7 && 0 ==
	 * sigma(Y4,Y7,lambda t : int . X_a[t]) - 1*Y2 && 0 == sigma(Y7,Y5,lambda t
	 * : int . X_a[t]) - 1*Y6 && 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1
	 * && 0 == sigma(0,Y4,lambda t : int . X_a[t]) - 1*Y3 && forall _t :
	 * dynamicType . (0 <= CIVL_SIZEOF(_t) - 1) && Y4 - 1*Y7 + 1 <= 0 && 0 <=
	 * SIZEOF_REAL - 1 && 0 <= X__mpi_nprocs_hi - 3 && 0 <= Y0 - 1*Y5 - 1 && 0
	 * <= Y0 - 1*Y7 - 1 && 0 <= Y0 - 3 && 0 <= Y5 - 1*Y7 && 0 <= Y4
	 */
	/*
	 * predicate: 0 == sigma(Y7,Y5 + 1,lambda t : int . X_a[t]) - 1*X_a[Y5] -
	 * 1*Y6 && 0 <= Y0 - 1*Y5 - 1 && 0 <= Y5 - 1*Y7 + 1
	 */
	@Test
	public void agt_2_assert_36() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.integerType());
		tmpVar_4 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.integerType());
		tmpVar_5 = (NumericExpression) universe.divide(tmpVar_4,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y7"),
						universe.integerType());
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.integerType());
		tmpVar_6 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y7));
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.integerType());
		tmpVar_7 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_5, tmpVar_6));
		BooleanExpression tmpVar_8 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.booleanType());
		tmpVar_8 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_7);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.realType());
		tmpVar_9 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(var_Y4, var_Y7, tmpVar_10);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.integerType());
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(var_Y7, var_Y5, tmpVar_15);
		NumericExpression var_Y6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y6"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y6));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicExpression tmpVar_20 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						tmpType_0);
		tmpVar_20 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.realType());
		tmpVar_21 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_20);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_22 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.realType());
		tmpVar_22 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_23 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.realType());
		tmpVar_23 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_21, tmpVar_22));
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_23);
		SymbolicExpression tmpVar_25 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						tmpType_0);
		tmpVar_25 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_26 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.realType());
		tmpVar_26 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y4,
				tmpVar_25);
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.realType());
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.realType());
		tmpVar_27 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y3));
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.realType());
		tmpVar_28 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_26, tmpVar_27));
		BooleanExpression tmpVar_29 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.booleanType());
		tmpVar_29 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_28);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_30 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.integerType());
		tmpVar_30 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(tmpVar_30,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		BooleanExpression tmpVar_33 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.booleanType());
		tmpVar_33 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_32);
		NumericExpression tmpVar_34 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.integerType());
		tmpVar_34 = (NumericExpression) universe
				.add(Arrays.asList(var_Y4, tmpVar_6,
						universe.number(universe.numberFactory().number("1"))));
		BooleanExpression tmpVar_35 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.booleanType());
		tmpVar_35 = (BooleanExpression) universe.lessThanEquals(tmpVar_34,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_36 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.integerType());
		tmpVar_36 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_37 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.booleanType());
		tmpVar_37 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_36);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_38 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.integerType());
		tmpVar_38 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_38);
		NumericExpression tmpVar_40 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.integerType());
		tmpVar_40 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y5));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				tmpVar_40,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_42 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						universe.booleanType());
		tmpVar_42 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_41);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.integerType());
		tmpVar_43 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				tmpVar_6,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_44 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.booleanType());
		tmpVar_44 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_43);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.integerType());
		tmpVar_45 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_46 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.booleanType());
		tmpVar_46 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_45);
		NumericExpression tmpVar_47 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.integerType());
		tmpVar_47 = (NumericExpression) universe
				.add(Arrays.asList(var_Y5, tmpVar_6));
		BooleanExpression tmpVar_48 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.booleanType());
		tmpVar_48 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_47);
		BooleanExpression tmpVar_49 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_49"),
						universe.booleanType());
		tmpVar_49 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y4);
		BooleanExpression tmpVar_50 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_50"),
						universe.booleanType());
		tmpVar_50 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_8, tmpVar_14, tmpVar_19, tmpVar_24, tmpVar_29, tmpVar_33,
				tmpVar_35, tmpVar_37, tmpVar_39, tmpVar_42, tmpVar_44,
				tmpVar_46, tmpVar_48, tmpVar_49));
		NumericExpression tmpVar_51 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_51"),
						universe.integerType());
		tmpVar_51 = (NumericExpression) universe.add(Arrays.asList(var_Y5,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_52 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_52"),
						tmpType_0);
		tmpVar_52 = universe.lambda((SymbolicConstant) var_t, tmpVar_9);
		NumericExpression tmpVar_53 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_53"),
						universe.realType());
		tmpVar_53 = universe.sigma(var_Y7, tmpVar_51, tmpVar_52);
		NumericExpression tmpVar_54 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_54"),
						universe.realType());
		tmpVar_54 = (NumericExpression) universe.arrayRead(var_X_a, var_Y5);
		NumericExpression tmpVar_55 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_55"),
						universe.realType());
		tmpVar_55 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_54));
		NumericExpression tmpVar_56 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_56"),
						universe.realType());
		tmpVar_56 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_53, tmpVar_55, tmpVar_17));
		BooleanExpression tmpVar_57 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_57"),
						universe.booleanType());
		tmpVar_57 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_56);
		NumericExpression tmpVar_58 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_58"),
						universe.integerType());
		tmpVar_58 = (NumericExpression) universe
				.add(Arrays.asList(var_Y5, tmpVar_6,
						universe.number(universe.numberFactory().number("1"))));
		BooleanExpression tmpVar_59 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_59"),
						universe.booleanType());
		tmpVar_59 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_58);
		BooleanExpression tmpVar_60 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_60"),
						universe.booleanType());
		tmpVar_60 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_57, tmpVar_42, tmpVar_59));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_50).valid(tmpVar_60).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(Y3,Y4,lambda t : int .
	 * X_a[t]) - 1*Y5 && 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && Y3 - 1*Y4 <= 0 && 0 <= (2*Y0) div 3 - 1*Y3
	 * - 1 && 0 <= (2*Y0) div 3 - 1*Y4 - 1 && 0 <= SIZEOF_REAL - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y4 + 1,lambda t : int . X_a[t]) - 1*X_a[Y4] -
	 * 1*Y5 && Y3 - 1*Y4 - 1 <= 0 && 0 <= (2*Y0) div 3 - 1*Y4 - 1
	 */
	@Test
	public void agt_3_assert_38() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(var_Y3, var_Y4, tmpVar_5);
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y5));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_10);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_15);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_22);
		NumericExpression tmpVar_24 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.integerType());
		tmpVar_24 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe
				.add(Arrays.asList(var_Y3, tmpVar_24));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(tmpVar_25,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.integerType());
		tmpVar_28 = (NumericExpression) universe.divide(tmpVar_27,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.integerType());
		tmpVar_29 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_29);
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.integerType());
		tmpVar_33 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_34 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.booleanType());
		tmpVar_34 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_33);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.integerType());
		tmpVar_35 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_36 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.booleanType());
		tmpVar_36 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_35);
		NumericExpression tmpVar_37 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.integerType());
		tmpVar_37 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_37);
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_40 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.booleanType());
		tmpVar_40 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_9, tmpVar_14, tmpVar_19, tmpVar_23, tmpVar_26, tmpVar_30,
				tmpVar_32, tmpVar_34, tmpVar_36, tmpVar_38, tmpVar_39));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y4,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_42 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						tmpType_0);
		tmpVar_42 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.realType());
		tmpVar_43 = universe.sigma(var_Y3, tmpVar_41, tmpVar_42);
		NumericExpression tmpVar_44 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.realType());
		tmpVar_44 = (NumericExpression) universe.arrayRead(var_X_a, var_Y4);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.realType());
		tmpVar_45 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_44));
		NumericExpression tmpVar_46 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.realType());
		tmpVar_46 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_43, tmpVar_45, tmpVar_7));
		BooleanExpression tmpVar_47 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.booleanType());
		tmpVar_47 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_46);
		NumericExpression tmpVar_48 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.integerType());
		tmpVar_48 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_49 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_49"),
						universe.booleanType());
		tmpVar_49 = (BooleanExpression) universe.lessThanEquals(tmpVar_48,
				universe.number(universe.numberFactory().number("0")));
		BooleanExpression tmpVar_50 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_50"),
						universe.booleanType());
		tmpVar_50 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_47, tmpVar_49, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_40).valid(tmpVar_50).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(Y3,Y4,lambda t : int .
	 * X_a[t]) - 1*Y5 && 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && Y3 - 1*Y4 <= 0 && 0 <= (2*Y0) div 3 - 1*Y3
	 * - 1 && 0 <= (2*Y0) div 3 - 1*Y4 - 1 && 0 <= SIZEOF_REAL - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y4 + 1,lambda t : int . X_a[t]) - 1*X_a[Y4] -
	 * 1*Y5 && Y3 - 1*Y4 - 1 <= 0 && 0 <= (2*Y0) div 3 - 1*Y4 - 1
	 */
	@Test
	public void agt_4_assert_38() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(var_Y3, var_Y4, tmpVar_5);
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y5));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_10);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_15);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_22);
		NumericExpression tmpVar_24 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.integerType());
		tmpVar_24 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe
				.add(Arrays.asList(var_Y3, tmpVar_24));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(tmpVar_25,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.integerType());
		tmpVar_28 = (NumericExpression) universe.divide(tmpVar_27,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.integerType());
		tmpVar_29 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_29);
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.integerType());
		tmpVar_33 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_34 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.booleanType());
		tmpVar_34 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_33);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.integerType());
		tmpVar_35 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_36 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.booleanType());
		tmpVar_36 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_35);
		NumericExpression tmpVar_37 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.integerType());
		tmpVar_37 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_37);
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_40 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.booleanType());
		tmpVar_40 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_9, tmpVar_14, tmpVar_19, tmpVar_23, tmpVar_26, tmpVar_30,
				tmpVar_32, tmpVar_34, tmpVar_36, tmpVar_38, tmpVar_39));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y4,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_42 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						tmpType_0);
		tmpVar_42 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.realType());
		tmpVar_43 = universe.sigma(var_Y3, tmpVar_41, tmpVar_42);
		NumericExpression tmpVar_44 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.realType());
		tmpVar_44 = (NumericExpression) universe.arrayRead(var_X_a, var_Y4);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.realType());
		tmpVar_45 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_44));
		NumericExpression tmpVar_46 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.realType());
		tmpVar_46 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_43, tmpVar_45, tmpVar_7));
		BooleanExpression tmpVar_47 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.booleanType());
		tmpVar_47 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_46);
		NumericExpression tmpVar_48 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.integerType());
		tmpVar_48 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_49 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_49"),
						universe.booleanType());
		tmpVar_49 = (BooleanExpression) universe.lessThanEquals(tmpVar_48,
				universe.number(universe.numberFactory().number("0")));
		BooleanExpression tmpVar_50 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_50"),
						universe.booleanType());
		tmpVar_50 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_47, tmpVar_49, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_40).valid(tmpVar_50).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y2,lambda t : int . X_a[t]) - 1*Y3 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y2
	 */
	/*
	 * predicate: 0 == sigma(0,Y2 + 1,lambda t : int . X_a[t]) - 1*X_a[Y2] -
	 * 1*Y3 && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <= Y2 + 1
	 */
	@Test
	public void agt_5_assert_40() {
		// This juint test is automatically generated.
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.realType());
		tmpVar_0 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_1 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"), tmpType_0);
		tmpVar_1 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.realType());
		tmpVar_2 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_1);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.realType());
		tmpVar_3 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_2, tmpVar_3));
		BooleanExpression tmpVar_5 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.booleanType());
		tmpVar_5 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_4);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.integerType());
		SymbolicExpression tmpVar_6 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"), tmpType_0);
		tmpVar_6 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y2,
				tmpVar_6);
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.realType());
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y3));
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.realType());
		tmpVar_9 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_7, tmpVar_8));
		BooleanExpression tmpVar_10 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						universe.booleanType());
		tmpVar_10 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_9);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.integerType());
		tmpVar_11 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.integerType());
		tmpVar_12 = (NumericExpression) universe.add(Arrays.asList(tmpVar_11,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_13 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.booleanType());
		tmpVar_13 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_12);
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_13);
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.integerType());
		tmpVar_16 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y2));
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.integerType());
		tmpVar_17 = (NumericExpression) universe.add(Arrays.asList(tmpVar_15,
				tmpVar_16,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_18 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.booleanType());
		tmpVar_18 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_17);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_20 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.booleanType());
		tmpVar_20 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_19);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y2);
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_5, tmpVar_10, tmpVar_14, tmpVar_18,
						tmpVar_20, tmpVar_22, tmpVar_23));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.add(Arrays.asList(var_Y2,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_26 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						tmpType_0);
		tmpVar_26 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.realType());
		tmpVar_27 = universe.sigma(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25, tmpVar_26);
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.realType());
		tmpVar_28 = (NumericExpression) universe.arrayRead(var_X_a, var_Y2);
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.realType());
		tmpVar_29 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_28));
		NumericExpression tmpVar_30 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.realType());
		tmpVar_30 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_27, tmpVar_29, tmpVar_8));
		BooleanExpression tmpVar_31 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.booleanType());
		tmpVar_31 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_30);
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25);
		BooleanExpression tmpVar_33 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.booleanType());
		tmpVar_33 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_31, tmpVar_18, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_24).valid(tmpVar_33).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y2,lambda t : int . X_a[t]) - 1*Y3 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y2
	 */
	/*
	 * predicate: 0 == sigma(0,Y2 + 1,lambda t : int . X_a[t]) - 1*X_a[Y2] -
	 * 1*Y3 && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <= Y2 + 1
	 */
	@Test
	public void agt_6_assert_40() {
		// This juint test is automatically generated.
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.realType());
		tmpVar_0 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_1 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"), tmpType_0);
		tmpVar_1 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.realType());
		tmpVar_2 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_1);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.realType());
		tmpVar_3 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_2, tmpVar_3));
		BooleanExpression tmpVar_5 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.booleanType());
		tmpVar_5 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_4);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.integerType());
		SymbolicExpression tmpVar_6 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"), tmpType_0);
		tmpVar_6 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y2,
				tmpVar_6);
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.realType());
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y3));
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.realType());
		tmpVar_9 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_7, tmpVar_8));
		BooleanExpression tmpVar_10 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						universe.booleanType());
		tmpVar_10 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_9);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.integerType());
		tmpVar_11 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.integerType());
		tmpVar_12 = (NumericExpression) universe.add(Arrays.asList(tmpVar_11,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_13 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.booleanType());
		tmpVar_13 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_12);
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_13);
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.integerType());
		tmpVar_16 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y2));
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.integerType());
		tmpVar_17 = (NumericExpression) universe.add(Arrays.asList(tmpVar_15,
				tmpVar_16,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_18 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.booleanType());
		tmpVar_18 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_17);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_20 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.booleanType());
		tmpVar_20 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_19);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y2);
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_5, tmpVar_10, tmpVar_14, tmpVar_18,
						tmpVar_20, tmpVar_22, tmpVar_23));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.add(Arrays.asList(var_Y2,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_26 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						tmpType_0);
		tmpVar_26 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.realType());
		tmpVar_27 = universe.sigma(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25, tmpVar_26);
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.realType());
		tmpVar_28 = (NumericExpression) universe.arrayRead(var_X_a, var_Y2);
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.realType());
		tmpVar_29 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_28));
		NumericExpression tmpVar_30 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.realType());
		tmpVar_30 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_27, tmpVar_29, tmpVar_8));
		BooleanExpression tmpVar_31 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.booleanType());
		tmpVar_31 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_30);
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25);
		BooleanExpression tmpVar_33 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.booleanType());
		tmpVar_33 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_31, tmpVar_18, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_24).valid(tmpVar_33).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y2,lambda t : int . X_a[t]) - 1*Y3 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y2
	 */
	/*
	 * predicate: 0 == sigma(0,Y2 + 1,lambda t : int . X_a[t]) - 1*X_a[Y2] -
	 * 1*Y3 && 0 <= Y0 div 3 - 1*Y2 - 1 && 0 <= Y2 + 1
	 */
	@Test
	public void agt_7_assert_40() {
		// This juint test is automatically generated.
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.realType());
		tmpVar_0 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_1 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"), tmpType_0);
		tmpVar_1 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.realType());
		tmpVar_2 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_1);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.realType());
		tmpVar_3 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_2, tmpVar_3));
		BooleanExpression tmpVar_5 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.booleanType());
		tmpVar_5 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_4);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.integerType());
		SymbolicExpression tmpVar_6 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"), tmpType_0);
		tmpVar_6 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y2,
				tmpVar_6);
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.realType());
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y3));
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.realType());
		tmpVar_9 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_7, tmpVar_8));
		BooleanExpression tmpVar_10 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						universe.booleanType());
		tmpVar_10 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_9);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.integerType());
		tmpVar_11 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.integerType());
		tmpVar_12 = (NumericExpression) universe.add(Arrays.asList(tmpVar_11,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_13 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.booleanType());
		tmpVar_13 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_12);
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_13);
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.integerType());
		tmpVar_16 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y2));
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.integerType());
		tmpVar_17 = (NumericExpression) universe.add(Arrays.asList(tmpVar_15,
				tmpVar_16,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_18 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.booleanType());
		tmpVar_18 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_17);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_20 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.booleanType());
		tmpVar_20 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_19);
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y2);
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_5, tmpVar_10, tmpVar_14, tmpVar_18,
						tmpVar_20, tmpVar_22, tmpVar_23));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.add(Arrays.asList(var_Y2,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_26 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						tmpType_0);
		tmpVar_26 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.realType());
		tmpVar_27 = universe.sigma(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25, tmpVar_26);
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.realType());
		tmpVar_28 = (NumericExpression) universe.arrayRead(var_X_a, var_Y2);
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.realType());
		tmpVar_29 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_28));
		NumericExpression tmpVar_30 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.realType());
		tmpVar_30 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_27, tmpVar_29, tmpVar_8));
		BooleanExpression tmpVar_31 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.booleanType());
		tmpVar_31 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_30);
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25);
		BooleanExpression tmpVar_33 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.booleanType());
		tmpVar_33 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_31, tmpVar_18, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_24).valid(tmpVar_33).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(0,Y0,lambda t : int . X_a[t])
	 * - 1*Y1 && 0 == sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t :
	 * dynamicType . (0 <= CIVL_SIZEOF(_t) - 1) && 0 <= (2*Y0) div 3 - 1*Y3 - 1
	 * && 0 <= SIZEOF_REAL - 1 && 0 <= X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0
	 * <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y3 + 1,lambda t : int . X_a[t]) - 1*X_a[Y3] && 0
	 * <= (2*Y0) div 3 - 1*Y3 - 1
	 */
	@Test
	public void agt_8_assert_40() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_5);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_10);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.integerType());
		tmpVar_16 = (NumericExpression) universe.add(Arrays.asList(tmpVar_15,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_17 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.booleanType());
		tmpVar_17 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_16);
		BooleanExpression tmpVar_18 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.booleanType());
		tmpVar_18 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_17);
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.integerType());
		tmpVar_19 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.divide(tmpVar_19,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_23 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.integerType());
		tmpVar_23 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_24 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.booleanType());
		tmpVar_24 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_23);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_25);
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_28 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.booleanType());
		tmpVar_28 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_27);
		BooleanExpression tmpVar_29 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.booleanType());
		tmpVar_29 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_3, tmpVar_9, tmpVar_14, tmpVar_18,
						tmpVar_22, tmpVar_24, tmpVar_26, tmpVar_28, tmpVar_29));
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_32 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						tmpType_0);
		tmpVar_32 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.realType());
		tmpVar_33 = universe.sigma(var_Y3, tmpVar_31, tmpVar_32);
		NumericExpression tmpVar_34 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.realType());
		tmpVar_34 = (NumericExpression) universe.arrayRead(var_X_a, var_Y3);
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.realType());
		tmpVar_35 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_34));
		NumericExpression tmpVar_36 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.realType());
		tmpVar_36 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_33, tmpVar_35));
		BooleanExpression tmpVar_37 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.booleanType());
		tmpVar_37 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_36);
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_37, tmpVar_22));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_30).valid(tmpVar_38).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(Y3,Y4,lambda t : int .
	 * X_a[t]) - 1*Y5 && 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && Y3 - 1*Y4 <= 0 && 0 <= (2*Y0) div 3 - 1*Y3
	 * - 1 && 0 <= (2*Y0) div 3 - 1*Y4 - 1 && 0 <= SIZEOF_REAL - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y4 + 1,lambda t : int . X_a[t]) - 1*X_a[Y4] -
	 * 1*Y5 && Y3 - 1*Y4 - 1 <= 0 && 0 <= (2*Y0) div 3 - 1*Y4 - 1
	 */
	@Test
	public void agt_9_assert_40() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(var_Y3, var_Y4, tmpVar_5);
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y5));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_10);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_15);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_22);
		NumericExpression tmpVar_24 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.integerType());
		tmpVar_24 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe
				.add(Arrays.asList(var_Y3, tmpVar_24));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(tmpVar_25,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.integerType());
		tmpVar_28 = (NumericExpression) universe.divide(tmpVar_27,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.integerType());
		tmpVar_29 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_29);
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.integerType());
		tmpVar_33 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_34 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.booleanType());
		tmpVar_34 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_33);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.integerType());
		tmpVar_35 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_36 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.booleanType());
		tmpVar_36 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_35);
		NumericExpression tmpVar_37 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.integerType());
		tmpVar_37 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_37);
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_40 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.booleanType());
		tmpVar_40 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_9, tmpVar_14, tmpVar_19, tmpVar_23, tmpVar_26, tmpVar_30,
				tmpVar_32, tmpVar_34, tmpVar_36, tmpVar_38, tmpVar_39));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y4,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_42 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						tmpType_0);
		tmpVar_42 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.realType());
		tmpVar_43 = universe.sigma(var_Y3, tmpVar_41, tmpVar_42);
		NumericExpression tmpVar_44 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.realType());
		tmpVar_44 = (NumericExpression) universe.arrayRead(var_X_a, var_Y4);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.realType());
		tmpVar_45 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_44));
		NumericExpression tmpVar_46 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.realType());
		tmpVar_46 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_43, tmpVar_45, tmpVar_7));
		BooleanExpression tmpVar_47 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.booleanType());
		tmpVar_47 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_46);
		NumericExpression tmpVar_48 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.integerType());
		tmpVar_48 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_49 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_49"),
						universe.booleanType());
		tmpVar_49 = (BooleanExpression) universe.lessThanEquals(tmpVar_48,
				universe.number(universe.numberFactory().number("0")));
		BooleanExpression tmpVar_50 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_50"),
						universe.booleanType());
		tmpVar_50 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_47, tmpVar_49, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_40).valid(tmpVar_50).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == Y0 div 3 - 1*Y3 && 0 == sigma(Y3,Y4,lambda t : int .
	 * X_a[t]) - 1*Y5 && 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 ==
	 * sigma(0,Y3,lambda t : int . X_a[t]) - 1*Y2 && forall _t : dynamicType .
	 * (0 <= CIVL_SIZEOF(_t) - 1) && Y3 - 1*Y4 <= 0 && 0 <= (2*Y0) div 3 - 1*Y3
	 * - 1 && 0 <= (2*Y0) div 3 - 1*Y4 - 1 && 0 <= SIZEOF_REAL - 1 && 0 <=
	 * X__mpi_nprocs_hi - 3 && 0 <= Y0 - 3 && 0 <= Y3
	 */
	/*
	 * predicate: 0 == sigma(Y3,Y4 + 1,lambda t : int . X_a[t]) - 1*X_a[Y4] -
	 * 1*Y5 && Y3 - 1*Y4 - 1 <= 0 && 0 <= (2*Y0) div 3 - 1*Y4 - 1
	 */
	@Test
	public void agt_10_assert_40() {
		// This juint test is automatically generated.
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.integerType());
		tmpVar_0 = (NumericExpression) universe.divide(var_Y0,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression var_Y3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y3"),
						universe.integerType());
		NumericExpression tmpVar_1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"),
						universe.integerType());
		tmpVar_1 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y3));
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.integerType());
		tmpVar_2 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_0, tmpVar_1));
		BooleanExpression tmpVar_3 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.booleanType());
		tmpVar_3 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_2);
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y4"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_Y0);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_5 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"), tmpType_0);
		tmpVar_5 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.realType());
		tmpVar_6 = universe.sigma(var_Y3, var_Y4, tmpVar_5);
		NumericExpression var_Y5 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y5"),
						universe.realType());
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.realType());
		tmpVar_7 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y5));
		NumericExpression tmpVar_8 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.realType());
		tmpVar_8 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_6, tmpVar_7));
		BooleanExpression tmpVar_9 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.booleanType());
		tmpVar_9 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_8);
		SymbolicExpression tmpVar_10 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						tmpType_0);
		tmpVar_10 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.realType());
		tmpVar_11 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_10);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_12 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_12"),
						universe.realType());
		tmpVar_12 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_13 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_13"),
						universe.realType());
		tmpVar_13 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_11, tmpVar_12));
		BooleanExpression tmpVar_14 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_14"),
						universe.booleanType());
		tmpVar_14 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_13);
		SymbolicExpression tmpVar_15 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						tmpType_0);
		tmpVar_15 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_16 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						universe.realType());
		tmpVar_16 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y3,
				tmpVar_15);
		NumericExpression var_Y2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y2"),
						universe.realType());
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y2));
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_16, tmpVar_17));
		BooleanExpression tmpVar_19 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.booleanType());
		tmpVar_19 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_18);
		SymbolicTupleType tmpType_3 = universe.tupleType(
				universe.stringObject("dynamicType"),
				Arrays.asList(universe.integerType()));
		SymbolicExpression var__t = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var__t"), tmpType_3);
		SymbolicFunctionType tmpType_4 = universe
				.functionType(Arrays.asList(tmpType_3), universe.integerType());
		SymbolicExpression var_CIVL_SIZEOF = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_CIVL_SIZEOF"),
						tmpType_4);
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.integerType());
		tmpVar_20 = (NumericExpression) universe.apply(var_CIVL_SIZEOF,
				Arrays.asList(var__t));
		NumericExpression tmpVar_21 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.integerType());
		tmpVar_21 = (NumericExpression) universe.add(Arrays.asList(tmpVar_20,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_21);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe
				.forall((SymbolicConstant) var__t, tmpVar_22);
		NumericExpression tmpVar_24 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_24"),
						universe.integerType());
		tmpVar_24 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y4));
		NumericExpression tmpVar_25 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_25"),
						universe.integerType());
		tmpVar_25 = (NumericExpression) universe
				.add(Arrays.asList(var_Y3, tmpVar_24));
		BooleanExpression tmpVar_26 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_26"),
						universe.booleanType());
		tmpVar_26 = (BooleanExpression) universe.lessThanEquals(tmpVar_25,
				universe.number(universe.numberFactory().number("0")));
		NumericExpression tmpVar_27 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_27"),
						universe.integerType());
		tmpVar_27 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("2")), var_Y0));
		NumericExpression tmpVar_28 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_28"),
						universe.integerType());
		tmpVar_28 = (NumericExpression) universe.divide(tmpVar_27,
				universe.number(universe.numberFactory().number("3")));
		NumericExpression tmpVar_29 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_29"),
						universe.integerType());
		tmpVar_29 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_1,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_30 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_30"),
						universe.booleanType());
		tmpVar_30 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_29);
		NumericExpression tmpVar_31 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_31"),
						universe.integerType());
		tmpVar_31 = (NumericExpression) universe.add(Arrays.asList(tmpVar_28,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_32 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_32"),
						universe.booleanType());
		tmpVar_32 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_31);
		NumericExpression var_SIZEOF_REAL = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_SIZEOF_REAL"),
						universe.integerType());
		NumericExpression tmpVar_33 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_33"),
						universe.integerType());
		tmpVar_33 = (NumericExpression) universe.add(Arrays.asList(
				var_SIZEOF_REAL,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_34 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_34"),
						universe.booleanType());
		tmpVar_34 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_33);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_35 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_35"),
						universe.integerType());
		tmpVar_35 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_36 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_36"),
						universe.booleanType());
		tmpVar_36 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_35);
		NumericExpression tmpVar_37 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_37"),
						universe.integerType());
		tmpVar_37 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("-3"))));
		BooleanExpression tmpVar_38 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_38"),
						universe.booleanType());
		tmpVar_38 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_37);
		BooleanExpression tmpVar_39 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_39"),
						universe.booleanType());
		tmpVar_39 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")), var_Y3);
		BooleanExpression tmpVar_40 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_40"),
						universe.booleanType());
		tmpVar_40 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_3,
				tmpVar_9, tmpVar_14, tmpVar_19, tmpVar_23, tmpVar_26, tmpVar_30,
				tmpVar_32, tmpVar_34, tmpVar_36, tmpVar_38, tmpVar_39));
		NumericExpression tmpVar_41 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_41"),
						universe.integerType());
		tmpVar_41 = (NumericExpression) universe.add(Arrays.asList(var_Y4,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_42 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_42"),
						tmpType_0);
		tmpVar_42 = universe.lambda((SymbolicConstant) var_t, tmpVar_4);
		NumericExpression tmpVar_43 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_43"),
						universe.realType());
		tmpVar_43 = universe.sigma(var_Y3, tmpVar_41, tmpVar_42);
		NumericExpression tmpVar_44 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_44"),
						universe.realType());
		tmpVar_44 = (NumericExpression) universe.arrayRead(var_X_a, var_Y4);
		NumericExpression tmpVar_45 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_45"),
						universe.realType());
		tmpVar_45 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_44));
		NumericExpression tmpVar_46 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_46"),
						universe.realType());
		tmpVar_46 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_43, tmpVar_45, tmpVar_7));
		BooleanExpression tmpVar_47 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_47"),
						universe.booleanType());
		tmpVar_47 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_46);
		NumericExpression tmpVar_48 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_48"),
						universe.integerType());
		tmpVar_48 = (NumericExpression) universe.add(Arrays.asList(var_Y3,
				tmpVar_24,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_49 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_49"),
						universe.booleanType());
		tmpVar_49 = (BooleanExpression) universe.lessThanEquals(tmpVar_48,
				universe.number(universe.numberFactory().number("0")));
		BooleanExpression tmpVar_50 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_50"),
						universe.booleanType());
		tmpVar_50 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_47, tmpVar_49, tmpVar_32));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_40).valid(tmpVar_50).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

	/*
	 * context: 0 == sigma(0,Y0,lambda t : int . X_a[t]) - 1*Y1 && 0 <= X_N -
	 * 1*Y0 - 1 && 0 <= X_N - 1 && 0 <= X__mpi_nprocs_hi - 3 && 0 <= Y0
	 */
	/*
	 * predicate: 0 == sigma(0,Y0 + 1,lambda t : int . X_a[t]) - 1*X_a[Y0] -
	 * 1*Y1 && 0 <= X_N - 1*Y0 - 1 && 0 <= Y0 + 1
	 */
	@Test
	public void agt_11_assert_69() {
		// This juint test is automatically generated.
		SymbolicFunctionType tmpType_0 = universe.functionType(
				Arrays.asList(universe.integerType()), universe.realType());
		NumericExpression var_Y0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y0"),
						universe.integerType());
		NumericExpression var_t = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("var_t"), universe.integerType());
		NumericExpression var_X_N = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X_N"),
						universe.integerType());
		SymbolicCompleteArrayType tmpType_2 = universe
				.arrayType(universe.realType(), var_X_N);
		SymbolicExpression var_X_a = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("var_X_a"), tmpType_2);
		NumericExpression tmpVar_0 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_0"),
						universe.realType());
		tmpVar_0 = (NumericExpression) universe.arrayRead(var_X_a, var_t);
		SymbolicExpression tmpVar_1 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_1"), tmpType_0);
		tmpVar_1 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_2"),
						universe.realType());
		tmpVar_2 = universe.sigma(
				universe.number(universe.numberFactory().number("0")), var_Y0,
				tmpVar_1);
		NumericExpression var_Y1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_Y1"),
						universe.realType());
		NumericExpression tmpVar_3 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_3"),
						universe.realType());
		tmpVar_3 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				var_Y1));
		NumericExpression tmpVar_4 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_4"),
						universe.realType());
		tmpVar_4 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_2, tmpVar_3));
		BooleanExpression tmpVar_5 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_5"),
						universe.booleanType());
		tmpVar_5 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_4);
		NumericExpression tmpVar_6 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_6"),
						universe.integerType());
		tmpVar_6 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().number("-1")),
				var_Y0));
		NumericExpression tmpVar_7 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_7"),
						universe.integerType());
		tmpVar_7 = (NumericExpression) universe.add(Arrays.asList(var_X_N,
				tmpVar_6,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_8 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_8"),
						universe.booleanType());
		tmpVar_8 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_7);
		NumericExpression tmpVar_9 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_9"),
						universe.integerType());
		tmpVar_9 = (NumericExpression) universe.add(Arrays.asList(var_X_N,
				universe.number(universe.numberFactory().number("-1"))));
		BooleanExpression tmpVar_10 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_10"),
						universe.booleanType());
		tmpVar_10 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_9);
		NumericExpression var_X__mpi_nprocs_hi = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("var_X__mpi_nprocs_hi"),
						universe.integerType());
		NumericExpression tmpVar_11 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_11"),
						universe.integerType());
		tmpVar_11 = (NumericExpression) universe.add(Arrays.asList(
				var_X__mpi_nprocs_hi,
				universe.number(universe.numberFactory().number("-3"))));
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
		tmpVar_14 = (BooleanExpression) universe.and(Arrays.asList(tmpVar_5,
				tmpVar_8, tmpVar_10, tmpVar_12, tmpVar_13));
		NumericExpression tmpVar_15 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_15"),
						universe.integerType());
		tmpVar_15 = (NumericExpression) universe.add(Arrays.asList(var_Y0,
				universe.number(universe.numberFactory().number("1"))));
		SymbolicExpression tmpVar_16 = (SymbolicExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_16"),
						tmpType_0);
		tmpVar_16 = universe.lambda((SymbolicConstant) var_t, tmpVar_0);
		NumericExpression tmpVar_17 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_17"),
						universe.realType());
		tmpVar_17 = universe.sigma(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_15, tmpVar_16);
		NumericExpression tmpVar_18 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_18"),
						universe.realType());
		tmpVar_18 = (NumericExpression) universe.arrayRead(var_X_a, var_Y0);
		NumericExpression tmpVar_19 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_19"),
						universe.realType());
		tmpVar_19 = (NumericExpression) universe.multiply(Arrays.asList(
				universe.number(universe.numberFactory().rational("-1")),
				tmpVar_18));
		NumericExpression tmpVar_20 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_20"),
						universe.realType());
		tmpVar_20 = (NumericExpression) universe
				.add(Arrays.asList(tmpVar_17, tmpVar_19, tmpVar_3));
		BooleanExpression tmpVar_21 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_21"),
						universe.booleanType());
		tmpVar_21 = (BooleanExpression) universe.equals(
				universe.number(universe.numberFactory().rational("0")),
				tmpVar_20);
		BooleanExpression tmpVar_22 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_22"),
						universe.booleanType());
		tmpVar_22 = (BooleanExpression) universe.lessThanEquals(
				universe.number(universe.numberFactory().number("0")),
				tmpVar_15);
		BooleanExpression tmpVar_23 = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("tmpVar_23"),
						universe.booleanType());
		tmpVar_23 = (BooleanExpression) universe
				.and(Arrays.asList(tmpVar_21, tmpVar_8, tmpVar_22));
		ResultType tmpRT_0;
		tmpRT_0 = universe.reasoner(tmpVar_14).valid(tmpVar_23).getResultType();
		org.junit.Assert.assertEquals(ResultType.YES, tmpRT_0);
	} // Test End

} // Class End
