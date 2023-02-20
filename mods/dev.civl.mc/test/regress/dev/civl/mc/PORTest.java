package dev.civl.mc;

import static dev.civl.mc.TestConstants.QUIET;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class PORTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "por");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void adder2() {
		assertTrue(
				ui.run("verify", "-inputN=4", QUIET, filename("adder2.cvl")));
	}

	@Test
	public void atomic0() {
		assertFalse(ui.run("verify", QUIET, filename("atomic0.cvl")));
	}

	@Test
	public void atomic1() {
		assertFalse(ui.run("verify", QUIET, filename("atomic1.cvl")));
	}

	@Test
	public void invisibility_c3() {
		assertFalse(ui.run("verify", QUIET, filename("invisibility_c3.cvl")));
	}

	@Test
	public void pointerShare() {
		assertFalse(ui.run("verify", QUIET, filename("pointerShare.cvl")));
	}

	@Test
	public void pointerShare1() {
		assertFalse(ui.run("verify", QUIET, filename("pointerShare1.cvl")));
	}

	@Test
	public void pointerShare2() {
		assertFalse(ui.run("verify", QUIET, filename("pointerShare2.cvl")));
	}

	@Test
	public void trade3() {
		assertFalse(ui.run("verify", QUIET, "-checkDeadlock=none",
				filename("trade3.cvl")));
	}

	@Test
	public void trade4() {
		assertFalse(ui.run("verify", QUIET, filename("trade4.cvl")));
	}

	@Test
	public void guard1() {
		assertFalse(ui.run("verify", QUIET, filename("guard1.cvl")));
	}

	@Test
	public void guard2() {
		assertFalse(ui.run("verify", QUIET, filename("guard2.cvl")));
	}

	@Test
	public void waitTest() {
		assertFalse(ui.run("verify", QUIET, filename("wait.cvl")));
	}

	@Test
	public void loop() {
		assertFalse(
				ui.run("verify -errorBound=4", QUIET, filename("loop.cvl")));
	}

	@Test
	public void loop2() {
		assertTrue(ui.run("verify", QUIET, filename("loop2.cvl")));
	}

	@Test
	public void loop3() {
		assertTrue(ui.run("verify", QUIET, filename("loop3.cvl")));
	}

	@Test
	public void por_ptr_analysis_node() {
		assertFalse(ui.run("verify", QUIET, "-checkMemoryLeak=false",
				filename("por_ptr_analysis_node.cvl")));
	}

	@Test
	public void por_ptr_analysis_list() {
		assertFalse(ui.run("verify", QUIET, "-checkMemoryLeak=false",
				filename("por_ptr_analysis_list.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
