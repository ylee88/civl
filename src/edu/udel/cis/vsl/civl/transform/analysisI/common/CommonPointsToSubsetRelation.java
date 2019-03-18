package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.Arrays;

import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToSubsetRelation;

public class CommonPointsToSubsetRelation implements PointsToSubsetRelation {

	private PointsToNode subSet;

	private PointsToNode superSet;

	private int hashCode = -1;

	public CommonPointsToSubsetRelation(PointsToNode subset,
			PointsToNode superset) {
		this.subSet = subset;
		this.superSet = superset;
	}

	@Override
	public PointsToNode subSet() {
		return subSet;
	}

	@Override
	public PointsToNode superSet() {
		return superSet;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof PointsToSubsetRelation) {
			PointsToSubsetRelation thatRelation = (PointsToSubsetRelation) that;

			return thatRelation.subSet().equals(subSet)
					&& thatRelation.superSet().equals(superSet);
		}
		return false;
	}
	@Override
	public int hashCode() {
		if (hashCode == -1)
			hashCode = Arrays.hashCode(new PointsToNode[]{subSet, superSet});
		return hashCode;
	}
}
