package edu.udel.cis.vsl.civl.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class LoopInvariantsWithAssignsPart2Test {
	private static File rootDir = new File(
			new File(new File("examples"), "loop_invariants"),
			"loop_assigns_given");

	private static File foVeOOSDir = new File(
			new File(new File("examples"), "loop_invariants"), "foVeOOS");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */
	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	private static String foVeOOSFilename(String folder, String name) {
		return new File(new File(foVeOOSDir, folder), name).getPath();
	}

	@Test
	public void foVeOOS_max() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("max", "max.cvl")));
	}

	@Test
	public void foVeOOS_maxBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("max", "max-bad_assert.cvl")));
	}

	@Test
	public void foVeOOS_maxBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("max", "max-bad_invariant.cvl")));
	}

	@Test
	public void foVeOOS_duplets() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("twoEqualElements", "two_equal_elements.cvl")));
	}

	@Test
	public void foVeOOS_dupletsBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("twoEqualElements",
						"two_equal_elements-bad_assert.cvl")));
	}

	@Test
	public void foVeOOS_dupletsBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				foVeOOSFilename("twoEqualElements",
						"two_equal_elements-bad_invariant.cvl")));
	}

	@Test
	public void arrayTwoSection() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("twoSectionArray.cvl")));
	}

	@Test
	public void arrayTwoSectionBad() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("twoSectionArray-bad.cvl")));
	}

	@Test
	public void arrayTwoSection2() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("twoSectionArray2.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
