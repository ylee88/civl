package dev.civl.mc.model.IF.expression;

import java.util.List;

import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.util.IF.Pair;

/**
 * An uninterpreted call to the derivative of an abstract function.
 * 
 * @author zirkel
 * 
 */
public interface DerivativeCallExpression extends
		AbstractFunctionCallExpression {

	/**
	 * @return The list of pairs of partial derivatives taken. Each pair has the
	 *         variable that is the parameter for which the partial derivative
	 *         is taken, and number of times that partial derivative is taken.
	 */
	List<Pair<Variable, IntegerLiteralExpression>> partials();

}
