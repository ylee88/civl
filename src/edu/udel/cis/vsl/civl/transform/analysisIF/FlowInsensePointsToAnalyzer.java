package edu.udel.cis.vsl.civl.transform.analysisIF;

/**
 * A flow-insensitive PointsToAnalyzer takes a {@link AssignmentSequence} and
 * returns a {@link PointsToGraph}
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
