package edu.udel.cis.vsl.civl.fortran;

import static edu.udel.cis.vsl.civl.TestConstants.QUIET;
import static edu.udel.cis.vsl.civl.TestConstants.VERIFY;

import java.io.File;

import org.junit.Test;

import edu.udel.cis.vsl.civl.run.IF.UserInterface;

public class ProvesaMXMTest {
	private static File rootDir = new File(
			"examples/fortran/provesaExamples/MXM");

	private static UserInterface ui = new UserInterface();

	private static String filename(String name) {
		return new File(rootDir, name).getPath();
	}

	@Test
	public void verify_ex1a() {
		ui.run(VERIFY, QUIET, filename("mxmdriver.F"), filename("ex1a.F"));
	}
}
