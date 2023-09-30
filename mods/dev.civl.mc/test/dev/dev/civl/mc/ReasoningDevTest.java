package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class ReasoningDevTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "reasoning");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String reasoningfilename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void stepedUniversal() {
		assertTrue(ui.run(VERIFY, QUIET,
				reasoningfilename("stepedUniversal.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
