package dev.civl.mc;

import static dev.civl.mc.TestConstants.LOOP;
import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class TicketsFixBigTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File("examples/tickets/");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
