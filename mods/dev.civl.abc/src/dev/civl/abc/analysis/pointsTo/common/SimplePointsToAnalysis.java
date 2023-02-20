package dev.civl.abc.analysis.pointsTo.common;

import dev.civl.abc.analysis.pointsTo.IF.FlowInsensePointsToAnalyzer;
import dev.civl.abc.analysis.pointsTo.IF.InvocationGraphNodeFactory;
import dev.civl.abc.analysis.pointsTo.IF.SimplePointsToAnalysisIF;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.type.IF.TypeFactory;

/**
 * The implementation that instantiates classes for
 * {@link SimplePointsToAnalysisIF}
 * 
 * @author ziqing
 *
 */
public class SimplePointsToAnalysis {

	static public FlowInsensePointsToAnalyzer flowInsensePointsToAnalyzer(
			AST program, TypeFactory typeFactory) {
		InvocationGraphNodeFactory igFactory = new CommonInvocationGraphFactory();
		return new CommonFlowInsensePointsToAnalyzer(program,
				new CommonInsensitiveFlowFactory(igFactory, typeFactory),
				igFactory);
	}
}
