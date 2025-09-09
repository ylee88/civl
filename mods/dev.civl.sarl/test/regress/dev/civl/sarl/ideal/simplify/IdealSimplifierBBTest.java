/*
 * Copyright 2013 Stephen F. Siegel, University of Delaware
 */
package dev.civl.sarl.ideal.simplify;

import static dev.civl.sarl.ideal.simplify.CommonObjects.bigMixedXYTermPoly;
import static dev.civl.sarl.ideal.simplify.CommonObjects.standardStrategy;
import static dev.civl.sarl.ideal.simplify.CommonObjects.testContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.out;
import static dev.civl.sarl.ideal.simplify.CommonObjects.preUniv;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat0;
import static dev.civl.sarl.ideal.simplify.CommonObjects.rat1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.ratNeg1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.threeX4th;
import static dev.civl.sarl.ideal.simplify.CommonObjects.trueExpr;
import static dev.civl.sarl.ideal.simplify.CommonObjects.x;
import static dev.civl.sarl.ideal.simplify.CommonObjects.x4th;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xNE;
import static dev.civl.sarl.ideal.simplify.CommonObjects.yNE;
import static dev.civl.sarl.ideal.simplify.CommonObjects.useBackwardSubstitution;
import static dev.civl.sarl.ideal.simplify.CommonObjects.newContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.prove.IF.Prove;

/**
 * Set of tests on IdealSimplifier based about assigning values to single
 * variables and a polynomial, and then confirming expected full and reduced
 * contexts.
 * 
 * @author danfried
 * 
 */
public class IdealSimplifierBBTest {

	// private final static boolean useBackwardSubstitution = true;

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
	 * Simple test to make sure simplifier has agreement between full and
	 * reduced context when variable, x, is set to 0.0
	 */
	@Test
	public void xGreater0Test() {
		testContext = newContext(preUniv.lessThan(rat0, xNE));
		out.println("full: " + testContext.getFullAssumption() + " reduced: "
				+ testContext.getReducedAssumption());
		assertEquals("0 < x", testContext.getReducedAssumption().toString());

		out.println(testContext.simplify(bigMixedXYTermPoly, standardStrategy));
		assertEquals(testContext.getReducedAssumption(),
				testContext.getFullAssumption());
	}

	/**
	 * This test involves the use of two separate assumptions that are
	 * compounded and applied to a mixed-term polynomial to test the
	 * simplification on large-term symbolic expressions.
	 */
	@Test
	public void twoStagePolyTest() {
		// first assumption: x == -1.0
		testContext = newContext(preUniv.equals(ratNeg1, x));
		out.println(testContext.simplify(bigMixedXYTermPoly, standardStrategy));
		SymbolicExpression noX = (SymbolicExpression) testContext
				.simplify(bigMixedXYTermPoly, standardStrategy); // intermediary
		// symbolic
		// expression

		// second assumption: y == 1.0
		testContext = newContext(preUniv.equals(rat1, yNE));
		out.println(testContext.simplify(noX, standardStrategy));
		// 0^3 should = 0...
		assertEquals(rat0.type(), ((SymbolicExpression) testContext
				.simplify(noX, standardStrategy)).type());
		assertEquals(rat0, testContext.simplify(noX, standardStrategy));
		out.println(testContext.getFullAssumption());
		out.println(testContext.getReducedAssumption());
	}

	/**
	 * Test on idealsimplifier's ability to determine that a reducedContext is
	 * true due to a value being solvable/ able to be determined
	 */
	@Test
	public void simplifySolvableTest() {
		testContext = newContext(preUniv.equals(rat0, yNE));
		// out.println("here: " + idealSimplifier.getFullContext());
		// out.println(idealSimplifier.getReducedContext());
		assertNotEquals(testContext.getFullAssumption(),
				testContext.getReducedAssumption());
		assertEquals(trueExpr, testContext.getReducedAssumption());
	}

	/**
	 * Tests idealSimplifer's ability to reduce a single-term variable of order
	 * > 1, when equal to 0
	 */
	@Test
	public void singlePowerTermSimplifyTest() {
		testContext = newContext(preUniv.equals(rat0, x4th));
		out.println("here: " + testContext.getFullAssumption());
		out.println(testContext.getReducedAssumption());
		assertEquals(rat0, testContext.simplify(threeX4th, standardStrategy));
		// x^4 == 0 should be reduced to x == 0
		assertNotEquals(testContext.getFullAssumption(),
				testContext.getReducedAssumption());
	}
}
