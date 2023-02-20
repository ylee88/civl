package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.REPLAY;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class ReplayTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	private static File rootDir = new File("examples");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	// @Ignore
	@Test
	public void locksBad() throws ABCException {
		assertFalse(
				ui.run(VERIFY, QUIET, filename("concurrency/locksBad.cvl")));

		assertFalse(ui.run(REPLAY, "-showStates", QUIET,
				filename("concurrency/locksBad.cvl")));
	}
}
