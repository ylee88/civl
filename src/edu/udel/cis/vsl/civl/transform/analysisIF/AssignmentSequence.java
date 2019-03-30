package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.util.IF.Pair;

/**
 * <p>
 * The abstraction of a function body for points-to analysis.
 * </p>
 * 
 * 
 * @author ziqing
 *
 */
public interface AssignmentSequence extends Iterable<AssignmentIF> {
	/**
	 * 
	 * @param lvalue
	 * @return an abstraction of an expression that appears in the
	 *         AssignmentSequence; or a fresh new abstraction. The returned
	 *         abstraction consists of a {@link AssignExprIF} "a" and a boolean
	 *         value indicating if the abstract is "*a" (true value) or just "a"
	 *         (false value).
	 */
	Pair<AssignExprIF, Boolean> getAbstraction(ExpressionNode expr);

	/**
	 * 
	 * @return the InvocationGraphNode that is associated with a function of
	 *         which this instance is an abstraction
	 */
	InvocationGraphNode getIGNode();
}
