package dev.civl.mc.model.common.expression;

import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.AbstractFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.DerivativeCallExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.IntegerLiteralExpression;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.util.IF.Pair;

public class CommonDerivativeCallExpression
		extends
			CommonAbstractFunctionCallExpression
		implements
			DerivativeCallExpression {

	private List<Pair<Variable, IntegerLiteralExpression>> partials;

	/**
	 * An abstract function call.
	 * 
	 * @param source
	 *            The source information corresponding to this abstract function
	 *            call.
	 * @param function
	 *            The abstract function.
	 * @param partials
	 *            The pairs representing which partial derivatives are taken.
	 *            Each pair is comprised of the variable for the parameter in
	 *            which the partial derivative is taken, and an integer
	 *            indicating how many times that partial is taken.
	 * @param arguments
	 *            Expressions for the arguments used in the abstract function
	 *            call.
	 */
	public CommonDerivativeCallExpression(CIVLSource source, Scope hscope,
			Scope lscope, AbstractFunction function,
			List<Pair<Variable, IntegerLiteralExpression>> partials,
			List<Expression> arguments) {
		super(source, hscope, lscope, function, arguments);
		this.partials = partials;
	}

	@Override
	public List<Pair<Variable, IntegerLiteralExpression>> partials() {
		return partials;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.DERIVATIVE;
	}

	@Override
	public String toString() {
		String result = "$D[" + function().name().name();

		for (Pair<Variable, IntegerLiteralExpression> partial : partials) {
			result += ", {";
			result += partial.left.name().name();
			result += ",";
			result += partial.right.value().toString();
			result += "}";
		}
		result += "](";
		for (int i = 0; i < arguments().size(); i++) {
			if (i != 0) {
				result += ", ";
			}
			result += arguments().get(i);
		}
		result += ")";
		return result;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		super.addFreeVariables(result);
		for (Pair<Variable, IntegerLiteralExpression> pair : partials) {
			result.add(pair.left);
			((CommonExpression) pair.right).addFreeVariables(result);
		}
	}
}
