package dev.civl.abc.ast.node.IF.statement;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * A for loop, in addition to the expression and body that all loops possess,
 * has an initializer and incrementer.
 * 
 * The initializer can be either an expression or a declaration.
 * 
 * See C11 Sec. 6.8.5.
 * 
 * @author siegel
 * 
 */
public interface ForLoopNode extends LoopNode {

	/**
	 * Gets the initializer part of this for loop node. Note that this is an
	 * instance of either {@link ExpressionNode} or {@link DeclarationListNode}.
	 */
	ForLoopInitializerNode getInitializer();

	/**
	 * Sets the initializer part of this for loop node.
	 * 
	 * @param initNode
	 *            the initializer
	 */
	void setInitializer(ForLoopInitializerNode initNode);

	/**
	 * Gets the incrementer part of this for loop node.
	 * 
	 * @return incrementer
	 */
	ExpressionNode getIncrementer();

	/**
	 * Sets the incrementer part of this for loop node.
	 * 
	 * @param node
	 *            the incrementer
	 */
	void setIncrementer(ExpressionNode node);

	/**
	 * Returns whether the loop has been marked as being "standard." See
	 * {@link setStandard} for what conditions are supposed to hold in order for
	 * this to return true.
	 * 
	 * @return whether the loop was marked as "standard"
	 */
	boolean isStandard();

	/**
	 * Mark whether the for loop is "standard" or not. A for loop is standard
	 * iff the following conditions are guaranteed to hold for all valid
	 * error-free executions (so in particular, an execution is allowed to
	 * violate one of these conditions as long as that execution is guaranteed
	 * to reach an error state such as an assertion violation):
	 * 
	 * 1. The loop has an initializer expression of the form
	 * <code>int i = a</code> or <code>i = a</code> for some variable
	 * <code>i</code>, called the "loop variable," and some integer expression
	 * <code>a</code>.
	 * 
	 * 2. The loop has a conditional of the form <code>i < b</code> or
	 * <code>i <= b</code> in which <code>i</code> is the loop variable and
	 * <code>b</code> is some integer expression.
	 * 
	 * 3. If evaluating the expression <code>b</code> at the start of an
	 * iteration results in the value <code>x</code> then evaluating it at the
	 * end of the iteration should also result in the value <code>x</code>.
	 * 
	 * 4. If the loop variable <code>i</code> evaluates to <code>y</code> at the
	 * start of an iteration, then at the end of that iteration, <code>i</code>
	 * should evaluate to <code>y+1</code>.
	 */
	void setStandard(boolean isStandard);

	@Override
	ForLoopNode copy();

}
