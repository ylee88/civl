package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.FunctionIdentifierExpression;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.model.common.type.CommonPointerType;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonFunctionIdentifierExpression extends CommonExpression
		implements
			FunctionIdentifierExpression {

	private CIVLFunction function;

	public CommonFunctionIdentifierExpression(CIVLSource source,
			CIVLFunction function, SymbolicType functionPointerType) {
		super(source, function.containingScope(), function.containingScope(),
				new CommonPointerType(function.functionType(),
						functionPointerType));
		this.function = function;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.FUNCTION_IDENTIFIER;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		// return function.variableAddressedOf(scope);
		return new HashSet<>();
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return new HashSet<>();
		// return function.variableAddressedOf();
	}

	@Override
	public Scope scope() {
		return this.expressionScope();
	}

	@Override
	public CIVLFunction function() {
		return this.function;
	}

	@Override
	public String toString() {
		return function.name().name();
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		FunctionIdentifierExpression that = (FunctionIdentifierExpression) expression;

		return this.function.name().name()
				.equals(that.function().name().name());
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
