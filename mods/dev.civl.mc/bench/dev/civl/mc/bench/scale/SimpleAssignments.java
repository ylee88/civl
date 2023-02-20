package dev.civl.mc.bench.scale;

import dev.civl.mc.run.IF.UserInterface;

/**
 * Benchmark of the adder example. Execution time should be within 20 to 58
 * seconds.
 * 
 * @author zmanchun
 * 
 */
public class SimpleAssignments {
	private static UserInterface ui = new UserInterface();

	public static void main(String[] args) {
		// time (s) : 157.0 on macbook air with i5 CPU
		ui.run("verify -DSIMPLIFY" + " ./examples/bench/assignments.cvl");
		// time (s) : 46.67 on macbook air with i5 CPU
		ui.run("verify -simplify=false" + " ./examples/bench/assignments.cvl");
	}
}
