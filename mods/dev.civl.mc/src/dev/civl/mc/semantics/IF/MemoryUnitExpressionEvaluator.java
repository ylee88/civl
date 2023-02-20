package dev.civl.mc.semantics.IF;

import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.MemoryUnitExpression;
import dev.civl.mc.state.IF.MemoryUnitSet;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public interface MemoryUnitExpressionEvaluator {
	/**
	 * evaluates the static impact memory unit expression which is the result of
	 * static analysis
	 * 
	 * @param state
	 *            the current state
	 * @param pid
	 *            the PID of the process that triggers this evaluation
	 * @param memUnit
	 *            the impact memory unit expression
	 * @param muSet
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	MemoryUnitSet evaluates(State state, int pid, MemoryUnitExpression memUnit,
			MemoryUnitSet muSet) throws UnsatisfiablePathConditionException;

	/**
	 * Evaluates the memory unit represented by an expression in a contract. A
	 * memory unit expression should be side-effect free.
	 * 
	 * @param state
	 * @param pid
	 * @param muExpr
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	MemoryUnitSet evaluateMemoryUnit(State state,
			Pair<Scope, SymbolicExpression[]> parameterScope, int pid,
			Expression muExpr) throws UnsatisfiablePathConditionException;
}
