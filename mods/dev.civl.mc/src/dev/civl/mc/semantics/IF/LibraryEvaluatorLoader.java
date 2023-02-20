package dev.civl.mc.semantics.IF;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.model.IF.ModelFactory;

/**
 * The library evaluator loader provides the mechanism for loading the library
 * evaluator of a certain library.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public interface LibraryEvaluatorLoader {
	/**
	 * Obtains the library evaluator of the given name. Given the same name, it
	 * will always return the same instance of the library evaluator of that
	 * name.
	 * 
	 * @param name
	 *            The name of the library whose evaluator is to be obtained.
	 * @param primaryEvaluator
	 *            The CIVL evaluator of the system.
	 * @param modelFacotry
	 *            The model factory of the system.
	 * @param symbolicUtil
	 *            The symbolic utility for manipulations of symbolic
	 *            expressions.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @return The library evaluator of the given name.
	 * @throws LibraryLoaderException
	 *             If the library evaluator of the given name cannot be found.
	 */
	LibraryEvaluator getLibraryEvaluator(String name,
			Evaluator primaryEvaluator, ModelFactory modelFacotry,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer)
			throws LibraryLoaderException;
}
