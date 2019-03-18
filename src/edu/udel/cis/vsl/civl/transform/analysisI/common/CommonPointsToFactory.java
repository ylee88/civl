package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToNode;

public class CommonPointsToFactory implements PointsToFactory {

	Map<Entity, PointsToNode> allNodes = new HashMap<>();

	@Override
	public PointsToNode newNode(Entity entity) {
		PointsToNode node = allNodes.get(entity);

		if (node == null)
			node = new CommonPointsToNode(entity, new HashSet<>());
		allNodes.put(entity, node);
		return node;
	}

	@Override
	public PointsToNode getNode(Entity entity) {
		return allNodes.get(entity);
	}
}
