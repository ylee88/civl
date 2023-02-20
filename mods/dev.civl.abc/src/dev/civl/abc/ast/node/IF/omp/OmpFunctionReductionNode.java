package dev.civl.abc.ast.node.IF.omp;

import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;

/**
 * This represents an OpenMP reduction identifier.
 * 
 * @author Manchun Zheng
 * 
 */
public interface OmpFunctionReductionNode extends OmpReductionNode {
	/**
	 * The function referred to by this OpenMP reduction identifier.
	 * 
	 * @return the function referred to by this OpenMP reduction identifier.
	 */
	IdentifierExpressionNode function();
}
