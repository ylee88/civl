package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class LoopInvariantsTest {
	private static File rootDir = new File(new File("examples"),
			"loop_invariants/loop_assigns_given");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}
	
	@Ignore // need why3
	public void lrs() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"../verifyThisUB/longestRepeatedSubstring/lrs_deductive.cvl ")));
	}

	@Ignore // requires why3 with TIMEOUT > 10 seconds
	public void lcp2() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop", filename(
				"../verifyThisUB/longestRepeatedSubstring/lcp2.cvl ")));
	}

	@Ignore // need return statement to work
	public void arrayEqualsNoReturn() {
		assertTrue(ui.run("verify", TestConstants.QUIET, "-loop",
				filename("arrayEqualsNoReturn.cvl")));
	}
}
