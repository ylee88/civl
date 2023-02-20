package dev.civl.mc.model.common.statement;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.statement.WithStatement;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;

public class CommonWithStatement extends CommonStatement
		implements
			WithStatement {

	private Expression colStateExpr;

	private boolean isEnter;

	private CIVLFunction function;

	public CommonWithStatement(CIVLSource source, Location srcLoc,
			Expression guard, LHSExpression colState, boolean isEnter) {
		super(source, colState.expressionScope(), colState.lowestScope(),
				srcLoc, guard);
		this.colStateExpr = colState;
		this.isEnter = isEnter;
	}

	public CommonWithStatement(CIVLSource source, Location srcLoc,
			Expression guard, Expression colState, CIVLFunction function) {
		super(source, colState.expressionScope(), colState.lowestScope(),
				srcLoc, guard);
		this.colStateExpr = colState;
		this.function = function;
	}

	@Override
	public Statement replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return colStateExpr.variableAddressedOf(scope);
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return this.colStateExpr.variableAddressedOf();
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.WITH;
	}

	@Override
	public boolean isEnter() {
		return this.isEnter;
	}

	@Override
	public boolean isExit() {
		return !this.isEnter;
	}

	@Override
	public Expression collateState() {
		return this.colStateExpr;
	}

	@Override
	protected void calculateConstantValueWork(SymbolicUniverse universe) {
		this.colStateExpr.calculateConstantValue(universe);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("$with ");
		// if (this.isEnter)
		// sb.append("enter");
		// else
		// sb.append("exit");
		sb.append(" (");
		sb.append(this.colStateExpr);
		sb.append(") ");
		sb.append(function.name());
		sb.append("()");
		return sb.toString();
	}

	@Override
	public CIVLFunction function() {
		return this.function;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof CommonWithStatement) {
			CommonWithStatement withStatement = (CommonWithStatement) obj;

			if (isEnter == withStatement.isEnter)
				return function == withStatement.function;
		}
		return false;
	}

	@Override
	public Set<Variable> freeVariables() {
		Set<Variable> result = super.freeVariables();

		result.addAll(colStateExpr.freeVariables());
		return result;
	}
}
