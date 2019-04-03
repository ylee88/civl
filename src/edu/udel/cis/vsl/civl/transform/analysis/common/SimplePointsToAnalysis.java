package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;

public class SimplePointsToAnalysis {

	static public AssignmentFactory newAssignmentFactory(
			InvocationGraphFactory igFactory) {
		return new CommonAssignmentFactory(igFactory);
	}

	static public InvocationGraphFactory newInvocationGraphFactory() {
		return new CommonInvocationGraphFactory();
	}

	static public PointsToGraph newPointsToGraph(
			AssignmentSequence programAbstraction, SymbolicUniverse universe) {
		return new CommonPointsToGraph(programAbstraction, universe);
	}
}
