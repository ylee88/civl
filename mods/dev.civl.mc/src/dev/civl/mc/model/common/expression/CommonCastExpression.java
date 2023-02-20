/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.CastExpression;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.VariableExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

/**
 * A cast of an expression to a different type.
 * 
 * @author zirkel
 * 
 */
public class CommonCastExpression extends CommonExpression
		implements
			CastExpression {

	private Expression expression;

	/**
	 * A cast of an expression to a different type.
	 * 
	 * @param type
	 *            The type to which the expression is cast.
	 * @param expression
	 *            The expression being cast to a new type.
	 */
	public CommonCastExpression(CIVLSource source, Scope scope, CIVLType type,
			Expression expression) {
		super(source, scope, expression.lowestScope(), type);
		this.expression = expression;
	}

	@Override
	public Expression getExpression() {
		return expression;
	}

	@Override
	public CIVLType getCastType() {
		return this.expressionType;
	}

	@Override
	public String toString() {
		return "(" + expressionType + ") " + expression;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.CAST;
	}

	@Override
	public void calculateDerefs() {
		this.expression.calculateDerefs();
		this.hasDerefs = this.expression.hasDerefs();
	}

	@Override
	public void purelyLocalAnalysisOfVariables(Scope funcScope) {
		this.expression.purelyLocalAnalysisOfVariables(funcScope);
	}

	@Override
	public void purelyLocalAnalysis() {
		if (this.hasDerefs) {
			this.purelyLocal = false;
			return;
		}
		this.expression.purelyLocalAnalysis();
		this.purelyLocal = this.expression.isPurelyLocal();
	}

	@Override
	public void replaceWith(ConditionalExpression oldExpression,
			VariableExpression newExpression) {
		if (expression == oldExpression) {
			expression = newExpression;
			return;
		}
		expression.replaceWith(oldExpression, newExpression);
	}

	@Override
	public Expression replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		Expression newOperand = expression.replaceWith(oldExpression,
				newExpression);
		CommonCastExpression result = null;

		if (newOperand != null) {
			result = new CommonCastExpression(this.getSource(),
					this.expressionScope(), this.expressionType, newOperand);
		}
		return result;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = expression.variableAddressedOf(scope);

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = expression.variableAddressedOf();

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		CastExpression that = (CastExpression) expression;

		return this.getCastType().equals(that.getCastType())
				&& this.expression.equals(that.getExpression());
	}

	@Override
	public boolean containsHere() {
		return expression.containsHere();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		((CommonExpression) expression).addFreeVariables(result);
	}
}
