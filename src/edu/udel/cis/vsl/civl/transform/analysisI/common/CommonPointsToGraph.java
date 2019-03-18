package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity.EntityKind;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.type.IF.Field;
import edu.udel.cis.vsl.abc.token.IF.Source;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToNode;

public class CommonPointsToGraph implements PointsToGraph {

	private Map<PointsToNode, Set<PointsToNode>> relations;

	private PointsToFactory factory;

	public CommonPointsToGraph(PointsToFactory factory) {
		relations = new HashMap<>();
		this.factory = factory;
	}

	@Override
	public void addRelation(PointsToNode subset, PointsToNode superset) {
		Set<PointsToNode> subsetsSharingCommonSuperset = relations
				.get(superset);

		if (subsetsSharingCommonSuperset == null)
			subsetsSharingCommonSuperset = new HashSet<>();
		subsetsSharingCommonSuperset.add(subset);
		relations.put(superset, subsetsSharingCommonSuperset);
	}

	@Override
	public Set<Entity> mayPointsTo(Entity entity) {
		PointsToNode node = factory.newNode(entity);

		if (node == null)
			throwError(entity);
		return mayPointsToWorker(node);
	}

	private Set<Entity> mayPointsToWorker(PointsToNode node) {
		Set<PointsToNode> subsets = relations.get(node);
		Set<Entity> result = new HashSet<>();

		for (PointsToNode referred : node.pointsTo())
			result.add(referred.getEntity());
		if (subsets != null)
			for (PointsToNode subset : subsets)
				result.addAll(mayPointsToWorker(subset));
		return result;
	}

	private void throwError(Entity entity) {
		EntityKind kind = entity.getEntityKind();
		Source source = null;

		switch (kind) {
			case FIELD :
				source = ((Field) entity).getDefinition().getSource();
				break;
			case VARIABLE :
				source = ((Variable) entity).getDefinition().getSource();
				break;
			default :
				throw new CIVLUnimplementedFeatureException(
						"Unknown memory location for points-to analysis: "
								+ entity.getName() + " : "
								+ entity.getEntityKind());
		}
		throw new CIVLInternalException(
				"missing points-to information of " + entity.getName(), source);
	}
}
