package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.SizeofExpression;
import dev.civl.mc.model.IF.expression.VariableExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonSizeofExpression extends CommonExpression
		implements
			SizeofExpression {

	private Expression argument;

	public CommonSizeofExpression(CIVLSource source, CIVLType type,
			Expression argument) {
		super(source, argument.expressionScope(), argument.lowestScope(), type);
		this.argument = argument;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.SIZEOF_EXPRESSION;
	}

	@Override
	public Expression getArgument() {
		return argument;
	}

	@Override
	public String toString() {
		return "sizeof(" + argument + ")";
	}

	@Override
	public void calculateDerefs() {
		this.argument.calculateDerefs();
		this.hasDerefs = this.argument.hasDerefs();
	}

	@Override
	public void purelyLocalAnalysisOfVariables(Scope funcScope) {
		this.argument.purelyLocalAnalysisOfVariables(funcScope);
	}

	@Override
	public void purelyLocalAnalysis() {
		if (this.hasDerefs) {
			this.purelyLocal = false;
			return;
		}

		this.argument.purelyLocalAnalysis();
		this.purelyLocal = this.argument.isPurelyLocal();
	}

	@Override
	public void replaceWith(ConditionalExpression oldExpression,
			VariableExpression newExpression) {
		if (argument == oldExpression) {
			argument = newExpression;
			return;
		}
		argument.replaceWith(oldExpression, newExpression);
	}

	@Override
	public Expression replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		Expression newArgument = argument.replaceWith(oldExpression,
				newExpression);
		CommonSizeofExpression result = null;

		if (newArgument != null) {
			result = new CommonSizeofExpression(this.getSource(),
					this.expressionType, newArgument);
		}
		return result;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = argument.variableAddressedOf(scope);

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = argument.variableAddressedOf();

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		return this.argument
				.equals(((SizeofExpression) expression).getArgument());
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		((CommonExpression) argument).addFreeVariables(result);
	}

}
