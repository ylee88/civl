package dev.civl.mc.library.pthread;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryEvaluator;
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
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class LibpthreadEvaluator extends BaseLibraryEvaluator
		implements
			LibraryEvaluator {

	public LibpthreadEvaluator(String name, Evaluator evaluator,
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
		int numArgs = arguments.length;
		SymbolicExpression[] argumentValues = new SymbolicExpression[numArgs];
		Evaluation eval;

		for (int i = 0; i < numArgs; i++) {
			eval = this.evaluator.evaluate(state, pid, arguments[i]);
			state = eval.state;
			argumentValues[i] = eval.value;
		}
		switch (function) {
			case "$pthread_gpool_join" :
				return evaluateGuard_pthread_gpool_join(source, state, pid,
						function, arguments, argumentValues);
			default :
				return super.evaluateGuard(source, state, pid, function,
						arguments);
		}
	}

	private Evaluation evaluateGuard_pthread_gpool_join(CIVLSource source,
			State state, int pid, String function, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression gpool = argumentValues[0];
		Evaluation eval;
		SymbolicExpression gpoolObj, threads;
		String process = state.getProcessState(pid).name();
		int numThreads;

		eval = this.evaluator.dereference(source, state, pid, process, gpool,
				false, true);
		gpoolObj = eval.value;
		state = eval.state;
		threads = this.universe.tupleRead(gpoolObj, zeroObject);
		numThreads = this.symbolicUtil.extractInt(source,
				universe.length(threads));
		for (int i = 0; i < numThreads; i++) {
			SymbolicExpression threadObj = universe.arrayRead(threads,
					universe.integer(i));
			SymbolicExpression pidValue;
			int pidInt;

			pidValue = universe.tupleRead(threadObj, this.zeroObject);
			pidInt = modelFactory.getProcessId(pidValue);
			if (pidInt != pid && !modelFactory.isProcessIdNull(pidInt)
					&& modelFactory.isPocessIdDefined(pidInt))
				if (!state.getProcessState(pidInt).hasEmptyStack())
					return new Evaluation(state, this.falseValue);
		}
		return new Evaluation(state, this.trueValue);
	}
}
