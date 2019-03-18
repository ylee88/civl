package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Map;
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
	 * Make the graph complete. A graph cannot be modified once it completes
	 * (i.e. addNode, addRelation). A graph is unreadable before it completes
	 * (i.e. getNodes, getRelations).
	 */
	void complete();

	/* ************ "modify" operations **************/
	/**
	 * add a node to the graph
	 * 
	 * @param node
	 */
	void addNode(PointsToNode node);

	/**
	 * add a relation to the graph
	 * 
	 * @param node
	 */
	void addRelation(PointsToSubsetRelation node);

	/* ************ "read" operations **************/

	/**
	 * 
	 * @return the set of nodes this graph contains in the form of a map from
	 *         memory locations to their unique associated nodes.
	 */
	Map<Entity, PointsToNode> getNodes();

	/**
	 * @return the set of relations over the set of nodes that makes this graph
	 *         minimal
	 */
	Set<PointsToSubsetRelation> getRelations();
}
