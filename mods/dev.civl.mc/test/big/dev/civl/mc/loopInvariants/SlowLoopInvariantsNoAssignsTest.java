package dev.civl.mc.loopInvariants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class SlowLoopInvariantsNoAssignsTest {

	private static File rootDir = new File(new File("examples"),
			"loop_invariants");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String folder, String name) {
		return new File(new File(rootDir, folder), name).getPath();
	}

	@Test
	public void arrayEquals() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "arrayEquals.cvl")));
	}

	@Ignore
	public void arrayEqualsBug() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "arrayEquals-bug.cvl")));
	}

	@Ignore
	public void arrayEqualsEarlyReturn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "arrayEquals_early_return.cvl")));
	}

	@Ignore
	public void arrayEqualsNoReturnBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen",
						"arrayEqualsNoReturn-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2d() {
		assertTrue(ui.run("verify", "-loop=true", TestConstants.QUIET,
				filename("loop_assigns_gen", "arrayZeroes2d.cvl")));
	}

	@Ignore
	public void arrayZeroes2dColumnPreserve() {
		assertTrue(ui.run("verify", "-loop=true", TestConstants.QUIET, filename(
				"loop_assigns_gen", "arrayZeroes2d_2columns_preserve.cvl")));
	}

	@Ignore
	public void arrayZeroes2d2ColumnsPreserve() {
		assertTrue(ui.run("verify", "-loop=true", TestConstants.QUIET, filename(
				"loop_assigns_gen", "arrayZeroes2d_2columns_preserve.cvl")));
	}

	@Test
	public void arrayZeroes2dBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "arrayZeroes2d-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2dBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen",
						"arrayZeroes2d-bad_invariants.cvl")));
	}
	@Ignore
	public void max() {
		assertTrue(ui.run("verify", TestConstants.QUIET,
				filename("loop_assigns_gen", "max.cvl")));
	}

	@Ignore // why3 needed
	public void relaxPrefix() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "relaxedPrefix_loop.cvl")));
	}

	@Ignore // Need why3 with timeout > 15 seconds
	public void lcp2() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("loop_assigns_gen", "lcp2.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
