package dev.civl.mc.transform;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static dev.civl.mc.TestConstants.QUIET;
import dev.civl.mc.run.IF.UserInterface;

public class CudaTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static UserInterface ui = new UserInterface();

	private static File rootDir = new File(new File("examples"), "cuda");

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* *************************** Test Methods **************************** */

	@Test
	public void simple() {
		assertTrue(ui.run("verify", filename("simple.cu")));
	}
	
	@Test
	public void sum() {
		assertTrue(
				ui.run("verify -enablePrintf=false -inputN=8 -inputNBLOCKS=4",
						QUIET, filename("sum.cu")));
	}

	@Test
	public void matMult1() {
		assertTrue(ui.run(
				"verify -enablePrintf=false -inputN=2 -inputTILE_WIDTH=1 ",
				QUIET, filename("matMult1.cu")));
	}

	@Test
	public void dotTest() {
		assertTrue(ui.run("verify -inputN_B=3 -input threadsPerBlock_B=3",
				QUIET, filename("dot.cu")));
	}

	@Test
	public void kernelAfterMain() {
		assertTrue(ui.run("verify", QUIET, filename("kernel_after_main.cu")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
