package dev.civl.mc;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class PORCommTest {
	private static File rootDir = new File(new File("examples"), "concurrency");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void porCommBug() {
		assertFalse(ui.run("verify", filename("porCommBug.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}

}
