package dev.civl.mc.semantics.IF;

import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * This represents a CIVL transition, which is deterministic and enabled at a
 * certain state. It is composed of a path condition, an atomic statement, a
 * PID, and a process identifier.
 * 
 * @author Manchun Zheng
 * 
 */
public interface Transition2 {

	public enum TransitionKind {
		NORMAL, NOOP
	}

	/**
	 * 
	 * @return a boolean-value clause. Execution of this transition will start
	 *         from a new state, which is obtained via conjunction of this
	 *         clause and the path condition of the source state.
	 */
	BooleanExpression clause();

	/**
	 * The statement that this transition is to execute, which should be atomic,
	 * deterministic, and enabled in the context of the path condition.
	 * 
	 * @return The statement that this transition is to execute
	 */
	Statement statement();

	/**
	 * The PID of the process that this transition belongs to.
	 * 
	 * @return The PID of the process that this transition belongs to.
	 */
	int pid();

	/**
	 * returns the kind of this transition.
	 * 
	 * @return
	 */
	TransitionKind transitionKind();

	/**
	 * Shall the state be simplified after the transition is done?
	 * 
	 * @return
	 */
	boolean simpifyState();
}
