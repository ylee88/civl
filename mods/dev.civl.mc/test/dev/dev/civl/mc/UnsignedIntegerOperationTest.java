package dev.civl.mc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class UnsignedIntegerOperationTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "uint");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void prePostIncrement() {
		assertTrue(ui.run("show  -showProgram", filename("prePostIncrement.c")));
	}
	
	@Test
	public void roundoff() {
		assertTrue(ui.run("show  -showProgram", filename("roundoff.c")));
	}
}
