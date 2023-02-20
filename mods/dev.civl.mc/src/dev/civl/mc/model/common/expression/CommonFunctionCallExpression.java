package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.FunctionCallExpression;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonFunctionCallExpression extends CommonExpression
		implements
			FunctionCallExpression {

	CallOrSpawnStatement callStatement;

	public CommonFunctionCallExpression(CIVLSource source,
			CallOrSpawnStatement callStatement) {
		super(source, callStatement.statementScope(),
				callStatement.lowestScope(), null);
		this.callStatement = callStatement;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.FUNC_CALL;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return callStatement.variableAddressedOf(scope);
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return callStatement.variableAddressedOf();
	}

	@Override
	public CallOrSpawnStatement callStatement() {
		return this.callStatement;
	}

	@Override
	public String toString() {
		return this.callStatement.toString();
	}

	@Override
	public void setExpressionType(CIVLType returnType) {
		this.expressionType = returnType;
	}

	@Override
	public CIVLType getExpressionType() {
		return this.callStatement.function().returnType();
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsHere() {
		return callStatement.containsHere();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		for (Expression arg : callStatement.arguments())
			((CommonExpression) arg).addFreeVariables(result);
		((CommonExpression) callStatement.functionExpression())
				.addFreeVariables(result);
	}
}
