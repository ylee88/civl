package dev.civl.mc.semantics.IF;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.log.IF.CIVLErrorLogger;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.common.CommonEvaluator;
import dev.civl.mc.semantics.common.CommonExecutor;
import dev.civl.mc.semantics.common.CommonLibraryEvaluatorLoader;
import dev.civl.mc.semantics.common.CommonLibraryExecutorLoader;
import dev.civl.mc.semantics.common.CommonMemoryUnitEvaluator;
import dev.civl.mc.semantics.common.CommonSymbolicAnalyzer;
import dev.civl.mc.semantics.common.CommonTransition;
import dev.civl.mc.semantics.common.ErrorSideEffectFreeEvaluator;
import dev.civl.mc.semantics.common.NoopTransition;
import dev.civl.mc.state.IF.MemoryUnitFactory;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * Entry point of the module civl.semantics.
 * 
 * @author zmanchun
 * 
 */
public class Semantics {

	/**
	 * Creates a new instance of library executor loader.
	 * 
	 * @return The new library executor loader.
	 */
	public static LibraryExecutorLoader newLibraryExecutorLoader(
			LibraryEvaluatorLoader libEvaluatorLoader,
			CIVLConfiguration civlConfig) {
		return new CommonLibraryExecutorLoader(libEvaluatorLoader, civlConfig);
	}

	/**
	 * Creates a new instance of library evaluator loader.
	 * 
	 * @return The new library evaluator loader.
	 */
	public static LibraryEvaluatorLoader newLibraryEvaluatorLoader(
			CIVLConfiguration civlConfig) {
		return new CommonLibraryEvaluatorLoader(civlConfig);
	}

	/**
	 * Creates a new instance of CIVL executor.
	 * 
	 * @param modelFactory
	 *            The model factory of the system.
	 * @param stateFactory
	 *            The state factory of the system.
	 * @param log
	 *            The error logger of the system.
	 * @param loader
	 *            The library executor loader for executing system functions.
	 * @param evaluator
	 *            The CIVL evaluator for evaluating expressions.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @param errLogger
	 *            The error logger for reporting execution errors.
	 * @param civlConfig
	 *            The CIVL configuration.
	 * @return The new CIVL executor.
	 */
	public static Executor newExecutor(ModelFactory modelFactory,
			StateFactory stateFactory, LibraryExecutorLoader loader,
			Evaluator evaluator, SymbolicAnalyzer symbolicAnalyzer,
			CIVLErrorLogger errLogger, CIVLConfiguration civlConfig) {
		return new CommonExecutor(modelFactory, stateFactory, loader, evaluator,
				symbolicAnalyzer, errLogger, civlConfig);
	}

	/**
	 * Creates a new instance of CIVL evaluator.
	 * 
	 * @param modelFactory
	 *            The model factory of the system.
	 * @param stateFactory
	 *            The state factory of the system.
	 * @param loader
	 *            The library evaluator loader for evaluating the guards of
	 *            system functions.
	 * @param symbolicUtil
	 *            The symbolic utility for manipulations of symbolic
	 *            expressions.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @param errLogger
	 *            The error logger for reporting execution errors.
	 * @return The new CIVL evaluator.
	 */
	public static Evaluator newEvaluator(ModelFactory modelFactory,
			StateFactory stateFactory, LibraryEvaluatorLoader loader,
			LibraryExecutorLoader loaderExec, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, MemoryUnitFactory memUnitFactory,
			CIVLErrorLogger errLogger, CIVLConfiguration config) {
		return new CommonEvaluator(modelFactory, stateFactory, loader,
				loaderExec, symbolicUtil, symbolicAnalyzer, memUnitFactory,
				errLogger, config);
	}

	/**
	 * Creates a new instance of {@link ErrorSideEffectFreeEvaluator}.
	 * 
	 * @param modelFactory
	 *            The model factory of the system.
	 * @param stateFactory
	 *            The state factory of the system.
	 * @param loader
	 *            The library evaluator loader for evaluating the guards of
	 *            system functions.
	 * @param symbolicUtil
	 *            The symbolic utility for manipulations of symbolic
	 *            expressions.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @param errLogger
	 *            The error logger for reporting execution errors.
	 * @return The new CIVL evaluator.
	 */
	public static Evaluator newErrorSideEffectFreeEvaluator(
			ModelFactory modelFactory, StateFactory stateFactory,
			LibraryEvaluatorLoader loader, LibraryExecutorLoader loaderExec,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer,
			MemoryUnitFactory memUnitFactory, CIVLErrorLogger errLogger,
			CIVLConfiguration config) {
		return new ErrorSideEffectFreeEvaluator(modelFactory, stateFactory,
				loader, loaderExec, symbolicUtil, symbolicAnalyzer,
				memUnitFactory, errLogger, config);
	}

	/**
	 * Creates a new instance of symbolic analyzer.
	 * 
	 * @param universe
	 *            The symbolic universe to be used.
	 * @param modelFactory
	 *            The model factory to be used.
	 * @param symbolicUtil
	 *            The symbolic utility to be used.
	 * @return The new symbolic analyzer.
	 */
	public static SymbolicAnalyzer newSymbolicAnalyzer(
			CIVLConfiguration civlConfig, CIVLErrorLogger errorLogger,
			SymbolicUniverse universe, ModelFactory modelFactory,
			SymbolicUtility symbolicUtil) {
		return new CommonSymbolicAnalyzer(civlConfig, errorLogger, universe,
				modelFactory, symbolicUtil);
	}

	/**
	 * Creates a new regular {@link Transition} whose statement will be executed
	 * by executor.
	 * 
	 * @param pid
	 *            The PID of the process associated with this transition.
	 * @param clause
	 *            The boolean value clause that will be conjuncted to the path
	 *            condition of the source state to form a new state immediately
	 *            before the execution.
	 * @param statement
	 *            The statement associated with this transition, it will be
	 *            executed by the executor.
	 * @return A new instance of regular {@link CommonTransition}
	 */
	public static Transition newTransition(int pid, BooleanExpression clause,
			Statement statement) {
		return new CommonTransition(clause, pid, statement);
	}

	/**
	 * Creates a new regular {@link Transition} whose statement will be executed
	 * by executor.
	 * 
	 * @param pid
	 *            The PID of the process associated with this transition.
	 * @param clause
	 *            The boolean value clause that will be conjuncted to the path
	 *            condition of the source state to form a new state immediately
	 *            before the execution.
	 * @param statement
	 *            The statement associated with this transition, it will be
	 *            executed by the executor.
	 * @param simplifyState
	 *            A flag, set to true if and only if the target state of this
	 *            transition must be simplified.
	 * @param atomicLockAction
	 *            An instance of {@link AtomicLockAction}
	 * @return A new instance of regular {@link CommonTransition}
	 */
	public static Transition newTransition(int pid, BooleanExpression clause,
			Statement statement, boolean simplifyState) {
		return new CommonTransition(clause, pid, statement, simplifyState);
	}

	/**
	 * Create a new {@link NoopTransition} whose statement will not be executed.
	 * 
	 * @param pid
	 *            The process id of the process executing this transition.
	 * @param clause
	 *            The boolean value clause that will be conjuncted to the path
	 *            condition of the source state to form a new state immediately
	 *            before the execution.
	 * @param statement
	 *            The statement associated with this transition, it will NOT be
	 *            executed by the executor.
	 * @param simplifyState
	 *            A flag, set to true if and only if the target state of this
	 *            transition must be simplified.
	 * @return A new instance of {@link NoopTransition}
	 */
	public static NoopTransition newNoopTransition(int pid,
			BooleanExpression assumption, Statement statement,
			boolean symplifyState) {
		return new NoopTransition(pid, assumption, statement, symplifyState);
	}

	public static MemoryUnitExpressionEvaluator newMemoryUnitEvaluator(
			Evaluator evaluator, MemoryUnitFactory memUnitFactory) {
		return new CommonMemoryUnitEvaluator(evaluator.symbolicUtility(),
				evaluator, memUnitFactory, evaluator.universe());
	}
}
