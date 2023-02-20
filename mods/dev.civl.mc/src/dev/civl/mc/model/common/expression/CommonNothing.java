package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.Nothing;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonNothing extends CommonExpression implements Nothing {

	public CommonNothing(CIVLSource source) {
		super(source, null, null, null);
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.NOTHING;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return null;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		if (expression instanceof Nothing)
			return true;
		return false;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}

}
