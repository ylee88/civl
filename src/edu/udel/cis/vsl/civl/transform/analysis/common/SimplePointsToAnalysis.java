package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;

public class SimplePointsToAnalysis {

	static public AssignmentFactory newAssignmentFactory() {
		return new CommonAssignmentFactory();
	}

	static public InvocationGraphFactory newIGFactory() {
		return new CommonInvocationGraphFactory();
	}

	static public AssignmentSequence newAssignmentSequence(
			Iterable<BlockItemNode> program, AssignmentFactory factory,
			InvocationGraphFactory igFactory, InvocationGraphNode igNode) {
		return new CommonAssignmentSequence(program, factory, igFactory,
				igNode);
	}

	static public PointsToGraph newPointsToGraph(
			AssignmentSequence programAbstraction) {
		return new CommonPointsToGraph(programAbstraction);
	}
}
