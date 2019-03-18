package edu.udel.cis.vsl.civl.transform.analysisI.common;

import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode.StatementKind;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;

public class CommonFlowInsensePointsToAnalyzer
		implements
			FlowInsensePointsToAnalyzer {

	@Override
	public PointsToGraph getGraph(AssignmentSequence stmts) {
		// TODO Auto-generated method stub
		return null;
	}

	private void processStatement(StatementNode stmt) {
		StatementKind kind = stmt.statementKind();

	}
}
