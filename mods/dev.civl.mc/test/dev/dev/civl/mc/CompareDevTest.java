package dev.civl.mc;

import static dev.civl.mc.TestConstants.COMPARE;
import static dev.civl.mc.TestConstants.IMPL;
import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.SPEC;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class CompareDevTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "compare");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String subfolder, String name) {
		return new File(new File(rootDir, subfolder), name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void vector() {
		ui.run("verify", filename("sliced_vector", "sliced_vector_addin.c"));
	}

	@Test
	public void queue() {
		ui.run(COMPARE, QUIET, SPEC, filename("queue", "driver.cvl"),
				filename("queue", "queue_two_lock.c"), IMPL,
				filename("queue", "driver.cvl"),
				filename("queue", "queue_non_blocking.c"));
	}

	@Test
	public void unableExtractInt() {
		ui.run("verify", QUIET, filename("petscBad", "ex2Driver.c"),
				filename("petscBad", "ex2a.c"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
