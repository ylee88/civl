package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.StructOrUnionLiteralExpression;
import dev.civl.mc.model.IF.type.CIVLStructOrUnionType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class CommonStructOrUnionLiteralExpression extends CommonExpression
		implements
			StructOrUnionLiteralExpression {

	public CommonStructOrUnionLiteralExpression(CIVLSource source, Scope hscope,
			Scope lscope, CIVLType type, SymbolicExpression constantValue) {
		super(source, hscope, lscope, type);
		this.constantValue = constantValue;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.STRUCT_OR_UNION_LITERAL;
	}

	@Override
	public CIVLStructOrUnionType structOrUnionType() {
		assert this.expressionType instanceof CIVLStructOrUnionType;
		return (CIVLStructOrUnionType) this.expressionType;
	}

	@Override
	public String toString() {
		return "(" + getExpressionType() + ")" + "{" + constantValue + "}";
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return new HashSet<>();
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return new HashSet<>();
	}

	@Override
	public boolean isStruct() {
		return this.structOrUnionType().isStructType();
	}

	@Override
	public LiteralKind literalKind() {
		return LiteralKind.STRUCT_OR_UNION;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		StructOrUnionLiteralExpression that = (StructOrUnionLiteralExpression) expression;

		if (that.getExpressionType().equals(this.getExpressionType()))
			return that.constantValue().equals(this.constantValue());
		return false;
	}

	@Override
	public void setLiteralConstantValue(SymbolicExpression value) {
		this.constantValue = value;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
