package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;

/**
 * <p>
 * A points-to graph is a set of {@link PointsToNode}s "N" and a set of
 * {@link PointsToSubsetRelation} "R" over "N". Note that a set of nodes itself
 * is a graph but may not be minimal. We call an instance of this class
 * <b>minimal graph for "N" over "R"</b>.
 * </p>
 *
 * @author ziqing
 *
 */
public interface PointsToGraph {

	/**
	 * add a relation to the graph
	 * 
	 * @param node
	 */
	void addRelation(PointsToNode subset, PointsToNode superset);

	/* ************ "read" operations **************/
	/**
	 * ask what memory locations a pointer may point to :
	 */
	Set<Entity> mayPointsTo(Entity entity);
}
