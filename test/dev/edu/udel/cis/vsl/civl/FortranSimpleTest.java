package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class FortranSimpleTest {
	/* *************************** Static Fields *************************** */

	private static File DIR_ROOT = new File(new File("examples"), "fortran");

	private static File DIR_SIMPLE = new File(DIR_ROOT, "simple");

	private static File DIR_FLASH_GETDATA = new File(DIR_ROOT,
			"flash/eos_getData_min");

//	private static File DIR_MXM = new File(DIR_ROOT, "nek/mxm");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(File dir, String name) {
		return new File(dir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void simple_loops() {
		assertTrue(ui.run("verify -showProgram=true",
				filename(DIR_SIMPLE, "loops.f")));
	}

	@Test
	public void simple_mxm() {
		assertTrue(ui.run("verify -showProgram=true",
				filename(DIR_SIMPLE, "mxm_naive.f")));
	}

	@Test
	public void flash_getData() {
		// ui.run("verify -showProgram=true",
		// filename(DIR_FLASH_GETDATA, "driver.F90"));
		ui.run("show -showProgram=true", //
				filename(DIR_FLASH_GETDATA, "Eos_getData_loop1.F90"), //
				filename(DIR_FLASH_GETDATA, "Eos_getData_new.F90"), //
				filename(DIR_FLASH_GETDATA, "driver.F90"));
	}
}
