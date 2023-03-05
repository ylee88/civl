package dev.civl.sarl.preuniverse;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicIntegerType;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;

public class Array2Test {

	public static PrintStream out = System.out;

	private static PreUniverse universe;

	private static SymbolicIntegerType intType;

	/**
	 * Array of int.
	 */
	private static SymbolicArrayType inta;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FactorySystem system = PreUniverses.newIdealFactorySystem();

		universe = PreUniverses.newPreUniverse(system);
		intType = universe.integerType();
		inta = universe.arrayType(intType);
	}

	@Test
	public void test1() {
		SymbolicConstant a = universe
				.symbolicConstant(universe.stringObject("a"), inta);
		NumericExpression i1 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("i1"), intType);
		NumericExpression i2 = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("i2"), intType);
		NumericExpression v = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("v"), intType);
		SymbolicExpression w = universe.arrayWrite(a, i1, v);
		SymbolicExpression r = universe.arrayRead(w, i2);
		SymbolicExpression c = universe.cond(universe.equals(i1, i2), v,
				universe.arrayRead(a, i2));

		out.println(r);
		out.println(c);
		assertEquals(c, r);
	}

}
