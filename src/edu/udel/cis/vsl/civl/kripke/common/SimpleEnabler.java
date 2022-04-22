package edu.udel.cis.vsl.civl.kripke.common;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.civl.config.IF.CIVLConfiguration;
import edu.udel.cis.vsl.civl.kripke.IF.Enabler;
import edu.udel.cis.vsl.civl.kripke.IF.LibraryEnabler;
import edu.udel.cis.vsl.civl.kripke.IF.LibraryEnablerLoader;
import edu.udel.cis.vsl.civl.log.IF.CIVLErrorLogger;
import edu.udel.cis.vsl.civl.model.IF.CIVLFunction;
import edu.udel.cis.vsl.civl.model.IF.CIVLSource;
import edu.udel.cis.vsl.civl.model.IF.Model;
import edu.udel.cis.vsl.civl.model.IF.ModelFactory;
import edu.udel.cis.vsl.civl.model.IF.statement.Statement;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluator;
import edu.udel.cis.vsl.civl.semantics.IF.Executor;
import edu.udel.cis.vsl.civl.semantics.IF.LibraryLoaderException;
import edu.udel.cis.vsl.civl.semantics.IF.SymbolicAnalyzer;
import edu.udel.cis.vsl.civl.semantics.IF.Transition;
import edu.udel.cis.vsl.civl.state.IF.State;
import edu.udel.cis.vsl.civl.state.IF.StateFactory;
import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.gmc.GMCConfiguration;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.expr.BooleanExpression;
import edu.udel.cis.vsl.sarl.util.EmptySet;

public class SimpleEnabler implements Enabler {

	/* *************************** Instance Fields ************************* */

	/**
	 * Turn on/off debugging option to print more information.
	 */
	protected boolean debugging = false;

	/**
	 * The output stream for printing debugging information.
	 */
	protected PrintStream debugOut = System.out;

	/**
	 * The unique evaluator used by the system for evaluating expressions.
	 */
	protected Evaluator evaluator;

	/**
	 * The executor used to execute statements.
	 */
	private Executor executor;

	/**
	 * The unique model factory used by the system.
	 */
	protected ModelFactory modelFactory;

	/**
	 * The option to enable/disable the printing of ample sets of each state.
	 */
	protected boolean showAmpleSet = false;

	/**
	 * Show the impact/reachable memory units?
	 */
	protected boolean showMemoryUnits = false;

	/**
	 * If negative, ignore, otherwise an upper bound on the number of live
	 * processes.
	 */
	protected int procBound;

	/**
	 * The unique symbolic universe used by the system.
	 */
	protected SymbolicUniverse universe;

	/**
	 * The symbolic expression for the boolean value false.
	 */
	protected BooleanExpression falseExpression;

	/**
	 * The symbolic expression for the boolean value true.
	 */
	protected BooleanExpression trueExpression;

	/**
	 * The library enabler loader.
	 */
	protected LibraryEnablerLoader libraryLoader;

	/**
	 * Show ample sets with the states?
	 */
	protected boolean showAmpleSetWtStates = false;

	/**
	 * The state factory that provides operations on states.
	 */
	protected StateFactory stateFactory;

	/**
	 * The error logger for reporting errors.
	 */
	protected CIVLErrorLogger errorLogger;

	/**
	 * The symbolic analyzer to be used.
	 */
	protected SymbolicAnalyzer symbolicAnalyzer;

	/**
	 * CIVL configuration object, which specifies values for all the
	 * command-line options.
	 */
	protected CIVLConfiguration config;

	/**
	 * Used by the state collation module which is an experimental feature used
	 * for checking collective properties.
	 */
	protected CollateExecutor collateExecutor;

	/**
	 * The system function named {@code $yield}, used by a process in an atomic
	 * block to release the lock temporarily and allow other processes to
	 * execute. This may be {@code null} if the model being analyzed does not
	 * use this function.
	 */
	protected CIVLFunction yieldFunction;

	/**
	 * The system function named {@code $assume} used to specify an assumption.
	 * This may be {@code null} if the model being analyzed does not use this
	 * function.
	 */
	protected CIVLFunction assumeFunction;

	/**
	 * The system function named {@code $comm_enqueue}, used to enqueue data
	 * onto a FIFO message queue, used for message-passing communication. This
	 * may be {@code null} if the model being analyzed does not use this
	 * function.
	 */
	protected CIVLFunction commEnqueueFunction;

	/**
	 * An unmodifiable empty set of transitions.
	 */
	private Collection<Transition> emptySet;

	/* ***************************** Constructor *************************** */

	/**
	 * Creates a new instance of Enabler, using the given arguments to
	 * initialize many of the instance fields.  
	 */
	public SimpleEnabler(StateFactory stateFactory, Evaluator evaluator,
			Executor executor, SymbolicAnalyzer symbolicAnalyzer,
			LibraryEnablerLoader libLoader, CIVLErrorLogger errorLogger,
			CIVLConfiguration civlConfig, GMCConfiguration gmcConfig) {
		this.errorLogger = errorLogger;
		this.evaluator = evaluator;
		this.executor = executor;
		this.symbolicAnalyzer = symbolicAnalyzer;
		this.debugOut = civlConfig.out();
		this.debugging = civlConfig.debug();
		this.showAmpleSet = civlConfig.showAmpleSet()
				|| civlConfig.showAmpleSetWtStates();
		this.showAmpleSetWtStates = civlConfig.showAmpleSetWtStates();
		this.modelFactory = evaluator.modelFactory();
		this.universe = modelFactory.universe();
		this.falseExpression = universe.falseExpression();
		this.trueExpression = universe.trueExpression();
		this.libraryLoader = libLoader;
		this.stateFactory = stateFactory;
		this.showMemoryUnits = civlConfig.showMemoryUnits();
		this.procBound = civlConfig.getProcBound();
		this.config = civlConfig;
		this.collateExecutor = new CollateExecutor(this, this.executor,
				errorLogger, civlConfig, gmcConfig);

		Model model = modelFactory.model();

		// the following will be null iff the model does not use $yield:
		this.yieldFunction = model.function("$yield");
		this.assumeFunction = model.function("$assume");
		this.commEnqueueFunction = model.function("$comm_enqueue");
		this.emptySet = new EmptySet<Transition>();
	}

	// TODO: find a way to associate the library enabler and executor
	// directly to the Statement.
	protected LibraryEnabler libraryEnabler(CIVLSource civlSource,
			String library) throws LibraryLoaderException {
		return this.libraryLoader.getLibraryEnabler(library, this, evaluator,
				evaluator.modelFactory(), evaluator.symbolicUtility(),
				this.symbolicAnalyzer);
	}

	@Override
	public Collection<Transition> ampleSet(State source) {
		SimpleEnablerWorker worker = new SimpleEnablerWorker(this, source);
		int pid = stateFactory.processInAtomic(source);

		try {
			Transition[] result;

			if (pid >= 0)
				result = worker.enabledTransitionsInProcess(pid);
			else {
				worker.computeAmpleSet();
				result = worker.ampleSet();
			}
			return Arrays.asList(result);
		} catch (UnsatisfiablePathConditionException e) {
			return emptySet;
		}
	}

	@Override
	public Collection<Transition> fullSet(State state) {
		SimpleEnablerWorker worker = new SimpleEnablerWorker(this, state);
		int pid = stateFactory.processInAtomic(state);

		try {
			if (pid >= 0)
				return Arrays.asList(worker.enabledTransitionsInProcess(pid));
			else {
				List<Transition> result = new LinkedList<>();
				int nprocs = state.numProcs();

				for (int i = 0; i < nprocs; i++)
					result.addAll(Arrays
							.asList(worker.enabledTransitionsInProcess(pid)));
				return result;
			}
		} catch (UnsatisfiablePathConditionException e) {
			return emptySet;
		}
	}

	@Override
	public void setDebugging(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean debugging() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDebugOut(PrintStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public PrintStream getDebugOut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanExpression getGuard(Statement statement, int pid,
			State state) {
		// TODO Auto-generated method stub
		return null;
	}

}
