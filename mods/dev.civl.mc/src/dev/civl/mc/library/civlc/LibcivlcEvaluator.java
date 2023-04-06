package dev.civl.mc.library.civlc;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryEvaluator;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.ArrayElementReference;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.ReferenceExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Number;

public class LibcivlcEvaluator extends BaseLibraryEvaluator
		implements
			LibraryEvaluator {

	public LibcivlcEvaluator(String name, Evaluator evaluator,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, evaluator, modelFactory, symbolicUtil, symbolicAnalyzer,
				civlConfig, libEvaluatorLoader);
	}

	@Override
	public Evaluation evaluateGuard(CIVLSource source, State state, int pid,
			String function, Expression[] arguments)
			throws UnsatisfiablePathConditionException {
		Pair<State, SymbolicExpression[]> argumentsEval;

		switch (function) {
			case "$wait" :
				argumentsEval = this.evaluateArguments(state, pid, arguments);
				return guardOfWait(argumentsEval.left, pid, arguments,
						argumentsEval.right);
			case "$waitall" :
				argumentsEval = this.evaluateArguments(state, pid, arguments);
				return guardOfWaitall(argumentsEval.left, pid, arguments,
						argumentsEval.right);
			default :
				return new Evaluation(state, trueValue);
		}
	}

	/**
	 * Computes the guard of $wait.
	 * 
	 * @param state
	 * @param pid
	 * @param arguments
	 * @param argumentValues
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation guardOfWait(State state, int pid, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression joinProcess = argumentValues[0];
		BooleanExpression guard;
		int pidValue;
		Expression joinProcessExpr = arguments[0];

		if (joinProcess.operator() != SymbolicOperator.TUPLE) {
			String process = state.getProcessState(pid).name() + "(id=" + pid
					+ ")";

			this.errorLogger.logSimpleError(joinProcessExpr.getSource(), state,
					pid, process, symbolicAnalyzer.stateInformation(state),
					CIVLProperty.OTHER,
					"the argument of $wait should be concrete, but the actual value is "
							+ joinProcess);
			throw new UnsatisfiablePathConditionException();
		}
		pidValue = modelFactory.getProcessId(joinProcess);
		if (modelFactory.isPocessIdDefined(pidValue)
				&& !modelFactory.isProcessIdNull(pidValue)
				&& state.getProcessState(pidValue) != null
				&& !state.getProcessState(pidValue).hasEmptyStack())
			guard = universe.falseExpression();
		else
			guard = universe.trueExpression();
		return new Evaluation(state, guard);
	}

	/**
	 * void $waitall($proc *procs, int numProcs);
	 * 
	 * @param state
	 *            the current state
	 * @param pid
	 *            the process ID of the process executing the $waitall
	 * @param arguments
	 *            two arguments: 0:pointer to $proc, 1:number of processes
	 * @param argumentValues
	 *            the results of evaluating of the two expressions of arguments
	 * @return a {@link BooleanExpression} which holds iff all processes in the
	 *         list have terminated
	 * @throws UnsatisfiablePathConditionException
	 *             if the second argument (the number of processes) is not
	 *             concrete, or the first argument does not point to a valid
	 *             list of processes of the given number.
	 */
	private Evaluation guardOfWaitall(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression procsPointer = argumentValues[0];
		SymbolicExpression numOfProcs = argumentValues[1];
		Reasoner reasoner = universe.reasoner(state.getPathCondition(universe));
		IntegerNumber number_nprocs = (IntegerNumber) reasoner
				.extractNumber((NumericExpression) numOfProcs);
		String process = state.getProcessState(pid).name() + "(id=" + pid + ")";

		if (number_nprocs == null) {
			this.errorLogger.logSimpleError(arguments[1].getSource(), state,
					pid, process, symbolicAnalyzer.stateInformation(state),
					CIVLProperty.OTHER, "the number of processes for $waitall "
							+ "needs a concrete value");
			throw new UnsatisfiablePathConditionException();
		}

		int numOfProcs_int = number_nprocs.intValue();

		if (numOfProcs_int == 0)
			return new Evaluation(state, trueValue);
		if (symbolicUtil.isNullPointer(procsPointer)
				&& civlConfig.isPropertyToggled(CIVLProperty.POINTER)) {
			this.errorLogger.logSimpleError(arguments[0].getSource(), state,
					pid, process, symbolicAnalyzer.stateInformation(state),
					CIVLProperty.POINTER,
					"pointer argument to $waitall is NULL");
			throw new UnsatisfiablePathConditionException();
		}

		CIVLSource procsSource = arguments[0].getSource();
		Evaluation eval;
		ReferenceExpression ptrRef = symbolicUtil.getSymRef(procsPointer);

		if (ptrRef.isArrayElementReference()) {
			ArrayElementReference elementRef = (ArrayElementReference) ptrRef;
			NumericExpression startIdxExpr = elementRef.getIndex();
			int startIndex, stopIndex;
			SymbolicExpression procArray, parentPtr;

			if (startIdxExpr.isZero()) {
				startIndex = 0;
			} else {
				Number startIdxNum = reasoner.extractNumber(startIdxExpr);

				if (startIdxNum == null) {
					this.errorLogger.logSimpleError(procsSource, state, pid,
							process, symbolicAnalyzer.stateInformation(state),
							CIVLProperty.OTHER,
							"pointer into proc array must have concrete index");
					throw new UnsatisfiablePathConditionException();
				}
				startIndex = ((IntegerNumber) startIdxNum).intValue();
			}
			parentPtr = symbolicUtil.parentPointer(procsPointer);
			eval = evaluator.dereference(procsSource, state, pid, process,
					parentPtr, false, true);
			state = eval.state;
			procArray = eval.value;
			stopIndex = startIndex + numOfProcs_int;
			for (int idx = startIndex; idx < stopIndex; idx++) {
				SymbolicExpression proc = universe.arrayRead(procArray,
						universe.integer(idx));
				int pidValue = modelFactory.getProcessId(proc);

				if (!modelFactory.isProcessIdNull(pidValue)
						&& modelFactory.isPocessIdDefined(pidValue)
						&& !state.getProcessState(pidValue).hasEmptyStack()) {
					eval.value = falseValue;
					return eval;
				}
			}
		} else {
			for (int i = 0; i < numOfProcs_int; i++) {
				NumericExpression offSetV = universe.integer(i);
				SymbolicExpression procPointer, proc;
				int pidValue;

				eval = evaluator.arrayElementReferenceAdd(state, pid,
						procsPointer, offSetV, procsSource).left;
				procPointer = eval.value;
				state = eval.state;
				eval = evaluator.dereference(procsSource, state, pid, process,
						procPointer, false, true);
				proc = eval.value;
				state = eval.state;
				pidValue = modelFactory.getProcessId(proc);
				if (!modelFactory.isProcessIdNull(pidValue)
						&& modelFactory.isPocessIdDefined(pidValue)
						&& !state.getProcessState(pidValue).hasEmptyStack()) {
					eval.value = falseValue;
					return eval;
				}
			}
		}
		return new Evaluation(state, trueValue);
	}
}
