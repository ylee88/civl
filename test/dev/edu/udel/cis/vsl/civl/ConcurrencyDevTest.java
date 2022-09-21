package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class ConcurrencyDevTest {
	private static File rootDir = new File(new File("examples"), "concurrency");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void dataRaceBug() {
		assertTrue(!ui.run("verify", filename("simplifiedPrintBug.cvl")));
	}

	/**
	 * TODO: This test fails because we don't explore the execution in which the
	 * spawned process executes the closure before the main process gives x a
	 * value. This is a buggy execution (namely because we try to use an
	 * uninitialized variable) which CIVL should ostensibly catch.
	 */
	@Test
	public void sendClosure() {
		assertTrue(!ui.run("verify", filename("sendClosure.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
		System.gc();
	}

}
