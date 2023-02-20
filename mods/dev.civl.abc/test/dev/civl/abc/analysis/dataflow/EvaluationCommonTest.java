package dev.civl.abc.analysis.dataflow;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.FrontEnd;

/**
 * Checks a number of simple C programs to make sure interval analysis
 * work on them.
 * 
 * @author dxu
 * 
 */

@Ignore
public class EvaluationCommonTest {

	@SuppressWarnings("unused")
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "c");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);
	
	EvaluationCommon evaluator = new EvaluationCommon();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private void check(String filenameRoot) throws ABCException, IOException {
		File file = new File(root, filenameRoot + ".c");
		@SuppressWarnings("unused")
		AST ast = fe.compile(new File[] { file }, Language.C, new File[0],
				new File[0], new HashMap<String, String>());

	}

	@Test
	public void nestedblocks() throws ABCException, IOException {
		check("nestedblocks");
	}

	@Test
	public void ifthen() throws ABCException, IOException {
		check("ifthen");
	}

	@Test
	public void loops() throws ABCException, IOException {
		check("loops");
	}

	@Test
	public void switches() throws ABCException, IOException {
		check("switches");
	}

	@Test
	public void switchloop() throws ABCException, IOException {
		check("switchloop");
	}

	@Test
	public void jumps() throws ABCException, IOException {
		check("jumps");
	}

	@Test
	public void matprod() throws ABCException, IOException {
		check("matprod");
	}

	@Test
	public void branchconst() throws ABCException, IOException {
		check("branchconst");
	}

}