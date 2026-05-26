package dev.civl.abc.preproc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.junit.Before;
import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.front.IF.PreprocessorRuntimeException;
import dev.civl.abc.front.c.preproc.CPreprocessor;
import dev.civl.abc.front.c.preproc.PreprocessorLexer;
import dev.civl.abc.front.c.preproc.PreprocessorParser;
import dev.civl.abc.front.c.preproc.PreprocessorUtils;
import dev.civl.abc.token.IF.FileIndexer;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.Tokens;
import dev.civl.abc.util.IF.ANTLRUtils;
import dev.civl.abc.util.IF.ANTLRUtils.LexerFactory;

public class PreprocessorTest {

	private static boolean debug = false;

	private static PrintStream out = System.out;

	private static File root = new File(new File("examples"), "preproc");

	private static File dir1 = new File(root, "dir1");

	private static File dir11 = new File(dir1, "dir1.1");

	private static File dir2 = new File(root, "dir2");

	private static File[] systemIncludes = new File[] { dir2 };

	private static File[] userIncludes = new File[] { dir1, dir11 };

	private static LexerFactory lf = new LexerFactory() {

		@Override
		public Lexer makeLexer(CharStream stream) {
			return new PreprocessorLexer(stream);
		}

	};

	private CPreprocessor p;

	@Before
	public void setUp() throws Exception {
		Configuration config = Configurations.newMinimalConfiguration();
		TokenFactory tf = Tokens.newTokenFactory();
		FileIndexer indexer = tf.newFileIndexer();

		p = new CPreprocessor(config, Language.C, indexer, tf);
	}

	private void lex(String rootName) throws IOException {
		String filename = (new File(root, rootName + ".txt")).getAbsolutePath();
		PrintStream lexOut = debug ? out : new PrintStream(new OutputStream() {
			public void write(int b) {
				// DO NOTHING
			}
		});

		ANTLRUtils.lex(lexOut, lf, filename);
	}

	private void readSource(TokenSource source) throws PreprocessorException {
		try {
			while (true) {
				Token token = source.nextToken();

				if (debug) {
					out.println(token);
				}
				if (token.getType() == PreprocessorParser.EOF)
					break;
			}
		} catch (PreprocessorRuntimeException e) {
			throw new PreprocessorException(e.toString());
		}
	}

	private void check(String rootName) throws PreprocessorException {
		File sourceFile = new File(root, rootName + ".txt");
		TokenSource source = p.preprocess(systemIncludes, userIncludes, new HashMap<String, String>(),
				new File[] { sourceFile });

		readSource(source);
	}

	/**
	 * Compares the two token sources for equivalence modulo white space.
	 * 
	 * @param actualSource
	 * @param expectedSource
	 */
	private void compare(TokenSource actualSource, TokenSource expectedSource) {
		while (true) {
			Token token1, token2;
			int type1, type2;

			do {
				token1 = actualSource.nextToken();
				type1 = token1.getType();
			} while (PreprocessorUtils.isWhiteSpace(token1));
			do {
				token2 = expectedSource.nextToken();
				// PreprocessorUtils.convertPreprocessorIdentifiers(token2);
				type2 = token2.getType();
			} while (PreprocessorUtils.isWhiteSpace(token2));
			if (debug) {
				out.println("actual: " + token1 + "     expected: " + token2);
				out.flush();
			}
			if (type1 == PreprocessorParser.PPRAGMA) {
				// expected should be two tokens: #, pragma
				assertEquals(PreprocessorParser.HASH, type2);
				do {
					token2 = expectedSource.nextToken();
					type2 = token2.getType();
				} while (PreprocessorUtils.isWhiteSpace(token2));
				if (debug) {
					out.println("expected also: " + token2);
					out.flush();
				}
				assertEquals(PreprocessorParser.PRAGMA, type2);
			} else {
				// the types are not necessarily equal because the preprocessor
				// will change the types of tokens which are preprocessor
				// keywords (e.g., "define") to IDENTIFIER.
				// assertEquals(type2, type1);
				if (type1 == PreprocessorParser.EOF)
					break;
				assertEquals(token2.getText(), token1.getText());
			}

		}
	}

	private void checkPair(String rootName) throws PreprocessorException {
		File sourceFile = new File(root, rootName + ".txt");
		File solutionFile = new File(root, rootName + ".sol.txt");
		TokenSource actualSource = p.preprocess(systemIncludes, userIncludes, new HashMap<String, String>(),
				new File[] { sourceFile });
		// readSource(actualSource); // for debugging only
		TokenSource expectedSource = p.lexer(solutionFile);
		compare(actualSource, expectedSource);
	}

	@Test
	public void backslashNewline1() throws PreprocessorException {
		checkPair("backslashNewline1");
	}

	@Test
	public void backslashNewline2() throws PreprocessorException {
		checkPair("backslashNewline2");
	}

	@Test
	public void conditions1() throws PreprocessorException {
		checkPair("conditions1");
	}

	@Test
	public void conditions2() throws PreprocessorException {
		checkPair("conditions2");
	}

	@Test
	public void expressions() throws PreprocessorException {
		checkPair("expressions");
	}

	@Test
	public void nondirectives() throws PreprocessorException {
		checkPair("nondirectives");
	}

	@Test(expected = PreprocessorException.class)
	public void badIfTest() throws PreprocessorException {
		check("badIf");
	}

	@Test
	public void ifdef() throws PreprocessorException {
		checkPair("ifdef");
	}

	@Test
	public void objectMacros() throws PreprocessorException {
		checkPair("objectMacros");
	}

	@Test
	public void functionMacros() throws PreprocessorException {
		checkPair("functionMacros");
	}

	@Test
	public void functionMacros2() throws PreprocessorException {
		checkPair("functionMacros2");
	}

	/**
	 * One function macro invokes another function macro in its replacement list.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void functionMacros3() throws PreprocessorException {
		checkPair("functionMacros3");
	}

	/**
	 * One function macro invokes another function macro in its replacement list,
	 * and the second function macro has empty replacement list.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void functionMacros4() throws PreprocessorException {
		checkPair("functionMacros4");
	}

	@Test
	public void emptyMacros() throws PreprocessorException {
		checkPair("emptyMacros");
	}

	@Test
	public void selfRefMacros() throws PreprocessorException {
		checkPair("selfRefMacros");
	}

	/**
	 * Checks that an exception is thrown when a sequence of tokens that can be
	 * interpreted as a preprocessor directive occurs in an argument of a macro
	 * invocation. According to C11, the behavior in this case is undefined, so we
	 * choose to report it as error.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badMacroArguments() throws PreprocessorException {
		check("badMacroArguments");
	}

	/**
	 * Checks that an exception occurs when a 0-argument function macro is invoked
	 * with an argument.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badMacroArguments2() throws PreprocessorException {
		check("badMacroArguments2");
	}

	/**
	 * Checks that an exception occurs when a 0-argument function macro is invoked
	 * incorrectly, with an open '(' but no matching ')'.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badMacroArguments3() throws PreprocessorException {
		check("badMacroArguments3");
	}

	/**
	 * Checks that an exception is thrown when a 1-argument function macro is
	 * invoked with 2 arguments.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void tooManyArguments() throws PreprocessorException {
		check("tooManyArguments");
	}

	/**
	 * Checks that #include directives work as expected, for both system includes
	 * and user includes.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void includes() throws PreprocessorException {
		checkPair("includes");
	}

	/**
	 * Checks that when a #error directive is processed, an exception is thrown.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void error() throws PreprocessorException {
		check("error");
	}

	/**
	 * This just tests that the pragma is preserved in the output, except that macro
	 * substitution does occur.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void pragmas() throws PreprocessorException {
		checkPair("pragmas");
	}

	/**
	 * Checks that #undef works as expected---results in the macro becoming
	 * undefined.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void undef() throws PreprocessorException {
		check("undef");
	}

	/**
	 * Checks that an error occurs if one attempts to re-define an object macro in a
	 * different way. See C11 Sec 6.10.3.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badDef() throws PreprocessorException {
		check("badDef");
	}

	/**
	 * Checks that no error is generated when a macro is defined twice using
	 * identical definitions. See C11 Sec 6.10.3.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void doubleDef() throws PreprocessorException {
		check("doubleDef");
	}

	/**
	 * Checks that an error occurs if one attempts to #include a file that is not in
	 * any of the search paths.
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badInclude() throws PreprocessorException {
		check("badInclude");
	}

	/**
	 * Checks that an error occurs if one attempts to #include a file named "".
	 * 
	 * @throws PreprocessorException always
	 */
	@Test(expected = PreprocessorException.class)
	public void badIncludeName() throws PreprocessorException {
		check("badIncludeName");
	}

	/**
	 * Checks that "#if 1" followed immediately by "#endif" yields nothing--when all
	 * alone.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void trivialConditional() throws PreprocessorException {
		checkPair("trivialConditional");
	}

	/**
	 * Checks that "#if 1" followed immediately by "#endif" yields nothing--when
	 * inserted in the middle of some lines.
	 * 
	 * @throws PreprocessorException never
	 */
	@Test
	public void trivialConditional2() throws PreprocessorException {
		checkPair("trivialConditional2");
	}

	/**
	 * Checks that two adjacent string literals are concatenated.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void concat() throws PreprocessorException {
		checkPair("concat");
	}

	/**
	 * Checks use of ## in object macro.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void concat2() throws PreprocessorException {
		checkPair("concat2");
	}

	/**
	 * Checks use of ## in function macro.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void concat3() throws PreprocessorException {
		checkPair("concat3");
	}

	/**
	 * Checks use of ## in function macro.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void concat4() throws PreprocessorException {
		checkPair("concat4");
	}

	/**
	 * Checks use of ## in function macro: checks that exception is thrown if result
	 * of concatenation is not a token.
	 * 
	 * @throws PreprocessorException
	 */
	@Test(expected = PreprocessorRuntimeException.class)
	public void concat5() throws PreprocessorException {
		checkPair("concat5");
	}

	/**
	 * Checks use of ## in function macro --- that second expansion occurs after
	 * concatenation.
	 * 
	 * @throws PreprocessorException
	 */
	@Test
	public void concat6() throws PreprocessorException {
		checkPair("concat6");
	}

	@Test
	public void stringification1() throws PreprocessorException {
		checkPair("stringification1");
	}

	@Test
	public void stringification2() throws PreprocessorException {
		checkPair("stringification2");
	}

	@Test
	public void variadic1() throws PreprocessorException {
		checkPair("variadic1");
	}

	@Test
	public void variadic2() throws PreprocessorException {
		checkPair("variadic2");
	}

	@Test
	public void once() throws PreprocessorException {
		checkPair("once");
	}

	@Test
	public void lex_test() throws IOException {
		lex("conditions2");
	}

	@Test
	public void dotdot_ppnumber() throws IOException, PreprocessorException {
		check("dotdot_preproc");
	}
}
