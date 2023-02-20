package dev.civl.abc.fortran;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;

public class FortranFlash5Test {
	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static final File ROOT = new File("examples/fortran/flash");
	private static final File DIR_EOS_GETDATA_MIN = new File(ROOT,
			"eos_getData_min");

	private static List<String> codes = Arrays.asList("prune", "sef");

	private void check(File dir, String fn) throws ABCException {
		File file = new File(dir, fn);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}

	@Test
	public void f5_Eos_getData_min_driver() throws ABCException {
		check(DIR_EOS_GETDATA_MIN, "driver.F90");
	}

	@Ignore
	public void f5_Eos_getData_min() throws ABCException {
		check(DIR_EOS_GETDATA_MIN, "Eos_getData_old.F90");
	}
}
