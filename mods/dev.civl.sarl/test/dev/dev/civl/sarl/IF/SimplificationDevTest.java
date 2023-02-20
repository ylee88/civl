package dev.civl.sarl.IF;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicIntegerType;
import dev.civl.sarl.IF.type.SymbolicType;

public class SimplificationDevTest {

	static PrintStream out = System.out;

	static SymbolicUniverse universe = SARL.newStandardUniverse();

	static SymbolicIntegerType intType = universe.integerType();

	static SymbolicType boolType = universe.booleanType();

	static NumericExpression zero = universe.integer(0);

	static NumericExpression one = universe.integer(1);

	static NumericExpression two = universe.integer(2);

	static NumericExpression three = universe.integer(3);

	/**
	 * These should be equivalent: p?(q?0:1):2 and p&&q?0:p&&!q?1:2.
	 */
	@Test
	public void ite1() {
		BooleanExpression p = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("p"), boolType);
		BooleanExpression q = (BooleanExpression) universe
				.symbolicConstant(universe.stringObject("q"), boolType);
		SymbolicExpression ite1 = universe.cond(p, universe.cond(q, zero, one),
				two);
		SymbolicExpression ite2 = universe.cond(universe.and(p, q), zero,
				universe.cond(universe.and(p, universe.not(q)), one, two));

		out.println("ite1  : " + ite1);
		out.println("ite2  : " + ite2);

		Reasoner reasoner = universe.reasoner(universe.trueExpression());
		SymbolicExpression simp1 = reasoner.simplify(ite1),
				simp2 = reasoner.simplify(ite2);

		out.println("simp1 : " + simp1);
		out.println("simp2 : " + simp2);

		assertEquals(simp1, simp2);
	}

	@Test
	public void ite2() {
		NumericSymbolicConstant i = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("i"), intType);
		NumericSymbolicConstant j = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("j"), intType);
		NumericSymbolicConstant N = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("N"), intType);
		NumericSymbolicConstant M = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("M"), intType);
		SymbolicConstant f = universe.symbolicConstant(
				universe.stringObject("f"), universe.functionType(
						Arrays.asList(intType, intType), intType));
		NumericExpression N2, M2;

		N2 = universe.multiply(two, N);
		M2 = universe.multiply(two, M);

		SymbolicArrayType arr2dType0 = universe
				.arrayType(universe.arrayType(intType, N2), N2);
		SymbolicArrayType arr2dType1 = universe
				.arrayType(universe.arrayType(intType, M2), M2);
		SymbolicConstant w, x, y, z;
		NumericExpression readW, readX, readY, readZ;

		w = universe.symbolicConstant(universe.stringObject("w"), arr2dType1);
		x = universe.symbolicConstant(universe.stringObject("x"), arr2dType1);
		y = universe.symbolicConstant(universe.stringObject("y"), arr2dType1);
		z = universe.symbolicConstant(universe.stringObject("z"), arr2dType1);
		readW = (NumericExpression) universe.arrayRead(universe.arrayRead(w, i),
				j);
		readX = (NumericExpression) universe.arrayRead(universe.arrayRead(x, i),
				j);
		readY = (NumericExpression) universe.arrayRead(universe.arrayRead(y, i),
				j);
		readZ = (NumericExpression) universe.arrayRead(universe.arrayRead(z, i),
				j);

		SymbolicExpression fij = universe.apply(f, Arrays.asList(i, j));
		BooleanExpression p = universe.forallInt(j, zero, N,
				universe.equals(fij, readW));
		BooleanExpression cxt = p;

		p = universe.forallInt(j, N, N2, universe.equals(fij, readX));
		cxt = universe.and(cxt, p);
		cxt = universe.forallInt(i, zero, N, cxt);
		p = universe.forallInt(j, zero, N, universe.equals(fij, readY));
		p = universe.and(p,
				universe.forallInt(j, N, N2, universe.equals(fij, readZ)));
		p = universe.forallInt(i, N, N2, p);
		cxt = universe.and(cxt, p);
		cxt = universe.and(Arrays.asList(cxt, universe.lessThan(zero, N),
				universe.lessThan(N, M)));

		SymbolicExpression tmp0 = universe.cond(inRange(j, N, N2), readX, zero);
		SymbolicExpression tmp1 = universe.cond(inRange(j, N, N2), readZ, zero);

		tmp0 = universe.cond(inRange(j, zero, N), readW, tmp0);
		tmp0 = universe.lambda(j, tmp0);
		tmp0 = universe.arrayLambda(universe.arrayType(intType, N2), tmp0);
		tmp1 = universe.cond(inRange(j, zero, N), readY, tmp1);
		tmp1 = universe.lambda(j, tmp1);
		tmp1 = universe.arrayLambda(universe.arrayType(intType, N2), tmp1);
		tmp0 = universe.cond(inRange(i, zero, N), tmp0, tmp1);
		tmp0 = universe.lambda(i, tmp0);

		SymbolicExpression a = universe
				.arrayLambda((SymbolicCompleteArrayType) arr2dType0, tmp0);
		Reasoner reasoner = universe.reasoner(cxt);
		BooleanExpression result = reasoner.simplify(universe.forallInt(i, zero,
				N2,
				universe.forallInt(j, zero, N2,
						universe.equals(
								universe.arrayRead(universe.arrayRead(a, i), j),
								fij))));

		assertTrue(result.isTrue());
	}

	// inclusive low & exclusive high:
	private BooleanExpression inRange(NumericExpression e,
			NumericExpression incLow, NumericExpression elcHigh) {
		return universe.and(universe.lessThanEquals(incLow, e),
				universe.lessThan(e, elcHigh));
	}
}
