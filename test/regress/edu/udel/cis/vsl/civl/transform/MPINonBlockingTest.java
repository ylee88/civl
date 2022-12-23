package edu.udel.cis.vsl.civl.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;
import static edu.udel.cis.vsl.civl.TestConstants.MPI_NONBLOCKING_MODEL;

public class MPINonBlockingTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(
			new File(new File(new File("examples"), "mpi"), "mpiFeature"),
			"Test_nonblocking");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */
	@Test
	public void nonblockingSendRecvOrder() {
		assertTrue(ui.run("verify -input_mpi_nprocs=2", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_order.c")));
		assertTrue(ui.run("verify -input_mpi_nprocs=2", "-DTAG=MPI_ANY_TAG",
				TestConstants.QUIET, MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_order.c")));
		assertTrue(ui.run("verify -input_mpi_nprocs=4", "-DTAG=MPI_ANY_TAG",
				"-DWILDCARD", TestConstants.QUIET, MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_order.c")));
	}

	@Test
	public void nonblockingSendRecvOutorder() {
		assertTrue(ui.run("verify -input_mpi_nprocs=2", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_outorder.c")));
		assertTrue(ui.run("verify -input_mpi_nprocs=4", "-DWILDCARD",
				TestConstants.QUIET, MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_outorder.c")));
	}

	@Test
	public void nonblockingSendRecvRing() {
		assertTrue(ui.run("verify -input_mpi_nprocs=4", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_ring.c")));
	}

	@Test
	public void nonblockingSendRecvRingBad() {
		assertFalse(ui.run("verify -input_mpi_nprocs=4", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_ring-bad.c")));
	}

	@Test
	public void nonblockingSendRecvPingpong() {
		assertTrue(ui.run("verify -input_mpi_nprocs=2", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_pingpong.c")));
	}

	@Test
	public void nonblockingSendRecvPingpongDL() {
		assertFalse(ui.run("verify -input_mpi_nprocs=2", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_pingpong-DL.c")));
	}

	@Test
	public void nonblockingSendRecvWildcard() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_wildcard.c")));
	}

	@Test
	public void nonblockingSendRecvWildcardBad() {
		assertFalse(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_sendrecv_wildcard-bad.c")));
	}

	@Test
	public void nonblockingManytoOneOrder() {
		assertTrue(ui.run("verify -input_mpi_nprocs=4", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_order.c")));
	}

	@Test
	public void nonblockingManytoOneOrderMix() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_order_mix.cvl")));
	}

	@Test
	public void nonblockingManytoOneAnyorder() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_anyorder.c")));
	}

	@Test
	public void nonblockingManytoOneAnyorderMix() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_anyorder_mix.cvl")));
	}

	@Test
	public void nonblockingManytoSomeWildcards() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_some_widlcards.c")));
	}

	@Test
	public void nonblockingManytoSomeWildcardsBad() {
		assertFalse(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_some_widlcards-bad.c")));
	}

	@Test
	public void nonblockingManytoSomeWildcards2() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_some_widlcards2.c")));
	}

	@Test
	public void nonblockingManytoSomeWildcards3() {
		assertTrue(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_some_widlcards3.c")));
	}

	@Test
	public void nonblockingManytoSomeWildcardsDL() {
		assertFalse(ui.run("verify -input_mpi_nprocs=3", TestConstants.QUIET,
				MPI_NONBLOCKING_MODEL,
				filename("nonblocking_manytoone_some_widlcards-dl.c")));
	}

	@Ignore
	public void nonblockingManytoOneMixBuggy() {
		assertTrue(ui.run("verify -input_mpi_nprocs=5", MPI_NONBLOCKING_MODEL,
				filename("bug.cvl")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
