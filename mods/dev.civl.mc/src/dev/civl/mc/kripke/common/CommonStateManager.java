package dev.civl.mc.kripke.common;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.config.IF.CIVLConstants.DeadlockKind;
import dev.civl.mc.kripke.IF.AtomicStep;
import dev.civl.mc.kripke.IF.CIVLStateManager;
import dev.civl.mc.kripke.IF.TraceStep;
import dev.civl.mc.log.IF.CIVLErrorLogger;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.IF.Executor;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.semantics.IF.Transition.TransitionKind;
import dev.civl.mc.state.IF.CIVLHeapException;
import dev.civl.mc.state.IF.CIVLHeapException.HeapErrorKind;
import dev.civl.mc.state.IF.ProcessState;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.mc.util.IF.Printable;
import dev.civl.mc.util.IF.Utils;
import dev.civl.gmc.ExcessiveErrorException;
import dev.civl.gmc.TraceStepIF;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * @author Timothy K. Zirkel (zirkel)
 * @author Manchun Zheng (zmanchun)
 * @author Stephen F. Siegel (siegel)
 * 
 */
public class CommonStateManager extends CIVLStateManager {

	/* *************************** Instance Fields ************************* */

	protected SymbolicUniverse universe;

	/**
	 * The unique enabler instance used by the system
	 */
	protected SimpleEnabler enabler;

	/**
	 * The unique executor instance used by the system
	 */
	protected Executor executor;

	protected CIVLConfiguration config;

	/**
	 * The maximal number of processes at a state, initialized as 0.
	 */
	protected AtomicInteger maxProcs = new AtomicInteger(0);

	/**
	 * The unique state factory used by the system.
	 */
	protected StateFactory stateFactory;

	/**
	 * The object whose toString() method will be used to print the periodic
	 * update. The toString method of this object should print a short
	 * (one-line) message on the state of the search.
	 */
	private Printable updater;

	/**
	 * If true, print a short one-line update message on the state of the search
	 * at the next opportunity, and then set this flag back to false. This flag
	 * is typically set by a separate thread. Access to this thread is protected
	 * by the lock on this StateManager.
	 */

	protected CIVLErrorLogger errorLogger;

	/**
	 * The symbolic analyzer to be used.
	 */
	protected SymbolicAnalyzer symbolicAnalyzer;

	protected BooleanExpression falseExpr;

	protected BooleanExpression trueExpr;

	protected AtomicInteger numStatesExplored = new AtomicInteger(1);

	private AtomicInteger MaxNormalizedId = new AtomicInteger(0);;

	private OutputCollector outputCollector;

	protected boolean printTransitions;

	protected boolean printAllStates;

	protected boolean printSavedStates;

	protected Set<HeapErrorKind> ignoredHeapErrors;

	// TODO: trying to fix this:
	// private boolean saveStates;

	/* ***************************** Constructor *************************** */

	/**
	 * Creates a new instance of state manager.
	 * 
	 * @param enabler
	 *            The enabler to be used.
	 * @param executor
	 *            The unique executor to by used in the system.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer to be used.
	 * @param errorLogger
	 *            The error logger to be used.
	 * @param config
	 *            The configuration of the civl model.
	 */
	public CommonStateManager(SimpleEnabler enabler, Executor executor,
			SymbolicAnalyzer symbolicAnalyzer, CIVLErrorLogger errorLogger,
			CIVLConfiguration config) {
		this.executor = executor;
		this.enabler = enabler;
		this.universe = enabler.universe;
		this.stateFactory = executor.stateFactory();
		this.config = config;
		this.printTransitions = config.printTransitions()
				|| config.debugOrVerbose();
		this.printAllStates = config.debugOrVerbose()
				|| this.config.showStates();
		this.printSavedStates = config.debugOrVerbose()
				|| this.config.showSavedStates();
		this.errorLogger = errorLogger;
		this.symbolicAnalyzer = symbolicAnalyzer;
		this.falseExpr = universe.falseExpression();
		this.trueExpr = universe.trueExpression();
		if (config.collectOutputs())
			this.outputCollector = new OutputCollector(
					this.enabler.modelFactory.model(), this.enabler.universe);
		this.ignoredHeapErrors = new HashSet<>(0);
	}

	/* *************************** Private Methods ************************* */

	protected void nextStateWork(State state, Transition transition,
			TraceStep traceStep) throws UnsatisfiablePathConditionException {
		int pid = transition.pid();

		while (true) {
			state = executor.execute(state, pid, transition);
			traceStep.addAtomicStep(new CommonAtomicStep(state, transition));

			int numProcs = state.numLiveProcs();
			Utils.biggerAndSet(maxProcs, numProcs);
			// if (numProcs > maxProcs) maxProcs = numProcs;
			if (config.collectOutputs())
				this.outputCollector.collectOutputs(state);

			// if the statement just executed was a yield-enter, return:
			if (enabler.isYield(transition.statement())
					&& !stateFactory.lockedByAtomic(state))
				return;

			ProcessState procState = state.getProcessState(pid);

			if (procState == null) // process terminated and was reclaimed
				return;

			Location location = state.getProcessState(pid).getLocation();

			// if the next statement is a yield-enter, do it, assuming
			// all other conditions hold
			// if the previous statement was a yield-return whatever
			// if the next statement is a yield return: impossible.
			// that would mean the previous statement was a yield-enter

			if (location == null)
				return; // process terminated

			int numIncoming = location.getNumIncoming(),
					numOutgoing = location.getNumOutgoing();

			if (numOutgoing == 0)
				return;
			if (numIncoming > 1
					&& !(location.isInLoop() && location.isSafeLoop()))
				return;
			if (!stateFactory.lockedByAtomic(state)
					&& !location.isPurelyLocal())
				return;

			Reasoner reasoner = universe
					.reasoner(state.getPathCondition(universe));
			Statement stmt = null;

			// the only safe situation is where one guard is true
			// and all others are false...
			for (int j = 0; j < numOutgoing; j++) {
				BooleanExpression guard = enabler.computeGuard(state, reasoner,
						pid, j);

				if (guard.isTrue()) {
					if (stmt != null)
						return; // more than one true
					else
						stmt = location.getOutgoing(j);
				} else if (!guard.isFalse())
					return; // something in between
			}
			if (stmt == null)
				return; // no true guard, possible deadlock
			if (config.checkDeadlockKind() == DeadlockKind.POTENTIAL
					&& enabler.isSend(state, pid, stmt))
				return;
			transition = enabler.singleTransitionFromStatement(state, pid,
					trueExpr, stmt);
			if (transition == null)
				return;
		}
	}

	/**
	 * Print a step of a statement, in the following form:
	 * <code>src->dst: statement at file:location text;</code>For example,<br>
	 * <code>32->17: sum = (sum+(3*i)) at f0:20.14-24 "sum += 3*i";</code><br>
	 * When the atomic lock variable is changed during executing the statement,
	 * then the corresponding information is printed as well. For example,<br>
	 * <code>13->6: ($ATOMIC_LOCK_VAR = $self) x = 0 at f0:30.17-22
	 "x = 0";</code>
	 * 
	 * @param s
	 *            The statement that has been executed in the current step.
	 * @param atomicKind
	 *            The atomic kind of the source location of the statement.
	 * @param atomicLockVarChanged
	 *            True iff the atomic lock variable is changed during the
	 *            execution of the statement.
	 * @throws UnsatisfiablePathConditionException
	 */
	protected void printStatement(State currentState, State newState,
			Transition transition) {
		Statement stmt = transition.statement();

		config.out().print("  ");
		config.out().print(stmt.locationStepString());
		config.out().print(": ");
		try {
			config.out().print(symbolicAnalyzer.statementEvaluation(
					currentState, newState, transition.pid(), stmt));
		} catch (UnsatisfiablePathConditionException e) {
			throw new CIVLInternalException(
					"UnsatisfiablePathConditionException happens when printing a statement",
					stmt.getSource());
		}
		if (transition.transitionKind() == TransitionKind.NOOP) {
			BooleanExpression assumption = transition.clause();

			if (assumption != null) {
				config.out().print(" [$assume(");
				config.out()
						.print(symbolicAnalyzer
								.symbolicExpressionToString(stmt.getSource(),
										currentState, this.enabler.modelFactory
												.typeFactory().booleanType(),
										assumption));
				config.out().print(")]");
			}
		}
		config.out().print(" at ");
		config.out().print(stmt.summaryOfSource());
		config.out().println();
	}

	/**
	 * Print the prefix of a transition.
	 * 
	 * @param printTransitions
	 *            True iff each step is to be printed.
	 * @param state
	 *            The source state of the transition.
	 * @param processIdentifier
	 *            The identifier of the process that this transition associates
	 *            with.
	 */
	private void printTransitionPrefix(State state, int processIdentifier,
			int stateID) {
		// Executed by p0 from State 1
		config.out().print("Executed by p");
		if (stateID < 0)
			config.out().println(processIdentifier + " from State " + state);
		else
			config.out().println(
					processIdentifier + " from State " + stateID + " " + state);
		config.out().flush();
	}

	/**
	 * Print the updated status.
	 */
	private void printUpdateWork() {
		updater.print(config.out());
		config.out().flush();
	}

	/* ********************* Methods from StateManagerIF ******************* */

	@Override
	public TraceStepIF<State> nextState(State state, Transition transition) {
		int pid = transition.pid();
		TraceStep result = new CommonTraceStep(pid);

		// nextStateCalls++;
		try {
			nextStateWork(state, transition, result);
		} catch (UnsatisfiablePathConditionException e) {
			// problem is the interface requires an actual State
			// be returned. There is no concept of executing a
			// transition and getting null or an exception.
			// since the error has been logged, just return
			// some state with false path condition, so there
			// will be no next state...
			State lastState = result.getFinalState();

			if (lastState == null)
				lastState = state;
			result.setFinalState(
					stateFactory.addToPathcondition(lastState, pid, falseExpr));
		}
		return result;
	}
	
	@Override
	public TraceStepIF<State> tryNextState(State state, Transition transition) {
		errorLogger.setIgnoreErrors(true);
		TraceStepIF<State> result = null;
		try {
			result = nextState(state, transition);
		} catch(ExcessiveErrorException e) {}
		errorLogger.setIgnoreErrors(false);
		return result;
	}

	@Override
	public void printAllStatesLong(PrintStream arg0) {

	}

	@Override
	public void printAllStatesShort(PrintStream arg0) {

	}

	@Override
	public void printStateLong(PrintStream out, State state) {
		out.print(this.symbolicAnalyzer.stateToString(state));
	}

	@Override
	public void printStateShort(PrintStream out, State state) {
		out.print(state.toString());
	}

	@Override
	public void printTransitionLong(PrintStream out, Transition transition) {
		out.print(transition.toString());
	}

	@Override
	public void printTransitionShort(PrintStream out, Transition transition) {
		out.print(transition.toString());
	}

	@Override
	public void printTraceStep(State source, TraceStepIF<State> traceStepIF) {
		if (!(printTransitions || printAllStates || printSavedStates))
			return;

		TraceStep traceStep = (TraceStep) traceStepIF;
		int pid = traceStep.processIdentifier();
		int startStateId = getId(source);
		int sequenceId = 1;
		Iterator<AtomicStep> atomicStepIter = traceStep.getAtomicSteps()
				.iterator();
		State oldState = source;
		AtomicStep atomicStep;

		if (!atomicStepIter.hasNext())
			return;
		atomicStep = atomicStepIter.next();
		// print the first transition from the source state:
		if (printTransitions) {
			if (this.printAllStates)
				config.out().println();
			printTransitionPrefix(source, pid, startStateId);
			printStatement(source, atomicStep.getPostState(),
					atomicStep.getTransition());
		}
		oldState = atomicStep.getPostState();
		while (atomicStepIter.hasNext()) {
			atomicStep = atomicStepIter.next();
			if (this.printAllStates) {
				config.out().println();
				config.out().print(symbolicAnalyzer.stateToString(oldState,
						startStateId, sequenceId++));
			}
			if (printTransitions) {
				if (this.printAllStates)
					config.out().println();
				printStatement(oldState, atomicStep.getPostState(),
						atomicStep.getTransition());
			}
			oldState = atomicStep.getPostState();
		}
	}

	@Override
	public void printTraceStepFinalState(State finalState, int normalizedID) {
		boolean newState = true;

		// Print transitions:
		if (printTransitions) {
			String stateID = "--> State ";

			if (normalizedID >= 0)
				stateID += normalizedID + " ";
			stateID += finalState;
			this.config.out().println(stateID);
			config.out().flush();
		}
		// I don't like increase "numStatesExplored" here but there is no other
		// place it can be in without further modification in GMC or CIVL,
		// because it needs to know if the final state is a seen state.
		if (normalizedID >= 0) {
			if (normalizedID > MaxNormalizedId.intValue()) {
				Utils.biggerAndSet(MaxNormalizedId, normalizedID);
				numStatesExplored.getAndIncrement();
			} else
				newState = false; // a seen state
		} else
			numStatesExplored.getAndIncrement();
		// Print states:
		if (newState) {
			if (printAllStates) {
				config.out().println();
				config.out().println(symbolicAnalyzer.stateToString(finalState,
						normalizedID, -1));
			} else if (printSavedStates) {
				if (normalizedID >= 0) {
					config.out().println();
					config.out().println(symbolicAnalyzer
							.stateToString(finalState, normalizedID, -1));
				}
			}
			config.out().flush();
		}
		// Print path conditions:
		if (config.showPathConditon()) {
			String prefix = "--> State ";
			String stateID = normalizedID >= 0
					? prefix + normalizedID
					: finalState.toString();

			config.out().print(stateID);
			config.out().print(" -- path condition: ");
			if (config.showPathConditonAsOneLine())
				config.out()
						.println(finalState.getPathCondition(enabler.universe));
			else
				config.out()
						.println(this.symbolicAnalyzer.pathconditionToString(
								null, finalState, "\t",
								finalState.getPathCondition(enabler.universe)));
			config.out().flush();
		}
	}

	/* ****************** Public Methods from StateManager ***************** */

	@Override
	public long getNumStateInstances() {
		return stateFactory.getNumStateInstances();
	}

	@Override
	public int maxProcs() {
		return maxProcs.intValue();
	}

	@Override
	public void printUpdate() {
		printUpdateWork();
	}

	@Override
	public void setUpdater(Printable updater) {
		this.updater = updater;
	}

	@Override
	public int numStatesExplored() {
		return numStatesExplored.get();
	}

	@Override
	public Map<BooleanExpression, Set<Pair<State, SymbolicExpression[]>>> collectedOutputs() {
		return this.outputCollector.collectedOutputs;
	}

	@Override
	public String[] outptutNames() {
		if (outputCollector != null)
			return this.outputCollector.outptutNames;
		return null;
	}

	@Override
	public void normalize(TraceStepIF<State> traceStepIF) {
		TraceStep traceStep = (TraceStep) traceStepIF;
		State state = traceStep.getFinalState();

		try {
			if (config.saveStates() || config.collectProcesses()
					|| config.collectScopes() || config.collectHeaps()
					|| config.collectSymbolicNames() || config.simplify()) {
				Set<HeapErrorKind> ignoredErrorSet = new HashSet<>(
						ignoredHeapErrors);
				boolean finished = false;

				do {
					try {
						if (ignoredErrorSet
								.size() == HeapErrorKind.values().length)
							finished = true;
						state = stateFactory.canonic(state,
								config.collectProcesses(),
								config.collectScopes(), config.collectHeaps(),
								config.collectSymbolicNames(),
								config.simplify(), ignoredErrorSet);
						finished = true;
					} catch (CIVLHeapException hex) {
						// TODO state never gets canonicalized and then gmc
						// can't
						// figure out if it has been seen before.
						String message = "";
						int pid = traceStep.processIdentifier();
						String process = "p" + pid;

						state = hex.state();
						switch (hex.heapErrorKind()) {
							case NONEMPTY :
								message = "The dyscope " + hex.dyscopeName()
										+ "(id=" + hex.dyscopeID()
										+ ") has a non-empty heap upon termination.\n";
								break;
							case UNREACHABLE :
								message = "An unreachable object (mallocID="
										+ hex.heapFieldID() + ", objectID="
										+ hex.heapObjectID()
										+ ") is detected in the heap of dyscope "
										+ hex.dyscopeName() + "(id="
										+ hex.dyscopeID() + ").\n";
								break;
							default :
						}
						message = message + "heap"
								+ symbolicAnalyzer.symbolicExpressionToString(
										hex.source(), hex.state(), null,
										hex.heapValue());
						errorLogger.logSimpleError(hex.source(), state, pid,
								process,
								symbolicAnalyzer.stateInformation(hex.state()),
								hex.civlProperty(), message);
						ignoredErrorSet.add(hex.heapErrorKind());
					}
				} while (!finished);
			} else if (config.simplify())
				state = stateFactory.simplify(state);
		} catch (UnsatisfiablePathConditionException e) {
			// Since the error has been logged, just return
			// some state with false path condition, so there
			// will be no next state...
			traceStep.setFinalState(stateFactory.addToPathcondition(state,
					traceStep.processIdentifier(), falseExpr));
		}
		traceStep.setFinalState(state);
	}

	@Override
	public int getPid(Transition transition) {
		return transition.pid();
	}

	@Override
	public Set<Integer> getEnabledProcesses(State state) {
		Set<Integer> enabProcs = new HashSet<>();
		for (ProcessState pstate : state.getProcessStates()) {
			if (pstate != null) {
				int pid = pstate.getPid();
				if (!getTransitions(state, pid).isEmpty())
					enabProcs.add(pid);
			}
		}
		return enabProcs;
	}

	@Override
	public Collection<Transition> getTransitions(State state, int pid) {
		return enabler.enabledTransitionsInProcess(state, pid);
	}
	
	@Override
	public void debug(State state, List<Integer> backtrack) {
		/*
		Collection<Transition> ampleSet = enabler.ampleSet(state);
		Set<Integer> ampleSetProcs = new HashSet<>();
		for (Transition tran : ampleSet) {
			ampleSetProcs.add(tran.pid());
		}
		for (int proc : backtrack) {
			if (!ampleSetProcs.contains(proc)) {
				printAllStates = true;
				printTraceStepFinalState(state, -1);
				throw new RuntimeException("Inefficiency!");
			}
		}
		*/
	}
}
