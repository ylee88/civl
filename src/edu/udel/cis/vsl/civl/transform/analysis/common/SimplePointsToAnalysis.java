package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;

public class SimplePointsToAnalysis {

	static public AssignmentFactory newAssignmentFactory() {
		return new CommonAssignmentFactory(new CommonInvocationGraphFactory());
	}

	static public PointsToGraph newPointsToGraph(
			AssignmentSequence programAbstraction) {
		return new CommonPointsToGraph(programAbstraction);
	}
}
