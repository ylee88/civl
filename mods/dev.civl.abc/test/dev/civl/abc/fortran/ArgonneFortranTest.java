package dev.civl.abc.fortran;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;

public class ArgonneFortranTest {
	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(
			new File(new File("examples"), "fortran"), "argonne");

	private static List<String> codes = Arrays.asList("prune", "sef");

	private void check(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}

	@SuppressWarnings("unused")
	private void print(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(true);
		task.setPrettyPrint(true);
		ABCExecutor.execute(task);
	}

	@Test
	public void loops_tracediff() throws ABCException {
		check("LOOPS/tracediff.f90");
	}

	@Test
	public void loops_a() throws ABCException {
		check("LOOPS/a.f90");
	}

	@Test
	public void loops_b() throws ABCException {
		check("LOOPS/b.f90");
	}

	@Test
	public void loops_c() throws ABCException {
		check("LOOPS/c.f");
	}
}
