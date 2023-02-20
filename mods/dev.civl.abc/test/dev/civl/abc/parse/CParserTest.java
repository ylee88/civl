package dev.civl.abc.parse;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.front.IF.Front;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.ParseTree;
import dev.civl.abc.front.IF.Parser;
import dev.civl.abc.front.IF.Preprocessor;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.token.IF.CivlcTokenSource;
import dev.civl.abc.token.IF.FileIndexer;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.Tokens;
import dev.civl.abc.util.IF.ANTLRUtils;

public class CParserTest {

	private static boolean debug = false;

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static TokenFactory tokenFactory = Tokens.newTokenFactory();

	private static FileIndexer indexer = tokenFactory.newFileIndexer();

	private static Preprocessor preprocessor = Front.newPreprocessor(Language.C,
			config, indexer, tokenFactory);

	private static PrintStream out = System.out;

	private static File root = new File(new File("examples"), "parse");

	private static File[] systemIncludes = new File[] {};

	private static File[] userIncludes = new File[] {
			new File(System.getProperty("user.dir")) };

	private void check(String filenameRoot)
			throws PreprocessorException, ParseException {
		File file = new File(root, filenameRoot + ".c");
		Parser parser = Front.newParser(Language.C);
		Map<String, String> macroMap = new HashMap<>();
		CivlcTokenSource tokenSource = preprocessor.preprocess(systemIncludes,
				userIncludes, macroMap, new File[] { file });
		ParseTree parseTree = parser.parse(tokenSource);
		CommonTree tree = parseTree.getRoot();

		if (debug)
			ANTLRUtils.printTree(out, tree);
	}
	
	private void abc_exec(String filename) throws ABCException {
		File file = new File(root, filename);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(Arrays.asList("prune", "sef"));
		task.setPrettyPrint(true);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}
	

	@Test
	public void if1() throws PreprocessorException, ParseException {
		check("if1");
	}

	@Test(expected=SyntaxException.class)
	public void bad_function_no_return_type() throws ABCException {
		abc_exec("c11_6.7.2.2_func_no_rtn_type.c");
	}
}
