/*
 * Copyright 2013 Stephen F. Siegel, University of Delaware
 */
package dev.civl.sarl.ideal.simplify;

import static dev.civl.sarl.ideal.simplify.CommonObjects.testContext;
import static dev.civl.sarl.ideal.simplify.CommonObjects.int0;
import static dev.civl.sarl.ideal.simplify.CommonObjects.intNeg1;
import static dev.civl.sarl.ideal.simplify.CommonObjects.preUniv;
import static dev.civl.sarl.ideal.simplify.CommonObjects.x;
import static dev.civl.sarl.ideal.simplify.CommonObjects.xInt;
import static dev.civl.sarl.ideal.simplify.CommonObjects.yInt;
import static dev.civl.sarl.ideal.simplify.CommonObjects.newContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.prove.IF.Prove;

// private static SymbolicConstant t;

/**
 * Testing on assumptionAsInterval method in IdealSimplifier to look for
 * expected behavior when giving mixed-type value, and also to confirm bounds of
 * the supplied interval
 * 
 * @author danfried
 *
 */
public class SimplifierIntervalTest {

	private final static boolean useBackwardSubstitution = true;

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
		// preUniv.equals(preUniv.multiply(rat5,x), preUniv.multiply(y, y));
		testContext = newContext(preUniv.lessThan(xInt, int0));
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
	 * Testing of IdealSimplifier on assumptionAsInterval method... Tests
	 * passing matched and mismatched symbolic constants as assumption
	 */
	@Test
	public void mixedTypeNullTest() {
		// non-matching symbolic constant in assumptionAsInterval and
		// the initial assumption should return null
		assertNull(testContext.assumptionAsInterval(x));
	}

	/**
	 * Testing of IdealSimplifier on assumptionAsInterval method, when
	 * matched-type (integer) expressions are used for the assumption and also
	 * expression assumption is applied to.
	 */
	@Test
	public void matchedTypeTest() {
		// the upper bound should be -1
		// out.println(intNeg1.toString());
		// out.println(idealSimplifier.assumptionAsInterval(xInt));
		// out.println(assumption.atomString());
		assertEquals(intNeg1.toString(),
				testContext.assumptionAsInterval(xInt).upper().toString());
	}

	/**
	 * <p>
	 * Assumption 0 <= xInt && xInt < 3 && yInt < 9
	 * 
	 * Call assumptionAsInterval( xInt ).
	 */
	@Test
	public void getSimpleIntervalFromContext2Free() {
		BooleanExpression assumption = preUniv.lessThanEquals(int0, xInt);
		assumption = preUniv.and(assumption,
				preUniv.lessThan(xInt, preUniv.integer(3)));
		assumption = preUniv.and(assumption,
				preUniv.lessThan(yInt, preUniv.integer(9)));
		testContext = newContext(assumption);
		Interval interval = testContext.computeRange(xInt).intervalOverApproximation();

		NumberFactory nf = preUniv.numberFactory();

		Interval expected = nf.newInterval(true, nf.zeroInteger(), false,
				nf.integer(3), true);
		assertEquals(expected, interval);
	}

	/**
	 * <p>
	 * Assumption 0 <= xInt && xInt < 3
	 * 
	 * Call assumptionAsInterval( xInt ): get [0,2]
	 * </p>
	 */
	@Test
	public void getSimpleIntervalFromContext1Free() {
		BooleanExpression assumption = preUniv.lessThanEquals(int0, xInt);
		assumption = preUniv.and(assumption,
				preUniv.lessThan(xInt, preUniv.integer(3)));
		testContext = newContext(assumption);

		assertNotNull(testContext.assumptionAsInterval(xInt));
	}
}
