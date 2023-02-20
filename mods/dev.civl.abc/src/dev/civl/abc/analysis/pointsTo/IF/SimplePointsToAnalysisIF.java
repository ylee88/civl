package dev.civl.abc.analysis.pointsTo.IF;

import dev.civl.abc.analysis.pointsTo.common.SimplePointsToAnalysis;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.type.IF.TypeFactory;

/**
 * The interface for create a points-to analyzer
 * 
 * @author ziqing
 *
 */
public class SimplePointsToAnalysisIF {
	static public FlowInsensePointsToAnalyzer flowInsensePointsToAnalyzer(
			AST program, TypeFactory typeFactory) {
		return SimplePointsToAnalysis.flowInsensePointsToAnalyzer(program,
				typeFactory);
	}
}
