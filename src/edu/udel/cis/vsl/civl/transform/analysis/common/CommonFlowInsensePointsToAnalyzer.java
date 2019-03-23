package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;

public class CommonFlowInsensePointsToAnalyzer
		implements
			FlowInsensePointsToAnalyzer {

	private SymbolicUniverse universe;

	public CommonFlowInsensePointsToAnalyzer(SymbolicUniverse universe) {
		this.universe = universe;
	}

	@Override
	public PointsToGraph getGraph(AssignmentSequence stmts) {
		return null;
	}
}
