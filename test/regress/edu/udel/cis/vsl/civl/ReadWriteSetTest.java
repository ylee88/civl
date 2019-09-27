package edu.udel.cis.vsl.civl;

import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import edu.udel.cis.vsl.abc.err.IF.ABCException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class ReadWriteSetTest {
	static String QUIET = TestConstants.QUIET;

	/* *************************** Static Fields *************************** */

	private static File writeDir = new File(
			new File(new File("examples"), "mem"), "writeset");

	private static File readDir = new File(
			new File(new File("examples"), "mem"), "readset");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filenameForWritesetTest(String name) {
		return new File(writeDir, name).getPath();
	}

	private static String filenameForReadsetTest(String name) {
		return new File(readDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */
	@Test
	public void wsPushPop() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop.cvl")));
	}

	@Test
	public void wsPushPopBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop-bad.cvl")));
	}

	@Test
	public void wsPushPop2() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop2.cvl")));
	}

	@Test
	public void wsPushPop2Bad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop2-bad.cvl")));
	}

	@Test
	public void wsPushPopLoop() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop_loop.cvl")));
	}

	@Test
	public void wsPushPopLoopBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop_loop-bad.cvl")));
	}

	@Test
	public void wsPushPopMemcpy() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop_memcpy.cvl")));
	}

	@Test
	public void wsPushPopMemcpyBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_pop_memcpy-bad.cvl")));
	}

	/* ****************** read set tests ******************** */
	@Test
	public void simple() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filenameForReadsetTest("simple.cvl")));
	}

	@Test
	public void pointer() throws ABCException {
		assertTrue(
				ui.run(VERIFY, QUIET, filenameForReadsetTest("pointer.cvl")));
	}

	@Test
	public void pointer2() throws ABCException {
		assertTrue(
				ui.run(VERIFY, QUIET, filenameForReadsetTest("pointer2.cvl")));
	}

	@Test
	public void call() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filenameForReadsetTest("call.cvl")));
	}

	@Test
	public void call2() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filenameForReadsetTest("call2.cvl")));
	}

	@Test
	public void array() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filenameForReadsetTest("array.cvl")));
	}

	@Test
	public void structComplex() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForReadsetTest("structComplex.cvl")));
	}

	@Test
	public void structComplex2() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForReadsetTest("structComplex2.cvl")));
	}

}
