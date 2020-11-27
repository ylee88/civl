package edu.udel.cis.vsl.civl.loopInvariants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class SlowLoopInvariantsWithAssigns2Test {
	private static File rootDir = new File(new File("examples"),
			"loop_invariants/");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */
	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void foVeOOS_max() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("foVeOOS/max/max.cvl")));
	}

	@Test
	public void foVeOOS_maxBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("foVeOOS/max/max-bad_assert.cvl")));
	}

	@Test
	public void foVeOOS_maxBadInvariant() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("foVeOOS/max/max-bad_invariant.cvl")));
	}

	@Test
	public void foVeOOS_duplets() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop=true",
				filename("foVeOOS/twoEqualElements/two_equal_elements.cvl")));
	}

	@Test
	public void foVeOOS_dupletsBadAssert() {
		assertFalse(
				ui.run("verify", TestConstants.QUIET, "-loop=true", filename(
						"foVeOOS/twoEqualElements/two_equal_elements-bad_assert.cvl")));
	}

	@Test
	public void foVeOOS_dupletsBadInvariant() {
		assertFalse(
				ui.run("verify", TestConstants.QUIET, "-loop=true", filename(
						"foVeOOS/twoEqualElements/two_equal_elements-bad_invariant.cvl")));
	}

	@Ignore // need why3
	public void JanLoop() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("/Jans_example/fixed_block/invariant.cvl")));
	}

	@Ignore // need why3
	public void JanLoopAbitraryBlock() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("/Jans_example/arbitrary_block/arbitrary_block.cvl")));
	}

	@Test
	public void JanLoopAbitraryBlockBadAssert() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"/Jans_example/arbitrary_block/arbitrary_block-bad_assert.cvl")));
	}

	@Test
	public void JanLoopAbitraryBlockBadInv() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"/Jans_example/arbitrary_block/arbitrary_block-bad_invariants1.cvl")));
	}
	@Test
	public void JanLoopAbitraryBlockBadInv2() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"/Jans_example/arbitrary_block/arbitrary_block-bad_invariants2.cvl")));
	}
	@Test
	public void JanLoopAbitraryBlockBadInv3() {
		assertFalse(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"/Jans_example/arbitrary_block/arbitrary_block-bad_invariants3.cvl")));
	}

	@Test
	public void adderCompare() {
		assertTrue(ui.run("compare", TestConstants.QUIET, "-loop",
				TestConstants.SPEC, "-loop", filename("/compare/adder_spec.c"),
				TestConstants.IMPL, "-loop", "-input_mpi_nprocs=3",
				filename("/compare/adder_par.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}
}
