package dev.civl.mc.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class MPIRoutineTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static File rootDir = new File(new File("examples"),
			"mpi/routines");

	private static UserInterface ui = new UserInterface();

	/* *************************** Helper Methods ************************** */

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	/* **************************** Test Methods *************************** */

	@Test
	public void mpiReduceLocal() {
		assertTrue(ui.run("verify -input_mpi_nprocs=1 -quiet",
				filename("mpi_reduce_local.c")));
	}

	@Test
	public void mpiReduceLocalBad() {
		// TODO: really want to check that there are 4 errors at 4 specific
		// locations
		assertFalse(ui.run("verify  -errorBound=4 -input_mpi_nprocs=1 -quiet",
				filename("mpi_reduce_local-bad.c")));
	}
	
	@Test
	public void mpiTypeSize() {
		assertTrue(ui.run("verify -input_mpi_nprocs=1 -quiet",
				filename("mpi_type_size.c")));
	}
	
	@Test
	public void mpiTypeSizeBad() {
		assertFalse(ui.run("verify  -errorBound=4 -input_mpi_nprocs=1 -quiet",
				filename("mpi_type_size-bad.c")));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ui = null;
		rootDir = null;
	}
}
