package edu.udel.cis.vsl.civl.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class LoopInvariantsWithAssignsPart1Test {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

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

	private static String filename(String folder1, String folder2,
			String name) {
		return new File(new File(new File(rootDir, folder1), folder2), name)
				.getPath();
	}

	@Test
	public void arrayEquals() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEquals.cvl")));
	}

	@Test
	public void arrayEquals2() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEquals2.cvl")));
	}

	@Test
	public void arrayEqualsNoReturnBadImpl() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEqualsNoReturn-bad_impl.cvl")));
	}

	@Test
	public void add() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("add.cvl")));
	}

	@Test
	public void arrayZeroes1d() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes1d.cvl")));
	}

	@Test
	public void arrayZeroes1dBad() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes1d-bad.cvl")));
	}

	@Test
	public void arrayZeroes2d() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("arrayZeroes2d.cvl")));
	}

	@Test
	public void arrayZeroes2dColumn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d_column.cvl")));
	}

	@Test
	public void arrayZeroes2d2Columns() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d_2columns.cvl")));
	}

	@Test
	public void arrayZeroes2dColumnPreserve() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d_2columns_preserve.cvl")));
	}

	@Test
	public void arrayZeroes2d2ColumnsPreserve() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("arrayZeroes2d_2columns_preserve.cvl")));
	}

	@Test
	public void arrayZeroes2d2ColumnsPreserveBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("arrayZeroes2d_2columns_preserve-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2dColumnBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d_column-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2dColumnBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d_column-bad_invariant.cvl")));
	}

	@Test
	public void arrayZeroes2dBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d-bad_assert.cvl")));
	}

	@Test
	public void arrayZeroes2dBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayZeroes2d-bad_invariants.cvl")));
	}

	@Test
	public void JanLoop1st() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET, filename(
				"Jans_example", "fixed_block", "invariant_1st_loop.cvl")));
	}

	@Test
	public void JanLoop1stBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example", "fixed_block",
						"invariant_1st_loop-bad_assert.cvl")));
	}

	@Test
	public void JanLoop1stBadAssigns() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example", "fixed_block",
						"invariant_1st_loop-bad_assigns.cvl")));
	}

	@Test
	public void JanLoop2nd() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET, filename(
				"Jans_example", "fixed_block/invariant_2nd_loop.cvl")));
	}

	@Test
	public void JanLoop2ndBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example",
						"fixed_block/invariant_2nd_loop-bad_assert.cvl")));
	}

	@Test
	public void JanLoop2ndBadAssigns() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example",
						"fixed_block/invariant_2nd_loop-bad_assigns.cvl")));
	}

	@Test
	public void JanLoop3rd() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET, filename(
				"Jans_example", "fixed_block/invariant_3rd_loop.cvl")));
	}

	@Test
	public void JanLoop3rdBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example",
						"fixed_block/invariant_3rd_loop-bad_assert.cvl")));
	}

	@Test
	public void JanLoop3rdBadAssigns() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("Jans_example",
						"fixed_block/invariant_3rd_loop-bad_assigns.cvl")));
	}

	@Test
	public void max() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("max.cvl")));
	}

	@Test
	public void maxBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("max-bad_assert.cvl")));
	}

	@Test
	public void maxBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("max-bad_invariants.cvl")));
	}

	@Test
	public void relaxedPrefixWeak() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("verifyThisUB",
						"relaxedPrefix/relaxedPrefix_loop-bad_weak.cvl")));
	}

	@Test
	public void relaxedPrefixWeak2() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("verifyThisUB",
						"relaxedPrefix/relaxedPrefix_loop-bad_weak2.cvl")));
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

	@Test
	public void pairInsertionBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"verifyThisUB",
				"pairInsertSort/pairInsertSort_partial-bad_assert.cvl")));
	}

	@Test
	public void pairInsertion() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"verifyThisUB", "pairInsertSort/pairInsertSort_partial.cvl")));
	}

	@Test
	public void summation() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("summation.cvl")));
	}

	@Test
	public void summationBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("summation-bad_invariant.cvl")));
	}

	@Test
	public void summationBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("summation-bad_assert.cvl")));
	}

	@Test
	public void twoLoopsUnreachable() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("twoLoops.cvl")));
	}

	@Test
	public void twoLoopsBad() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("twoLoops2.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
