package dev.civl.mc.model.common.statement;

import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.ParallelAssignStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.SymbolicUniverse;

public class CommonParallelAssignStatement extends CommonStatement
		implements
			ParallelAssignStatement {

	private List<Pair<LHSExpression, Expression>> assignPairs;

	public CommonParallelAssignStatement(CIVLSource source, Location sourceLoc,
			Expression guard,
			List<Pair<LHSExpression, Expression>> assignPairs) {
		super(source, guard.expressionScope(), guard.lowestScope(), sourceLoc,
				guard);
		this.assignPairs = assignPairs;
	}

	@Override
	public Statement replaceWith(ConditionalExpression oldExpression,
			Expression newExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.PARALLEL_ASSIGN;
	}

	@Override
	public List<Pair<LHSExpression, Expression>> assignments() {
		return this.assignPairs;
	}

	@Override
	protected void calculateConstantValueWork(SymbolicUniverse universe) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			if (obj instanceof CommonParallelAssignStatement) {
				CommonParallelAssignStatement other = (CommonParallelAssignStatement) obj;

				return other.assignPairs.equals(assignPairs);
			}
		return false;
	}

	@Override
	public Set<Variable> freeVariables() {
		Set<Variable> result = super.freeVariables();

		for (Pair<LHSExpression, Expression> pair : assignPairs) {
			result.addAll(pair.left.freeVariables());
			result.addAll(pair.right.freeVariables());
		}
		return result;
	}

}
