/**
 * 
 */
package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.variable.Variable;

/**
 * A use of a variable in an expression.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public interface VariableExpression extends LHSExpression {

	/**
	 * @return The variable
	 */
	Variable variable();

	/**
	 * @param variable
	 *            The variable.
	 */
	void setVariable(Variable variable);
}
