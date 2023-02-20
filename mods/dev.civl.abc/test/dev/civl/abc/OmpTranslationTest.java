package dev.civl.abc;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;

/**
 * Checks a number of simple C programs to make sure they pass on the parsing
 * and analysis stages, while also applying the prune and side-effect-free
 * transformations.
 * 
 * @author siegel
 * 
 */
public class OmpTranslationTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "omp");

	private static List<String> codes = Arrays.asList("prune", "sef");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	private void check(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot + ".c");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		ABCExecutor.execute(fe, task);
	}

	@Test
	public void dijkstra_openmp() throws ABCException {
		check("dijkstra_openmp");
	}

	@Test
	public void dotProduct_critical() throws ABCException {
		check("dotProduct_critical");
	}

	@Test
	public void dotProduct1() throws ABCException {
		check("dotProduct1");
	}

	// TODO: why?
	@Ignore
	@Test
	public void fft_openmp() throws ABCException {
		check("fft_openmp");
	}

	@Test
	public void fig310_mxv_omp() throws ABCException {
		check("fig3.10-mxv-omp");
	}

	@Test
	public void fig498_threadprivate() throws ABCException {
		check("fig4.98-threadprivate");
	}

	@Test
	public void matProduct1() throws ABCException {
		check("matProduct1");
	}

	@Test
	public void matProduct2() throws ABCException {
		check("matProduct2");
	}

	@Test
	public void md_openmp() throws ABCException {
		check("md_openmp");
	}

	@Test
	public void nested() throws ABCException {
		check("nested");
	}

	@Test
	public void parallel() throws ABCException {
		check("parallel");
	}

	@Test
	public void parallelfor() throws ABCException {
		check("parallelfor");
	}

	@Test
	public void poisson_openmp() throws ABCException {
		check("poisson_openmp");
	}

	@Test
	public void quad_openmp() throws ABCException {
		check("quad_openmp");
	}

	@Test
	public void raceCond1() throws ABCException {
		check("raceCond1");
	}

	@Test
	public void raceCond2() throws ABCException {
		check("raceCond2");
	}

	@Test
	public void vecAdd_deadlock() throws ABCException {
		check("vecAdd_deadlock");
	}

	@Test
	public void vecAdd_fix() throws ABCException {
		check("vecAdd_fix");
	}

	@Test
	public void simd() throws ABCException {
		check("simd");
	}

	@Test
	public void test() throws ABCException {
		check("test");
	}
}
