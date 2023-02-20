package dev.civl.abc.analysis.pointsTo.IF;

import dev.civl.abc.ast.entity.IF.Variable;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;

public interface AssignStoreExprIF extends AssignExprIF {

	boolean isAllocation();

	/**
	 * either {@link IdentifierExpressionNode} or an expression node represening
	 * string literal or allocation.
	 * 
	 * @return
	 */
	ExpressionNode store();

	Variable variable();
}
