package dev.civl.abc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.main.UnitTask;

/**
 * Tests linkage issues: internal, external, or "none".
 * 
 * @author siegel
 * 
 */
public class CIVLLinkageTest {

	public final static boolean debug = false;

	private File root = new File(new File("examples"), "link");

	private boolean compileAndLink(String[] filenames,
			File[] systemIncludePaths) throws ABCException, IOException {
		int numUnits = filenames.length;
		UnitTask[] unitTasks = new UnitTask[numUnits];

		for (int i = 0; i < numUnits; i++) {
			UnitTask unitTask = new UnitTask(
					new File[] { new File(root, filenames[i]) });

			unitTask.setSystemIncludes(systemIncludePaths);
			unitTasks[i] = unitTask;
		}

		TranslationTask task = new TranslationTask(unitTasks);

		ABCExecutor executor = new ABCExecutor(task);

		executor.execute();

		if (debug)
			executor.getProgram().prettyPrint(System.out);
		return true;
	}

	// TODO: incorrect use of / as file separator.
	// change to use platform-independent way

	@Test
	public void barrier() throws ABCException, IOException {
		assertTrue(compileAndLink(
				new String[] { "barrier/barrier.cvl",
						"barrier/concurrency.cvl" },
				new File[] { new File(root, "barrier") }));
	}

	@Test
	public void messageUnpack() throws ABCException, IOException {
		assertTrue(compileAndLink(
				new String[] { "comm/messageUnpack.cvl", "comm/comm.cvl" },
				new File[] { new File(root, "comm") }));
	}

}
