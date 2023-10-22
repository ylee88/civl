package dev.civl.mc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static dev.civl.mc.TestConstants.QUIET;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class FairnessTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "fairness");

	private static UserInterface ui = new UserInterface();

	private static String TERM = "-checkTermination";

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void fair1() {
		assertFalse(
				ui.run("verify", QUIET, TERM, "-fair", filename("fair1.cvl")));
	}

	@Test
	public void fair2() {
		assertFalse(
				ui.run("verify", QUIET, TERM, "-fair", filename("fair2.cvl")));
	}

	@Test
	public void fair3() {
		assertFalse(
				ui.run("verify", QUIET, TERM, "-fair", filename("fair3.cvl")));
	}

	@Test
	public void fair4() {
		assertFalse(
				ui.run("verify", QUIET, TERM, "-fair", filename("fair4.cvl")));
	}

	@Test
	public void fair5() {
		assertFalse(
				ui.run("verify", QUIET, TERM, "-fair", filename("fair5.cvl")));
	}

	@Test
	public void unfair1() {
		assertTrue(ui.run("verify", QUIET, TERM, "-fair",
				filename("unfair1.cvl")));
	}

	@Test
	public void unfair2() {
		assertTrue(ui.run("verify", QUIET, TERM, "-fair",
				filename("unfair2.cvl")));
	}

	@Test
	public void unfair3() {
		assertTrue(ui.run("verify", QUIET, TERM, "-fair",
				filename("unfair3.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
