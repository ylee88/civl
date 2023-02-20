package dev.civl.mc.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.TestConstants;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.run.IF.UserInterface;

public class IOTransformerTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

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
		assertTrue(ui.run("verify", TestConstants.NO_PRINTF,
				TestConstants.QUIET, filename("printf.cvl")));
	}

	@Test
	public void scanf() {
		assertTrue(
				ui.run("verify", TestConstants.QUIET, filename("fscanf.cvl")));
	}

	@Test
	public void stringTestBad() {
		try {
			assertFalse(ui.run("verify -DNEGINDEX", TestConstants.QUIET,
					filename("fileOpen.cvl")));
		} catch (CIVLInternalException e) {
			System.out.println(e.getMessage());
		}
		assertFalse(ui.run("verify", TestConstants.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DNCINDEX", TestConstants.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DNCARRAY", TestConstants.QUIET,
				filename("fileOpen.cvl")));
		assertFalse(ui.run("verify -DSCHAR", TestConstants.QUIET,
				filename("fileOpen.cvl")));
	}

	@Test
	public void textFileLengthCompare() {
		assertTrue(ui.run("compare", TestConstants.QUIET, TestConstants.SPEC,
				filename("textFileLengthSpec.cvl"), TestConstants.IMPL,
				filename("textFileLengthImpl.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
