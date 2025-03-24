/**
 * 
 */
package dev.civl.mc.model.IF.expression;

import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * The parent of all literal expressions.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public interface LiteralExpression extends Expression {
	public enum LiteralKind {
		ARRAY, BOOLEAN, CHAR, INTEGER, REAL, COMPOUND, DOMAIN
	}

	LiteralKind literalKind();

	/**
	 * Directly set a symbolic expression as value of this literal expression
	 * 
	 * @param value
	 *            The symbolic expression of the constant value of this literal
	 *            expression
	 */
	void setLiteralConstantValue(SymbolicExpression value);
}
