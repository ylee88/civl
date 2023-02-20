package dev.civl.mc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import dev.civl.mc.run.IF.UserInterface;

public class QuantifierTest {
	private static File rootDir = new File(new File("examples"),
			"experimental");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void quantifier() {
		assertTrue(ui.run("verify", "-showQueries", "-showProverQueries",
				filename("quantifier.c")));
	}

	@Test
	public void quantifierSARLBug() {
		assertTrue(ui.run("verify", "-showQueries", "-showProverQueries",
				filename("quantifierSARLBug.cvl")));
	}
}
