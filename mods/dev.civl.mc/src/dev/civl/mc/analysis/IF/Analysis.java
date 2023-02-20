package dev.civl.mc.analysis.IF;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import dev.civl.mc.analysis.common.AbsCallAnalyzer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.state.IF.State;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * Entry point of the module civl.analysis.
 * 
 * @author Manchun Zheng
 *
 */
public class Analysis {

	/**
	 * Name of the absolute value function.
	 */
	public static final String ABS = "abs";

	/**
	 * performs static analysis on the given statement to decide if any code
	 * analysis provided by the specified code analyzers is needed.
	 * 
	 * @param statement
	 *            the statement to be analyzed
	 * @param analyzers
	 *            the list of analyzers that CIVL wants to use for statements
	 */
	public static void staticAnalysis(Statement statement,
			List<CodeAnalyzer> analyzers) {
		for (CodeAnalyzer analyzer : analyzers)
			analyzer.staticAnalysis(statement);
	}

	/**
	 * gets all code analyzers as required in the configuration.
	 * 
	 * @param config
	 *            the configuration which contains the information of what
	 *            analyzers are requested
	 * @param universe
	 *            the symbolic universe to be used by the analyzers
	 * @return all code analyzers as required in the configuration
	 */
	public static List<CodeAnalyzer> getAnalyzers(CIVLConfiguration config,
			SymbolicUniverse universe) {
		List<CodeAnalyzer> result = new LinkedList<>();

		if (config.analyzeAbs())
			result.add(new AbsCallAnalyzer(universe));
		return result;
	}

	/**
	 * Analyzes a call statement at a certain state.
	 * 
	 * @param analyzers
	 *            the list of analyzers to be applied to the statement
	 * @param state
	 *            the state where the analysis happens
	 * @param pid
	 *            the PID of the process that executes the statement
	 * @param statement
	 *            the call statement to be analyzed
	 * @param arguments
	 *            the evaluation of the arguments of the call statement
	 */
	public static void analyzeCall(List<CodeAnalyzer> analyzers, State state,
			int pid, CallOrSpawnStatement statement,
			SymbolicExpression[] arguments) {
		for (CodeAnalyzer analyzer : analyzers)
			analyzer.analyze(state, pid, statement, arguments);
	}

	/**
	 * prints the analysis result of each code analyzer.
	 * 
	 * @param analyzers
	 *            the list of code analyzers, the result of which is to be
	 *            printed
	 * @param out
	 *            the output stream
	 */
	public static void printResults(List<CodeAnalyzer> analyzers,
			PrintStream out) {
		for (CodeAnalyzer analyzer : analyzers)
			analyzer.printAnalysis(out);
	}
}
