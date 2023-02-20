package dev.civl.abc.preproc;

import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.main.ABC;

// TODO: fix these so they test something and don't write output

public class MacroTest {

	private void run(String... args) {
		ABC.main(args);
	}

	@Ignore
	// -D<name>
	@Test
	public void adder1() {
		run("-DCIVL_PROG", "-p", "examples/macro/adder.c");
	}

	@Ignore
	// -D<name>=<object>
	@Test
	public void adder2() {
		run("-p", "examples/macro/adder.c");
	}

	@Ignore
	@Test
	public void intro() {
		run("-DNAME=Joseph", "-DCOUNTRY=United States", "-DCITY=Newark",
				"-DSTATE=Delaware", "-E", "-v", "examples/macro/intro.txt");
	}
}
