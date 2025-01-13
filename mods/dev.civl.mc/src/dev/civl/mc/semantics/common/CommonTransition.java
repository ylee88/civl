/**
 * 
 */
package dev.civl.mc.semantics.common;

import dev.civl.mc.model.IF.location.Location.AtomicKind;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * A CIVL transition involves a single atomic statement in one process. It is to
 * be contrasted with a synchronous transition, which involves two statements
 * executing together in two different processes. It also contains an atomic
 * lock action, which denotes that whether the process is going to grab/release
 * the atomic lock.
 * 
 * @author Manchun Zheng
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class CommonTransition implements Transition {

	/* *************************** Instance Fields ************************* */

	/**
	 * The boolean-value clause which will be conjuncted with the path condition
	 * of the source state in order to get the new path condition after this
	 * transitions is executed.
	 */
	private BooleanExpression clause;

	/**
	 * The PID of the process that this transition belongs to.
	 */
	private int pid;

	/**
	 * The statement that this transition is to execute, which should be atomic,
	 * deterministic, and enabled in the context of the path condition.
	 */
	private Statement statement;

	protected boolean simplifyState;

	/* ***************************** Constructors ************************** */

	/**
	 * <p>
	 * Creates a new instance of a CIVL transition.
	 * </p>
	 * <p>
	 * Precondition: the statement is enabled in the context of the given path
	 * condition. There exists a state in the state space such that the process
	 * PID with the given identifier has the given atomic, deterministic
	 * statement enabled.
	 * </p>
	 * 
	 * @param clause
	 *            The boolean-value clause which will be conjuncted with the
	 *            path condition of the source state to form a new state
	 *            immediately before the execution of this transition starts.
	 * @param pid
	 *            The PID of the process that the transition belongs to.
	 * @param statement
	 *            The statement of the transition.
	 */
	public CommonTransition(BooleanExpression clause, int pid,
			Statement statement, boolean symplifyState) {
		this.clause = clause;
		this.pid = pid;
		this.statement = statement;
		this.simplifyState = symplifyState;
	}

	public CommonTransition(BooleanExpression pathCondition, int pid,
			Statement statement) {
		this(pathCondition, pid, statement, false);
	}

	/* ************************* Methods from Object *********************** */

	@Override
	public String toString() {
		String result = "p" + pid + ": ";

		result += statement.toStepString(AtomicKind.NONE, pid, false);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Transition) {
			Transition that = (Transition) object;

			if (this.pid == that.pid() && this.clause.equals(that.clause())
					&& this.statement.equals(that.statement()))
				return true;
		}
		return false;
	}
	
	// TODO: Fix hashing in Statement so that we can use it here
	@Override
	public int hashCode() {
		return Integer.hashCode(pid) ^ clause.hashCode();
	}

	/* *********************** Methods from Transition ********************* */

	public int pid() {
		return pid;
	}

	public Statement statement() {
		return statement;
	}

	@Override
	public BooleanExpression clause() {
		return this.clause;
	}

	@Override
	public TransitionKind transitionKind() {
		return TransitionKind.NORMAL;
	}

	@Override
	public boolean simpifyState() {
		return simplifyState;
	}

}
