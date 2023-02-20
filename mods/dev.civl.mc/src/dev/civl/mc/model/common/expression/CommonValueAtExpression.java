package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.ValueAtExpression;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonValueAtExpression extends CommonExpression
		implements
			ValueAtExpression {
	private Expression state;
	private Expression pid;
	private Expression expression;

	public CommonValueAtExpression(CIVLSource source, Expression state,
			Expression pid, Expression expression) {
		super(source, null, null, expression.getExpressionType());
		this.state = state;
		this.pid = pid;
		this.expression = expression;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.VALUE_AT;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		Set<Variable> result = new HashSet<>(),
				subResult = state.variableAddressedOf(scope);

		if (subResult != null)
			result.addAll(subResult);
		subResult = pid.variableAddressedOf(scope);
		if (subResult != null)
			result.addAll(subResult);
		if (result.isEmpty())
			return null;
		return result;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		Set<Variable> result = new HashSet<>(),
				subResult = state.variableAddressedOf();

		if (subResult != null)
			result.addAll(subResult);
		subResult = pid.variableAddressedOf();
		if (subResult != null)
			result.addAll(subResult);
		if (result.isEmpty())
			return null;
		return result;
	}

	@Override
	public Expression state() {
		return state;
	}

	@Override
	public Expression expression() {
		return expression;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		if (expression instanceof ValueAtExpression) {
			ValueAtExpression that = (ValueAtExpression) expression;

			return state.equals(that.state()) && pid.equals(that.pid())
					&& this.expression.equals(that.expression());
		}
		return false;
	}

	@Override
	public Expression pid() {
		return this.pid;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("$value_at (");

		result.append(state);
		result.append(", ");
		result.append(pid);
		result.append(", ");
		result.append(expression);
		result.append(")");
		return result.toString();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		((CommonExpression) expression).addFreeVariables(result);
		((CommonExpression) pid).addFreeVariables(result);
		((CommonExpression) state).addFreeVariables(result);
	}

}
