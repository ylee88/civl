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

public class ShadowRemoverTest {

	public static boolean debug = false;

	private final static List<String> codes = Arrays.asList("prune", "shadow",
			"prune");

	private final static File root = new File(new File("examples"),
			"shadow");

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
	public void shadow1() throws ABCException {
		check("shadow1.c");
	}

}
