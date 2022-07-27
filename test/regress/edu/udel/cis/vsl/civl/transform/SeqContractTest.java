package edu.udel.cis.vsl.civl.transform;

import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.udel.cis.vsl.civl.TestConstants;
import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class SeqContractTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

	/* *************************** Static Fields *************************** */

	private static UserInterface ui = new UserInterface();

	private static File rootDir = new File(new File("examples"),
			"contracts/contractsSeq");

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void sum() {
		assertTrue(ui.run(VERIFY, TestConstants.QUIET,
				"-mpiContract=_CIVL_CONTRACT_ALL", "-loop",
				filename("sum.cvl")));
	}

}
