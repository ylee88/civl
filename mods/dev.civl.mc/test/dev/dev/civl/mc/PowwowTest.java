package dev.civl.mc;

import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class PowwowTest {
	/* *************************** Static Fields *************************** */

	private static UserInterface ui = new UserInterface();

	private static File rootDir = new File(new File("examples"), "powwow");

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void loopAssert() {
		assertTrue(ui.run(VERIFY, filename("loopAssert.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
