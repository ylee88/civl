package edu.udel.cis.vsl.civl.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.ConstantsTest;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class IOTransformerTest {
	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "io");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void printf() {
		assertTrue(ui.run("verify", ConstantsTest.NO_PRINTF,
				ConstantsTest.QUIET, filename("printf.cvl")));
	}

	@Test
	public void scanf() {
		assertTrue(
				ui.run("verify", ConstantsTest.QUIET, filename("fscanf.cvl")));
	}

	@Test
	public void stringTestBad() {
		try {
			assertFalse(ui.run("verify -DNEGINDEX", ConstantsTest.QUIET,
					filename("fileOpen.cvl")));
		} catch (CIVLInternalException e) {
			System.out.println(e.getMessage());
		}
		assertFalse(ui.run("verify", ConstantsTest.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DNCINDEX", ConstantsTest.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DNCARRAY", ConstantsTest.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DSCHAR", ConstantsTest.QUIET,
				filename("fileOpen.cvl")));
	}

	@Test
	public void textFileLengthCompare() {
		assertTrue(ui.run("compare", ConstantsTest.QUIET, ConstantsTest.SPEC,
				filename("textFileLengthSpec.cvl"), ConstantsTest.IMPL,
				filename("textFileLengthImpl.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
