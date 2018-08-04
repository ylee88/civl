package edu.udel.cis.vsl.civl.reasoning;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class SlowReasoningTest {
	private static File loopInvDir = new File(new File("examples"),
			"loop_invariants/");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */
	private static String filename(File dir, String name) {
		return new File(dir, name).getPath();
	}

	@Test
	public void lemmasOfArbitraryBlock() {
		assertTrue(ui.run("verify", TestConstants.QUIET, filename(loopInvDir,
				"Jans_example/arbitrary_block/lemmas.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		loopInvDir = null;
		System.gc();
	}
}
