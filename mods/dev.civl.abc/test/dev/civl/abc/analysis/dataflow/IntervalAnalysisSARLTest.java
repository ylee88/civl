package dev.civl.abc.analysis.dataflow;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import dev.civl.abc.analysis.common.CallAnalyzer;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.entity.IF.Function;
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
public class IntervalAnalysisSARLTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "c");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	private static IntervalAnalysisSARL ia;

	@Before
	public void setUp() throws Exception {
		ia = IntervalAnalysisSARL.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		ia.clear();
	}

	private void check(String filenameRoot) throws ABCException, IOException {
		File file = new File(root, filenameRoot + ".c");
		AST ast = fe.compile(new File[] { file }, Language.C, new File[0],
				new File[0], new HashMap<String, String>());
		
		PrintStream p = new PrintStream("/Users/edward/Desktop/1.txt");
		ast.prettyPrint(p, true);

		// Call graph construction is a standard analysis
		for (Function f : CallAnalyzer.functions(ast)) {
			if (f.getDefinition() == null) continue;
			ia.analyze(f);
		}
		if (debug) {
			System.out.println(ia.getResultString());

//			for (Function f : CallAnalyzer.functions(ast)) {
//				System.out.println("Dominator tree for function " + f);
//				dom.printDominatorTree(f);
//			}
		}
	}

	@Test//(timeout=1000)
	public void simple() throws ABCException, IOException {
		check("simple");
	}
/*	
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
*/
	@Ignore
	public void branchconst() throws ABCException, IOException {
		check("branchconst");
	}

}