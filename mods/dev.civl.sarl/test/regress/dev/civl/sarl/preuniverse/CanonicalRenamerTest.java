/**
 * Test canonical renamer.
 * 
 * @author Stephen F. Siegel
 */
package dev.civl.sarl.preuniverse;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicTypeSequence;
import dev.civl.sarl.IF.type.SymbolicUnionType;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.preuniverse.common.CommonPreUniverse;

@SuppressWarnings("unused")
public class CanonicalRenamerTest {

	private static PrintStream out = System.out;

	private static PreUniverse universe;

	private static NumericExpression x; // real x

	private static NumericExpression y; // real y

	private static SymbolicExpression expression1; // (x+y)^3

	private static SymbolicType integerType, intArrayType, functionType,
			functionType1, realType, booleanType;

	private static SymbolicTupleType tupleType;

	private static SymbolicUnionType unionType;

	private static SymbolicTypeSequence sequence, sequence1;

	private static SymbolicCompleteArrayType completeArrayType;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FactorySystem system = PreUniverses.newIdealFactorySystem();
		universe = new CommonPreUniverse(system);
		integerType = universe.integerType();
		realType = universe.realType();
		booleanType = universe.booleanType();
		intArrayType = universe.arrayType(integerType);
		completeArrayType = universe
				.arrayType(integerType, universe.integer(2));
		tupleType = universe.tupleType(
				universe.stringObject("SequenceofInteger"),
				Arrays.asList(integerType, integerType, integerType));
		unionType = universe
				.unionType(universe.stringObject("union1"), Arrays.asList(
						integerType, realType, booleanType, intArrayType));
		sequence = tupleType.sequence();
		sequence1 = universe.typeSequence(Arrays.asList(integerType, realType,
				booleanType, intArrayType));
		functionType = universe.functionType(sequence, realType);
		functionType1 = universe.functionType(sequence1, realType);

		x = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("x"), realType);
		y = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("y"), realType);
		expression1 = universe.power(universe.add(x, y), 3);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void canonic1() {
		UnaryOperator<SymbolicExpression> renamer = universe
				.canonicalRenamer("x");
		NumericExpression x_expected = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("x0"), realType);
		SymbolicExpression x_new = renamer.apply(x);

		out.println(x + " -> " + x_new);
		assertEquals(x_expected, x_new);

		NumericExpression y_expected = y;
		SymbolicExpression y_new = renamer.apply(y);

		out.println(y + " -> " + y_new);
		assertEquals(y_expected, y_new);

		NumericExpression expression1_expected = universe.power(
				universe.add(x_expected, y_expected), 3);
		SymbolicExpression expression1_new = renamer.apply(expression1);

		out.println(expression1 + " -> " + expression1_new);
		assertEquals(expression1_expected, expression1_new);
	}

	@Test
	public void canonic2() {
		SymbolicArrayType int5 = universe.arrayType(integerType,
				universe.integer(5));
		SymbolicConstant X1 = universe.symbolicConstant(
				universe.stringObject("X1"), integerType);
		SymbolicConstant X2 = universe.symbolicConstant(
				universe.stringObject("X2"), int5);
		SymbolicExpression a = universe.arrayWrite(X2, universe.integer(3), X1);
		UnaryOperator<SymbolicExpression> renamer = universe
				.canonicalRenamer("X");
		SymbolicExpression X1_expected = universe.symbolicConstant(
				universe.stringObject("X0"), integerType);
		SymbolicExpression X1_new = renamer.apply(X1);

		out.println(X1 + " -> " + X1_new);
		assertEquals(X1_expected, X1_new);

		SymbolicExpression a_expected = universe.arrayWrite(
				universe.symbolicConstant(universe.stringObject("X1"), int5),
				universe.integer(3), X1_expected);
		SymbolicExpression a_new = renamer.apply(a);

		out.println(a + " -> " + a_new);
		assertEquals(a_expected, a_new);
	}

	@Test
	public void quantifier() {

	}
}
