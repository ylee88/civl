package dev.civl.mc.analysis.common;

import dev.civl.mc.analysis.IF.CodeAnalyzer;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.state.IF.State;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * A general and abstract implementation of a code analyzer.
 * 
 * @author Manchun Zheng
 *
 */
public abstract class CommonCodeAnalyzer implements CodeAnalyzer {

	@Override
	public void analyze(State state, int pid, CallOrSpawnStatement statement,
			SymbolicExpression[] argumentValues) {
		return;
	}

}
