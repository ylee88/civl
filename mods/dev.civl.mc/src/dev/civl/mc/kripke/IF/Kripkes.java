package dev.civl.mc.kripke.IF;

import dev.civl.gmc.GMCConfiguration;
import dev.civl.gmc.dpor.DependencyAnalyzer;
import dev.civl.gmc.seq.StateManager;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.kripke.common.CommonLibraryEnablerLoader;
import dev.civl.mc.kripke.common.CommonStateManager;
import dev.civl.mc.kripke.common.SimpleDependencyAnalyzer;
import dev.civl.mc.kripke.common.SimpleEnabler;
import dev.civl.mc.log.IF.CIVLErrorLogger;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.Executor;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.MemoryUnitFactory;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;

/**
 * This is the entry point of the module <strong>kripke</strong>.
 * 
 * @author Manchun Zheng
 * 
 */
public class Kripkes {

	/**
	 * Creates a new instance of enabler.
	 * 
	 * @param stateFactory
	 *            The state factory to be used.
	 * @param evaluator
	 *            The evaluator to be used.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @param memUnitFactory
	 *            The memory unit factory for memory analysis.
	 * @param libLoader
	 *            The library enabler loader to be used.
	 * @param errorLogger
	 *            The error logger to be used.
	 * @param civlConfig
	 *            The configuration of the CIVL model.
	 * @return The new enabler created.
	 */
	public static Enabler newEnabler(StateFactory stateFactory,
			Evaluator evaluator, Executor executor,
			SymbolicAnalyzer symbolicAnalyzer, MemoryUnitFactory memUnitFactory,
			LibraryEnablerLoader libLoader, CIVLErrorLogger errorLogger,
			CIVLConfiguration civlConfig, GMCConfiguration gmcConfig) {
		return new SimpleEnabler(stateFactory, evaluator, executor,
				symbolicAnalyzer, libLoader, errorLogger, civlConfig,
				gmcConfig);
	}
	
	public static DependencyAnalyzer<State, Transition> newDependencyAnalyzer(StateManager<State, Transition> manager, StateFactory stateFactory, SimpleEnabler enabler) {
		//return new CrossStateDependencyAnalyzer(manager, stateFactory, enabler);
		return new SimpleDependencyAnalyzer(manager, stateFactory, enabler);
	}

	/**
	 * Creates a new instance of library enabler loader.
	 * 
	 * @param libEvaluatorLoader
	 *            the library evaluator loader
	 * @param civlConfig
	 *            the CIVL configuration
	 * 
	 * @return The new library enabler loader created.
	 */
	public static LibraryEnablerLoader newLibraryEnablerLoader(
			LibraryEvaluatorLoader libEvaluatorLoader,
			CIVLConfiguration civlConfig) {
		return new CommonLibraryEnablerLoader(libEvaluatorLoader, civlConfig);
	}

	/**
	 * Creates a new instance of state manager.
	 * 
	 * @param enabler
	 *            The enabler to be used.
	 * @param executor
	 *            The executor to be used.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer to be used.
	 * @param errorLogger
	 *            The error logger to be used.
	 * @param config
	 *            The configuration of the CIVL model.
	 * @return The new state manager created.
	 */
	public static CIVLStateManager newStateManager(Enabler enabler,
			Executor executor, SymbolicAnalyzer symbolicAnalyzer,
			CIVLErrorLogger errorLogger, CIVLConfiguration config) {
		return new CommonStateManager((SimpleEnabler) enabler, executor,
				symbolicAnalyzer, errorLogger, config);
	}
}
