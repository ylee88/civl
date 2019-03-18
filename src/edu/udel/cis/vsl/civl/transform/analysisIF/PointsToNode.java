package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;

/**
 * <p>
 * A PointsToNode is associated with a unique memory location <code>m</code>
 * (i.e., variable, string, heap object, of type {@link Entity}) representing
 * the locations pointed by <code>m</code> (i.e., what one may get from
 * <code>*m</code>).
 * </p>
 * 
 * <p>
 * Every two {@link PointsToNode}s are disjoint, i.e., their {@link #pointsTo()}
 * sets are disjoint.
 * </p>
 * 
 * 
 * @author ziqing
 *
 */
public interface PointsToNode {

	/**
	 * @return the set of memory locations pointed to by this node. Each of the
	 *         memory location is associated with a {@link PointsToNode} in the
	 *         returned set of nodes. One can get the associated memory location
	 *         of a node via the node's interface
	 *         {@link PointsToNode#getEntity}.
	 */
	Set<PointsToNode> pointsTo();

	/**
	 * 
	 * @return the associated memory location which is an {@link Entity}
	 */
	Entity getEntity();
}
