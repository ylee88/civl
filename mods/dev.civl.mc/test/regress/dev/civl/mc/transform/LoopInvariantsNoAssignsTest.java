package dev.civl.mc.transform;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class LoopInvariantsNoAssignsTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	private static File rootDir = new File(
			new File(new File("examples"), "loop_invariants"),
			"loop_assigns_gen");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void arrayEqualsNoReturnBadImpl() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayEqualsNoReturn-bad_impl.cvl")));
	}

	@Test
	public void arrayZeroes1dBad() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes1d-bad.cvl")));
	}

	@Test
	public void arrayZeroes2dColumnBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes2d_column-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2dColumnBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("arrayZeroes2d_column-bad_invariant.cvl")));
	}

	@Test
	public void arrayZeroes2d2ColumnsPreserveBadAssert() {
		assertFalse(ui.run("verify", "-loop=true", TestConstants.QUIET,
				filename("arrayZeroes2d_2columns_preserve-bad_assert.cvl")));
	}

	@Test
	public void maxBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("max-bad_assert.cvl")));
	}

	@Test
	public void maxBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("max-bad_invariants.cvl")));
	}

	@Test
	public void summationBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("summation-bad_invariant.cvl")));
	}

	@Test
	public void summationBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("summation-bad_assert.cvl")));
	}

	@Test
	public void selectSortBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				TestConstants.errorBound(3),
				filename("selectSort-bad_assert.cvl")));
	}

	@Test
	public void selectSortBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("selectSort-bad_invariants.cvl")));
	}

	@Test
	public void selectSortBadThink() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("selectSort-bad_think.cvl")));
	}

	@Test
	public void twoLoopsBad() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("twoLoops2.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
