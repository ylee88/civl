package dev.civl.mc.loopInvariants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class SlowLoopInvariantsWithAssignsTest {
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

	@Ignore
	public void arrayEqualsBug() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEquals-bug.cvl")));
	}

	@Ignore
	public void arrayEqualsEarlyReturn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEquals_early_return.cvl")));
	}

	@Ignore
	public void arrayEqualsNoReturnBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEqualsNoReturn-bad_assert.cvl")));
	}

	@Ignore // experimental permutation predicates need why3
	public void insertionSort() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("insertSort.cvl")));
	}

	@Test
	public void insertionSortBadAssert() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("insertSort-bad_assert.cvl")));
	}

	@Test
	public void insertionSortBadAssert2() {
		assertFalse(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("insertSort-bad_assert2.cvl")));
	}

	@Ignore // ignore, experimental permutation predicate for why3 only
	public void selectSort() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET,
				filename("selectSort.cvl")));
	}

	@Test
	public void selectSortBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				TestConstants.errorBound(3),
				filename("selectSort-bad_assert.cvl")));
	}

	@Test
	public void selectSortBadInvariants() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("selectSort-bad_invariants.cvl")));
	}

	@Ignore // needs why3
	public void relaxedPrefix() {
		assertTrue(ui.run("verify", "-loop", TestConstants.QUIET, filename(
				"verifyThisUB", "relaxedPrefix", "relaxedPrefix_loop.cvl")));
	}

	@Ignore // need why3
	public void sort() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("verifyThisUB", "longestRepeatedSubstring",
						"sort_deductive.cvl")));
	}

	@Test
	public void sortBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("verifyThisUB", "longestRepeatedSubstring",
						"sort-bad_invariant.cvl")));
	}

	@Test
	public void sortBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("verifyThisUB", "longestRepeatedSubstring",
						"sort-bad_assert.cvl")));
	}

	@Test
	public void lrsBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("verifyThisUB", "longestRepeatedSubstring",
						"lrs-bad_invariant.cvl")));
	}

	@Test
	public void lrsBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("verifyThisUB", "longestRepeatedSubstring",
						"lrs-bad_assert.cvl")));
	}

	@Ignore // need why3
	public void JanLoop() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("Jans_example", "fixed_block", "invariant.cvl")));
	}

	@Ignore // need why3
	public void JanLoopAbitraryBlock() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"Jans_example", "fixed_block", "arbitrary_block.cvl")));
	}

	@Test
	public void JanLoopAbitraryBlockBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("Jans_example", "arbitrary_block",
						"arbitrary_block-bad_assert.cvl")));
	}

	@Test
	public void JanLoopAbitraryBlockBadInv() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("Jans_example", "arbitrary_block",
						"arbitrary_block-bad_invariants1.cvl")));
	}
	@Test
	public void JanLoopAbitraryBlockBadInv2() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("Jans_example", "arbitrary_block",
						"arbitrary_block-bad_invariants2.cvl")));
	}
	@Test
	public void JanLoopAbitraryBlockBadInv3() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("Jans_example", "arbitrary_block",
						"arbitrary_block-bad_invariants3.cvl")));
	}

	@Test
	public void adderCompare() {
		assertTrue(ui.run("compare", TestConstants.QUIET, "-loop",
				TestConstants.SPEC, "-loop",
				filename("compare", "adder_spec.c"), TestConstants.IMPL,
				"-loop", "-input_mpi_nprocs=3",
				filename("compare", "adder_par.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
