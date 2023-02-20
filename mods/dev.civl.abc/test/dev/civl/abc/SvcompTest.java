package dev.civl.abc;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.config.IF.Configuration.Architecture;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.common.Pruner;
import dev.civl.abc.transform.common.SideEffectRemover;

public class SvcompTest {
	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = false;

	private static File root = new File(new File("examples"), "svcomp");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private File file(String name) {
		return new File(root, name);
	}

	private void check(File file) throws ABCException {
		TranslationTask task = new TranslationTask(file);

		task.setVerbose(debug);
		task.setSilent(!debug);
		task.setShowUndefinedFunctions(debug);
		task.setSVCOMP(true);
		task.addTransformCode(SideEffectRemover.CODE);
		task.addTransformCode(Pruner.CODE);
		task.setArchitecture(Architecture._32_BIT);

		// debugging:
		// task.setStage(TranslationStage.PREPROCESS_CONSUME);

		ABCExecutor.execute(task);
	}

	// this test checks for the GNU C feature __attribute__(...)
	@Test
	public void queue_ok_longest_true() throws ABCException {
		check(this.file("queue_ok_longest_true-unreach-call.c"));
	}

	// this test checks for the GNU C feature that allows zero parameter for a
	// function prototype, non-return-type function, implicit functions
	@Test
	public void function() throws ABCException {
		check(this.file("function.c"));
	}

	@Test
	public void array() throws ABCException {
		check(this.file("array.c"));
	}

	@Test
	public void svcompHeader() throws ABCException {
		check(this.file("svcompHeader.c"));
	}

	@Test
	public void integerpromotion_false() throws ABCException {
		check(this.file("integerpromotion_false-unreach-call.i"));
	}

	@Test
	public void pointerIntConversions() throws ABCException {
		check(this.file("pointerIntConversions.c"));
	}

	@Test
	public void sssc12_variant() throws ABCException {
		check(this.file("sssc12_variant_true-unreach-call.i"));
	}

	@Test
	public void simple_loop5() throws ABCException {
		check(this.file("31_simple_loop5_vs_true-unreach-call.i"));
	}

	// implicit functions
	@Ignore
	@Test
	public void cdaudio_simpl1() throws ABCException {
		check(this.file(
				"cdaudio_simpl1_false-unreach-call_true-termination.cil.c"));
	}

	@Test
	public void emptyStruct() throws ABCException {
		check(this.file("emptyStruct.c"));
	}

	@Test
	public void sll_to_dll_rev() throws ABCException {
		check(this.file("sll_to_dll_rev_false-unreach-call.i"));
	}

	// failing because svcomp.h and this input file have
	// conflicting definitions of malloc, due to conflicting
	// definitions of size_t
	@Test
	public void parport() throws ABCException {
		check(this.file("parport_true-unreach-call.i.cil.c"));
	}

	@Test
	public void cs_fib() throws ABCException {
		check(this.file("cs_fib_false-unreach-call.i"));
	}

	@Test
	public void fpointer() throws ABCException {
		check(this.file("fpointer.c"));
	}

	@Test
	public void noEOF() throws ABCException {
		check(this.file("noEOF.c"));
	}

	@Test
	public void parts() throws ABCException {
		check(this.file("Parts_true-termination.c"));
	}

	@Test
	public void cond() throws ABCException {
		check(this.file("cond.c"));
	}

	// lazy01_false-unreach-call.i
	@Test
	public void lazy01_false() throws ABCException {
		check(this.file("lazy01_false-unreach-call.i"));
	}

	@Test
	public void implicitFunction() throws ABCException {
		check(this.file("implicitFunction.c"));
	}

	@Test
	public void emptyReturn() throws ABCException {
		check(this.file("emptyReturn.c"));
	}

	@Test
	public void statementExpression() throws ABCException {
		check(this.file("statementExpression.c"));
	}

	@Test
	public void typeof() throws ABCException {
		check(this.file("typeof.c"));
	}

	@Test
	public void externVar() throws ABCException {
		check(this.file("externVar.c"));
	}

	// TODO: Why? What's the bug?
	@Test(expected = SyntaxException.class)
	public void svcompbug() throws ABCException {
		check(this.file("svcomp_frontend_bug.c"));
	}

	/**
	 * Checks that ABC can parse assembly "asm" statements but will ignore them.
	 * 
	 * @throws ABCException
	 *             never
	 */
	@Test
	public void asm() throws ABCException {
		check(this.file("asm_ignore.c"));
	}

	/**
	 * Checks validity of pointer addition when the pointer has type void*,
	 * which is allowed in GNU-C, and therefore in SV-COMP.
	 * 
	 * @throws ABCException
	 *             never
	 */
	@Test
	public void void_star_add() throws ABCException {
		check(this.file("void_star_add.c"));
	}
}
