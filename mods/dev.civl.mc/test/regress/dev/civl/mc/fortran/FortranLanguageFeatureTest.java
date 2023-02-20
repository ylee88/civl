package dev.civl.mc.fortran;

import static dev.civl.mc.TestConstants.OMP_NO_SIMP;
import static dev.civl.mc.TestConstants.OMP_THREAD_TWO;
import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;
import static dev.civl.mc.TestConstants.COMPARE;
import static dev.civl.mc.TestConstants.IMPL;
import static dev.civl.mc.TestConstants.SPEC;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class FortranLanguageFeatureTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	private static File DIR_ROOT = new File("examples/fortran/");
	private static File DIR_SMACK = new File(DIR_ROOT, "smack");
	private static File DIR_CIVL = new File(DIR_ROOT, "civl");
	private static UserInterface UI = new UserInterface();

	private static String filename(File dir, String fileName) {
		return new File(dir, fileName).getPath();
	}

	@Test
	public void civl_abs() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_CIVL, "abs.f90")));
	}

	@Test
	public void civl_array_section() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_CIVL, "array_section.f90")));
	}

	@Test
	public void civl_intent() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_CIVL, "intent.f90")));
	}

	@Test
	public void civl_omp_do() {
		assertTrue(UI.run(VERIFY, OMP_NO_SIMP, OMP_THREAD_TWO, QUIET, //
				filename(DIR_CIVL, "omp_do.f90")));
	}

	@Test
	public void civl_short_circuit() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_CIVL, "short_circuit.f90")));
	}

	@Test
	public void civl_truncate() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_CIVL, "truncate.f")));
	}

	@Test
	public void civl_modulo() {
		assertTrue(UI.run(COMPARE, QUIET, //
				SPEC, filename(DIR_CIVL, "mod_spec.f90"), //
				IMPL, filename(DIR_CIVL, "mod_impl.f90")));
	}

	@Test
	public void smack_array() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "array.f90")));
	}

	@Test
	public void smack_array_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "array_fail.f90")));
	}

	@Test
	public void smack_compound() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "compound.f90")));
	}

	@Test
	public void smack_compound_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "compound_fail.f90")));
	}

	@Test
	public void smack_compound_fail_2() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "compound_fail_2.f90")));
	}

	@Test
	public void smack_compute() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "compute.f90")));
	}

	@Test
	public void smack_compute_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "compute_fail.f90")));
	}

	@Test
	public void smack_fib() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "fib.f90")));
	}

	@Test
	public void smack_fib_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "fib_fail.f90")));
	}

	@Test
	public void smack_fib_fail_2() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "fib_fail_2.f90")));
	}

	@Test
	public void smack_forloop() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "forloop.f90")));
	}

	@Test
	public void smack_forloop_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "forloop_fail.f90")));
	}

	@Test
	public void smack_function() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "function.f90")));
	}

	@Test
	public void smack_function_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "function_fail.f90")));
	}

	@Test
	public void smack_function_fail_2() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "function_fail_2.f90")));
	}

	@Test
	public void smack_function_fail_3() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "function_fail_3.f90")));
	}

	@Test
	public void smack_hello() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "hello.f90")));
	}

	@Test
	public void smack_hello_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "hello_fail.f90")));
	}

	@Test
	public void smack_inout() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "inout.f90")));
	}

	@Test
	public void smack_inout_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "inout_fail.f90")));
	}

	@Test
	public void smack_pointer() {
		assertTrue(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "pointer.f90")));
	}

	@Test
	public void smack_pointer_fail() {
		assertFalse(UI.run(VERIFY, QUIET, //
				filename(DIR_SMACK, "pointer_fail.f90")));
	}

}
