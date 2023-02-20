/**
 * 
 */
package dev.civl.mc.model.common.statement;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.NoopStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;

/**
 * A noop statement.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class CommonNoopStatement extends CommonStatement
		implements
			NoopStatement {

	protected NoopKind noopKind;

	protected Expression expression;

	/**
	 * true iff this is a temporary noop inserted by the model translator
	 */
	private boolean isTemporary = false;

	private boolean removable = false;

	/**
	 * true iff this associates to a variable declaration
	 */
	private boolean isVariableDeclaration = false;

	/**
	 * A noop statement.
	 * 
	 * @param source
	 *            The source location for this noop.
	 */
	public CommonNoopStatement(CIVLSource civlSource, Location source,
			Expression guard, Expression expression) {
		super(civlSource, null, null, source, guard);
		noopKind = NoopKind.NONE;
		this.expression = expression;
	}

	/**
	 * A noop statement.
	 * 
	 * @param source
	 *            The source location for this noop.
	 */
	public CommonNoopStatement(CIVLSource civlSource, Location source,
			Expression guard, boolean isTemporary) {
		super(civlSource, null, null, source, guard);
		noopKind = NoopKind.NONE;
		this.isTemporary = isTemporary;
	}

	/**
	 * A noop statement.
	 * 
	 * @param source
	 *            The source location for this noop.
	 */
	public CommonNoopStatement(CIVLSource civlSource, Location source,
			Expression guard, boolean isTemporary,
			boolean isVariableDeclaration) {
		super(civlSource, null, null, source, guard);
		noopKind = NoopKind.NONE;
		this.isTemporary = isTemporary;
		this.isVariableDeclaration = isVariableDeclaration;
	}

	public CommonNoopStatement() {
		super();
	}

	@Override
	public String toString() {
		return "NO_OP";
	}

	@Override
	public Statement replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		Expression newGuard = guardReplaceWith(oldExpression, newExpression);
		CommonNoopStatement newStatement = null;

		if (newGuard != null) {
			newStatement = new CommonNoopStatement(this.getSource(),
					this.source(), newGuard, expression);

		}
		Expression newExpressionArg = expression.replaceWith(oldExpression,
				newExpression);

		if (newExpressionArg != null)
			newStatement = new CommonNoopStatement(this.getSource(),
					this.source(), this.guard(), newExpressionArg);
		return newStatement;
	}

	@Override
	public NoopKind noopKind() {
		return this.noopKind;
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
	public StatementKind statementKind() {
		return StatementKind.NOOP;
	}

	@Override
	protected void calculateConstantValueWork(SymbolicUniverse universe) {
	}

	@Override
	public Expression expression() {
		return this.expression;
	}

	@Override
	public void setRemovable() {
		removable = true;
	}

	@Override
	public boolean isRemovable() {
		return removable;
	}

	@Override
	public boolean isTemporary() {
		return this.isTemporary;
	}

	@Override
	public boolean isVariableDeclaration() {
		return this.isVariableDeclaration;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			CommonNoopStatement other = (CommonNoopStatement) obj;

			if (other.noopKind == noopKind) {
				return this.nullableObjectEquals(expression, other.expression);
			}
		}
		return false;
	}

	@Override
	public Set<Variable> freeVariables() {
		Set<Variable> result = super.freeVariables();

		if (expression != null)
			result.addAll(expression.freeVariables());
		return result;
	}
}
