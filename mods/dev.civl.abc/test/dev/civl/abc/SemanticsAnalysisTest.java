package dev.civl.abc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.token.IF.SyntaxException;

public class SemanticsAnalysisTest {
	private static boolean debug = false;

	private static List<String> codes = Arrays.asList("prune", "sef");

	private File root = new File(new File(new File("examples"), "c"),
			"semanticsAnalysis");

	private void checkFile(String filename) throws ABCException {
		File file = new File(root, filename);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setPrettyPrint(true);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}

	private void check(String filenameRoot) throws ABCException {
		checkFile(filenameRoot + ".c");
	}

	@Test
	public void operandOfAddressOf() throws ABCException {
		SyntaxException expected = null;
		try {
			check("valid_addressof");
		} catch (SyntaxException e) {
			expected = e;
		} finally {
			assertTrue(expected != null
					&& expected.getMessage().contains("ADDRESS_OF operation")
					&& expected.getMessage()
							.contains("not an lvalue expression"));
		}
	}
}
