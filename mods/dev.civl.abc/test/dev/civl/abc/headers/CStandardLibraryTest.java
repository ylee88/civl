/**
 * 
 */
package dev.civl.abc.headers;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;

/**
 * @author Wenhao Wu (wuwenhao@udel.edu)
 *
 */
public class CStandardLibraryTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	/**
	 * The directory in which the examples are located.
	 */
	private static File root = new File("examples/c/libraries");

	/**
	 * The transformations which will be applied to each example.
	 */
	private static List<String> codes = Arrays.asList("prune", "sef");

	/**
	 * Re-use a single front end for all tests in this class.
	 */
	private static FrontEnd fe = new FrontEnd(
			Configurations.newMinimalConfiguration());

	/**
	 * Compile and transform the specified example.
	 * 
	 * @param filenameRoot
	 *            the name of the source file without the ".c" suffix
	 * @throws ABCException
	 *             if anything goes wrong with compiling or transforming
	 */
	private void check(String filenameRoot) throws ABCException {
		File file = new File(root, filenameRoot + ".c");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);

		ABCExecutor executor = ABCExecutor.newExecutor(fe, task);

		executor.execute();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void include_assert_h() throws ABCException {
		check("lib_assert");
	}

	@Test
	public void include_complex_h() throws ABCException {
		check("lib_complex");
	}

	@Test
	public void include_ctype_h() throws ABCException {
		check("lib_ctype");
	}

	@Test
	public void include_errno_h() throws ABCException {
		check("lib_errno");
	}

	@Test
	public void include_fenv_h() throws ABCException {
		check("lib_fenv");
	}

	@Test
	public void include_float_h() throws ABCException {
		check("lib_float");
	}

	@Test
	public void include_inttypes_h() throws ABCException {
		check("lib_inttypes");
	}

	@Test
	public void include_iso646_h() throws ABCException {
		check("lib_iso646");
	}

	@Test
	public void include_limits_h() throws ABCException {
		check("lib_limits");
	}

	@Test
	public void include_locale_h() throws ABCException {
		check("lib_locale");
	}

	@Test
	public void include_math_h() throws ABCException {
		check("lib_math");
	}

	@Test
	public void include_setjmp_h() throws ABCException {
		check("lib_setjmp");
	}

	@Test
	public void include_signal_h() throws ABCException {
		check("lib_signal");
	}

	@Test
	public void include_stdalign_h() throws ABCException {
		check("lib_stdalign");
	}

	@Test
	public void include_stdarg_h() throws ABCException {
		check("lib_stdarg");
	}

	@Test
	public void include_stdatomic_h() throws ABCException {
		check("lib_stdatomic");
	}

	@Test
	public void include_stdbool_h() throws ABCException {
		check("lib_stdbool");
	}

	@Test
	public void include_stddef_h() throws ABCException {
		check("lib_stddef");
	}

	@Test
	public void include_stdint_h() throws ABCException {
		check("lib_stdint");
	}

	@Test
	public void include_stdio_h() throws ABCException {
		check("lib_stdio");
	}

	@Test
	public void include_stdnoreturn_h() throws ABCException {
		check("lib_stdnoreturn");
	}

	@Test
	public void include_string_h() throws ABCException {
		check("lib_string");
	}

	@Test
	public void include_tgmath_h() throws ABCException {
		check("lib_tgmath");
	}

	@Test
	public void include_threads_h() throws ABCException {
		check("lib_threads");
	}

	@Test
	public void include_time_h() throws ABCException {
		check("lib_time");
	}

	@Test
	public void include_uchar_h() throws ABCException {
		check("lib_uchar");
	}

	@Test
	public void include_wchar_h() throws ABCException {
		check("lib_wchar");
	}

	@Test
	public void include_wctype_h() throws ABCException {
		check("lib_wctype");
	}

}
