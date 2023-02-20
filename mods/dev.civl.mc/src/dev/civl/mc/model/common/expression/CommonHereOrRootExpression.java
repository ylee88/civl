package dev.civl.mc.model.common.expression;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.HereOrRootExpression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class CommonHereOrRootExpression extends CommonExpression
		implements
			HereOrRootExpression {

	private boolean isRoot;

	public CommonHereOrRootExpression(CIVLSource source, CIVLType type,
			boolean isRoot, SymbolicExpression constantValue) {
		super(source, null, null, type);
		this.isRoot = isRoot;
		this.constantValue = constantValue;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.HERE_OR_ROOT;
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
	public boolean isHere() {
		return !this.isRoot;
	}

	@Override
	public boolean isRoot() {
		return this.isRoot;
	}

	@Override
	public String toString() {
		if (this.isRoot)
			return "$root";
		return "$here";
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		HereOrRootExpression that = (HereOrRootExpression) expression;

		return this.isRoot == that.isRoot();
	}

	@Override
	public boolean containsHere() {
		return this.isHere();
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
	}
}
