package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class BackendBigTest {
	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "backend");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void pathCondition() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("pathCondition.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
