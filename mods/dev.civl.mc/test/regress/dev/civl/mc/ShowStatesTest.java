package dev.civl.mc;

import static org.junit.Assert.assertTrue;
import static dev.civl.mc.TestConstants.QUIET;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

/**
 * This test class contains test methods for the pretty-printing of states.
 * 
 * @author Manchun Zheng
 *
 */
public class ShowStatesTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "showStates");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void structsArray() {
		assertTrue(ui.run("verify", "-showSavedStates",
				QUIET, filename("structsArray.cvl")));
	}

	@Test
	public void symbolicArrayWrite() {
		assertTrue(ui.run("verify", "-showSavedStates",
				QUIET, filename("symbolicArrayWrite.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
