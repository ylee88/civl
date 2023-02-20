/**
 * 
 */
package dev.civl.mc.model.IF.expression;

import java.math.BigDecimal;

import dev.civl.mc.model.IF.type.CIVLPrimitiveType;

/**
 * A real literal.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public interface RealLiteralExpression extends LiteralExpression {

	/**
	 * @return The (arbitrary precision) real value.
	 */
	BigDecimal value();

	/**
	 * @param value
	 *            The (arbitrary precision) real value.
	 */
	void setValue(BigDecimal value);

	/**
	 * Returns the real type.
	 */
	CIVLPrimitiveType getExpressionType();

}
