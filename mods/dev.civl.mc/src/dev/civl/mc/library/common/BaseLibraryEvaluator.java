package dev.civl.mc.library.common;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.log.IF.CIVLErrorLogger;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;

/**
 * This class provides the common data and operations of library evaluators.
 * 
 * @author Manchun Zheng
 * 
 */
public abstract class BaseLibraryEvaluator extends LibraryComponent implements
		LibraryEvaluator {
	/**
	 * The state factory for state-related computation.
	 */
	protected StateFactory stateFactory;

	protected CIVLErrorLogger errorLogger;

	/* ***************************** Constructor *************************** */

	/**
	 * Creates a new instance of library enabler.
	 * 
	 * @param primaryEnabler
	 *            The enabler for normal CIVL execution.
	 * @param output
	 *            The output stream to be used in the enabler.
	 * @param modelFactory
	 *            The model factory of the system.
	 * @param symbolicUtil
	 *            The symbolic utility used in the system.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 */
	public BaseLibraryEvaluator(String name, Evaluator evaluator,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, evaluator.universe(), symbolicUtil, symbolicAnalyzer,
				civlConfig, libEvaluatorLoader, modelFactory, evaluator
						.errorLogger(), evaluator);
		this.stateFactory = evaluator.stateFactory();
		this.errorLogger = evaluator.errorLogger();
	}

	/* ******************** Methods from LibraryEvaluator ****************** */

	@Override
	public Evaluation evaluateGuard(CIVLSource source, State state, int pid,
			String function, Expression[] arguments)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state, universe.trueExpression());
	}

	@Override
	public void setPrimaryEvaluator(Evaluator primaryEvaluator) {
		super.evaluator = primaryEvaluator;
	}


	/* ******************** Public Array Utility functions ****************** */

	/* ************* Private helper functions ************ */
}
