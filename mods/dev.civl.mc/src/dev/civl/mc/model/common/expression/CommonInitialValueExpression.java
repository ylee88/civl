package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.InitialValueExpression;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonInitialValueExpression extends CommonExpression implements
		InitialValueExpression {

	private Variable variable;

	public CommonInitialValueExpression(CIVLSource source, Variable variable) {
		super(source, variable.scope(), variable.scope(), variable.type());
		this.variable = variable;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.INITIAL_VALUE;
	}

	@Override
	public Variable variable() {
		return variable;
	}

	@Override
	public String toString() {
		return "InitialValue(" + variable.name() + ")";
	}

	@Override
	public void purelyLocalAnalysis() {
		this.purelyLocal = true;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		result.add(variable);
	}
}
