package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;

import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class Cuda2CIVLTransformTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "cuda");

	private static UserInterface ui = new UserInterface();

	@SuppressWarnings("unused")
	private PrintStream out = System.out;

	private File root = new File(new File("examples"), "cuda");

	@SuppressWarnings("unused")
	private File cudaHelper = new File(root, "cuda-helper.cvh");

	// private static List<String> codes = Arrays.asList("prune", "sef");

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void sum() {
		assertTrue(ui.run("verify", "-inputN=8", "-inputNBLOCKS=4",
				filename("sum.cu")));
	}

	@Test
	public void dot() {
		assertTrue(ui.run("verify", "-inputN_B=6", "-inputthreadsPerBlock_B=4",
				filename("dot.cu")));
	}

	@Test
	public void matMult() {
		assertTrue(ui.run("verify", "-inputN=2", "-inputTILE_WIDTH=1",
				filename("matMult1.cu")));
	}

	@Test
	public void cudaOmp() {
		assertTrue(ui.run("verify", "-inputBLOCK_B=4", "-inputTHREADS_B=2",
				filename("cuda-omp.cu")));
	}

	@Test
	public void deadlockBugTest() {
		assertFalse(ui.run("verify", filename("deadlockBug.cu")));
	}
}
