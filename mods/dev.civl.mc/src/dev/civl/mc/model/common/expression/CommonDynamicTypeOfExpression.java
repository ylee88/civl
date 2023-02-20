/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.DynamicTypeOfExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

/**
 * A cast of an expression to a different type.
 * 
 * @author zirkel
 * 
 */
public class CommonDynamicTypeOfExpression extends CommonExpression
		implements
			DynamicTypeOfExpression {

	private CIVLType type;

	public CommonDynamicTypeOfExpression(CIVLSource source, CIVLType myType,
			CIVLType type) {
		super(source, null, null, myType);
		this.type = type;
	}

	@Override
	public CIVLType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "DynamicTypeOf(" + type + ") ";
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.DYNAMIC_TYPE_OF;
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
		DynamicTypeOfExpression that = (DynamicTypeOfExpression) expression;

		return this.type.equals(that.getType());
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		result.addAll(type.freeVariables());
	}

}
