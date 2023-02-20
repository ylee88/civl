package dev.civl.sarl.ideal;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * Benchmark test which computes the polynomial (x+y)^N for a big integer N.
 * 
 * @author siegel
 * 
 */
public class BigPowerBenchmark {

	/** The exponent N */
	public final static int N = 2000;

	public final static SymbolicUniverse universe = SARL.newIdealUniverse();

	public final static SymbolicType realType = universe.realType();

	public final static NumericSymbolicConstant x = (NumericSymbolicConstant) universe
			.symbolicConstant(universe.stringObject("x"), realType);

	public final static NumericSymbolicConstant y = (NumericSymbolicConstant) universe
			.symbolicConstant(universe.stringObject("y"), realType);

	public final static NumericExpression xpy = universe.add(x, y);

	/**
	 * Runs the test, prints the total time, takes no arguments.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime(), stopTime;
		double totalTime;

		NumericExpression result = universe.power(xpy, N);
		stopTime = System.nanoTime();
		totalTime = ((double) (stopTime - startTime)) / 1000000000.0;
		System.out.println(result);
		System.out.println("Time (s): " + totalTime);
	}

}
