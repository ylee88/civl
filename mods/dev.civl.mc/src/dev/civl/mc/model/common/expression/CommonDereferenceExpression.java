package dev.civl.mc.model.common.expression;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.BinaryExpression;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.DereferenceExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.model.IF.expression.VariableExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonDereferenceExpression extends CommonExpression
		implements
			DereferenceExpression {

	private Expression pointer;

	public CommonDereferenceExpression(CIVLSource source, Scope scope,
			CIVLType type, Expression pointer) {
		super(source, scope, pointer.lowestScope(), type);
		this.pointer = pointer;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.DEREFERENCE;
	}

	@Override
	public Expression pointer() {
		return pointer;
	}

	@Override
	public String toString() {
		return "*(" + pointer + ")";
	}

	@Override
	public void calculateDerefs() {
		this.hasDerefs = true;
	}

	@Override
	public void setPurelyLocal(boolean pl) {
		this.purelyLocal = pl;
	}

	@Override
	public void purelyLocalAnalysisOfVariables(Scope funcScope) {
		this.pointer.purelyLocalAnalysisOfVariables(funcScope);
	}

	@Override
	public void purelyLocalAnalysis() {
		this.purelyLocal = false;
	}

	@Override
	public void replaceWith(ConditionalExpression oldExpression,
			VariableExpression newExpression) {
		if (pointer == oldExpression) {
			pointer = newExpression;
			return;
		}
		pointer.replaceWith(oldExpression, newExpression);
	}

	@Override
	public Expression replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		Expression newPointer = pointer.replaceWith(oldExpression,
				newExpression);
		CommonDereferenceExpression result = null;

		if (newPointer != null) {
			result = new CommonDereferenceExpression(this.getSource(),
					this.expressionScope(), this.expressionType, newPointer);
		}
		return result;
	}

	@Override
	public Variable variableWritten(Scope scope) {
		if (pointer instanceof LHSExpression) {
			return ((LHSExpression) pointer).variableWritten(scope);
		}
		if (pointer instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) pointer;

			if (binaryExpression
					.operator() == BinaryExpression.BINARY_OPERATOR.POINTER_ADD) {
				Expression pointerVariable;

				if (binaryExpression.left().getExpressionType()
						.isPointerType()) {
					pointerVariable = binaryExpression.left();
				} else {
					pointerVariable = binaryExpression.right();
				}
				if (pointerVariable instanceof LHSExpression) {
					return ((LHSExpression) pointerVariable)
							.variableWritten(scope);
				}
			}
		}
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = pointer.variableAddressedOf(scope);

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	public Variable variableWritten() {
		if (pointer instanceof LHSExpression) {
			return ((LHSExpression) pointer).variableWritten();
		}
		if (pointer instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) pointer;

			if (binaryExpression
					.operator() == BinaryExpression.BINARY_OPERATOR.POINTER_ADD) {
				Expression pointerVariable;

				if (binaryExpression.left().getExpressionType()
						.isPointerType()) {
					pointerVariable = binaryExpression.left();
				} else {
					pointerVariable = binaryExpression.right();
				}
				if (pointerVariable instanceof LHSExpression) {
					return ((LHSExpression) pointerVariable).variableWritten();
				}
			}
		}
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		Set<Variable> variableSet = new HashSet<>();
		Set<Variable> operandResult = pointer.variableAddressedOf();

		if (operandResult != null)
			variableSet.addAll(operandResult);
		return variableSet;
	}

	@Override
	public LHSExpressionKind lhsExpressionKind() {
		return LHSExpressionKind.DEREFERENCE;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		DereferenceExpression that = (DereferenceExpression) expression;

		return this.pointer.equals(that.pointer());
	}

	@Override
	public boolean containsHere() {
		return this.pointer.containsHere();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		((CommonExpression) pointer).addFreeVariables(result);
	}
}
