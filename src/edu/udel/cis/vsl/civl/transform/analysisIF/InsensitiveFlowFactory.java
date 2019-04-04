package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

public interface InsensitiveFlowFactory {

	/**
	 * Creates a new {@link InsensitiveFlow} for a defined function
	 * 
	 * @param function
	 *            a {@link Function} entity whose
	 *            {@link Function#getDefinition()} shall not return null
	 * @param igNode
	 *            the {@link InvocationGraphNode} associated with the given
	 *            function
	 * @return
	 */
	InsensitiveFlow InsensitiveFlow(Function function,
			InvocationGraphNode igNode);

	/**
	 * <p>
	 * Creates a new instance of {@link AssignmentIF}. Given a left-hand side
	 * abstraction "lhs" and a right-hand side abstraction "rhs", the created
	 * assignment is one of the four cases: <code> 
	 * 1. lhs = &rhs;
	 * 2. lhs = rhs;
	 * 3. *lhs = rhs;
	 * 4  lhs = *rhs;
	 * </code>
	 * </p>
	 * 
	 * @param lhs
	 *            the {@link AssignExprIF} of the left-hand side of the
	 *            assignment
	 * @param lhsDeref
	 *            will the left-hand side be dereferenced ?
	 * @param rhs
	 *            lhs the {@link AssignExprIF} of the right-hand side of the
	 *            assignment
	 * @param rhsDeref
	 *            will the right-hand side be dereferenced ?
	 * @param rhsAddrof
	 *            will the right-hand side be taken address-of ?
	 * @return
	 */
	AssignmentIF assignment(AssignExprIF lhs, boolean lhsDeref,
			AssignExprIF rhs, boolean rhsDeref, boolean rhsAddrof);

	/**
	 * <p>
	 * A abstraction of an expression at one side of an {@link AssignmentIF},
	 * which is associated with a pointer-type variable
	 * </p>
	 * 
	 * @param source
	 * @return
	 */
	AssignExprIF assignExpr(Entity source);

	/**
	 * <p>
	 * A abstraction of an expression at one side of an {@link AssignmentIF},
	 * which is associated with an non-trivial expression
	 * </p>
	 * 
	 * @param source
	 * @return
	 */
	AssignExprIF assignExpr(ExpressionNode source);

	/**
	 * <p>
	 * A abstraction of an expression at right-hand side of an assignment,which
	 * represents the worst case that it points to EVERYTHING
	 * </p>
	 * 
	 * @return
	 */
	AssignExprIF full();
}
