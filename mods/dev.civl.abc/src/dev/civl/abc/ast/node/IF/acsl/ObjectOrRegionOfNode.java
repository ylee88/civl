package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * This represents a <code>$object_of</code> of <code>$region_of</code>
 * expression.
 * 
 * @author Manchun Zheng
 *
 */
public interface ObjectOrRegionOfNode extends ExpressionNode {
	/**
	 * The operand, which has pointer type
	 * 
	 * @return
	 */
	ExpressionNode operand();

	/**
	 * True iff this expression is <code>$object_of</code>
	 * 
	 * @return
	 */
	boolean isObjectOf();

	/**
	 * True iff this expression is <code>$region_of</code>
	 * 
	 * @return
	 */
	boolean isRegionOf();
}
