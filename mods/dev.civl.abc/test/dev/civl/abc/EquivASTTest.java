package dev.civl.abc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.program.IF.Program;

public class EquivASTTest {

	private static boolean debug = false;

	private static PrintStream out = System.out;

	private static List<String> codes = Arrays.asList("prune", "sef");

	private File root = new File(new File("examples"), "equiv");

	private boolean isEquiv(String filenameRoot1, String filenameRoot2)
			throws ABCException, IOException {
		File file1 = new File(root, filenameRoot1 + ".c");
		TranslationTask task1 = new TranslationTask(file1);

		task1.addAllTransformCodes(codes);

		Program program1 = ABCExecutor.execute(task1).getProgram();

		File file2 = new File(root, filenameRoot2 + ".c");
		TranslationTask task2 = new TranslationTask(file2);

		task2.addAllTransformCodes(codes);

		Program program2 = ABCExecutor.execute(task2).getProgram();

		if (debug) {
			out.println("First program is: ");
			program1.prettyPrint(out);
			out.println("Second program is: ");
			program2.prettyPrint(out);
		}

		DifferenceObject diff = program1.getAST().diff(program2.getAST());

		if (diff == null)
			return true;
		else {
			if (debug)
				diff.print(out);
			return false;
		}
	}

	@Test
	public void test1() throws ABCException, IOException {
		assertTrue(isEquiv("test1_0", "test1_1"));
	}

	@Test
	public void test2() throws ABCException, IOException {
		assertFalse(isEquiv("test2_0", "test2_1"));
	}

	@Test
	public void test3() throws ABCException, IOException {
		assertFalse(isEquiv("test3_0", "test3_1"));
	}

	@Test
	public void test4() throws ABCException, IOException {
		assertFalse(isEquiv("test4_0", "test4_1"));
	}
}
