package dev.civl.sarl.preuniverse.common;

import java.util.Arrays;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverses;

/**
 * 
 * @author Mohammad Alsulmi (malsulmi)
 * 
 *         The main purpose of the benchmark is to provide a performance
 *         comparison between array() and append() methods that are used in
 *         creating arrays
 * 
 *         Here, our comparison will be a time comparison
 */

public class ArrayBenchmark {

	public final static SymbolicUniverse universe = SARL.newIdealUniverse();
	public final static FactorySystem system = PreUniverses
			.newIdealFactorySystem();

	public final static SymbolicType integerType = universe.integerType();

	public final static ExpressionFactory expressionFactory = system
			.expressionFactory();

	public static SymbolicExpression array;

	public static void main(String[] args) {
		NumericExpression elementsArray[];
		int maxSize = (int) Math.pow(2, 15);
		int size;
		long startingTime, endingTime;
		double totalTime;
		System.out.println("Testing of array creation using array method");
		// the case of array creation
		for (int i = 1; i <= maxSize; i = i * 2) {
			size = i;
			// starting the time
			startingTime = System.nanoTime();
			elementsArray = new NumericExpression[size];
			for (int j = 0; j < size; j++) {
				elementsArray[j] = universe.integer(j);
			}
			array = universe.array(integerType, Arrays.asList(elementsArray));
			endingTime = System.nanoTime();

			// total time calculation
			totalTime = ((double) (endingTime - startingTime)) / 1000000000.0;
			System.out.println("Total Time in seconds: " + totalTime
					+ " for size: " + size);

		}
		System.out.println("Testing of array creation using append method");

		// the case of append to an empty array

		for (int i = 1; i <= maxSize; i = i * 2) {
			size = i;
			// starting the time
			startingTime = System.nanoTime();
			elementsArray = new NumericExpression[0];
			array = universe.array(integerType, Arrays.asList(elementsArray));

			for (int j = 0; j < size; j++) {
				array = universe.append(array, universe.integer(j));
			}
			// stopping the time
			endingTime = System.nanoTime();

			// total time calculation
			totalTime = ((double) (endingTime - startingTime)) / 1000000000.0;
			System.out.println("Total Time in seconds: " + totalTime
					+ " for size: " + size);

		}

	}

}
