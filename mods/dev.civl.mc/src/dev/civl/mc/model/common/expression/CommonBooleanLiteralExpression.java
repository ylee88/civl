/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.BooleanLiteralExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * A literal boolean value.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class CommonBooleanLiteralExpression extends CommonExpression
		implements
			BooleanLiteralExpression {

	/* ************************** Private Fields *************************** */

	/**
	 * The boolean value of this literal. Immutable.
	 */
	private boolean value;

	/* **************************** Constructor **************************** */

	/**
	 * Creates a new boolean literal with the given value.
	 * 
	 * @param source
	 *            The source information corresponding to this expression.
	 * @param type
	 *            The type of this expression.
	 * @param value
	 *            The value of this boolean literal.
	 */
	public CommonBooleanLiteralExpression(CIVLSource source, CIVLType type,
			boolean value) {
		// the expression scope is null because no variable is accessed by a
		// boolean literal.
		super(source, null, null, type);
		this.value = value;
	}

	/* *************** Methods from BooleanLiteralExpression *************** */

	/**
	 * @return The value of this boolean literal.
	 */
	@Override
	public boolean value() {
		return value;
	}

	/* ******************* Methods from LiteralExpression ****************** */

	@Override
	public LiteralKind literalKind() {
		return LiteralKind.BOOLEAN;
	}

	/* *************** Methods from Expression *************** */

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.BOOLEAN_LITERAL;
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
	protected void addFreeVariables(Set<Variable> result) {
	}

	/* ************************ Methods from Object ************************ */

	@Override
	public String toString() {
		if (value) {
			return "true";
		}
		return "false";
	}

	@Override
	public void calculateConstantValueWork(SymbolicUniverse universe) {
		this.constantValue = value
				? universe.trueExpression()
				: universe.falseExpression();
	}

	@Override
	public void setLiteralConstantValue(SymbolicExpression value) {
		this.constantValue = value;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		return this.value == ((BooleanLiteralExpression) expression).value();
	}
}
