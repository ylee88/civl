package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.util.IF.Pair;

/**
 * <p>
 * Abstract an program fragment to a sequence of {@link AssignmentIF} for
 * flow-insensitive analysis.
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
}
