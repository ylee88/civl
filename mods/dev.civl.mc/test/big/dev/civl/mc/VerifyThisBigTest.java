package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class VerifyThisBigTest {

	private static File rootDir = new File(new File("examples"), "verifyThis");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void pairInsertionSort() {
		assertTrue(ui.run(VERIFY, QUIET,
				// "-showTransitions",
				// "-showStates",
				filename("pairInsertSort.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
