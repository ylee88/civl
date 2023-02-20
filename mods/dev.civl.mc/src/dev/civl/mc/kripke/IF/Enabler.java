package dev.civl.mc.kripke.IF;

import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.State;
import dev.civl.gmc.seq.EnablerIF;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * Enabler extends {@link EnablerIF} for CIVL models.
 * 
 * @author Manchun Zheng
 * 
 */
public interface Enabler extends EnablerIF<State, Transition> {

	/**
	 * Computes the guard of a statement. Since we have SystemGuardExpression
	 * and WaitGuardExpression, we don't need to compute the guard for system
	 * function calls and wait statements explicitly, which are now handled by
	 * the evaluator.
	 * 
	 * @param statement
	 *            The statement whose guard is to computed.
	 * @param pid
	 *            The ID of the process that the statement belongs to.
	 * @param state
	 *            The current state that the computation happens.
	 * @return The value of the guard of the given statement.
	 */
	BooleanExpression getGuard(Statement statement, int pid, State state);
}
