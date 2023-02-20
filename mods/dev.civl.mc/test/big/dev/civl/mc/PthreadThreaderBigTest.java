package dev.civl.mc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class PthreadThreaderBigTest {
	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples", "pthread"),
			"threader");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void qrcu_true() throws ABCException {
		assertTrue(ui.run("verify", "-svcomp16", filename("qrcu_true.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
