package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

/**
 * <p>
 * A graph G = (V, E, Pt) where V are nodes, E are edges and Pt is a function
 * from V to sets of V. In a PointsToGraph, V are memory locations; E are the
 * binary relations over V such that if (v,v') in E, v is a subset of v'; Pt is
 * the "points-to" function such that Pt(v) is the set of memory locations
 * referred by v.
 * </p>
 * 
 * @author ziqing
 *
 */
public interface PointsToGraph {

	Iterable<AssignExprIF> mayPointsTo(Entity entity);

	Iterable<AssignExprIF> mayPointsTo(AssignExprIF expr);

	/**
	 * Adds point-to information to the graph. If there is any new information,
	 * the graph will be re-computed.
	 * 
	 * @param object
	 *            the object whose points-to set will be updated
	 * @param pointsTo
	 *            the points-to set that will be added
	 * @return true iff the graph will be re-computed.
	 */
	boolean addPointsTo(AssignExprIF object, Iterable<AssignExprIF> pointsTo);

	/**
	 * 
	 * @return a new instance that was deep copied from this instance
	 */
	PointsToGraph clone();
}
