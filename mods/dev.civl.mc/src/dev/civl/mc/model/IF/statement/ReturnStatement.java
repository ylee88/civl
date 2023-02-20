/**
 * 
 */
package dev.civl.mc.model.IF.statement;

import dev.civl.mc.model.IF.expression.Expression;

/**
 * A return statement.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public interface ReturnStatement extends Statement {

	/**
	 * @return The expression being returned. Null if non-existent.
	 */
	Expression expression();

	/**
	 * @param expression
	 *            The expression being returned. Null if non-existent.
	 */
	void setExpression(Expression expression);
}
