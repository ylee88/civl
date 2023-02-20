package dev.civl.mc.library.time;

import java.util.Arrays;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryExecutor;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Executor;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.LibraryExecutor;
import dev.civl.mc.semantics.IF.LibraryExecutorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicType;

public class LibtimeExecutor extends BaseLibraryExecutor
		implements
			LibraryExecutor {

	private SymbolicConstant localtimeFunc;
	private CIVLType tmType;
	private SymbolicType tmSymbolicType;
	private SymbolicConstant tmToStrFunc;
	private SymbolicArrayType stringSymbolicType;
	private SymbolicConstant tmToStrSizeFunc;

	public LibtimeExecutor(String name, Executor primaryExecutor,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryExecutorLoader libExecutorLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryExecutor, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libExecutorLoader,
				libEvaluatorLoader);
		this.tmType = this.typeFactory.systemType(ModelConfiguration.TM_TYPE);
		if (tmType != null)
			this.tmSymbolicType = tmType.getDynamicType(universe);
		this.stringSymbolicType = universe.arrayType(universe.characterType());
		if (tmType != null)
			this.localtimeFunc = universe.symbolicConstant(
					universe.stringObject("localtime"),
					universe.functionType(Arrays.asList(universe.realType()),
							this.tmSymbolicType));
		if (tmType != null)
			this.tmToStrFunc = universe.symbolicConstant(
					universe.stringObject("strftime"),
					universe.functionType(
							Arrays.asList(universe.integerType(),
									typeFactory.pointerSymbolicType(),
									this.tmSymbolicType),
							this.stringSymbolicType));
		if (tmType != null)
			this.tmToStrSizeFunc = universe.symbolicConstant(
					universe.stringObject("strftimeSize"),
					universe.functionType(
							Arrays.asList(universe.integerType(),
									typeFactory.pointerSymbolicType(),
									this.tmSymbolicType),
							universe.integerType()));
	}

	@Override
	protected Evaluation executeValue(State state, int pid, String process,
			CIVLSource source, String functionName, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		Evaluation callEval = null;

		switch (functionName) {
			case "localtime" :
				callEval = this.executeLocalTime(state, pid, arguments,
						argumentValues);
				break;
			case "strftime" :
				callEval = this.executeStrftime(state, pid, arguments,
						argumentValues);
				break;
			default :
				throw new CIVLUnimplementedFeatureException(
						"execution of function " + name + " in time library");
		}
		return callEval;
	}

	/**
	 * <pre>
	 * size_t strftime(char * restrict s,
	 *               size_t maxsize,
	 *               const char * restrict format,
	 *               const struct tm * restrict timeptr);
	 * </pre>
	 * 
	 * @param state
	 * @param pid
	 * @param lhs
	 * @param arguments
	 * @param argumentValues
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation executeStrftime(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression resultPointer = argumentValues[0];
		String process = state.getProcessState(pid).name();
		Evaluation eval = this.evaluator.dereference(arguments[3].getSource(),
				state, pid, process, argumentValues[3], false, true);
		SymbolicExpression tmValue, sizeValue, tmStr;

		resultPointer = this.symbolicUtil.parentPointer(resultPointer);
		state = eval.state;
		tmValue = eval.value;
		tmStr = universe.apply(tmToStrFunc,
				Arrays.asList(argumentValues[1], argumentValues[2], tmValue));
		state = this.primaryExecutor.assign(arguments[0].getSource(), state,
				pid, resultPointer, tmStr);
		sizeValue = universe.apply(tmToStrSizeFunc,
				Arrays.asList(argumentValues[1], argumentValues[2], tmValue));
		return new Evaluation(state, sizeValue);
	}

	/**
	 * <pre>
	 * #include <time.h>
	 * struct tm *localtime(const time_t *timer);
	 * 
	 * Description
	 * The localtime function converts the calendar time pointed to by timer 
	 * into a broken-down time, expressed as local time.
	 * 
	 * Returns
	 * The localtime function returns a pointer to the broken-down time, 
	 * or a null pointer if the specified time cannot be converted to 
	 * local time.
	 * </pre>
	 * 
	 * @param state
	 * @param pid
	 * @param lhs
	 * @param arguments
	 * @param argumentValues
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation executeLocalTime(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		String process = state.getProcessState(pid).name();
		Evaluation eval = evaluator.dereference(arguments[0].getSource(), state,
				pid, process, argumentValues[0], false, true);
		SymbolicExpression result;
		Variable brokenTimeVar = this.modelFactory.brokenTimeVariable();
		SymbolicExpression brokenTimePointer;

		state = eval.state;
		result = universe.apply(localtimeFunc, Arrays.asList(eval.value));
		state = this.stateFactory.setVariable(state, brokenTimeVar, pid,
				result);
		brokenTimePointer = this.symbolicUtil.makePointer(
				state.getDyscope(pid, brokenTimeVar.scope()),
				brokenTimeVar.vid(), universe.identityReference());
		return new Evaluation(state, brokenTimePointer);
	}
}
