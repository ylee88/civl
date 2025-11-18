package dev.civl.mc.transform;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class LoopInvariantsNoAssignsBigTest {

	private static File rootDir = new File(
			new File(new File("examples"), "loop_invariants"),
			"loop_assigns_gen");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void arrayEquals() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEquals.cvl")));
	}

	@Test
	public void arrayEquals2() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayEquals2.cvl")));
	}

	@Test
	public void arrayEqualsNoReturn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayEqualsNoReturn.cvl")));
	}

	@Test
	public void arrayZeroes1d() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes1d.cvl")));
	}

	@Test
	public void arrayZeroes2dColumn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes2d_column.cvl")));
	}

	@Test
	public void arrayZeroes2d2Columns() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes2d_2columns.cvl")));
	}

	@Test
	public void foVeOOS_max() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("max2.cvl")));
	}

	@Test
	public void summation() {
		assertTrue(ui.run("verify", "-loop=true", TestConstants.QUIET,
				filename("summation.cvl")));
	}

	@Test
	public void selectSort() {
		assertTrue(ui.run("verify", "-loop=true", TestConstants.QUIET,
				filename("selectSort.cvl")));
	}

	@Test
	public void twoLoopsUnreachable() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("twoLoops.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
