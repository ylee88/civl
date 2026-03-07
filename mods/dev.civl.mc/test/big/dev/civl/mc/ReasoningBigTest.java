package dev.civl.mc;

import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class ReasoningBigTest {

	/* *************************** Static Fields *************************** */

	private static File cgDir = new File(new File("examples"), "cg");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String cgfilename(String name) {
		return new File(cgDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void cg5Probabilistic() {
		assertTrue(ui.run(VERIFY, "-inputN=5", TestConstants.ENABLE_PROB,
				TestConstants.NO_CHECK_DIVISION_BY_ZERO, cgfilename("cg.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		cgDir = null;
	}
}
