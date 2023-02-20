package dev.civl.abc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.program.IF.Program;

public class SideEffectRemoverTest {

	public static boolean debug = false;

	/**
	 * Prune, remove side effects, and prune again. Second prune is necessary
	 * because after removing side-effects, variable might emerge that is never
	 * read.
	 */
	private final static List<String> codes = Arrays.asList("prune", "sef",
			"prune");

	private final static File root = new File(new File("examples"),
			"side-effects");

	private final static Configuration config = Configurations
			.newMinimalConfiguration();

	private final static FrontEnd f = new FrontEnd(config);

	private final static PrintStream out = System.out;

	private void check(String filename) throws ABCException {
		File file = new File(root, filename);
		File outputFile = new File(root, "out_" + filename);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);

		ABCExecutor executor = ABCExecutor.execute(f, task);
		Program program = executor.getProgram();

		task = new TranslationTask(outputFile);
		task.addTransformCode("prune");
		executor = ABCExecutor.execute(f, task);

		Program outputProgram = executor.getProgram();
		boolean equiv = program.getAST().equiv(outputProgram.getAST());

		if (debug && !equiv) {
			out.println("Difference:");
			out.println(program.getAST().diff(outputProgram.getAST()));
			out.println("Output program:");
			program.prettyPrint(out);
			out.println("Oracle program:");
			outputProgram.prettyPrint(out);
		}
		assertTrue(equiv);
	}

	@Test
	public void assign1() throws ABCException {
		check("assign1.c");
	}

	@Test
	public void assign2() throws ABCException {
		check("assign2.c");
	}

	@Test
	public void assign3() throws ABCException {
		check("assign3.c");
	}

	@Test
	public void comma() throws ABCException {
		check("comma.c");
	}

	@Test
	public void enums() throws ABCException {
		check("enums.c");
	}
	/* TODO: loops transform differently now, reevaluate commented out tests
	@Test
	public void for_se() throws ABCException {
		check("for-se.c");
	}
	*/
	@Test
	public void inc() throws ABCException {
		check("inc.c");
	}

	@Test
	public void recurse() throws ABCException {
		check("recurse.c");
	}

	@Test
	public void returns() throws ABCException {
		check("returns.c");
	}
	/*
	@Test
	public void types() throws ABCException {
		check("types.c");
	}
	*/
	@Test
	public void doWhile() throws ABCException {
		check("doWhile.c");
	}

	@Test
	public void cond() throws ABCException {
		check("cond.c");
	}

	@Test
	public void dereference() throws ABCException {
		check("dereference.c");
	}

	@Test
	public void stmtExpr() throws ABCException {
		check("stmtExpression.c");
	}

	@Test
	public void shortCircuit() throws ABCException {
		check("shortCircuit.c");
	}
	/*
	@Test
	public void loopShortCircuit() throws ABCException {
		check("loopShortCircuit.c");
	}
	*/
	/*
	@Test
	public void nestedLoop() throws ABCException {
		check("nestedLoop.c");
	}
	*/
	@Test
	public void abstractFunc() throws ABCException {
		check("abstractFunctions.cvl");
	}

	@Test
	public void funcalls() throws ABCException {
		check("funcalls.c");
	}

}
