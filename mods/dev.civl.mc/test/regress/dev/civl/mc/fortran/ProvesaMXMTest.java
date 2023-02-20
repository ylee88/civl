package dev.civl.mc.fortran;

import static dev.civl.mc.TestConstants.QUIET;
import static dev.civl.mc.TestConstants.VERIFY;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.mc.run.IF.UserInterface;

public class ProvesaMXMTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(30);

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
