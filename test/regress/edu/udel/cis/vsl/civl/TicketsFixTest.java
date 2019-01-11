package edu.udel.cis.vsl.civl;

import static edu.udel.cis.vsl.civl.TestConstants.LOOP;
import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.udel.cis.vsl.abc.err.IF.ABCException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;
public class TicketsFixTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File("examples/tickets/");

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
	 * See: https://vsl.cis.udel.edu/trac/civl/ticket/913
	 * 
	 * @author Wenhao Wu (wuwenhao@udel.edu)
	 * @throws ABCException
	 */
	@Test
	public void ticket_913_inaccurate_array_index_error_message_provesa()
			throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, LOOP, filename(
				"ticket_913_inaccurate_array_index_error_message_provesa.cvl")));
	}

	/**
	 * See: https://vsl.cis.udel.edu/trac/civl/ticket/913#comment:4
	 * 
	 * @author Wenhao Wu (wuwenhao@udel.edu)
	 * @throws ABCException
	 */
	@Test
	public void ticket_913_inaccurate_array_index_error_message_simple()
			throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename(
				"ticket_913_inaccurate_array_index_error_message_simple.cvl")));
	}

}
