package edu.udel.cis.vsl.civl.transform;

import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class ContractTest {
	/* *************************** Static Fields *************************** */

	private static UserInterface ui = new UserInterface();

	private static File rootDir = new File(new File("examples"), "contracts");

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	private static String MPI_NPROCS = "-input_mpi_nprocs=2";

	@Test
	public void allgather() {
		assertTrue(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=allgather",
				filename("/contractsMPI/civl_mpi_collectives/allgather.c")));
	}

	@Test
	public void allgatherEnsuresBad() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, "-mpiContract=allgather",
				TestConstants.QUIET, "-collectSymbolicConstants=true", filename(
						"/contractsMPI/civl_mpi_collectives/allgather-bad_ensures.c")));
	}

	@Test
	public void allgatherDatasizeBad() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=allgather", "-collectSymbolicConstants=true",
				filename(
						"/contractsMPI/civl_mpi_collectives/allgather-bad_datasize.c")));
	}

	@Test
	public void allgatherImplBad() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=allgather", "-collectSymbolicConstants=true",
				filename(
						"/contractsMPI/civl_mpi_collectives/allgather-bad_impl.c")));
	}

	@Test
	public void broadcast() {
		assertTrue(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=broadcast", "-collectSymbolicConstants=true",
				filename("/contractsMPI/civl_mpi_collectives/broadcast.c")));
	}

	@Test
	public void broadcastBad() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=broadcast", "-collectSymbolicConstants=true",
				filename("/contractsMPI/broadcast-bad_ensures.c")));
	}

	@Test
	public void diff1dExchange() {
		assertTrue(ui.run(VERIFY, "-mpiContract=exchange_ghost_cells",
				TestConstants.QUIET, "-collectSymbolicConstants=true",
				MPI_NPROCS, filename("/contractsMPI/diffusion1d.c")));
	}

	@Test
	public void diff1dExchange_ensures_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=exchange_ghost_cells",
				"-collectSymbolicConstants=true", TestConstants.QUIET,
				MPI_NPROCS,
				filename("/contractsMPI/diffusion1dExchange_ensures_bad.c")));
	}

	@Test
	public void diff1dExchange_impl_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=exchange_ghost_cells",
				"-collectSymbolicConstants=true", TestConstants.QUIET,
				MPI_NPROCS,
				filename("/contractsMPI/diffusion1dExchange_impl_bad.c")));
	}

	// TODO: this example cannot scale to nprocs > 3, nearly 1000 seconds
	// required for nprocs == 3
	@Test
	public void diff1dIter() {
		assertTrue(ui.run(VERIFY, MPI_NPROCS, "-mpiContract=diff1d_iter",
				TestConstants.QUIET, "-collectSymbolicConstants",
				filename("/contractsMPI/diffusion1d.c")));
	}

	@Test
	public void diff1dIter_ensures_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=diff1d_iter",
				TestConstants.QUIET, MPI_NPROCS, "-collectSymbolicConstants",
				filename("/contractsMPI/diffusion1dIter-ensures_bad.c")));
	}

	@Test
	public void diff1dIter_ensures_bad2() {
		assertFalse(ui.run(VERIFY, "-mpiContract=diff1d_iter",
				TestConstants.QUIET, MPI_NPROCS, "-collectSymbolicConstants",
				filename("/contractsMPI/diffusion1dIter-ensures_bad2.c")));
	}

	@Test
	public void diff1dIter_ensures_bad3() {
		assertFalse(ui.run(VERIFY, "-mpiContract=diff1d_iter",
				TestConstants.QUIET, MPI_NPROCS, "-collectSymbolicConstants",
				filename("/contractsMPI/diffusion1dIter-ensures_bad3.c")));
	}

	@Test
	public void diff1dIter_requires_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=diff1d_iter",
				TestConstants.QUIET, MPI_NPROCS, "-collectSymbolicConstants",
				filename("/contractsMPI/diffusion1dIter-requires_bad.c")));
	}

	@Test
	public void diff1dUpdate() {
		assertTrue(ui.run(VERIFY, "-mpiContract=update", TestConstants.QUIET,
				"-loop", "-input_mpi_nprocs=1",
				filename("/contractsMPI/diffusion1d.c")));
	}

	@Test
	public void diff1dUpdate_assert_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=update", TestConstants.QUIET,
				"-loop", "-input_mpi_nprocs=1",
				filename("/contractsMPI/diffusion1dUpdate_assert_bad.c")));
	}

	@Test
	public void diff1dUpdate_ensures_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=update", TestConstants.QUIET,
				"-loop", "-input_mpi_nprocs=1",
				filename("/contractsMPI/diffusion1dUpdate_ensures_bad.c")));
	}

	@Test
	public void diff1dUpdate_loop_bad() {
		assertFalse(ui.run(VERIFY, "-mpiContract=update", TestConstants.QUIET,
				"-loop", "-input_mpi_nprocs=1",
				filename("/contractsMPI/diffusion1dUpdate_loop_bad.c")));
	}

	@Test
	public void diff2dUpdate() {
		assertTrue(ui.run(VERIFY, TestConstants.QUIET, "-mpiContract=update",
				"-loop", "-input_mpi_nprocs=1",
				filename("/contractsMPI/diffusion2d.c")));
	}

	@Test
	public void diff2dExchange() {
		assertTrue(ui.run("verify", MPI_NPROCS, TestConstants.QUIET, "-loop",
				"-mpiContract=exchange",
				filename("/contractsMPI/diffusion2d.c")));
	}

	@Test
	public void diff2dExchangeBadImpl() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET, "-loop",
				"-mpiContract=exchange",
				filename("/contractsMPI/diffusion2d_dev-impl_bad.c")));
	}

	@Test
	public void diff2dExchangeBadEnsure() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET, "-loop",
				"-mpiContract=exchange",
				filename("/contractsMPI/diffusion2d_dev-ensures_bad.c")));
	}

	@Test
	public void gather() {
		assertTrue(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=gather", "-collectSymbolicConstants=true",
				filename("/contractsMPI/civl_mpi_collectives/gather.c")));
	}

	@Test
	public void gatherBad() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=gather", "-collectSymbolicConstants=true",
				filename(
						"/contractsMPI/civl_mpi_collectives/gather-bad_impl.c")));
	}

	@Test
	public void scatter() {
		assertTrue(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=scatter", "-collectSymbolicConstants=true",
				filename("/contractsMPI/civl_mpi_collectives/scatter.c")));
	}

	@Test
	public void scatterBadEnsures() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=scatter", "-collectSymbolicConstants=true",
				filename(
						"/contractsMPI/civl_mpi_collectives/scatter-bad_ensures.c")));
	}

	@Test
	public void scatterBadImpl() {
		assertFalse(ui.run(VERIFY, MPI_NPROCS, TestConstants.QUIET,
				"-mpiContract=scatter", "-collectSymbolicConstants=true",
				filename(
						"/contractsMPI/civl_mpi_collectives/scatter-bad_impl.c")));
	}

	/* **************************** Unit Tests *******************************/

	@Test
	public void assignsTest() {
		assertTrue(ui.run(TestConstants.SHOW, TestConstants.QUIET,
				"-mpiContract=g", filename("/contractsMPI/acslAssignsTest.c")));
	}

	@Ignore
	public void acslPredicate() {
		assertTrue(ui.run(VERIFY, TestConstants.QUIET,
				filename("/../contracts/pred.cvl")));
	}

	@Ignore
	public void acslPredicateBad() {
		assertFalse(ui.run(VERIFY, TestConstants.QUIET,
				filename("/../contracts/pred_bad.cvl")));
	}
}
