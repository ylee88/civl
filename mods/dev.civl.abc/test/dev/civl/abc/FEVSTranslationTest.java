package dev.civl.abc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;

/**
 * Checks programs from the FEVS suite. These use libraries such as MPI and GD.
 * 
 * @author siegel
 * 
 */
public class FEVSTranslationTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "fevs");

	// removed "sef" from below since FEVS contains some function calls
	// as arguments in function calls...
	private static List<String> codes = Arrays.asList("prune", "sef");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	private void check(String directory, String filenameRoot)
			throws ABCException, IOException {
		File file = new File(new File(root, directory), filenameRoot + ".c");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		ABCExecutor.execute(fe, task);
	}

	@Test
	public void diffusion1d_nb() throws ABCException, IOException {
		check("diffusion1d", "diffusion1d_nb");
	}

	@Test
	public void diffusion1d_spec() throws ABCException, IOException {
		check("diffusion1d", "diffusion1d_spec");
	}

	@Test
	public void gausselim_spec() throws ABCException, IOException {
		check("gausselim", "gausselim_spec");
	}

	@Test
	public void gausselim_rowdist() throws ABCException, IOException {
		check("gausselim", "gausselim_rowdist");
	}

	@Test
	public void gausselim_bad() throws ABCException, IOException {
		check("gausselim", "gausselim_bad");
	}

}
