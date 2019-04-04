package edu.udel.cis.vsl.civl.transform.analysis.common;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.InsensitiveFlowFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNodeFactory;

/**
 * The interface of the simple points-to analysis module
 * 
 * @author ziqing
 *
 */
public class SimplePointsToAnalysis {

	static public FlowInsensePointsToAnalyzer flowInsensePointsToAnalyzer(
			AST program) {
		InvocationGraphNodeFactory igFactory = new CommonInvocationGraphFactory();
		return new CommonFlowInsensePointsToAnalyzer(program,
				newInsensitiveFlowFactory(igFactory), igFactory);
	}

	static private InsensitiveFlowFactory newInsensitiveFlowFactory(
			InvocationGraphNodeFactory igFactory) {
		return new CommonInsensitiveFlowFactory(igFactory);
	}
}
