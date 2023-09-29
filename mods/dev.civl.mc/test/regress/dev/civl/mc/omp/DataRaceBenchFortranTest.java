package dev.civl.mc.omp;

import static dev.civl.mc.TestConstants.OMP_NO_SIMP;
import static dev.civl.mc.TestConstants.OMP_THREAD_TEN;
import static dev.civl.mc.TestConstants.OMP_THREAD_TWO;
import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class DataRaceBenchFortranTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* ******************* Fields *************************** */

	private final boolean DEBUG = false;
	private final String VER_DRB = "1.3.2";
	private final String PATH_DIR_DRB_SRC = "examples/omp/dataracebench-"
			+ VER_DRB + "/micro-benchmarks-fortran";
	// private final String DBAD = "-DBAD";
	private final String DN_10 = "-DN=10";
	// private final String DN_60 = "-DN=60";
	// private final String DN_100 = "-DN=100";
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
	@SuppressWarnings("unused")
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
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET,
				pathToSrcFile("DRB001-antidep1-orig-yes.f95")));
	}

	@Ignore
	public void DRB002_antidep1_var_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, DN_10, QUIET,
				pathToSrcFile("DRB002-antidep1-var-yes.f95")));
	}

	@Test
	public void DRB011_minusminus_orig_yes() {
		assertFalse(ui.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TEN, DN_10, QUIET,
				pathToSrcFile("DRB011-minusminus-orig-yes.f95")));
	}
}
