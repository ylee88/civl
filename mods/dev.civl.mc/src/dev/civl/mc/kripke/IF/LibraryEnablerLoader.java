package dev.civl.mc.kripke.IF;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryLoaderException;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;

/**
 * The library enabler loader provides the mechanism for loading the library
 * enabler of a certain library.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public interface LibraryEnablerLoader {
	/**
	 * Obtains the library executor of the given name. Given the same name, it
	 * will always return the same instance of the library enabler of that name.
	 * 
	 * @param name
	 *            The name of the library whose enabler is to be obtained.
	 * @param primaryEnabler
	 *            The CIVL enabler for normal CIVL statements.
	 * @param evaluator
	 *            The CIVL evaluator of the system.
	 * @param modelFacotry
	 *            The model factory to be used in the library enabler.
	 * @param symbolicUtil
	 *            The symbolic utility for manipulations of symbolic
	 *            expressions.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @return The library enabler of the given name.
	 * @throws LibraryLoaderException 
	 */
	LibraryEnabler getLibraryEnabler(String name, Enabler primaryEnabler,
			Evaluator evaluator, ModelFactory modelFacotry,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer)
			throws LibraryLoaderException;
}
