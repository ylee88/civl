package dev.civl.abc;

import java.io.File;
import java.io.IOException;
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
import dev.civl.abc.transform.IF.Transform;
import dev.civl.abc.transform.common.CompareCombiner;

/**
 * Tests the {@link CompareCombiner} transform.
 * 
 * @author siegel
 */
public class CompareTest {
	private static boolean debug = false;

	private File root = new File(new File("examples"), "compare");

	private PrintStream out = System.out;

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd frontEnd = new FrontEnd(config);

	private void check(String filename0, String filename1)
			throws ABCException, IOException {
		File file0 = new File(root, filename0 + ".cvl");
		ABCExecutor executor0 = ABCExecutor.newExecutor(frontEnd,
				new TranslationTask(file0));

		executor0.execute();

		Program program0 = executor0.getProgram();

		if (debug)
			program0.prettyPrint(out);

		File file1 = new File(root, filename1 + ".cvl");
		ABCExecutor executor1 = ABCExecutor.newExecutor(frontEnd,
				new TranslationTask(file1));

		executor1.execute();

		Program program1 = executor1.getProgram();

		if (debug)
			program1.prettyPrint(out);

		AST combinedAST = Transform.compareCombiner().combine(program0.getAST(),
				program1.getAST());

		if (debug)
			combinedAST.prettyPrint(out, false);

		Program compositeProgram = frontEnd.getProgramFactory(Language.CIVL_C)
				.newProgram(combinedAST);

		if (debug)
			compositeProgram.prettyPrint(out);
	}

	@Test
	public void adder() throws ABCException, IOException {
		check("adder_spec", "adder_impl");
	}
}
