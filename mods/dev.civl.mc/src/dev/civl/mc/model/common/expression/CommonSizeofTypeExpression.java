package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.SizeofTypeExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonSizeofTypeExpression extends CommonExpression
		implements
			SizeofTypeExpression {

	private CIVLType type;

	public CommonSizeofTypeExpression(CIVLSource source, Scope scope,
			CIVLType myType, CIVLType type) {
		super(source, scope, scope, myType);
		this.type = type;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.SIZEOF_TYPE;
	}

	@Override
	public CIVLType getTypeArgument() {
		return type;
	}

	@Override
	public String toString() {
		return "sizeof(" + getTypeArgument() + ")";
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
		result.addAll(type.freeVariables());
	}
}
