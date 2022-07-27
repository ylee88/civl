package edu.udel.cis.vsl.civl;

import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.udel.cis.vsl.abc.err.IF.ABCException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;
public class RobustnessTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File("examples/robustness");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
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

	/**
	 * A dummy EOF Token will be created and added into the pragma
	 * CivlcTokenSubSequence. And the EOF token will have a dummy formation
	 * related with a 'fake' file called 'CivlcTokenSubSequence Constructor'
	 * 
	 * @throws ABCException
	 */
	@Test
	public void missing_source_file() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("missing_source_file.cvl")));
	}

	@Test
	public void string_memory_location() {
		assertTrue(
				ui.run(VERIFY, QUIET, filename("string_memory_storage.cvl")));
	}

}
