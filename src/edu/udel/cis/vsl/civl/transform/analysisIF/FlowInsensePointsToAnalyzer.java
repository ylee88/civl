package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.IF.AST;

/**
 * A flow-insensitive PointsToAnalyzer takes a program ({@link AST}), builds
 * points-to graph for the program and provides service for querying the
 * "may points-to" set of a lvalue
 * 
 * @author ziqing
 *
 */
public interface FlowInsensePointsToAnalyzer {
	/**
	 * starts a points-to analysis on a sequence of statements
	 * 
	 * @param stmts
	 *            a sequence of statements
	 * @return the minimal points to graph that corresponds to the given
	 *         {@link AssignmentSequence}
	 */
	PointsToGraph getGraph(AssignmentSequence stmts);
}
