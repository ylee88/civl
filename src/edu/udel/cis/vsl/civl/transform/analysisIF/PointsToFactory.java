package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;

/**
 * A factory that provides the interface for generating {@link PointsToNode}s
 * and {@link PointsToSubsetRelation}s in fly-weight pattern.
 * 
 * @author ziqing
 *
 */
public interface PointsToFactory {

	/**
	 * Creates a new {@link PointsToNode}
	 * 
	 * @return
	 */
	PointsToNode newNode(Entity entity);

	/**
	 * 
	 * @param entity
	 * @return null if the node is never seen before
	 */
	PointsToNode getNode(Entity entity);
}
