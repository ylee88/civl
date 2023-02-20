package dev.civl.mc.transform;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.TestConstants;
import dev.civl.mc.run.IF.UserInterface;

public class GenTransformerTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "gen");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void gen_argc() {
		assertTrue(ui.run("verify", TestConstants.QUIET, filename("gen.c")));
	}

	@Test
	public void simpleMPI() {
		assertTrue(ui.run("verify -input_mpi_nprocs=2 -enablePrintf=false",
				TestConstants.QUIET, filename("simpleMPI.c")));
	}

	@Test
	public void simpleMPI2() {
		assertTrue(ui.run("verify -input_mpi_nprocs=2 -enablePrintf=false",
				TestConstants.QUIET, filename("simpleMPI2.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
