package dev.civl.abc.fortran;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;

public class SimpleTest {
	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(
			new File(new File("examples"), "fortran"), "simple");

	private static List<String> codes = Arrays.asList("prune", "sef");

	private void check(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot + ".f");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}

	private void print(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot + ".f");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		if (debug) {
			task.setVerbose(true);
			task.setPrettyPrint(true);
		}
		ABCExecutor.execute(task);
	}

	@Test
	public void fortran_HelloWorld() throws ABCException {
		check("HelloWorld");
	}

	@Test
	public void fortran_Loops() throws ABCException {
		check("loops");
	}

	@Test
	public void simple_array() throws ABCException {
		print("simple_array");
	}

}
