package edu.udel.cis.vsl.civl.transform;

import static edu.udel.cis.vsl.civl.ConstantsTest.OMP_ONLY_SIMP;
import static edu.udel.cis.vsl.civl.ConstantsTest.QUIET;
import static edu.udel.cis.vsl.civl.ConstantsTest.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class OpenMPSimplifierTest {

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(
			new File(new File("examples"), "omp"), "simplifier");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void antidep1orig() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB001-antidep1-orig-yes.c")));
	}

	@Test
	public void antidep1origFix() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB001-antidep1-orig-fix.c")));
	}

	@Test
	public void antidep1var() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB002-antidep1-var-yes.c")));
	}

	@Test
	public void antidep1varFix() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB001-antidep1-orig-fix.c")));
	}

	@Test
	public void antidep2orig() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB003-antidep2-orig-yes.c")));
	}

	@Test
	public void antidep2origFix() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB003-antidep2-orig-fix.c")));
	}

	@Test
	public void indirectaccess1orig() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB005-indirectaccess1-orig-yes.c")));
	}

	@Test
	public void indirectaccess1origBadFix() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB005-indirectaccess1-orig-badFix.c")));
	}

	@Test
	public void indirectaccess1origFix() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB005-indirectaccess1-orig-fix.c")));
	}

	@Test
	public void allocNoAlias() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB066-pointernoaliasing-orig-no.c")));
	}

	@Test
	public void allocAlias() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("DRB066-pointernoaliasing-orig-bad.c")));
	}

	@Test
	public void structAlias() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structAlias.c")));
	}

	@Test
	public void structNoAlias() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structNoAlias.c")));
	}

	@Test
	public void structAlias2() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structAlias2.c")));
	}

	@Test
	public void structNoAlias2() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structNoAlias2.c")));
	}

	@Test
	public void structAlias3() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structAlias3.c")));
	}

	@Test
	public void structAlias3_1() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structAlias3.1.c")));
	}

	@Test
	public void structNoAlias3() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structNoAlias3.c")));
	}

	@Test
	public void structNoAlias3_1() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("structNoAlias3.1.c")));
	}

	@Test
	public void pointerAddAlias() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("pointerAddAlias.c")));
	}

	@Test
	public void pointerAddAlias2() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("pointerAddAlias2.c")));
	}

	@Test
	public void pointerAddNoAlias() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("pointerAddNoAlias.c")));
	}

	@Test
	public void simdNoSafelen() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("simd_no_safelen.c")));
	}

	@Test
	public void simdNoSafelenFix() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("simd_no_safelen_fix.c")));
	}

	@Test
	public void simdWithSafelen() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("simd_safelen.c")));
	}

	@Test
	public void arrayReshape() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("arrayReshape.c")));
	}

	@Test
	public void arrayReshape2Race() {
		assertFalse(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP,
				filename("arrayReshape2-yes.c")));
		System.err.println(
				"warning: this is a sound and expected result but not precise enough. "
						+ "Improvement is expected in near future.");
	}

	@Ignore // TODO:
	public void calls() {
		assertTrue(ui.run(VERIFY, QUIET, OMP_ONLY_SIMP, filename("calls.c")));
	}
}
