package dev.civl.mc.model.common.statement;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.VariableExpression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.CivlParForSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.type.CIVLCompleteDomainType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;

public class CommonCivlParForSpawnStatement extends CommonStatement
		implements
			CivlParForSpawnStatement {

	private Expression domain;

	private VariableExpression domSizeVar;

	private VariableExpression parProcsVar;

	private CIVLFunction parProcFunction;

	public CommonCivlParForSpawnStatement(CIVLSource source, Location start,
			Expression guard, Expression domain, VariableExpression domSize,
			VariableExpression parProcsVar, CIVLFunction parProcFunc) {
		super(source, domain.expressionScope(), domain.lowestScope(), start,
				guard);
		this.domain = domain;
		this.domSizeVar = domSize;
		this.parProcsVar = parProcsVar;
		this.parProcFunction = parProcFunc;
	}

	@Override
	public Statement replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		return domain.variableAddressedOf(scope);
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		return domain.variableAddressedOf();
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.CIVL_PAR_FOR_ENTER;
	}

	@Override
	public CIVLFunction parProcFunction() {
		return this.parProcFunction;
	}

	@Override
	public Expression domain() {
		return this.domain;
	}

	@Override
	public VariableExpression domSizeVar() {
		return this.domSizeVar;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		string.append("CIVL_PAR_FOR_ENTER: ");
		string.append("$spawn ");
		string.append(this.parProcFunction.name().name());
		string.append("() : ");
		string.append(this.domain);
		return string.toString();
	}

	@Override
	public int dimension() {
		return ((CIVLCompleteDomainType) this.domain.getExpressionType())
				.getDimension();
	}

	@Override
	public VariableExpression parProcsVar() {
		return this.parProcsVar;
	}

	@Override
	protected void calculateConstantValueWork(SymbolicUniverse universe) {
		this.domain.calculateConstantValue(universe);
	}

	@Override
	public void setParProcFunction(CIVLFunction function) {
		this.parProcFunction = function;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			if (obj instanceof CommonCivlParForSpawnStatement) {
				CommonCivlParForSpawnStatement other = (CommonCivlParForSpawnStatement) obj;

				if (other.domain.equals(domain))
					if (other.domSizeVar.equals(domSizeVar))
						if (other.parProcsVar.equals(parProcsVar))
							if (other.parProcFunction == parProcFunction)
								return true;

			}
		}
		return false;
	}

	@Override
	public Set<Variable> freeVariables() {
		Set<Variable> result = super.freeVariables();

		result.addAll(domain.freeVariables());
		result.addAll(domSizeVar.freeVariables());
		result.addAll(parProcsVar.freeVariables());
		return result;
	}
}
