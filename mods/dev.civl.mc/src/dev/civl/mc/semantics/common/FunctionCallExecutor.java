package dev.civl.mc.semantics.common;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.log.IF.CIVLErrorLogger;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.SystemFunction;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryExecutorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;

public class FunctionCallExecutor extends CommonExecutor {

	public FunctionCallExecutor(ModelFactory modelFactory,
			StateFactory stateFactory, LibraryExecutorLoader loader,
			Evaluator evaluator, SymbolicAnalyzer symbolicAnalyzer,
			CIVLErrorLogger errorLogger, CIVLConfiguration civlConfig) {
		super(modelFactory, stateFactory, loader, evaluator, symbolicAnalyzer,
				errorLogger, civlConfig);
	}

	Evaluation evaluateAtomicPureFunction(State state, int pid,
			CallOrSpawnStatement call)
			throws UnsatisfiablePathConditionException {
		return this.executeSystemFunctionCall(state, pid, call,
				(SystemFunction) call.function());
	}
}
