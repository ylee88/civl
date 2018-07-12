package edu.udel.cis.vsl.civl;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import edu.udel.cis.vsl.abc.err.IF.ABCException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class AcfInterfaceTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "slice");

	private static UserInterface ui = new UserInterface();
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	
	private static boolean NO_DIRECT = false;
	private static boolean DIRECT = true;

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}
	
	/*
	 * Check whether the output produced by running either
	 * 
	 *   civl verify -svcomp16 stress_test.c; or
	 *   civl verify -svcomp16 -direct=stress_test.direct stress_test.c
	 *   
	 * followed by
	 *  
	 *  civl replay -sliceAnalysis stress_test.c
	 *  
	 * contains the oracle string defined in 
	 * examples/slice/stress_test.oracle.
	 * 
	 * @param filename The base name of the C program,
	 * its corresponding oracle, and possibly a file 
	 * with branch directives.
	 * 
	 * @throws ABCException
	 * 
	 * @throws IOException
	 */
	private boolean expectedOutput(String filename, boolean direct) throws ABCException, IOException {
		String fileStr = filename + ".c";
		
		if (direct) {
			String directFlag = "-direct=examples/slice/"+filename+".direct";
			ui.run("verify", "-svcomp16", directFlag, filename(fileStr)); 	
		} else {
			ui.run("verify", "-svcomp16", filename(fileStr));			
		}
		ui.run("replay", "-sliceAnalysis", filename(fileStr));
		
		String oraclePath = "examples/slice/"+filename+".oracle";
		String oracle = new String(Files.readAllBytes(Paths.get(oraclePath)), 
				StandardCharsets.UTF_8);
		
		return (outContent.toString()).contains(oracle);
	}
	
	/* **************************** Test Methods *************************** */
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}
	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	}

	@Test
	public void stressNoDirection() throws ABCException, IOException {	
		assertTrue(expectedOutput("stress_test", NO_DIRECT));
	}
	
	@Test
	public void stressWithDirection() throws ABCException, IOException {	
		assertTrue(expectedOutput("stress_test", DIRECT));
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
