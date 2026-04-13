package dev.civl.mc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class PthreadTransformerTest {
	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "experimental");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void pthreadTransformerTest() {
		assertTrue(ui.run("verify -showProgram", filename("pthreadTransformerTest.cvl")));
	}
}
