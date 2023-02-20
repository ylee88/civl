package dev.civl.abc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.program.IF.Program;
import dev.civl.abc.transform.common.Pruner;

/**
 * Tests the {@link Pruner}. Each test consists of a pair of programs. The first
 * is the input to the pruner, the second is the expected result: the program
 * that should result from pruning the first program.
 * 
 * @author siegel
 */
public class PruneTest {

	public final static PrintStream out = System.out;

	public final static boolean debug = false;

	private File root = new File("examples/prune");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	private void check(File[] inputs, File oracle) throws ABCException {
		TranslationTask task = new TranslationTask(inputs);

		task.addTransformCode("prune");

		ABCExecutor executor = ABCExecutor.execute(fe, task);
		Program program = executor.getProgram();
		AST actual = program.getAST();
		AST expected = fe.compile(new File[] { oracle }, Language.C);
		if (actual.getRootNode().equiv(expected.getRootNode())) {
			// OK
		} else {
			if (debug) {
				out.println("Expected:");
				expected.prettyPrint(out, false);
				out.println();
				out.println("Actual:");
				actual.prettyPrint(out, false);
			}
			assertTrue(false);
		}
	}

	private void check(String[] inputNames, String oracleName)
			throws ABCException {
		File[] inputs = new File[inputNames.length];
		File oracle;

		for (int i = 0; i < inputs.length; i++)
			inputs[i] = new File(root, inputNames[i]);
		oracle = new File(root, oracleName);
		check(inputs, oracle);

	}

	@Test
	public void structs1() throws ABCException {
		check(new String[] { "structs1.c" }, "structs1_pruned.c");
	}

	@Test
	public void function() throws ABCException {
		check(new String[] { "func.c" }, "func_pruned.c");
	}

	@Test
	public void structsInFunction() throws ABCException {
		check(new String[] { "structsInFunction.cvl" },
				"structsInFunction_pruned.cvl");
	}

	@Test
	public void structsInFunction1() throws ABCException {
		check(new String[] { "structsInFunction1.cvl" },
				"structsInFunction_pruned.cvl");
	}

	@Test
	public void functionDef() throws ABCException {
		check(new String[] { "functionDef.c" }, "functionDef_pruned.c");
	}

}
