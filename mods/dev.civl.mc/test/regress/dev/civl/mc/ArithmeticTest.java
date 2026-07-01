package dev.civl.mc;

import static dev.civl.mc.TestConstants.DMATH_ELABORATE_ASSUMPTIONS;
import static dev.civl.mc.TestConstants.MIN;
import static dev.civl.mc.TestConstants.NO_PRINTF;
import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.RUN;
import static dev.civl.mc.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class ArithmeticTest {
	@SuppressWarnings("exports")
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"), "arithmetic");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private void check(String name) {
		assertTrue(ui.run(VERIFY, QUIET, filename(name)));
	}

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void algebra() {
		check("algebra.cvl");
	}

	@Test
	public void algebra2() {
		check("algebra2.cvl");
	}

	@Test
	public void assoc() {
		assertTrue(ui.run(VERIFY, QUIET, "-inputB=10", filename("assoc.cvl")));
	}

	@Test
	public void derivative() {
		check("derivative.cvl");
	}

	@Test
	public void diffusion() {
		check("diffusion.cvl");
	}

	@Test
	public void division() {
		check("division.cvl");
	}

	@Test
	public void divisionBad() {
		assertFalse(ui.run(VERIFY, QUIET, filename("divisionBad.cvl")));
	}

	@Test
	public void laplace() {
		check("laplace.cvl");
	}

	@Test
	public void matmat() {
		assertTrue(ui.run(VERIFY, QUIET, "-inputBOUND=3", filename("matmat.cvl")));
	}

	@Test
	public void matmatBad() {
		assertFalse(ui.run(VERIFY, QUIET, "-inputBOUND=3", filename("matmatBad.cvl")));
	}

	@Test
	public void mean() {
		assertTrue(ui.run(VERIFY, QUIET, "-inputB=10", filename("mean.cvl")));
	}

	@Test
	public void meanBad() {
		assertFalse(ui.run(VERIFY, QUIET, "-inputB=10", MIN, filename("meanBad.cvl")));
	}

	@Test
	public void multiplicationInLoopCondition() {
		assertTrue(ui.run(VERIFY, QUIET, filename("multiplicationInLoopCondition.cvl")));
	}

	@Test
	public void math() {
		assertTrue(ui.run(VERIFY, QUIET, NO_PRINTF, filename("mathematical.cvl")));
	}

	@Test
	public void exp1() {
		assertFalse(ui.run(RUN, QUIET, filename("div0.cvl")));
	}

	@Test
	public void sqrt() {
		assertTrue(ui.run(VERIFY, QUIET, filename("sqrt.cvl")));
	}

	@Test
	public void sqrt_elaborate() {
		assertTrue(ui.run(VERIFY, DMATH_ELABORATE_ASSUMPTIONS, QUIET, filename("sqrt.cvl")));
	}

	@Test
	public void sqrtBad1() {
		assertFalse(ui.run(VERIFY, QUIET, filename("sqrtBad1.cvl")));
	}

	@Test
	public void sqrtBad1_elaborate() {
		assertFalse(ui.run(VERIFY, DMATH_ELABORATE_ASSUMPTIONS, QUIET, filename("sqrtBad1.cvl")));
	}

	@Test
	public void sqrtBad2() {
		assertFalse(ui.run(VERIFY, QUIET, filename("sqrtBad2.cvl")));
	}

	@Test
	public void sqrtBad2_elaborate() {
		assertFalse(ui.run(VERIFY, DMATH_ELABORATE_ASSUMPTIONS, QUIET, filename("sqrtBad2.cvl")));
	}

	@Test
	public void quadratic1() {
		assertTrue(ui.run(VERIFY, QUIET, filename("quadratic1.cvl")));
	}

	@Test
	public void quadratic2() {
		assertTrue(ui.run(VERIFY, QUIET, filename("quadratic2.cvl")));
	}

	@Test
	public void sqrtCall() {
		assertTrue(ui.run(VERIFY, QUIET, filename("sqrtCall.cvl")));
	}

	@Test
	public void powerSimplify_posConstant_SymbolMonic() {
		assertFalse(ui.run(VERIFY, QUIET, filename("power_simplify1.cvl")));
	}

	@Test
	public void powerSimplify_negConstant_SymbolMonic() {
		assertFalse(ui.run(VERIFY, QUIET, filename("power_simplify2.cvl")));
	}

	@Test
	public void powerSimplify_negConstant_negMonic() {
		assertTrue(ui.run(VERIFY, QUIET, filename("power_simplify3.cvl")));
	}

	@Test
	public void floor() {
		assertTrue(ui.run(VERIFY, QUIET, filename("floor.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
