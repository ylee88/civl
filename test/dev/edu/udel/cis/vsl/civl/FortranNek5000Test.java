package edu.udel.cis.vsl.civl;

import static org.junit.Assert.*;
import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static edu.udel.cis.vsl.civl.TestConstants.COMPARE;
import static edu.udel.cis.vsl.civl.TestConstants.SPEC;
import static edu.udel.cis.vsl.civl.TestConstants.IMPL;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class FortranNek5000Test {
	/* *************************** Static Fields *************************** */

	private static File DIR_ROOT = new File("examples/fortran/nek5000");
	private static File DIR_SOURCE = new File(DIR_ROOT, "core");
	private static File DIR_DRIVER = new File(DIR_ROOT, "verification");
	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(File dir, String name) {
		return new File(dir, name).getPath();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/* **************************** Test Methods *************************** */

	@Ignore
	public void math() {
		assertTrue(ui.run("verify -showProgram=true",
				filename(DIR_DRIVER, "driver_math.f"),
				filename(DIR_SOURCE, "math.f")));
	}

	@Test
	public void gauss() {
		assertTrue(ui.run(VERIFY, //
				" -checkMemoryLeak=false", //
				// " -showProgram=true", //
				filename(DIR_DRIVER, "driver_gauss.f"),
				filename(DIR_SOURCE, "gauss.f")));
	}

	@Test
	public void speclib() {
		assertFalse(ui.run(VERIFY, QUIET, //
				"-showProgram", //
				" -checkMemoryLeak=false", //
				" -showQueries ", //
				" -checkDivisionByZero=false", //
				filename(DIR_DRIVER, "util.f"),
				filename(DIR_DRIVER, "driver_speclib.f"),
				filename(DIR_DRIVER, "speclib.f")));
	}

	@Test
	public void speclib_jan() {
		assertTrue(ui.run(VERIFY, // QUIET, //
				" -checkMemoryLeak=false", //
				filename(DIR_DRIVER, "util.f"),
				filename(DIR_DRIVER, "driver_speclib_jan.f"),
				filename(DIR_DRIVER, "speclib.f")));
	}
	

	@Test
	public void mxm() {
		assertTrue(ui.run(VERIFY, // QUIET, //
				filename(DIR_DRIVER, "mxm_driver.f"),
				filename(DIR_DRIVER, "mxm_pencil.f")));
	}
	

	@Test
	public void mxm_fe() {
		assertTrue(ui.run(COMPARE, // QUIET, //
				SPEC, 
				filename(DIR_DRIVER, "mxm_driver.f"),
				filename(DIR_DRIVER, "mxm_naive.f"),
				IMPL,
				filename(DIR_DRIVER, "mxm_driver.f"),
				filename(DIR_DRIVER, "mxm_pencil2.f")));
	}

}
