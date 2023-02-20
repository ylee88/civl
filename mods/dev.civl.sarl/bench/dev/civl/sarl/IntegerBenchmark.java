package dev.civl.sarl;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;

public class IntegerBenchmark {
	static FactorySystem system = PreUniverses.newIdealFactorySystem();
	private static PreUniverse universe = PreUniverses.newPreUniverse(system);
	
	static SymbolicConstant index = universe.symbolicConstant(
			universe.stringObject("name"), universe.integerType());
	static NumericExpression low = universe.integer(1000);
	static NumericExpression high = universe.integer(1000000);
	static BooleanExpression trueExp = universe.bool(true);

	public static void main(String[] args) {
		long startTime = System.nanoTime(), stopTime;
		double totalTime;
		
		// This is a benchmark for the 'forallInt()' method
		System.out.println("Benchmark for 'forallInt()'");
		universe.forallInt((NumericSymbolicConstant)index, 
				low, high, trueExp);
		stopTime = System.nanoTime();
		totalTime = ((double) (stopTime - startTime)) / 1000000000.0;
		System.out.println("Time (s): " + totalTime);
		
		// This is a benchmark for the 'existsInt()' method
		System.out.println("\nBenchmark for 'existsInt()");
		universe.existsInt((NumericSymbolicConstant)index,
				low, high, trueExp);
		stopTime = System.nanoTime();
		totalTime = ((double) (stopTime - startTime)) / 1000000000.0;
		System.out.println("Time (s): " + totalTime);
	}

}
