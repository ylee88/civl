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

	List<AssignExprIF> mayPointsTo(Function func, Entity ptr);
}
