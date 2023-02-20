package dev.civl.abc.parse;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.token.IF.Macro;
import dev.civl.abc.token.IF.SyntaxException;

public class ACSLTest {

	private boolean debug = false;
	private static PrintStream out = System.out;
	@SuppressWarnings("unused")
	private static Map<String, Macro> implicitMacros = new HashMap<>();
	@SuppressWarnings("unused")
	private static File[] systemIncludes = new File[0];
	@SuppressWarnings("unused")
	private static File[] userIncludes = new File[0];
	private static File root = new File(new File("examples"), "contract");
	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd frontEnd = new FrontEnd(config);

	private void parse(String name)
			throws PreprocessorException, SyntaxException, ParseException {
		File file = new File(root, name);
		AST result;

		// tokens = preprocessor.outputTokenSource(systemIncludes, userIncludes,
		// implicitMacros, file);
		result = frontEnd.compile(new File[]{file}, Language.CIVL_C);
		if (debug) {
			// result.print(out);
			result.prettyPrint(out, false);
			out.println();
			out.flush();
		}
	}

	private void checkAssignableForMemLocationSets(boolean expectingError,
			String filename) throws PreprocessorException, ParseException {
		File file = new File(root, filename);
		AST result;
		String expectedErrorMsg = "doesn't designate an object or a set of memory"
				+ " locations and thus can't be used as the left argument of assigns/reads";
		String errorMsg = null;

		try {
			result = frontEnd.compile(new File[]{file}, Language.CIVL_C);
			if (debug) {
				result.prettyPrint(out, false);
				out.println();
				out.flush();
			}
		} catch (SyntaxException e) {
			errorMsg = e.getMessage();
		} finally {
			if (expectingError)
				assertEquals(true, errorMsg != null
						&& errorMsg.endsWith(expectedErrorMsg));
			else
				assertEquals(true, errorMsg == null);
		}
	}

	@Test
	public void acslDemo()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("acslDemo.c");
	}

	@Test
	public void acslOperators()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("acslOperators.c");
	}

	@Test
	public void cqueue()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("cqueue.c");
	}

	@Test
	public void mpiCollective()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("mpiCollectiveTest.c");
	}

	@Test
	public void mpiConstants()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("mpiConstants.c");
	}

	@Test
	public void wildcard()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("wildcard_contract_bad.c");
	}

	@Test
	public void quantifiers()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("quantifiers.c");
	}

	@Test
	public void emptyInBad()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("isRecvBufEmpty_BAD.c");
	}

	@Test
	public void emptyInOk()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("isRecvBufEmpty_OK.c");
	}

	@Test
	public void loopInvariant()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("loopInvariant.c");
	}

	@Test
	public void validPointers()
			throws PreprocessorException, SyntaxException, ParseException {
		// this.parse("valid.c");
		parse("validPointers.c");
	}

	@Test
	public void remoteAccess()
			throws PreprocessorException, SyntaxException, ParseException {
		// this.parse("valid.c");
		parse("remoteAccess.c");
	}

	@Test
	public void memLocation1()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("memLocation1.c");
	}

	@Test
	public void pointers()
			throws PreprocessorException, SyntaxException, ParseException {
		this.parse("pointers.c");
	}

	@Test
	public void extendQuant()
			throws PreprocessorException, SyntaxException, ParseException {
		parse("extendQuant.c");
	}

	@Test
	public void notParseACSL()
			throws PreprocessorException, SyntaxException, ParseException {
		parse("notParseACSL.c");
	}

	@Test
	public void memArray2dSliceBad()
			throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_array2d_slice-bad.c");
	}

	@Test
	public void memArray2dSlice() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_array2d_slice.c");
	}

	@Test
	public void memArraySliceBad()
			throws PreprocessorException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_array_slice-bad.c");
		} catch (SyntaxException e) {
			assertEquals(true, e.getMessage().startsWith(
					"First argument to subscript operator not pointer to complete object type"));
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memArraySlice() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_array_slice.c");
	}

	@Test
	public void memArrowBad() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_arrow-bad.c");
	}

	@Test
	public void memArrow() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_arrow.c");
	}

	@Test
	public void memComplex() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_complex.c");
	}

	@Test
	public void memDotBad() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_dot-bad.c");
	}

	@Test
	public void memDot() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_dot.c");
	}

	@Test
	public void memPlus() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_plus.c");
	}

	@Test
	public void memPlusBad() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_plus-bad.c");
	}

	@Test
	public void memPlusBad2() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_plus-bad2.c");
	}

	@Test
	public void memPlusBad3() throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true, "acsl_mem_plus-bad3.c");
	}

	@Test
	public void memPointerPointer()
			throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_pointer_pointer.c");
	}

	@Test
	public void memPointerPointer2()
			throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(false, "acsl_mem_pointer_pointer2.c");
	}

	@Test
	public void memPointerPointerBad()
			throws PreprocessorException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_pointer_pointer-bad.c");
		} catch (SyntaxException e) {
			assertEquals(true, e.getMessage()
					.startsWith("Argument to * has non-pointer type"));
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memPointerPointerBad2()
			throws PreprocessorException, ParseException {
		checkAssignableForMemLocationSets(true,
				"acsl_mem_pointer_pointer-bad2.c");
	}

	@Test
	public void memPointerPointerBad3()
			throws PreprocessorException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_pointer_pointer-bad3.c");
		} catch (SyntaxException e) {
			assertEquals(true,
					e.getMessage().startsWith("Invalid arguments for +."));
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memPointerPointerBad4()
			throws PreprocessorException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_pointer_pointer-bad4.c");
		} catch (SyntaxException e) {
			assertEquals(true,
					e.getMessage().startsWith("Invalid arguments for +."));
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memSubscript()
			throws PreprocessorException, SyntaxException, ParseException {
		parse("acsl_mem_subscript.c");
	}

	@Test
	public void memSubscriptBad()
			throws PreprocessorException, SyntaxException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_subscript-bad.c");
		} catch (SyntaxException e) {
			checked = e.getMessage()
					.startsWith("First argument to subscript operator"
							+ " not pointer to complete object type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memSubscriptBad2()
			throws PreprocessorException, SyntaxException, ParseException {
		boolean checked = false;

		try {
			parse("acsl_mem_subscript-bad2.c");
		} catch (SyntaxException e) {
			checked = e.getMessage().endsWith(
					"shall have integer type or set of integer types");
		} finally {
			assertEquals(true, checked);
		}
	}
}
