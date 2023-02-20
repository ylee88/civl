package dev.civl.mc.library.civlc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.library.common.BaseLibraryEnabler;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.BinaryExpression;
import dev.civl.mc.model.IF.expression.BinaryExpression.BINARY_OPERATOR;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.InitialValueExpression;
import dev.civl.mc.model.IF.statement.AssignStatement;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.Semantics;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.MemoryUnitSet;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.Number;

/**
 * Implementation of the enabler-related logics for system functions declared
 * civlc.h.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public class LibcivlcEnabler extends BaseLibraryEnabler
		implements
			LibraryEnabler {

	private final static int ELABORATE_UPPER_BOUND = 100;

	/* **************************** Constructors *************************** */
	/**
	 * Creates a new instance of the library enabler for civlc.h.
	 * 
	 * @param primaryEnabler
	 *            The enabler for normal CIVL execution.
	 * @param output
	 *            The output stream to be used in the enabler.
	 * @param modelFactory
	 *            The model factory of the system.
	 */
	public LibcivlcEnabler(String name, Enabler primaryEnabler,
			Evaluator evaluator, ModelFactory modelFactory,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer,
			CIVLConfiguration civlConfig, LibraryEnablerLoader libEnablerLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryEnabler, evaluator, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libEnablerLoader,
				libEvaluatorLoader);
	}

	/* ********************* Methods from LibraryEnabler ******************* */

	@Override
	public BitSet ampleSet(State state, int pid, CallOrSpawnStatement call,
			MemoryUnitSet[] setsReachableRead,
			MemoryUnitSet[] setsReachableWrite)
			throws UnsatisfiablePathConditionException {
		return this.ampleSetWork(state, pid, call);
	}

	@Override
	public List<Transition> enabledTransitions(State state,
			CallOrSpawnStatement call, BooleanExpression clause, int pid)
			throws UnsatisfiablePathConditionException {
		String functionName = call.function().name().name();
		AssignStatement assignmentCall;
		Expression[] arguments = new Expression[call.arguments().size()];// call.arguments();
		List<Transition> localTransitions = new LinkedList<>();
		String process = "p" + pid;
		Pair<State, SymbolicExpression[]> argumentsEval;

		call.arguments().toArray(arguments);
		switch (functionName) {
			case "$assume" : {
				localTransitions.add(
						Semantics.newTransition(pid, trueValue, call, true));
				return localTransitions;
			}
			case "$choose_int" :
				if (call.lhs() == null) {
					// if no left-hand side expression, this is a no-op
					// transition:
					localTransitions.add(Semantics.newNoopTransition(pid,
							clause, call, false));
					return localTransitions;
				}
				argumentsEval = evaluateArguments(state, pid, arguments);
				state = argumentsEval.left;

				IntegerNumber upperNumber = (IntegerNumber) universe
						.reasoner(state.getPathCondition(universe))
						.extractNumber(
								(NumericExpression) argumentsEval.right[0]);
				int upper;

				if (upperNumber == null) {
					this.errorLogger.logSimpleError(arguments[0].getSource(),
							state, pid, process,
							symbolicAnalyzer.stateInformation(state),
							CIVLProperty.INTERNAL,
							"argument to $choose_int not concrete: "
									+ argumentsEval.right[0]);
					throw new UnsatisfiablePathConditionException();
				}
				upper = upperNumber.intValue();
				for (int i = 0; i < upper; i++) {
					Expression singleChoice = modelFactory
							.integerLiteralExpression(arguments[0].getSource(),
									BigInteger.valueOf(i));

					assignmentCall = modelFactory.assignStatement(
							arguments[0].getSource(), call.source(), call.lhs(),
							singleChoice,
							(call.lhs() instanceof InitialValueExpression));
					assignmentCall.setTargetTemp(call.target());
					assignmentCall.setTarget(call.target());
					assignmentCall.source().removeOutgoing(assignmentCall);
					localTransitions.add(Semantics.newTransition(pid, clause,
							assignmentCall));
				}
				return localTransitions;
			case "$elaborate" :
				argumentsEval = this.evaluateArguments(state, pid, arguments);
				return this.elaborateIntWorker(argumentsEval.left, pid, call,
						call.getSource(), arguments, argumentsEval.right);
			case "$elaborate_domain" :
				argumentsEval = this.evaluateArguments(state, pid, arguments);
				return this.elaborateRectangularDomainWorker(argumentsEval.left,
						pid, call, call.getSource(), arguments,
						argumentsEval.right);
			case "$unidirectional_when" :
				BooleanExpression condition = (BooleanExpression) evaluateArguments(
						state, pid, arguments).right[0];

				// This function $unidirectional_when is same as $when but is
				// guaranteed to be invisible for deadlock property by
				// programmer.
				if (condition.isTrue())
					// If condition is simply true, enables a no-op transition:
					localTransitions.add(Semantics.newNoopTransition(pid,
							trueValue, call, false));
				else if (!universe.reasoner(state.getPathCondition(universe))
						.isValid(universe.not(condition)))
					// If condition is satisfiable (or prover cannot prove it is
					// unsatisfiable), enables a no-op transition and adds the
					// condition into the path condition:
					localTransitions.add(Semantics.newNoopTransition(pid,
							condition, call, true));
				return localTransitions;
			default :
				return super.enabledTransitions(state, call, clause, pid);
		}
	}

	/* *************************** Private Methods ************************* */

	/**
	 * Computes the ample set process ID's from a system function call.
	 * 
	 * @param state
	 *            The current state.
	 * @param pid
	 *            The ID of the process that the system function call belongs
	 *            to.
	 * @param call
	 *            The system function call statement.
	 * @param reachableMemUnitsMap
	 *            The map of reachable memory units of all active processes.
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	private BitSet ampleSetWork(State state, int pid, CallOrSpawnStatement call)
			throws UnsatisfiablePathConditionException {
		int numArgs;
		numArgs = call.arguments().size();
		Expression[] arguments;
		SymbolicExpression[] argumentValues;
		String function = call.function().name().name();

		arguments = new Expression[numArgs];
		argumentValues = new SymbolicExpression[numArgs];
		for (int i = 0; i < numArgs; i++) {
			Evaluation eval = null;

			arguments[i] = call.arguments().get(i);
			try {
				eval = evaluator.evaluate(state, pid, arguments[i]);
			} catch (UnsatisfiablePathConditionException e) {
				return new BitSet(0);
			}
			argumentValues[i] = eval.value;
			state = eval.state;
		}

		switch (function) {
			case "$wait" :
				return ampleSetOfWait(state, pid, arguments, argumentValues);
			case "$waitall" :
				return ampleSetOfWaitall(state, pid, arguments, argumentValues);
			default :
				return super.ampleSet(state, pid, call, null, null);
		}
	}

	private BitSet ampleSetOfWait(State state, int pid, Expression[] arguments,
			SymbolicExpression[] argumentValues) {
		SymbolicExpression joinProc = argumentValues[0];
		int joinPid = modelFactory.getProcessId(joinProc);
		BitSet ampleSet = new BitSet();

		if (modelFactory.isPocessIdDefined(joinPid)
				&& !modelFactory.isProcNull(joinProc)) {
			ampleSet.set(joinPid);
		}
		return ampleSet;
	}

	/**
	 * computes the ample set for $waitall. The ample set is the set of
	 * processes being waited for.
	 * 
	 * @param state
	 *            the current state
	 * @param pid
	 *            the PID of the process which executes the $waitall function
	 *            call
	 * @param arguments
	 *            the arguments of $waitall, where argument 0 is a pointer to
	 *            the first $proc object, and 1 is the number of processes to
	 *            wait for.
	 * @param argumentValues
	 *            the evaluation results of the arguments of $waitall
	 * @return the set of processes being waited for as the ample set
	 * @throws UnsatisfiablePathConditionException
	 */
	private BitSet ampleSetOfWaitall(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression procsPointer = argumentValues[0];
		SymbolicExpression numOfProcs = argumentValues[1];
		Reasoner reasoner = universe.reasoner(state.getPathCondition(universe));
		IntegerNumber number_nprocs = (IntegerNumber) reasoner
				.extractNumber((NumericExpression) numOfProcs);
		String process = state.getProcessState(pid).name() + "(id=" + pid + ")";
		BitSet ampleSet = new BitSet();

		if (number_nprocs == null) {
			this.evaluator.errorLogger().logSimpleError(
					arguments[1].getSource(), state, pid, process,
					symbolicAnalyzer.stateInformation(state),
					CIVLProperty.OTHER, "the number of processes for $waitall "
							+ "needs a concrete value");
			throw new UnsatisfiablePathConditionException();
		} else {
			int numOfProcs_int = number_nprocs.intValue();
			BinaryExpression pointerAdd;
			CIVLSource procsSource = arguments[0].getSource();
			Evaluation eval;

			for (int i = 0; i < numOfProcs_int; i++) {
				Expression offSet = modelFactory.integerLiteralExpression(
						procsSource, BigInteger.valueOf(i));
				NumericExpression offSetV = universe.integer(i);
				SymbolicExpression procPointer, proc;
				int pidValue;

				pointerAdd = modelFactory.binaryExpression(procsSource,
						BINARY_OPERATOR.POINTER_ADD, arguments[0], offSet);
				eval = evaluator.evaluatePointerAdd(state, pid, pointerAdd,
						procsPointer, offSetV);
				procPointer = eval.value;
				state = eval.state;
				eval = evaluator.dereference(procsSource, state, pid, process,
						procPointer, false, true);
				proc = eval.value;
				state = eval.state;
				pidValue = modelFactory.getProcessId(proc);
				if (!modelFactory.isProcessIdNull(pidValue)
						&& modelFactory.isPocessIdDefined(pidValue))
					ampleSet.set(pidValue);
			}
		}
		return ampleSet;
	}

	/**
	 * This methods elaborates all symbolic constants contained in an integer
	 * expression.
	 * 
	 * @param state
	 * @param pid
	 * @param source
	 * @param arguments
	 * @param argumentValues
	 * @param atomicLockAction
	 * @return
	 */
	private List<Transition> elaborateIntWorker(State state, int pid,
			Statement call, CIVLSource source, Expression[] arguments,
			SymbolicExpression[] argumentValues) {
		Set<SymbolicConstant> symbolicConstants = universe
				.getFreeSymbolicConstants(argumentValues[0]);

		return this.elaborateSymbolicConstants(state, pid, call, source,
				symbolicConstants);
	}

	private List<Transition> elaborateRectangularDomainWorker(State state,
			int pid, CallOrSpawnStatement call, CIVLSource source,
			Expression[] arguments, SymbolicExpression[] argumentValues) {
		Set<SymbolicConstant> symbolicConstants = universe
				.getFreeSymbolicConstants(argumentValues[0]);

		return this.elaborateSymbolicConstants(state, pid, call, source,
				symbolicConstants);
	}

	private List<Transition> elaborateSymbolicConstants(State state, int pid,
			Statement call, CIVLSource source,
			Set<SymbolicConstant> symbolicConstants) {
		BooleanExpression pathCondition = state.getPathCondition(universe);
		List<ConstantBound> bounds = new ArrayList<>();
		ConstantBound[] constantBounds;
		Set<BooleanExpression> concreteValueClauses;
		List<Transition> transitions = new LinkedList<>();
		Reasoner reasoner = universe.reasoner(pathCondition);

		if (symbolicConstants.size() < 1) {
			// noop if no symbolic constant is contained
			return Arrays.asList((Transition) Semantics.newNoopTransition(pid,
					trueValue, call, false));
		}
		for (SymbolicConstant var : symbolicConstants) {
			// no need to elaborate non-numeric symbolic constants:
			if (!var.isNumeric())
				continue;
			Interval interval = reasoner
					.intervalApproximation((NumericExpression) var);

			if (interval.isIntegral()) {
				Number lowerNum = interval.lower(), upperNum = interval.upper();
				int lower = Integer.MIN_VALUE, upper = Integer.MAX_VALUE;

				if (this.civlConfig.svcomp()
						&& (lowerNum.isInfinite() || upperNum.isInfinite())) {
					continue;
				}
				if (!lowerNum.isInfinite()) {
					lower = ((IntegerNumber) lowerNum).intValue();
				}
				if (!upperNum.isInfinite()) {
					upper = ((IntegerNumber) upperNum).intValue();
				}
				bounds.add(new ConstantBound(var, lower, upper));
			}
		}
		constantBounds = new ConstantBound[bounds.size()];
		bounds.toArray(constantBounds);
		// If there is no elaborated constants, return a default unchanged
		// transition:
		if (constantBounds.length == 0) {
			transitions.add(
					Semantics.newNoopTransition(pid, trueValue, call, true));
			return transitions;
		}
		concreteValueClauses = this.generateConcreteValueClauses(reasoner,
				constantBounds, 0);
		for (BooleanExpression clause : concreteValueClauses)
			transitions
					.add(Semantics.newNoopTransition(pid, clause, call, true));
		return transitions;
	}

	/**
	 * generates boolean expressions by elaborating symbolic constants according
	 * to their upper/lower bound. The result is the permutation of the possible
	 * values of all symbolic constants. For example, if the constant bounds are
	 * {(X, [2, 3]), (Y, [6,7]), (Z, [8,9])} then the result will be { X=2 &&
	 * Y=6 && Z==8, X=2 && Y=6 && Z=9, X=2 && Y=7 && Z=8, X=2 && Y=7 && Z=9, X=3
	 * && Y=6 && Z=8, X=3 && Y=6 && Z=9, X=3 && Y=7 && Z=8, X=3 && Y=7 && Z=9}.
	 * 
	 * @param reasoner
	 * @param constantBounds
	 * @param start
	 * @return
	 */
	private Set<BooleanExpression> generateConcreteValueClauses(
			Reasoner reasoner, ConstantBound[] constantBounds, int start) {
		Set<BooleanExpression> myResult = new LinkedHashSet<>();
		ConstantBound myConstantBound = constantBounds[start];
		Set<BooleanExpression> subfixResult;
		Set<BooleanExpression> result = new LinkedHashSet<>();
		// last constant bound
		int lower = myConstantBound.lower, upper = myConstantBound.upper;
		NumericExpression symbol = (NumericExpression) myConstantBound.constant;
		BooleanExpression newClause;
		boolean upperBoundCluaseNeeded = false;

		if (lower < 0) {
			lower = 0;
			newClause = universe.lessThan(symbol, universe.integer(lower));
			if (!reasoner.isValid(universe.not(newClause)))
				myResult.add(newClause);
		}
		if (upper > lower + ELABORATE_UPPER_BOUND) {
			upper = lower + ELABORATE_UPPER_BOUND;
			upperBoundCluaseNeeded = true;
			newClause = universe.lessThan(universe.integer(upper), symbol);
			if (!reasoner.isValid(universe.not(newClause)))
				myResult.add(newClause);
		}
		for (int value = lower; value <= upper; value++) {
			newClause = universe.equals(symbol, universe.integer(value));
			if (!reasoner.isValid(universe.not(newClause)))
				myResult.add(newClause);
		}
		if (upperBoundCluaseNeeded)
			myResult.add(universe.lessThan(universe.integer(upper), symbol));
		if (start == constantBounds.length - 1)
			return myResult;
		subfixResult = this.generateConcreteValueClauses(reasoner,
				constantBounds, start + 1);
		for (BooleanExpression myClause : myResult) {
			for (BooleanExpression subfixClause : subfixResult) {
				result.add(universe.and(myClause, subfixClause));
			}
		}
		return result;
	}
}

/**
 * This represents the bound specification of a symbolic constant.
 * 
 * @author Manchun Zheng
 *
 */
class ConstantBound {
	/**
	 * The symbolic constant associates with this object
	 */
	SymbolicConstant constant;
	/**
	 * The lower bound of the symbolic constant
	 */
	int lower;
	/**
	 * The upper bound of the symbolic constant
	 */
	int upper;

	/**
	 * 
	 * @param constant
	 *            the symbolic constant
	 * @param lower
	 *            the lower bound
	 * @param upper
	 *            the upper bound
	 */
	ConstantBound(SymbolicConstant constant, int lower, int upper) {
		this.constant = constant;
		this.lower = lower;
		this.upper = upper;
	}
}
