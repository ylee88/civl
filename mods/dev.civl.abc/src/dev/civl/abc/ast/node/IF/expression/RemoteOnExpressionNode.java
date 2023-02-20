package dev.civl.abc.ast.node.IF.expression;

/**
 * A CIVL-C remote expression is used to reference a variable in another
 * process. Not yet implemented.
 * 
 * @author siegel
 * 
 */
public interface RemoteOnExpressionNode extends ExpressionNode {

	/**
	 * Gets the expression which yields the process on which the foreign
	 * variable resides.
	 * 
	 * @return the process expression
	 */
	ExpressionNode getProcessExpression();

	/**
	 * Sets the process expressions argument.
	 * 
	 * @param arg
	 *            the process expression argument
	 */
	void setProcessExpression(ExpressionNode arg);

	/**
	 * Gets the expression node which represents the foreign expression.
	 * 
	 * @return the foreign expression.
	 */
	ExpressionNode getForeignExpressionNode();

	/**
	 * Sets the identifier node argument.
	 * 
	 * @param arg
	 *            the identifier node argument
	 */
	void setForeignExpressionNode(ExpressionNode arg);

	@Override
	RemoteOnExpressionNode copy();

}
