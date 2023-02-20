package dev.civl.abc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.token.IF.SyntaxException;

public class CIVLMemTest {

	private static boolean debug = false;

	private static List<String> codes = Arrays.asList("prune", "sef");

	private File root = new File(new File(new File("examples"), "civl"), "mem");

	private void checkFile(String filename) throws ABCException {
		File file = new File(root, filename);
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setPrettyPrint(true);
		task.setVerbose(debug);
		ABCExecutor.execute(task);
	}

	private void check(String filenameRoot) throws ABCException {
		checkFile(filenameRoot + ".cvl");
	}

	@Test
	public void memDecl() throws ABCException {
		check("memDecl");
	}

	@Test
	public void memDeclBad() throws ABCException {
		boolean checked = false;

		try {
			check("memDecl-bad");
		} catch (SyntaxException e) {
			checked = e.getMessage()
					.startsWith("Incompatible types for operator EQUALS");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memCall() throws ABCException {
		check("memCall");
	}

	@Test
	public void memCallBad() throws ABCException {
		boolean checked = false;

		try {
			check("memCall-bad");
		} catch (SyntaxException e) {
			checked = e.getMessage().startsWith(
					"No conversion from set of non-pointer type Type[kind=SET] to $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memCallBad2() throws ABCException {
		boolean checked = false;

		try {
			check("memConversionBad");
		} catch (SyntaxException e) {
			checked = e.getMessage()
					.startsWith("No conversion from non set-type")
					&& e.getMessage().endsWith("to $mem type.");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestriction() throws ABCException {
		check("mem_conversion_restriction");
	}

	@Test
	public void memConversionRestriction2() throws ABCException {
		check("mem_conversion_restriction2");
	}

	@Test
	public void memConversionRestriction3() throws ABCException {
		check("mem_conversion_restriction3");
	}

	@Test
	public void memConversionRestriction4() throws ABCException {
		check("mem_conversion_restriction4");
	}

	@Test
	public void memConversionRestriction5() throws ABCException {
		check("mem_conversion_restriction5");
	}

	@Test
	public void memConversionRestriction6() throws ABCException {
		check("mem_conversion_restriction6");
	}

	@Test
	public void memConversionRestrictionBad() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().endsWith(
					"is not allowed to be an sub-expression of an expression that will be converted to have $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestrictionBad2() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction2_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().endsWith(
					"is not allowed to be an sub-expression of an expression that will be converted to have $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestrictionBad3() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction3_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().contains("is not allowed because")
					&& e.getMessage().contains("has set type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestrictionBad4() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction4_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().endsWith(
					"is not allowed to be an sub-expression of an expression that will be converted to have $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestrictionBad5() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction5_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().endsWith(
					"is not allowed to be an sub-expression of an expression that will be converted to have $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void memConversionRestrictionBad6() throws ABCException {
		boolean checked = false;

		try {
			check("mem_conversion_restriction6_bad");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = e.getMessage().endsWith(
					"is not allowed to be an sub-expression of an expression that will be converted to have $mem type");
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void invalidSetTerms() throws ABCException {
		boolean checked = false;

		try {
			check("invalid_set_terms");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}

	@Test
	public void invalidSetTerms2() throws ABCException {
		boolean checked = false;

		try {
			check("invalid_set_terms2");
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
			checked = true;
		} finally {
			assertEquals(true, checked);
		}
	}
}
