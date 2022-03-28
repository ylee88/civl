package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.util.IF.SeqSet;

public class SeqSetTest {

	private static PrintStream out = System.out;

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
	public void equals() {
		SeqSet ss1 = new SeqSet();
		assertEquals(ss1, ss1);
		SeqSet ss2 = new SeqSet();
		assertEquals(ss1, ss2);
		ss1.add(3, 7);
		assertNotEquals(ss1, ss2);
		assertEquals(ss1, ss1);
		ss2.add(3, 7);
		assertEquals(ss1, ss2);
		ss1.add(1);
		ss2.add(1);
		assertEquals(ss1, ss2);
	}

	@Test
	public void add() {
		SeqSet ss = new SeqSet();
		boolean result;

		out.println(ss);
		assertTrue(ss.isEmpty());
		assertFalse(ss.contains());
		assertFalse(ss.contains(1));
		result = ss.add(3, 7);
		out.println(ss);
		assertTrue(result);
		assertFalse(ss.isEmpty());
		assertFalse(ss.contains());
		assertFalse(ss.contains(1));
		assertFalse(ss.contains(3));
		assertTrue(ss.contains(3, 7));
		assertTrue(ss.contains(3, 7, 1));
		assertFalse(ss.contains(3, 4));
		result = ss.add(2);
		out.println(ss);
		assertTrue(result);
		assertTrue(ss.contains(2));
		assertTrue(ss.contains(2, 3));
		assertTrue(ss.contains(3, 7));
		result = ss.add(3, 6);
		out.println(ss);
		assertTrue(result);
		assertTrue(ss.contains(3, 6));
		assertFalse(ss.contains(3));
		result = ss.add(3);
		out.println(ss);
		assertTrue(result);
		assertTrue(ss.contains(3));
		assertTrue(ss.contains(3, 17));
		result = ss.add(3, 7, 8);
		out.println(ss);
		assertFalse(result);
		ss.clear();
		out.println(ss);
		assertTrue(ss.isEmpty());
		result = ss.add(3, 7);
		out.println(ss);
		assertTrue(result);
		assertFalse(ss.isEmpty());
		result = ss.add(2, 2);
		out.println(ss);
		assertTrue(result);
		result = ss.add(3);
		out.println(ss);
		assertTrue(result);
		result = ss.add(2);
		out.println(ss);
		assertTrue(result);
		ss.clear();
		assertTrue(ss.isEmpty());
		out.println(ss);
	}

	@Test
	public void addAll() {
		out.println("addAll...");
		SeqSet ss1 = new SeqSet(), ss2 = new SeqSet();

		ss2.add(7);
		out.println("ss1 = " + ss1);
		out.println("ss2 = " + ss2);
		out.println("Adding ss2 to ss1...");
		ss1.addAll(ss2);
		out.println("ss1 = " + ss1);
		assertEquals(ss1, ss2);
	}

	@Test
	public void contains() {
		SeqSet ss1 = new SeqSet();

		ss1.add(1, 3, 5);
		ss1.add(2);
		assertTrue(ss1.contains(2));
		assertTrue(ss1.contains(2, 3));
		assertFalse(ss1.contains(1));
		assertFalse(ss1.contains(7));
		assertFalse(ss1.contains(1, 3));
		assertTrue(ss1.contains(1, 3, 5));
		assertTrue(ss1.contains(1, 3, 5, 6));
	}

	@Test
	public void containsAll() {
		SeqSet ss1 = new SeqSet(), ss2 = new SeqSet(), ss3 = new SeqSet();

		ss1.add(1, 3, 5);
		ss1.add(2);
		ss2.add(1, 3);
		ss2.add(2);
		ss3.add(1);
		ss3.add(2, 3);
		assertTrue(ss1.containsAll(ss1));
		assertTrue(ss2.containsAll(ss1));
		assertFalse(ss1.containsAll(ss2));
		assertTrue(ss2.containsAll(ss2));
		assertFalse(ss1.containsAll(ss3));
		assertFalse(ss3.containsAll(ss1));
	}

	@Test
	public void disjoint() {
		SeqSet ss1 = new SeqSet(), ss2 = new SeqSet(), ss3 = new SeqSet();

		ss1.add(1, 3, 5);
		ss1.add(2);
		ss2.add(1, 3, 6);
		ss2.add(7);
		assertFalse(ss1.disjoint(ss1));
		assertTrue(ss1.disjoint(ss2));
		assertTrue(ss2.disjoint(ss1));
		assertFalse(ss2.disjoint(ss2));
		ss3.add(1, 4);
		ss3.add(2, 4);
		assertFalse(ss1.disjoint(ss3));
		assertFalse(ss3.disjoint(ss1));
	}

	@Test
	public void getLeaves() {
		SeqSet ss = new SeqSet();

		ss.add(1, 3);
		ss.add(1, 4, 6);

		List<int[]> leaves = ss.getLeaves();

		assertEquals(2, leaves.size());
		
		int[] l0 = leaves.get(0), l1 = leaves.get(1);
		
		if (l0.length != 2) {
			int[] tmp = l0;
			l0 = l1;
			l1 = tmp;
		}
		assertArrayEquals(new int[]{1, 3}, l0);
		assertArrayEquals(new int[]{1, 4, 6}, l1);
	}

}
