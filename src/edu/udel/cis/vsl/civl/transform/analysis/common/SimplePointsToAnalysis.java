package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;

public class SimplePointsToAnalysis {

	static public AssignmentFactory newAssignmentFactory() {
		return new CommonAssignmentFactory();
	}

	static public AssignmentSequence newAssignmentSequence(
			Iterable<BlockItemNode> program, AssignmentFactory factory) {
		return new CommonAssignmentSequence(program, factory);
	}
}
