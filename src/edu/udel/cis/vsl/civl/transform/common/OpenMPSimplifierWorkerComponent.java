package edu.udel.cis.vsl.civl.transform.common;

import java.util.Collection;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;

public class OpenMPSimplifierWorkerComponent {

	static public void collectDereference(Collection<Entity> privateIDs,
			Collection<Entity> loopPrivateIDs, Collection<Entity> localDecls,
			Set<Entity> outputAccessSet, OperatorNode opNode, Function function,
			FlowInsensePointsToAnalyzer pointsToAnalyzer) {
		assert opNode.getOperator() == Operator.DEREFERENCE;
		ExpressionNode ptr = opNode.getArgument(0);
		Iterable<AssignExprIF> pts = pointsToAnalyzer.mayPointsTo(function,
				ptr);

		for (AssignExprIF pt : pts) {
			Entity ptEntity = pt.source();

			if (ptEntity == null)
				throw new CIVLUnimplementedFeatureException(
						"process shared non-entity kind object "
								+ pt.nonEntitySource()
								+ " for openMP simplifier");
			if (!privateIDs.contains(ptEntity)
					&& !loopPrivateIDs.contains(ptEntity)
					&& !localDecls.contains(ptEntity))
				outputAccessSet.add(ptEntity);
		}
	}
}
