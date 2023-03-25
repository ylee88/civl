package dev.civl.mc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class SvcompTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples", "pthread"),
			"svcomp");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void intPointer() throws ABCException {
		assertTrue(ui.run("verify", "-svcomp16", TestConstants.QUIET,
				filename("intPointer.c")));
	}

	@Ignore
	public void thread_local() throws ABCException {
		assertTrue(ui.run("verify", "-svcomp16", TestConstants.QUIET,
				filename("threadLocal.c")));
	}

	@Ignore
	public void threadLocal() {
		assertTrue(ui.run("verify", "-svcomp16", TestConstants.QUIET,
				filename("threadLocal.c")));
	}

	@Test
	public void assume_with_disjuncts() {
		assertFalse(ui.run("verify  -svcomp16", TestConstants.QUIET,
				filename("assume_with_disjuncts.cvl")));
	}
}
