/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.math.BigInteger;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.IntegerLiteralExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * An integer literal.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class CommonIntegerLiteralExpression extends CommonExpression implements
		IntegerLiteralExpression {

	private BigInteger value;

	/**
	 * An integer literal.
	 * 
	 * @param value
	 *            The (arbitrary precision) value of the integer.
	 */
	public CommonIntegerLiteralExpression(CIVLSource source, CIVLType type,
			BigInteger value) {
		super(source, null, null, type);
		this.value = value;
	}

	/**
	 * @return The (arbitrary precision) value of the integer.
	 */
	public BigInteger value() {
		return value;
	}

	/**
	 * @param value
	 *            The (arbitrary precision) value of the integer.
	 */
	public void setValue(BigInteger value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.INTEGER_LITERAL;
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
		return LiteralKind.INTEGER;
	}

	@Override
	public void calculateConstantValueWork(SymbolicUniverse universe) {
		this.constantValue = universe.integer(value);
	}

	@Override
	public void setLiteralConstantValue(SymbolicExpression value) {
		this.constantValue = value;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		IntegerLiteralExpression that = (IntegerLiteralExpression) expression;

		return this.value.equals(that.value());
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {		
	}
}
