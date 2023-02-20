/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.SelfExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

/**
 * Self expression. Returns a reference to the process in which the expression
 * is evaluated.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class CommonSelfExpression extends CommonExpression implements
		SelfExpression {

	/**
	 * Self expression. Returns a reference to the process in which the
	 * expression is evaluated.
	 */
	public CommonSelfExpression(CIVLSource source, CIVLType type) {
		super(source, null, null, type);
	}

	@Override
	public String toString() {
		return "$self";
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.SELF;
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
		return true;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
