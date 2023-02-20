package dev.civl.mc.analysis.IF;

import java.io.PrintStream;

import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.state.IF.State;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * This represents a code analyzer for analyzing source code for a certain
 * property.
 * 
 * @author Manchun Zheng
 *
 */
public interface CodeAnalyzer {
	/**
	 * Analyzes a call statement.
	 * 
	 * @param state
	 * @param pid
	 * @param statement
	 * @param argumentValues
	 */
	void analyze(State state, int pid, CallOrSpawnStatement statement,
			SymbolicExpression[] argumentValues);

	/**
	 * Static analysis of the given statement. Basically, it answers the
	 * question is the analyzer applicable for the given statement?
	 * 
	 * @param statement
	 */
	void staticAnalysis(Statement statement);

	/**
	 * Print the results the analyzer.
	 * 
	 * @param out
	 */
	void printAnalysis(PrintStream out);
}
