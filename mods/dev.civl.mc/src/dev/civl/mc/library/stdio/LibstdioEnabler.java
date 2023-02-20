package dev.civl.mc.library.stdio;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.library.common.BaseLibraryEnabler;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;

/**
 * Implementation of the enabler-related logics for system functions declared
 * stdio.h.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public class LibstdioEnabler extends BaseLibraryEnabler
		implements
			LibraryEnabler {

	/* **************************** Constructors *************************** */

	/**
	 * Creates a new instance of the library enabler for stdio.h.
	 * 
	 * @param primaryEnabler
	 *            The enabler for normal CIVL execution.
	 * @param output
	 *            The output stream to be used in the enabler.
	 * @param modelFactory
	 *            The model factory of the system.
	 */
	public LibstdioEnabler(String name, Enabler primaryEnabler,
			Evaluator evaluator, ModelFactory modelFactory,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer,
			CIVLConfiguration civlConfig, LibraryEnablerLoader libEnablerLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryEnabler, evaluator, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libEnablerLoader,
				libEvaluatorLoader);
	}

}
