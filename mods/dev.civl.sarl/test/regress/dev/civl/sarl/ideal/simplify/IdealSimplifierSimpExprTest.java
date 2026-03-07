package dev.civl.sarl.ideal.simplify;

import static dev.civl.sarl.ideal.simplify.CommonObjects.newContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.onePxPxSqdP3x4th;
import static dev.civl.sarl.ideal.simplify.CommonObjects.out;
import static dev.civl.sarl.ideal.simplify.CommonObjects.preUniv;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat0;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat2;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat20;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat200;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat25;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat3;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat4;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat5;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat6;
import static dev.civl.sarl.ideal.simplify.CommonObjects.ratNeg300;
import static dev.civl.sarl.ideal.simplify.CommonObjects.standardStrategy;
import static dev.civl.sarl.ideal.simplify.CommonObjects.testContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.useBackwardSubstitution;
import static dev.civl.sarl.ideal.simplify.CommonObjects.x;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xx;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xy;
import static dev.civl.sarl.ideal.simplify.CommonObjects.y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * Testing on IdealSimplifier based on Polynomials for both assumptions and
 * simplification.
 * 
 * 
 * @author mbrahma
 */

public class IdealSimplifierSimpExprTest {

	private NumericExpression numExpr, numExpect, expr1, expr2, expr3, expr4;

	private SymbolicExpression symExpr, expected;

	/**
	 * Calls the setUp() method in CommonObjects to make use of consolidated
	 * SARL object declarations and initializations for testing of "Simplify"
	 * module. Also initialized objects in the CommonObjects class that are used
	 * often and therefore not given an initial value.
	 * 
	 * @throws java.lang.Exception
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonObjects.setUp();
		useBackwardSubstitution = true;
	}

	/**
	 * @throws java.lang.Exception
	 */

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */

	/**
	 * Test on IdealSimplifier to check follows basic arithmetic conventions
	 */
	@Test
	public void simplifyExpression0() {
		numExpr = preUniv.multiply(rat0, x);
		symExpr = numExpr;
		testContext = newContext(preUniv.equals(rat0, x));

		assertEquals(rat0, testContext.simplify(symExpr, standardStrategy));
	}

	/**
	 * Test on IdealSimplifier to check if the correct exception is thrown when
	 * 0 is given as a denominator regardless of what the numerator is.
	 */
	@Test(expected = ArithmeticException.class)
	public void simplifyExpressionIllegalArg() {
		numExpr = preUniv.divide(x, rat0);
		symExpr = numExpr;

		testContext = newContext(preUniv.equals(rat0, x));

		assertEquals(rat0, testContext.simplify(symExpr, standardStrategy));
	}

	/**
	 * Test on IdealSimplifier using simple substitution for one variable x
	 */

	@Test
	public void simplifyExpressionTrivial() {

		numExpr = preUniv.multiply(preUniv.divide(onePxPxSqdP3x4th, x), x);

		symExpr = numExpr;

		testContext = newContext(preUniv.equals(preUniv.multiply(rat5, x),
				preUniv.multiply(y, y)));

		assertEquals(testContext.simplify(onePxPxSqdP3x4th, standardStrategy),
				testContext.simplify(symExpr, standardStrategy));
	}

	@Test
	public void simplifyExpressionAddition() {
		/*
		 * NumericExpression one = preUniv.multiply(xy, rat5); NumericExpression
		 * two; NumericExpression three = preUniv.multiply(preUniv.power(x,
		 * 27),preUniv.power(z, 9)); NumericExpression four =
		 * preUniv.multiply(rat6, ratNeg25); NumericExpression five =
		 * preUniv.rational(0.75); NumericExpression six =
		 * preUniv.multiply(preUniv.power(xy, 4), rat5); NumericExpression
		 * seven; NumericExpression eight =
		 * preUniv.multiply(rat6,preUniv.multiply(preUniv.power(x,
		 * 27),preUniv.power(z, 9))); NumericExpression nine; NumericExpression
		 * ten = preUniv.rational(1081392); NumericExpression eleven;
		 * NumericExpression twelve; NumericExpression thirteen;
		 * NumericExpression fourteen; NumericExpression fifteen =
		 * preUniv.rational(1081392);
		 */
		// expr1= preUniv.add(one, preUniv.add(two, three));
		// expr2= preUniv.add(four, preUniv.add(five, six));
		// expr3= preUniv.add(seven, preUniv.add(eight, nine));
		// expr4= preUniv.add(ten,preUniv.add(eleven, twelve));
		// NumericExpression expr5 = preUniv.add(thirteen, preUniv.add(fourteen,
		// fifteen));

	}

	/**
	 * Test on IdealSimplifier that tests the method simplifyExpression() when
	 * only subtracting polynomials
	 * 
	 */
	@Test
	public void simplifyExpressionSubtraction() {
		expr1 = preUniv.multiply(rat25,
				preUniv.multiply(preUniv.power(x, 4), preUniv.power(y, 3)));
		out.println(expr1);
		expr2 = preUniv.multiply(ratNeg300, xx);
		out.println(expr2);
		expr3 = preUniv.multiply(rat20,
				preUniv.multiply(preUniv.power(x, 4), preUniv.power(y, 3)));
		out.println(expr3);
		expr4 = preUniv.multiply(rat200, xx);
		out.println(expr4);

		// 5x^4y^3 + 500x^2 -> 5*5^4y^3 + 500*5^2 = 3125y^3+12500

		numExpr = preUniv.subtract(preUniv.subtract(expr1, expr3),
				preUniv.subtract(expr2, expr4));

		symExpr = numExpr;

		testContext = newContext(preUniv.equals(x, rat5));

		numExpect = preUniv.add(preUniv.rational(12500),
				preUniv.multiply(preUniv.rational(3125), preUniv.power(y, 3)));

		out.println(numExpect);
		expected = numExpect;

		assertEquals(expected, testContext.simplify(symExpr, standardStrategy));
	}

	/**
	 * Test on IdealSimplifier that tests the method simplifyExpression() when
	 * only dividing polynomials.
	 * 
	 * Let e = (6x+2x*y^2)/(2x) = 3+y^2 Assume 5x=y^2. Simplify e: 3+5x. =
	 * 5*(x+3/5). OK
	 * 
	 */
	@Test
	public void simplifyExpressionDivide() {
		NumericExpression num = preUniv.add(preUniv.multiply(rat6, x), preUniv
				.multiply(preUniv.multiply(rat2, x), preUniv.power(y, 2)));
		NumericExpression denom = preUniv.multiply(rat2, x);

		numExpr = preUniv.divide(num, denom);
		symExpr = numExpr;
		testContext = newContext(preUniv.equals(preUniv.multiply(rat5, x),
				preUniv.multiply(y, y)));
		numExpect = preUniv.add(preUniv.power(y, 2), rat3);
		expected = numExpect;

		NumericExpression expect2 = preUniv.add(rat3,
				preUniv.multiply(rat5, x));
		SymbolicExpression actual = (SymbolicExpression) testContext
				.simplify(symExpr, standardStrategy);

		// out.println("expect2 = " + expect2);
		// out.println("actual = " + actual);
		assertTrue(actual == expected || actual == expect2);
		// assertEquals(expected, idealSimp.apply(symExpr));
	}

	/**
	 * Test on IdealSimplifier that tests the method simplifyExpression() when
	 * only multiplying polynomials
	 * 
	 */
	@Test
	public void simplifyExpressionPoly() {
		// [ (4*x^3y^2 + 2x^2y)+3xy ] / [xy] =
		// (4*x^2y + 2x) + 3 =
		// 4*x*(xy + (1/2)x) + 3 =
		// 4[x(xy + (1/2)x) + 3/4]
		NumericExpression num = preUniv.add(
				preUniv.add(
						preUniv.multiply(rat4,
								preUniv.multiply(preUniv.power(x, 3),
										preUniv.power(y, 2))),
						preUniv.multiply(rat2, preUniv.multiply(xy, x))),
				preUniv.multiply(rat3, xy));

		NumericExpression denom = preUniv.multiply(y, x);

		numExpr = preUniv.divide(num, denom);

		symExpr = numExpr;

		testContext = newContext(preUniv.equals(preUniv.multiply(rat5, x),
				preUniv.multiply(y, y)));

		// x(4xy+2)+3 = 4x^2y+2x+3
		numExpect = preUniv.add(
				preUniv.multiply(x, preUniv.add(
						preUniv.multiply(preUniv.multiply(rat4, x), y), rat2)),
				rat3);

		expected = (SymbolicExpression) testContext.simplify(numExpect,
				standardStrategy);

		// simplifying symExpr=numExpr
		NumericExpression simplified = (NumericExpression) testContext
				.simplify(symExpr, standardStrategy);

		assertEquals(expected, simplified);
	}

	@Test
	public void arrayReadOnWriteSimplify() {
		SymbolicUniverse u = SARL.newStandardUniverse();
		SymbolicExpression array = u.symbolicConstant(u.stringObject("a"),
				u.arrayType(u.integerType()));
		NumericExpression x, y, z, idx;

		x = (NumericExpression) u.symbolicConstant(u.stringObject("x"),
				u.integerType());
		y = (NumericExpression) u.symbolicConstant(u.stringObject("y"),
				u.integerType());
		z = (NumericExpression) u.symbolicConstant(u.stringObject("z"),
				u.integerType());
		idx = (NumericExpression) u.symbolicConstant(u.stringObject("idx"),
				u.integerType());

		BooleanExpression cxt = u.and(
				Arrays.asList(u.neq(x, idx), u.neq(y, idx), u.neq(z, idx)));
		SymbolicExpression read = u.arrayRead(array, idx);

		assertEquals(read,
				u.reasoner(cxt)
						.simplify(u.arrayRead(
								u.arrayWrite(u.arrayWrite(
										u.arrayWrite(array, z, z), y, y), x, x),
								idx)));
	}

	@Test
	public void arrayReadOnDenseWriteSimplify() {
		SymbolicUniverse u = SARL.newStandardUniverse();
		SymbolicExpression array = u.symbolicConstant(u.stringObject("a"),
				u.arrayType(u.integerType()));
		NumericExpression x, y, z, idx;

		x = (NumericExpression) u.symbolicConstant(u.stringObject("x"),
				u.integerType());
		y = (NumericExpression) u.symbolicConstant(u.stringObject("y"),
				u.integerType());
		z = (NumericExpression) u.symbolicConstant(u.stringObject("z"),
				u.integerType());
		idx = (NumericExpression) u.symbolicConstant(u.stringObject("idx"),
				u.integerType());

		SymbolicExpression newArray = u.denseArrayWrite(array,
				Arrays.asList(x, y, z));
		BooleanExpression cxt = u.equals(idx, u.integer(1));

		assertEquals(y, u.reasoner(cxt).simplify(u.arrayRead(newArray, idx)));

		cxt = u.lessThanEquals(u.integer(3), idx);
		assertEquals(u.arrayRead(array, idx),
				u.reasoner(cxt).simplify(u.arrayRead(newArray, idx)));
	}

	@Test
	public void arrayReadOnConstantArraySimplify() {
		SymbolicUniverse u = SARL.newStandardUniverse();
		SymbolicExpression array = u.array(u.integerType(),
				Arrays.asList(u.zeroInt(), u.zeroInt(), u.zeroInt()));
		NumericExpression idx;

		idx = (NumericExpression) u.symbolicConstant(u.stringObject("idx"),
				u.integerType());

		BooleanExpression cxt = u.and(u.lessThanEquals(u.zeroInt(), idx),
				u.lessThan(idx, u.integer(3)));

		assertEquals(u.zeroInt(),
				u.reasoner(cxt).simplify(u.arrayRead(array, idx)));
	}
}
