package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.List;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

/**
 * A flow-insensitive PointsToAnalyzer takes a program ({@link AST}), builds
 * points-to graph for the program and provides service for querying the "may
 * points-to" set of a lvalue
 * 
 * @author ziqing
 *
 */
public interface FlowInsensePointsToAnalyzer {

	/**
	 * Returns the points-to set of the given variable entity.
	 * 
	 * @param func
	 *            the function where the entity appears
	 * @param var
	 *            the variable entity
	 * @return the points-to set of the given variable entity
	 */
	List<AssignExprIF> mayPointsTo(Function func, Entity var);

	/**
	 * Returns the points-to set of the given {@link AssignExprIF}, which is an
	 * abstraction of an object.
	 * 
	 * @param func
	 *            the function where the pointer expression appears
	 * @param ptr
	 *            an expression abstraction
	 * @return the points-to set of the given {@link AssignExprIF}
	 */
	List<AssignExprIF> mayPointsTo(Function func, AssignExprIF ptr);

	/**
	 * 
	 * @return the analyzed program associated with this analyzer
	 */
	AST analyzedProgram();
}
