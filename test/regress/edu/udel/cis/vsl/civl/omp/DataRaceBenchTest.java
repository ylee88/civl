package edu.udel.cis.vsl.civl.omp;

import static edu.udel.cis.vsl.civl.TestConstants.OMP_NO_SIMP;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_THREAD_TEN;
import static edu.udel.cis.vsl.civl.TestConstants.OMP_THREAD_TWO;
import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class DataRaceBenchTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* ******************* Fields *************************** */

	private final boolean DEBUG = false;
	private final String VER_DRB = "1.3.2";
	private final String PATH_DIR_DRB_SRC = "examples/omp/dataracebench-"
			+ VER_DRB + "/micro-benchmarks";
	private final String DBAD = "-DBAD";
	private final String DN_10 = "-DN=10";
	private final String DN_60 = "-DN=60";
	private final File DIR_DRB_SRC = new File(PATH_DIR_DRB_SRC);

	private UserInterface ui = new UserInterface();

	/* ******************* Helper Methods ******************* */

	private String pathToSrcFile(String srcFileName) {
		return new File(DIR_DRB_SRC, srcFileName).getPath();
	}

	/**
	 * If the number of threads is 2 and data-race behavior depends on the
	 * result of iteration-number mod 2, then all data-race behaviors are
	 * scheduled in a single thread. As a result, the potential data-race (when
	 * num_threads > 2) will not be reported.
	 */
	private void loopDistributionRoundRobinLimitation(String drb_name) {
		if (DEBUG)
			System.out.println("WARNING:\n"//
					+ "\tDataRaceBenchTests: " + drb_name + " shall fail."
					+ "\tThe conditional update operation is based on iteration number moded by 2. "
					+ "\tIf the number of threads is 2, then all update operations will be distributed "
					+ "\tto a single thread. In this case, the data race can not be detected.");
	}

	/* ******************* Test Cases *********************** */

	@Test
	public void DRB001_antidep1_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB001-antidep1-orig-yes.c")));
	}

	@Test
	public void DRB002_antidep1_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB002-antidep1-var-yes.c")));
	}

	@Test
	public void DRB003_antidep2_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB003-antidep2-orig-yes.c")));
	}

	@Test
	public void DRB004_antidep2_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB004-antidep2-var-yes.c")));
	}

	@Test
	public void DRB005_indirectaccess1_orig_yes() {
		// iter48 and 53 have a data race,
		// N > 54
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_60, QUIET,
				pathToSrcFile("DRB005-indirectaccess1-orig-yes.c")));
	}

	@Test
	public void DRB007_indirectaccess2_orig_yes() {
		// iter0 and 5 have a data race,
		// N > 6 && n/num_threads <= 5;
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB006-indirectaccess2-orig-yes.c")));
	}

	@Test
	public void DRB008_indirectaccess4_orig_yes() {
		// iter0 and 1 have a data race,
		// N > 2 && n/num_threads <= 1;
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, DN_10, QUIET,
				pathToSrcFile("DRB008-indirectaccess4-orig-yes.c")));
	}

	// @Ignore // TODO: lastprivate
	// public void DRB009_lastprivatemissing_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB009-lastprivatemissing-orig-yes.c")));
	// }
	//
	// @Ignore // TODO: lastprivate
	// public void DRB010_lastprivatemissing_var_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB009-lastprivatemissing-var-yes.c")));
	// }

	@Test
	public void DRB011_minusminus_orig_yes() {
		loopDistributionRoundRobinLimitation("DRB011-minusminus-orig-yes.c");
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, DN_10, QUIET,
				pathToSrcFile("DRB011-minusminus-orig-yes.c")));
	}

	@Test
	public void DRB012_minusminus_var_yes() {
		loopDistributionRoundRobinLimitation("DRB012-minusminus-var-yes.c");
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, QUIET,
				pathToSrcFile("DRB012-minusminus-var-yes.c")));
	}

	@Test
	public void DRB013_nowait_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB013-nowait-orig-yes.c")));
	}

	@Test
	public void DRB014_outofbounds_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB014-outofbounds-orig-yes.c")));
	}

	@Test
	public void DRB015_outofbounds_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB015-outofbounds-var-yes.c")));
	}

	@Test
	public void DRB016_outputdep_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB016-outputdep-orig-yes.c")));
	}

	@Test
	public void DRB017_outputdep_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB017-outputdep-var-yes.c")));
	}

	@Test
	public void DRB018_plusplus_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB018-plusplus-orig-yes.c")));
	}

	@Test
	public void DRB019_plusplus_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB019-plusplus-var-yes.c")));
	}

	@Test
	public void DRB020_privatemissing_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB020-privatemissing-var-yes.c")));
	}

	@Test
	public void DRB021_reductionmissing_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB021-reductionmissing-orig-yes.c")));
	}

	@Test
	public void DRB022_reductionmissing_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB022-reductionmissing-var-yes.c")));
	}

	@Test
	public void DRB023_sections1_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB023-sections1-orig-yes.c")));
	}

	// @Ignore // TODO:
	// public void DRB024_simdtruedep_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB024-simdtruedep-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB025_simdtruedep_var_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB025-simdtruedep-var-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB026_targetparallelfor_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB026-targetparallelfor-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB027_taskdependmissing_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB027-taskdependmissing-orig-yes.c")));
	// }

	@Test
	public void DRB028_privatemissing_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB028-privatemissing-orig-yes.c")));
	}

	@Test
	public void DRB029_truedep1_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB029-truedep1-orig-yes.c")));
	}

	@Test
	public void DRB030_truedep1_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB030-truedep1-var-yes.c")));
	}

	@Test
	public void DRB031_truedepfirstdimension_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB031-truedepfirstdimension-orig-yes.c")));
	}

	@Test
	public void DRB032_truedepfirstdimension_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB032-truedepfirstdimension-var-yes.c")));
	}

	@Test
	public void DRB033_truedeplinear_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB033-truedeplinear-orig-yes.c")));
	}

	@Test
	public void DRB034_truedeplinear_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB034-truedeplinear-var-yes.c")));
	}

	@Test
	public void DRB035_truedepscalar_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB035-truedepscalar-orig-yes.c")));
	}

	@Test
	public void DRB036_truedepscalar_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB036-truedepscalar-var-yes.c")));
	}

	@Test
	public void DRB037_truedepseconddimension_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB037-truedepseconddimension-orig-yes.c")));
	}

	@Test
	public void DRB038_truedepseconddimension_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB038-truedepseconddimension-var-yes.c")));
	}

	@Test
	public void DRB039_truedepsingleelement_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB039-truedepsingleelement-orig-yes.c")));
	}

	@Test
	public void DRB040_truedepsingleelement_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB040-truedepsingleelement-var-yes.c")));
	}

	// @Ignore // Containing external functions, which have no definition.
	// public void DRB041_3mm_parallel_no() {
	// assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("utilities/polybench.c"),
	// pathToSrcFile("DRB041-3mm-parallel-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB042_3mm_tile_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB042-3mm-tile-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB043_adi_parallel_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB043-adi-parallel-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB044_adi_tile_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB044-adi-tile-no.c")));
	// }

	@Test
	public void DRB045_doall1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB045-doall1-orig-no.c")));
	}

	@Test
	public void DRB046_doall2_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB046-doall2-orig-no.c")));
	}

	@Test
	public void DRB047_doallchar_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB047-doallchar-orig-no.c")));
	}

	@Test
	public void DRB048_firstprivate_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB048-firstprivate-orig-no.c")));
	}

	// @Ignore // File operations are not supported yet.
	// public void DRB049_fprintf_orig_no() {
	// assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB049-fprintf-orig-no.c")));
	// }

	@Test
	public void DRB050_functionparameter_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB050-functionparameter-orig-no.c")));
	}

	@Test
	public void DRB051_getthreadnum_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB051-getthreadnum-orig-no.c")));
	}

	@Test
	public void DRB052_indirectaccesssharebase_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB052-indirectaccesssharebase-orig-no.c")));
	}

	@Test
	public void DRB053_inneronly1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB053-inneronly1-orig-no.c")));
	}

	@Test
	public void DRB054_inneronly2_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB054-inneronly2-orig-no.c")));
	}

	// @Ignore // PolyBench See above -- DRB041
	// public void DRB055_jacobi2d_parallel_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB055-jacobi2d-parallel-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB056_jacobi2d_tile_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB056-jacobi2d-tile-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB057_jacobiinitialize_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB057-jacobiinitialize-orig-no.c")));
	// }
	//
	// @Ignore // PolyBench See above -- DRB041
	// public void DRB058_jacobikernel_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB058-jacobikernel-orig-no.c")));
	// }

	// @Ignore // TODO:
	// public void DRB059_lastprivate_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB059-lastprivate-orig-no.c")));
	// }

	@Test
	public void DRB060_matrixmultiply_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB060-matrixmultiply-orig-no.c")));
	}

	@Test
	public void DRB061_matrixvector1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB061-matrixvector1-orig-no.c")));
	}

	@Test
	public void DRB062_matrixvector2_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB062-matrixvector2-orig-no.c")));
	}

	@Test
	public void DRB063_outeronly1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB063-outeronly1-orig-no.c")));
	}

	@Test
	public void DRB064_outeronly2_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB064-outeronly2-orig-no.c")));
	}

	@Test
	public void DRB065_pireduction_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_60, QUIET,
				pathToSrcFile("DRB065-pireduction-orig-no.c")));
	}

	@Test
	public void DRB066_pointernoaliasing_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB066-pointernoaliasing-orig-no.c")));
	}

	@Test
	public void DRB067_restrictpointer1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB067-restrictpointer1-orig-no.c")));
	}

	@Test
	public void DRB068_restrictpointer2_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB068-restrictpointer2-orig-no.c")));
	}

	@Test
	public void DRB069_sectionslock1_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB069-sectionslock1-orig-no.c")));
	}

	// @Ignore // TODO:
	// public void DRB070_simd1_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB070-simd1-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB071_targetparallelfor_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB071-targetparallelfor-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB072_taskdep1_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB072-taskdep1-orig-no.c")));
	// }

	@Test
	public void DRB073_doall2_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB073-doall2-orig-yes.c")));
	}

	@Test
	public void DRB074_flush_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB074-flush-orig-yes.c")));
	}

	@Test
	public void DRB075_getthreadnum_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB075-getthreadnum-orig-yes.c")));
	}

	@Test
	public void DRB076_flush_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB076-flush-orig-no.c")));
	}

	@Test
	public void DRB077_single_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB077-single-orig-no.c")));
	}

	// @Ignore // TODO:
	// public void DRB078_taskdep2_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB078-taskdep2-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB079_taskdep3_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB079-taskdep3-orig-no.c")));
	// }

	@Test
	public void DRB080_func_arg_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB080-func-arg-orig-yes.c")));
	}

	@Test
	public void DRB081_func_arg_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB081-func-arg-orig-no.c")));
	}

	@Test
	public void DRB082_declared_in_func_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB082-declared-in-func-orig-yes.c")));
	}

	@Test
	public void DRB083_declared_in_func_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB083-declared-in-func-orig-no.c")));
	}

	@Test
	public void DRB084_threadprivatemissing_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB084-threadprivatemissing-orig-yes.c")));
	}

	// @Ignore // TODO: thread private
	// public void DRB085_threadprivate_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB085-threadprivate-orig-no.c")));
	// }

	// @Ignore // C++ is not supported by CIVL
	// public void DRB086_static_data_member_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB086-static-data-member-orig-yes.cpp")));
	// }
	//
	// @Ignore // C++ is not supported by CIVL
	// public void DRB087_static_data_member2_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB087-static-data-member2-orig-yes.cpp")));
	// }

	@Test
	public void DRB088_dynamic_storage_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB088-dynamic-storage-orig-yes.c")));
	}

	@Test
	public void DRB089_dynamic_storage2_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB089-dynamic-storage2-orig-yes.c")));
	}

	@Test
	public void DRB090_static_local_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB090-static-local-orig-yes.c")));
	}

	// @Ignore // TODO:
	// public void DRB091_threadprivate2_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB091-threadprivate2-orig-no.c")));
	// }

	@Test
	public void DRB092_threadprivatemissing2_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB092-threadprivatemissing2-orig-yes.c")));
	}

	@Test
	public void DRB093_doall2_collapse_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB093-doall2-collapse-orig-no.c")));
	}

	// @Ignore // TODO: ordered dep/src
	// public void DRB094_doall2_ordered_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB094-doall2-ordered-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB095_doall2_taskloop_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB095-doall2-taskloop-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB096_doall2_taskloop_collapse_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB096-doall2-taskloop-collapse-orig-no.c")));
	// }

	// @Ignore // TODO:
	// public void DRB097_target_teams_distribute_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB097-target-teams-distribute-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB098_simd2_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB098-simd2-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB099_targetparallelfor2_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB099-targetparallelfor2-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB100_task_reference_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB100-task-reference-orig-no.cpp")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB101_task_value_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB101-task-value-orig-no.cpp")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB102_copyprivate_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB102-copyprivate-orig-no.c")));
	// }

	@Test
	public void DRB103_master_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB103-master-orig-no.c")));
	}

	@Test
	public void DRB104_nowait_barrier_orig_no() {
		// N > 10
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB104-nowait-barrier-orig-no.c")));
	}

	// @Ignore // TODO:
	// public void DRB105_taskwait_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB105-taskwait-orig-no.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB106_taskwaitmissing_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB106-taskwaitmissing-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB107_taskgroup_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB107-taskgroup-orig-no.c")));
	// }

	@Test
	public void DRB108_atomic_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB108-atomic-orig-no.c")));
	}

	@Test
	public void DRB108_2_atomic_var_syntax_err() {
		// Add BAD to enable incorrect atomic_claues
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DBAD, QUIET,
				pathToSrcFile("DRB108-atomic-orig-no.c")));
	}

	@Test
	public void DRB109_orderedmissing_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB109-orderedmissing-orig-yes.c")));
	}

	@Test
	public void DRB110_ordered_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB110-ordered-orig-no.c")));
	}

	@Test
	public void DRB111_linearmissing_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB111-linearmissing-orig-yes.c")));
	}

	// @Ignore // TODO:
	// public void DRB112_linear_orig_no() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB112-linear-orig-no.c")));
	// }

	@Test
	public void DRB113_default_orig_no() {
		assertTrue(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB113-default-orig-no.c")));
	}

	// @Ignore // TODO:
	// public void DRB114_if_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB114-if-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB115_forsimd_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB115-forsimd-orig-yes.c")));
	// }
	//
	// @Ignore // TODO:
	// public void DRB116_target_teams_orig_yes() {
	// assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
	// pathToSrcFile("DRB116-target-teams-orig-yes.c")));
	// }

}
