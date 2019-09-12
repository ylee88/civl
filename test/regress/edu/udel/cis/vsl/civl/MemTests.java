package edu.udel.cis.vsl.civl;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import edu.udel.cis.vsl.abc.err.IF.ABCException;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;
public class MemTests {

	static String QUIET = TestConstants.QUIET;

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(
			new File(new File("examples"), "mem"), "mem_tests");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods *************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */
	/* ******************** Test for Conversion **********************/
	@Test
	public void memConvertion() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_convertion.cvl")));
	}

	@Test
	public void memConvertion2() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_convertion2.cvl")));
	}

	@Test
	public void memConvertion3() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_convertion3.cvl")));
	}

	@Test
	public void memConvertionBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad.cvl")));
	}

	@Test
	public void memConvertionBad2() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad2.cvl")));
	}

	@Test
	public void memConvertionBad3() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad3.cvl")));
	}

	@Test
	public void memConvertionBad4() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad4.cvl")));
	}

	@Test
	public void memConvertionBad5() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad5.cvl")));
	}

	@Test
	public void memConvertionBad6() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_convertion-bad6.cvl")));
	}

	/* ********************* Test for Contains ************************/

	@Test
	public void memComplex() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_contains_complex.cvl")));
	}

	@Test
	public void memComplexBad1() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_complex-bad.cvl")));
	}

	@Test
	public void memComplexBad2() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_complex-bad2.cvl")));
	}

	@Test
	public void memComplex2() throws ABCException {
		assertTrue(
				ui.run(VERIFY, QUIET, filename("mem_contains_complex2.cvl")));
	}

	@Test
	public void memComplex2Bad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_complex2-bad.cvl")));
	}

	@Test
	public void memComplex2Bad2() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_complex2-bad2.cvl")));
	}

	@Test
	public void memMalloced() throws ABCException {
		assertTrue(
				ui.run(VERIFY, QUIET, filename("mem_contains_malloced.cvl")));
	}

	@Test
	public void memMallocedBad1() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_malloced-bad.cvl")));
	}

	@Test
	public void memMallocedBad2() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_malloced-bad2.cvl")));
	}

	@Test
	public void memArray2d() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_contains_array2d.cvl")));
	}

	@Test
	public void memArray2dBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_array2d-bad.cvl")));
	}

	@Test
	public void memArray3d() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_contains_array3d.cvl")));
	}

	@Test
	public void memArray3dBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_contains_array3d-bad.cvl")));
	}

	/* ************* Test for Union ****************/
	@Test
	public void memUnion() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_union.cvl")));
	}

	@Test
	public void memUnionBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_union-bad.cvl")));
	}

	@Test
	public void memUnion2() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_union2.cvl")));
	}

	@Test
	public void memUnion2Bad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_union2-bad.cvl")));
	}

	/* ************* Test for Union Widening ****************/
	@Test
	public void memUnionWideningArr() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_array.cvl")));
	}

	@Test
	public void memUnionWideningArrBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_array-bad.cvl")));
	}

	@Test
	public void memUnionWideningArr2d() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_array2d.cvl")));
	}

	@Test
	public void memUnionWideningArr2dBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_array2d-bad.cvl")));
	}

	@Test
	public void memUnionWidening2Arrs() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_2arrays.cvl")));
	}

	@Test
	public void memUnionWidening2ArrsBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_union_widening_2arrays-bad.cvl")));
	}

	/* ************* Test for Havoc ****************/
	@Test
	public void memHavocArr() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_array.cvl")));
	}

	@Test
	public void memHavocArrBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET, filename("mem_havoc_array-bad.cvl")));
	}

	@Test
	public void memHavocArr2d() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_array2d.cvl")));
	}

	@Test
	public void memHavocArr2dBad() throws ABCException {
		assertFalse(
				ui.run(VERIFY, QUIET, filename("mem_havoc_array2d-bad.cvl")));
	}

	@Test
	public void memHavocMalloced() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_malloced.cvl")));
	}

	@Test
	public void memHavocMallocedBad() throws ABCException {
		assertFalse(
				ui.run(VERIFY, QUIET, filename("mem_havoc_malloced-bad.cvl")));
	}

	@Test
	public void memHavocComplex() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_complex.cvl")));
	}

	@Test
	public void memHavocComplexBad() throws ABCException {
		assertFalse(
				ui.run(VERIFY, QUIET, filename("mem_havoc_complex-bad.cvl")));
	}

	@Test
	public void memHavocArrNonConcrete() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_array_nc.cvl")));
	}

	@Test
	public void memHavocArrNonConcreteBad() throws ABCException {
		assertFalse(
				ui.run(VERIFY, QUIET, filename("mem_havoc_array_nc-bad.cvl")));
	}

	@Test
	public void memHavocComplexNonConcrete() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET, filename("mem_havoc_complex_nc.cvl")));
	}

	@Test
	public void memHavocComplexNonConcreteBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_havoc_complex_nc-bad.cvl")));
	}

	@Test
	public void memHavocComplexNonConcreteBad2() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_havoc_complex_nc-bad2.cvl")));
	}

	@Test
	public void memNoIntersect() throws ABCException {
		assertTrue(ui.run(VERIFY, QUIET,
				filename("mem_no_intersect_array2d.cvl")));
	}

	@Test
	public void memNoIntersectBad() throws ABCException {
		assertFalse(ui.run(VERIFY, QUIET,
				filename("mem_no_intersect_array2d-bad.cvl")));
	}

}
