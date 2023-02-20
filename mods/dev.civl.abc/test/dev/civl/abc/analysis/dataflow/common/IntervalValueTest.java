package dev.civl.abc.analysis.dataflow.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.number.IF.Numbers;

public class IntervalValueTest {
	
	private static NumberFactory numFactory = Numbers.REAL_FACTORY;
	private static IntegerNumber INT_ZERO = numFactory.zeroInteger();
	private static IntegerNumber INT_POS_ONE = numFactory.integer(1);
	private static IntegerNumber INT_POS_TWO = numFactory.integer(2);
	private static IntegerNumber INT_POS_THREE = numFactory.integer(3);
	private static IntegerNumber INT_POS_FOUR = numFactory.integer(4);
	private static IntegerNumber INT_POS_EIGHT = numFactory.integer(8);
//	private static IntegerNumber INT_NEG_ONE = numFactory.negate(INT_POS_ONE);
	private static IntegerNumber INT_NEG_TWO = numFactory.negate(INT_POS_TWO);
//	private static IntegerNumber INT_NEG_FOUR = numFactory.negate(INT_POS_FOUR);

	Interval i12 = numFactory.newInterval(true, INT_POS_ONE,
			false, INT_POS_TWO, false);
	Interval i24 = numFactory.newInterval(true, INT_POS_TWO,
			false, INT_POS_FOUR, false);
	Interval i28 = numFactory.newInterval(true, INT_POS_TWO,
			false, INT_POS_EIGHT, false);
	Interval i48 = numFactory.newInterval(true, INT_POS_FOUR,
			false, INT_POS_EIGHT, false);
	Interval i_21 = numFactory.newInterval(true, INT_NEG_TWO,
			false, INT_POS_ONE, false);
	Interval i_22 = numFactory.newInterval(true, INT_NEG_TWO,
			false, INT_POS_TWO, false);
	Interval i_24 = numFactory.newInterval(true, INT_NEG_TWO,
			false, INT_POS_FOUR, false);
	
	IntervalValue iv12 = new IntervalValue(i12);
	IntervalValue iv24 = new IntervalValue(i24);
	IntervalValue iv28 = new IntervalValue(i28);
	IntervalValue iv48 = new IntervalValue(i48);
	IntervalValue iv_22 = new IntervalValue(i_22);
	IntervalValue iv_21 = new IntervalValue(i_21);
	IntervalValue iv_24 = new IntervalValue(i_24);
	
	IntervalValue ivres = new IntervalValue();

	
	@Test
	public void testPlus() {
		
		ivres = (IntervalValue) ivres.plus(iv12, iv12);		
		assertEquals(iv24.interval,ivres.interval);

	}

	@Test
	public void testMinus() {
		ivres = (IntervalValue) ivres.minus(iv24, iv12);
		assertEquals(numFactory.newInterval(true, INT_ZERO, false, INT_POS_THREE, false),ivres.interval);
	}
	
	@Test
	public void testMultiply() {
		ivres = (IntervalValue) ivres.multiply(iv24, iv12);
		assertEquals(iv28.interval,ivres.interval);
	}

	@Test
	public void testDivide() {
		ivres = (IntervalValue) ivres.divide(iv48, iv12);
		assertEquals(numFactory.newInterval(true, INT_ZERO, false, INT_POS_EIGHT, false),ivres.interval);
	}

	@Test
	public void testTop() {
		ivres = (IntervalValue) ivres.top();
		assertEquals(numFactory.universalIntegerInterval(), ivres.interval);
	}

	@Test
	public void testUnion() {
		ivres = (IntervalValue) ivres.union(iv_24, iv28);
		assertEquals(numFactory.newInterval(true, INT_NEG_TWO, false, INT_POS_EIGHT, false), ivres.interval);
		
		ivres = (IntervalValue) ivres.union(iv_21, iv24);
		assertEquals(numFactory.newInterval(true, INT_NEG_TWO, false, INT_POS_FOUR, false), ivres.interval);
	}
	
	@Ignore
	public void testSetValue() {
		fail("Not yet implemented");
	}
}
