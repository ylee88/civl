package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.StatenullExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class CommonStatenullExpression extends CommonExpression
		implements
			StatenullExpression {

	public CommonStatenullExpression(CIVLSource source, CIVLType type,
			SymbolicExpression constantValue) {
		super(source, null, null, type);
		this.constantValue = constantValue;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.STATE_NULL;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return new HashSet<>();
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return new HashSet<>();
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		return expression instanceof StatenullExpression;
	}

	@Override
	public String toString() {
		return "$state_null";
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
