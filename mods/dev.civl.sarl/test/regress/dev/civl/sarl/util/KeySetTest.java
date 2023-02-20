package dev.civl.sarl.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeySetTest {

	static Comparator<Integer> intComparator = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	};

	static class IntSetFactory extends SetFactory<Integer> {
		public IntSetFactory() {
			super(intComparator);
		}

		@Override
		protected Integer[] newSet(int size) {
			return new Integer[size];
		}
	}

	static SetFactory<Integer> fac = new IntSetFactory();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void put1() {
		Integer[] s1 = fac.emptySet();
		assertEquals(0, s1.length);
		Integer[] s2 = fac.put(s1, 2);
		assertArrayEquals(new Integer[] { 2 }, s2);
		Integer[] s3 = fac.put(s2, 0);
		assertArrayEquals(new Integer[] { 0, 2 }, s3);
		Integer[] s4 = fac.put(s3, 1);
		assertArrayEquals(new Integer[] { 0, 1, 2 }, s4);
	}

	@Test
	public void intersection1() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] { 2, 3, 4 };
		Integer[] s3 = new Integer[] { 3, 4, 5 };
		List<Integer[]> sets = Arrays.asList(s1, s2, s3);
		Integer[] result = fac.intersection(sets);

		assertArrayEquals(new Integer[] { 3 }, result);
	}

	@Test
	public void factor1() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] { 2, 3, 4 };
		Integer[] s3 = new Integer[] { 3, 4, 5 };
		Integer[][] sets = new Integer[][] { s1, s2, s3 };
		Integer[] result = fac.factor(sets);

		assertArrayEquals(new Integer[] { 3 }, result);
		assertArrayEquals(new Integer[] { 1, 5 }, sets[0]);
		assertArrayEquals(new Integer[] { 2, 4 }, sets[1]);
		assertArrayEquals(new Integer[] { 4, 5 }, sets[2]);
	}

	@Test
	public void intersectionNoSets() {
		List<Integer[]> sets = Arrays.asList();
		Integer[] result = fac.intersection(sets);
		assertArrayEquals(new Integer[] {}, result);
	}

	@Test
	public void factorNoSets() {
		Integer[][] sets = new Integer[0][];
		Integer[] result = fac.factor(sets);
		assertArrayEquals(new Integer[] {}, result);
		assertArrayEquals(new Integer[0][], sets);
	}

	@Test
	public void intersectionOneSet() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		List<Integer[]> sets = new LinkedList<>();
		sets.add(s1);
		Integer[] result = fac.intersection(sets);
		assertArrayEquals(s1, result);
	}

	@Test
	public void factorOneSet() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[][] sets = new Integer[1][];
		sets[0] = s1;
		Integer[] result = fac.factor(sets);
		assertArrayEquals(s1, result);
		Integer[][] expected = new Integer[1][];
		expected[0] = new Integer[0];
		assertArrayEquals(expected, sets);
	}

	@Test
	public void intersectionIsEmpty() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] { 2, 4, 6 };
		List<Integer[]> sets = Arrays.asList(s1, s2);
		Integer[] result = fac.intersection(sets);

		assertArrayEquals(new Integer[] {}, result);
	}

	@Test
	public void factorIsEmpty() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] { 2, 4, 6 };
		Integer[][] sets = new Integer[][] { s1, s2 };
		Integer[] result = fac.factor(sets);

		assertArrayEquals(new Integer[] {}, result);
		assertArrayEquals(new Integer[][] { { 1, 3, 5 }, { 2, 4, 6 } }, sets);
	}

	@Test
	public void intersectionIsFull() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		List<Integer[]> sets = Arrays.asList(s1, s1, s1);
		Integer[] result = fac.intersection(sets);

		assertArrayEquals(s1, result);
	}

	@Test
	public void factorIsFull() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[][] sets = new Integer[][] { s1, s1, s1 };
		Integer[] result = fac.factor(sets);

		assertArrayEquals(s1, result);
		assertArrayEquals(new Integer[][] { {}, {}, {} }, sets);
	}

	@Test
	public void intersectionOneSetEmpty() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] {};
		List<Integer[]> sets = Arrays.asList(s1, s2);
		Integer[] result = fac.intersection(sets);

		assertArrayEquals(new Integer[] {}, result);
	}

	@Test
	public void factorOneSetEmpty() {
		Integer[] s1 = new Integer[] { 1, 3, 5 };
		Integer[] s2 = new Integer[] {};
		Integer[][] sets = new Integer[][] { s1, s2 };
		Integer[] result = fac.factor(sets);

		assertArrayEquals(new Integer[] {}, result);
		assertArrayEquals(new Integer[][] { s1, s2 }, sets);
	}

}
