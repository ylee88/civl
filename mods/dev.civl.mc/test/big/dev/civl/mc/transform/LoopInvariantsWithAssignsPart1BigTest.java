package dev.civl.mc.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class LoopInvariantsWithAssignsPart1BigTest {

	private static File rootDir = new File(new File("examples"),
			"loop_invariants");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */
	private static String filename(String name) {
		return new File(new File(rootDir, "loop_assigns_given"), name)
				.getPath();
	}

	private static String filename(String folder, String name) {
		return new File(new File(rootDir, folder), name).getPath();
	}

	@Test
	public void max() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("max.cvl")));
	}

	@Test
	public void relaxedPrefixBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("verifyThisUB",
						"relaxedPrefix/relaxedPrefix_loop-bad_assert.cvl")));
	}

	@Test
	public void relaxedPrefixWeak4Assert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET, filename(
				"verifyThisUB",
				"relaxedPrefix/relaxedPrefix_loop-bad_weak4assert.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
