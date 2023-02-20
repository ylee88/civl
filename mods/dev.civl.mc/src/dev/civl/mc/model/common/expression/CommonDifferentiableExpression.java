package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.AbstractFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.DifferentiableExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonDifferentiableExpression extends CommonExpression
		implements
			DifferentiableExpression {

	private AbstractFunction function;

	private int degree;

	private Expression[] lowerBounds;

	private Expression[] upperBounds;

	public CommonDifferentiableExpression(CIVLSource source, Scope hscope,
			Scope lscope, CIVLType booleanType, AbstractFunction function,
			int degree, Expression[] lowerBounds, Expression[] upperBounds) {
		super(source, hscope, lscope, booleanType);
		this.function = function;
		this.degree = degree;
		this.lowerBounds = lowerBounds;
		this.upperBounds = upperBounds;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.DIFFERENTIABLE;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		// TODO : fix me
		// the union of the sets from lowerBounds and upperBounds?
		return new HashSet<>();
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public int degree() {
		return degree;
	}

	@Override
	public Expression[] lowerBounds() {
		return lowerBounds;
	}

	@Override
	public Expression[] upperBounds() {
		return upperBounds;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		// TODO check the fields...
		return expression == this;
	}

	@Override
	public AbstractFunction function() {
		return function;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		for (Expression expr : lowerBounds)
			((CommonExpression) expr).addFreeVariables(result);
		for (Expression expr : upperBounds)
			((CommonExpression) expr).addFreeVariables(result);
	}

}
