package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.BoundVariableExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonBoundVariableExpression extends CommonExpression
		implements
			BoundVariableExpression {

	/* ************************** Private Fields *************************** */

	/**
	 * The name of the variable.
	 */
	private Identifier name;

	/* **************************** Constructor **************************** */

	public CommonBoundVariableExpression(CIVLSource source, CIVLType type,
			Identifier name) {
		super(source, null, null, type);
		this.name = name;
	}

	/* ********************** Methods from Expression ********************** */

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.BOUND_VARIABLE;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}

	/* *************** Methods from BoundVariableExpression **************** */

	@Override
	public Identifier name() {
		return name;
	}

	/* ************************ Methods from Object ************************ */

	@Override
	public String toString() {
		return name.name();
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
		return this.name.name()
				.equals(((BoundVariableExpression) expression).name().name());
	}
}
