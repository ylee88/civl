/**
 * 
 */
package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.UndefinedProcessExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * Undefined process expression, i.e., a process expression with id -1. Used
 * when translating atomic statements.
 * 
 * @author Manchun Zheng (zheng)
 * 
 */
public class CommonUndefinedProcessExpression extends CommonExpression
		implements
			UndefinedProcessExpression {

	/**
	 * Self expression. Returns a reference to the process in which the
	 * expression is evaluated.
	 */
	public CommonUndefinedProcessExpression(CIVLSource source, CIVLType type,
			SymbolicExpression constantValue) {
		super(source, null, null, type);
		this.constantValue = constantValue;
	}

	@Override
	public String toString() {
		return "process<-1>";
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.UNDEFINED_PROC;
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
	protected boolean expressionEquals(Expression expression) {
		return true;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
