package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
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

	/**
	 * 
	 * @param var
	 *            a variable
	 * @return the set of memory locations (in the form of their
	 *         abstractions---AssignExprIF) that the variable may points to
	 */
	Iterable<AssignExprIF> mayPointsTo(Variable var);
}
