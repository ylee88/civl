package dev.civl.mc;

import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.mc.run.IF.UserInterface;

public class ReadWriteSetTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

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

	@Test
	public void wsPushPopSeqUnsupported() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForWritesetTest("ws_push_seq.cvl")));
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
	public void conditional() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filenameForReadsetTest("conditional.cvl")));
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
	
	@Test
	public void sequenceReadSet() throws ABCException {
		assertTrue(
				ui.run(VERIFY, QUIET, filenameForReadsetTest("sequence.cvl")));
	}
}
