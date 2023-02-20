package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.CharLiteralExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class CommonCharLiteralExpression extends CommonExpression
		implements
			CharLiteralExpression {

	private char value;

	/**
	 * Create a new char literal expression.
	 * 
	 * @param source
	 */
	public CommonCharLiteralExpression(CIVLSource source, CIVLType type,
			char value) {
		super(source, null, null, type);
		this.value = value;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.CHAR_LITERAL;
	}

	@Override
	public char value() {
		return this.value;
	}

	@Override
	public void setValue(char value) {
		this.value = value;
	}

	@Override
	public String toString() {
		switch (value) {
			case 0 :
				return "''";
			case '\u000C' :
				return "'\\f'";
			case '\u0007' :
				return "'\\a'";
			case '\b' :
				return "'\\b'";
			case '\n' :
				return "'\\n'";
			case '\t' :
				return "'\\t'";
			case '\r' :
				return "'\\r'";
			case ' ' :
				return "' '";
		}
		return "'" + Character.toString(value) + "'";
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
	public LiteralKind literalKind() {
		return LiteralKind.CHAR;
	}

	@Override
	public void calculateConstantValueWork(SymbolicUniverse universe) {
		this.constantValue = universe.character(this.value);
	}

	@Override
	public void setLiteralConstantValue(SymbolicExpression value) {
		this.constantValue = value;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		CharLiteralExpression that = (CharLiteralExpression) expression;

		return this.value == that.value();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
