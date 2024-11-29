package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class CastTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "cast");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private void check(String name) {
		assertTrue(ui.run(VERIFY, QUIET, filename(name)));
	}

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void structDown1() {
		check("structDown1.cvl");
	}

	@Test
	public void structUp1() {
		check("structUp1.cvl");
	}

	@Test
	public void structDown2() {
		check("structDown2.cvl");
	}

	@Test
	public void structUp2() {
		check("structUp2.cvl");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
