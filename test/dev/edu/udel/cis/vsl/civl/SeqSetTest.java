package edu.udel.cis.vsl.civl;

import java.io.PrintStream;

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
	public void test() {
		SeqSet ss = new SeqSet();

		out.println(ss);
		ss.add(3, 7);
		out.println(ss);
		ss.add(2);
		out.println(ss);
		ss.add(3, 6);
		out.println(ss);
		ss.add(3);
		out.println(ss);
		ss.add(3,7,8);
		out.println(ss);
		ss.clear();
		out.println(ss);
		ss.add(3,7);
		out.println(ss);
		ss.add(2,2);
		out.println(ss);
		ss.add(3);
		out.println(ss);
		ss.add(2);
		out.println(ss);
		ss.clear();
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
	}

}
