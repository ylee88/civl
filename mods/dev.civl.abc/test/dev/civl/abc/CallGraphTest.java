package dev.civl.abc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.token.IF.SyntaxException;

/**
 * Checks a number of simple C programs to make sure they pass on the call graph
 * construction analyzer.
 * 
 * @author siegel
 * 
 */
public class CallGraphTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "c");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private AST getAST(File file) throws ParseException, SyntaxException,
			PreprocessorException {
		AST ast = fe.compile(new File[] {file}, Language.C, new File[0], new File[0],
				new HashMap<String, String>());
		return ast;
	}

	private void check(String filenameRoot) throws ABCException, IOException {
		File file = new File(root, filenameRoot + ".c");
		AST root = getAST(file);

		if (debug) {
			dev.civl.abc.analysis.common.CallAnalyzer
					.printCallGraph(root);
		}
	}

	@Test
	public void dagcall() throws ABCException, IOException {
		check("dagcall");
	}

	@Test
	public void indirectcall() throws ABCException, IOException {
		check("indirectcall");
	}

	@Test
	public void recursivecall() throws ABCException, IOException {
		check("recursivecall");
	}
}
