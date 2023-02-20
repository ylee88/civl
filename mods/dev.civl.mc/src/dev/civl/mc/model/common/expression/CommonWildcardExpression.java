package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.WildcardExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonWildcardExpression extends CommonExpression
		implements
			WildcardExpression {

	public CommonWildcardExpression(CIVLSource source, CIVLType type) {
		super(source, null, null, type);
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.WILDCARD;
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
	public String toString() {
		return "...";
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
