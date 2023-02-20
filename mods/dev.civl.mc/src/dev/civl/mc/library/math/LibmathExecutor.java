package dev.civl.mc.library.math;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryExecutor;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Executor;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.LibraryExecutor;
import dev.civl.mc.semantics.IF.LibraryExecutorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Number;

public class LibmathExecutor extends BaseLibraryExecutor
		implements
			LibraryExecutor {

	public LibmathExecutor(String name, Executor primaryExecutor,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryExecutorLoader libExecutorLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryExecutor, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libExecutorLoader,
				libEvaluatorLoader);
	}

	@Override
	protected Evaluation executeValue(State state, int pid, String process,
			CIVLSource source, String functionName, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		Evaluation callEval = null;

		switch (functionName) {
			case "ceil" :
			case "ceilf" :
			case "ceill" :
				callEval = executeCeil(state, pid, process, source, arguments,
						argumentValues);
				break;
			case "floor" :
			case "floorf" :
			case "floorl" :
				callEval = executeFloor(state, pid, process, source, arguments,
						argumentValues);
				break;
			default :
				throw new CIVLInternalException(
						"Unknown civl-mpi function: " + name, source);
		}
		return callEval;
	}

	/**
	 * Executes the series of "floor" functions in C11 math library. Including
	 * <code>floor, floorf, and floorl</code>. The execution returns the
	 * {@link SymbolicUniverse#floor(NumericExpression)} of the sole argument.
	 */
	private Evaluation executeFloor(State state, int pid, String process,
			CIVLSource source, Expression[] arguments,
			SymbolicExpression[] argumentValues) {
		NumericExpression ret;
		NumericExpression number = (NumericExpression) argumentValues[0];
		Number concreteValue = universe.extractNumber(number);

		if (concreteValue == null) {
			Reasoner reasoner = universe
					.reasoner(state.getPathCondition(universe));

			concreteValue = reasoner.extractNumber(number);
		}
		if (concreteValue != null)
			// The number has a concrete value:
			ret = universe.floor(universe.number(concreteValue));
		else
			ret = universe.floor(number);
		ret = (NumericExpression) universe.cast(universe.realType(), ret);
		return new Evaluation(state, ret);
	}

	/**
	 * Executes the series of "ceil" functions in C11 math library. Including
	 * <code>ceil, ceilf, and ceill</code>. The execution returns the
	 * {@link SymbolicUniverse#ceil(NumericExpression)} of the sole argument.
	 */
	private Evaluation executeCeil(State state, int pid, String process,
			CIVLSource source, Expression[] arguments,
			SymbolicExpression[] argumentValues) {
		NumericExpression ret;
		NumericExpression number = (NumericExpression) argumentValues[0];
		Number concreteValue = universe.extractNumber(number);

		if (concreteValue == null) {
			Reasoner reasoner = universe
					.reasoner(state.getPathCondition(universe));

			concreteValue = reasoner.extractNumber(number);
		}
		if (concreteValue != null)
			// The number has a concrete value:
			ret = universe.ceil(universe.number(concreteValue));
		else
			ret = universe.ceil(number);
		ret = (NumericExpression) universe.cast(universe.realType(), ret);
		return new Evaluation(state, ret);
	}
}
