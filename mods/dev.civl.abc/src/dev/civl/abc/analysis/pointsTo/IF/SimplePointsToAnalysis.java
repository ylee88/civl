package dev.civl.abc.analysis.pointsTo.IF;

import dev.civl.abc.analysis.pointsTo.common.CommonFlowInsensePointsToAnalyzer;
import dev.civl.abc.analysis.pointsTo.common.CommonInsensitiveFlowFactory;
import dev.civl.abc.analysis.pointsTo.common.CommonInvocationGraphFactory;
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
