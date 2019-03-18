package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToNode;

public class CommonPointsToNode implements PointsToNode {

	private Entity memoryLocation;

	private Set<PointsToNode> pointsTo = null;

	public CommonPointsToNode(Entity memoryLocation,
			Set<PointsToNode> pointsTo) {
		this.memoryLocation = memoryLocation;
		this.pointsTo = pointsTo;
	}

	@Override
	public Set<PointsToNode> pointsTo() {
		return pointsTo;
	}

	@Override
	public Entity getEntity() {
		return memoryLocation;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof PointsToNode)
			return memoryLocation.equals(((PointsToNode) that).getEntity());
		return false;
	}

	@Override
	public int hashCode() {
		return this.memoryLocation.hashCode();
	}

	@Override
	public void addPointed(PointsToNode pointed) {
		this.pointsTo.add(pointed);
	}
}
