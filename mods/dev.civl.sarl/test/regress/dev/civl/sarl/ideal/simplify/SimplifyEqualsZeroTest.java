/*
 * Copyright 2013 Stephen F. Siegel, University of Delaware
 */
package dev.civl.sarl.ideal.simplify;

import static dev.civl.sarl.ideal.simplify.CommonObjects.bigMixedXYTermPoly;
import static dev.civl.sarl.ideal.simplify.CommonObjects.int0;
import static dev.civl.sarl.ideal.simplify.CommonObjects.int1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.intNeg1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.mixedXYTermPoly;
import static dev.civl.sarl.ideal.simplify.CommonObjects.newContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.preUniv;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat0;
import static dev.civl.sarl.ideal.simplify.CommonObjects.standardStrategy;
import static dev.civl.sarl.ideal.simplify.CommonObjects.testContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.x;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xInt;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xSqrLess1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xSqrP1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.y;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing on IdealSimplifier with expressions that have a variable and its
 * respective terms drop due to being set equal to 0. Aims to confirm that
 * remaining terms, be they constant or variable, agree with expectations.
 * 
 * @author danfried
 *
 */
public class SimplifyEqualsZeroTest {

	/**
	 * Calls the setUp() method in CommonObjects to make use of consolidated SARL
	 * object declarations and initializations for testing of "Simplify" module.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonObjects.setUp();
		// assumption = preUniv.lessThan(int0, xInt);
		testContext = newContext(preUniv.equals(xInt, int0));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Testing of IdealSimplifier with expressions when variable is dropped, and
	 * only a constant (integer) remains.
	 */
	@Test
	public void assumptionTest() {
		// out.println(xSqrLess1);
		// out.println(idealSimplifier.simplifyExpression(xSqrLess1));
		// assumption: x=0. Simplify x^2-1 -> -1
		assertEquals(intNeg1, testContext.simplify(xSqrLess1, standardStrategy));
		assertEquals(int1, testContext.simplify(xSqrP1, standardStrategy));
		// out.println(idealSimplifier.apply(symbExpr_xpyInt));
		// out.println(idealSimplifier.simplifyExpression(symbExpr_xpyInt));
		// out.println("xx - 1 : " + xSqrLess1.toString());
		// IdealSimplifier.
		// out.println(mixedXYTermPoly);
		// out.println(bigMixedXYTermPoly);
	}

	/**
	 * Testing of IdealSimplifier with expressions of two two variables, when one is
	 * dropped by being set equal to 0.
	 */
	@Test
	public void assumptionOnPolyTest() {
		testContext = newContext(preUniv.equals(x, rat0));

		// out.println(idealSimplifier.apply(bigMixedXYTermPoly));
		assertEquals(testContext.simplify(bigMixedXYTermPoly, standardStrategy).toString(),
				preUniv.multiply(y, preUniv.multiply(y, y)).toString());
		// out.println(idealSimplifier.simplifyExpression(mixedXYTermPoly));
		assertEquals(testContext.simplify(mixedXYTermPoly, standardStrategy), y);
	}

}
