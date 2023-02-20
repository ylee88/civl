package dev.civl.sarl.IF;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicIntegerType;
import dev.civl.sarl.IF.type.SymbolicType;

public class ConditionalReasonerTest {

	static PrintStream out = System.out;

	static SymbolicUniverse universe = SARL.newStandardUniverse();

	static SymbolicIntegerType intType = universe.integerType();

	static SymbolicType boolType = universe.booleanType();

	static NumericExpression zero = universe.integer(0);

	static NumericExpression one = universe.integer(1);

	static NumericExpression two = universe.integer(2);

	static NumericExpression three = universe.integer(3);

	/**
	 * <pre>
	 * (p||q)&&(p||!q)  ==>  p||(q&&!q) ==> p||false ==> p
	 * </pre>
	 */
	@Test
	public void logic1() {
		BooleanExpression p = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("p"), boolType);
		BooleanExpression q = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("q"), boolType);
		BooleanExpression r = universe.and(universe.or(p, q),
				universe.or(p, universe.not(q)));

		out.println("Expression  : " + r);

		BooleanExpression r2 = universe.reasoner(universe.trueExpression())
				.simplify(r);

		out.println("Simplified  : " + r2);
		assertEquals(p, r2);
	}

	/**
	 * p?(p?1:2):3 -> p?1:3.
	 */
	@Test
	public void ppTest() {
		BooleanExpression p = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("p"), boolType);
		SymbolicExpression p12 = universe.cond(p, one, two);
		SymbolicExpression pp123 = universe.cond(p, p12, three);
		SymbolicExpression p13 = universe.cond(p, one, three);
		Reasoner r = universe.reasoner(universe.trueExpression());
		SymbolicExpression s = r.simplify(pp123);

		assertEquals(p13, s);
	}

	/**
	 * p?1:(p?2:3) -> p?1:3.
	 */
	@Test
	public void p1pTest() {
		BooleanExpression p = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("p"), boolType);
		SymbolicExpression e1 = universe.cond(p, one,
				universe.cond(p, two, three));
		SymbolicExpression e2 = universe.cond(p, one, three);
		Reasoner r = universe.reasoner(universe.trueExpression());
		SymbolicExpression e3 = r.simplify(e1);

		assertEquals(e2, e3);
	}
}
