package dev.civl.mc;
import static dev.civl.mc.TestConstants.QUIET;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class ArrayLambdaTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "arrayLambda");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */
	@Test
	public void arrayLambda() {
		assertTrue(ui.run("verify", QUIET, filename("arrayLambda.cvl")));
	}

	@Test
	public void arrayLambda2() {
		assertTrue(ui.run("verify", QUIET, filename("arrayLambda2.cvl")));
	}

	@Test
	public void arrayLambda3() {
		assertTrue(ui.run("verify", QUIET, filename("arrayLambda3.cvl")));
	}

	// lhs and rhs have incompatible dynamic types:
	@Test
	public void arrayLambdaAssignmentTypeInCompatible() {
		assertFalse(ui.run("verify", QUIET,
				filename("arrayLambdaAssignTypesBad.cvl")));
	}
}
