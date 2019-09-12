package edu.udel.cis.vsl.civl.transform;

import static edu.udel.cis.vsl.civl.TestConstants.NO_PRINTF;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_NO_SIMP;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_THREAD_TWO;
import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class OpenMP2CIVLTransformerTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "omp");
	private static File simpleDir = new File(rootDir, "simple");
	private static File transformDir = new File(rootDir, "transform");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	private static String filename(File dir, String fname) {
		return new File(dir, fname).getPath();
	}

	private static String atomicFilename(String fname) {
		return new File(new File(rootDir, "atomics"), fname).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void atomicReadWrite() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_read_write.c")));
	}

	@Test
	public void atomicDefault() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_default.c")));
	}

	@Test
	public void atomicUpdate() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_update.c")));
	}

	@Ignore
	public void atomicReadWriteDot() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, "-showProgram", // QUIET,
				atomicFilename("atomic_read_write_dot.c")));
	}

	@Ignore
	public void atomicReadWriteArray() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, "-showProgram", // QUIET,
				atomicFilename("atomic_read_write_array.c")));
	}

	@Test
	public void eijkhout() {
		assertFalse(ui.run(VERIFY, OMP_THREAD_TWO, "-ompLoopDecomp=ALL", QUIET,
				filename(new File(rootDir, "simple"), "eijkhout.c")));
	}

	@Test
	public void overflushCycleViolate() {
		assertFalse(ui.run(VERIFY, OMP_THREAD_TWO, QUIET, "-cyclesViolate",
				filename("overflush.cvl")));
	}

	@Test
	public void overflush() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				filename("overflush.cvl")));
	}

	@Test
	public void dotProduct1() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename("dotProduct1.c")));
	}

	@Test
	public void dotProduct1Simplify() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_THREAD_TWO, QUIET,
				filename("dotProduct1.c")));
	}

	@Test
	public void dotProductCritical() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename("dotProduct_critical.c")));
	}

	@Test
	public void dotProductCriticalSimplify() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_THREAD_TWO, QUIET,
				filename("dotProduct_critical.c")));
	}

	@Test
	public void matProduct1Simplify() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_THREAD_TWO, QUIET,
				filename("matProduct1.c")));
	}

	@Test
	public void parallelfor() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename("parallelfor.c")));
	}

	@Test
	public void parallelforSimplify() {
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_THREAD_TWO, QUIET,
				filename("parallelfor.c")));
	}

	@Test
	public void sharedVarTest1() {
		// after moving the function definition into the parallel region, this
		// example will work.
		assertTrue(ui.run(VERIFY, NO_PRINTF, OMP_THREAD_TWO, QUIET,
				filename("sharedVar1.cvl")));
	}

	@Test
	public void omp_reduce() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(simpleDir, "omp_reduce.c")));
	}

	@Test
	public void omp_reduce_bad_undecl_id() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(simpleDir, "omp_reduce_bad.c")));
	}

	@Test
	public void omp_reduce_bad_syntax_err() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(simpleDir, "omp_reduce_bad2.c")));
	}

	@Test
	public void new_transform_manual_DRB028() {
		assertFalse(ui.run(VERIFY, "-DNTHREADS=10", QUIET,
				filename(transformDir, "DRB028_manual_transform.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
