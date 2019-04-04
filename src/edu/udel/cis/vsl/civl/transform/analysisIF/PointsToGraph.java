package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

/**
 * <p>
 * A graph G = (V, E, Pt) where V are nodes, E are edges and Pt is a function
 * from V to sets of V. In a PointsToGraph, V are memory locations; E are the
 * binary relations over V such that (v,v'), which means v is a subset of v'; Pt
 * is the "points-to" function such that Pt(v) is the set of memory locations
 * referred by v.
 * </p>
 * 
 * *
 * <p>
 * The inputs of a points-to graph is an {@link InsensitiveFlow} and an initial
 * points-to set (optional). A points-to graph encodes all may points-to
 * informations of the associated {@link InsensitiveFlow}. Initial points-to set
 * can be given via the method {@link #addPointsTo(AssignExprIF, Iterable)}.
 * Adding initial points-to set information will cause a re-computation of the
 * graph when next time {@link #mayPointsTo} gets called.
 * </p>
 * 
 * @author ziqing
 *
 */
public interface PointsToGraph {

	/**
	 * 
	 * @param entity
	 * 
	 * @return the may points-to set of the given entity
	 */
	Iterable<AssignExprIF> mayPointsTo(Entity entity);

	/**
	 * 
	 * @param expr
	 * @return the may points-to set of the given {@link AssignExprIF} which is
	 *         the abstract representation of an expression in this graph and
	 *         associated insensitive flow
	 */
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
