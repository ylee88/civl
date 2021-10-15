package edu.udel.cis.vsl.civl.transform;

import static edu.udel.cis.vsl.civl.TestConstants.NO_PRINTF;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_NO_SIMP;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_THREAD_TEN;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_THREAD_TWO;
import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class OpenMP2CIVLTransformerTest {

	/* *************************** Static Fields *************************** */

	private static final String BAD = "-DBAD";

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

	@Ignore
	public void atomicReadWrite() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_read_write.c")));
	}

	@Ignore
	public void atomicDefault() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_default.c")));
	}

	@Ignore
	public void atomicUpdate() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_update.c")));
	}

	@Ignore
	public void atomicReadWriteDot() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, QUIET,
				atomicFilename("atomic_read_write_dot.c")));
	}

	@Ignore
	public void atomicReadWriteArray() {
		assertTrue(ui.run(VERIFY, OMP_THREAD_TWO, 
				atomicFilename("atomic_read_write_array.c")));
	}

	@Ignore
	public void eijkhout() {
		// TODO: the last place using StructOrUnionLiteralExpr need to be
		// removed: it is used by domain_decomp in civl-omp.cvl line 479
		assertFalse(ui.run(VERIFY, OMP_THREAD_TWO, "-ompLoopDecomp=ALL",
				"-showProgram",
				filename(new File(rootDir, "simple"), "eijkhout.c")));
	}

	@Ignore // @Test
	public void overflushCycleViolate() {
		assertFalse(ui.run(VERIFY, OMP_THREAD_TWO, QUIET, "-cyclesViolate",
				filename("overflush.cvl")));
	}

	@Ignore // @Test
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

	@Ignore // @Test
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

	@Test
	public void omp_parallel() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_parallel.c")));
	}

	@Test
	public void omp_parallel_arr() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_parallel_arr.c")));
	}

	@Test
	public void omp_parallel_arr_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, BAD, QUIET,
				filename(transformDir, "omp_parallel_arr.c")));
	}

	@Test
	public void omp_parallel_func() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_parallel_func.c")));
	}

	@Test
	public void omp_parallel_func_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, BAD, QUIET,
				filename(transformDir, "omp_parallel_func.c")));
	}

	@Test
	public void omp_parallel_ptr() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_parallel_ptr.c")));
	}

	@Test
	public void omp_parallel_ptr_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, BAD, QUIET,
				filename(transformDir, "omp_parallel_ptr.c")));
	}

	@Test
	public void omp_parallel_ptr_bad2() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_parallel_ptr_bad.c")));
	}

	@Test
	public void omp_reduction_parallel() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_reduction_parallel.c")));
	}

	@Test
	public void omp_reduction_parallel_for() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_reduction_parallel_for.c")));
	}

	@Test
	public void omp_sections() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				filename(transformDir, "omp_sections.c")));
	}

	@Test
	public void omp_sections_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, BAD, QUIET,
				filename(transformDir, "omp_sections.c")));
	}

	@Test
	public void omp_loop_ordered_collapse() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(transformDir, "omp_loop_ordered_collapse.c")));
	}

	@Test
	public void omp_simple_lock() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(transformDir, "omp_simple_lock.c")));
	}

	@Test
	public void omp_simple_lock_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, BAD, QUIET,
				filename(transformDir, "omp_simple_lock.c")));
	}

	@Ignore
	public void omp_simple_atomic() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				filename(transformDir, "omp_atomic.c")));
	}

	@Test
	public void omp_simple_atomic_bad() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, BAD, QUIET,
				filename(transformDir, "omp_atomic.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
