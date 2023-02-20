package dev.civl.mc.semantics.IF;

import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;

/**
 * A Library Executor provides the semantics for system functions defined in a
 * library. It provides a method to "execute" each system library function.A new
 * library is implemented in the package named as
 * "dev.civl.mc.library." ( {@link CommonLibraryLoader#CLASS_PREFIX})
 * + library name. And the class name of the executor is: "Lib" + library name +
 * "Enabler". For example, the stdio library executor is implemented as the
 * class dev.civl.mc.library.stdio.LibstdioExecutor.
 * 
 */
public interface LibraryExecutor {

	/**
	 * <p>
	 * Executes the given call statement for a certain process at the given
	 * state.
	 * </p>
	 * <p>
	 * Precondition: the given call statement is enabled for the process with
	 * the given pid, and the function of the call statement is provided by this
	 * library.
	 * </p>
	 * 
	 * @param state
	 *            The state where the call statement is to be executed.
	 * @param pid
	 *            The PID of the process that the statement belongs to.
	 * @param statement
	 *            The call statement to be executed.
	 * @param functionName
	 *            The name of the system function that this call is going to
	 *            executed. Note: we need this when the function of the call
	 *            statement is a function pointer.
	 * @return The resulting state after executing the call statement, plus the
	 *         return value of the function call, which is NULL if the function
	 *         returns void.
	 * @throws UnsatisfiablePathConditionException
	 */
	Evaluation execute(State state, int pid, CallOrSpawnStatement statement,
			String functionName) throws UnsatisfiablePathConditionException;

	/**
	 * @param evaluator
	 *         the  {@link Evaluator} that will be used by this library
	 *         executor after setting
	 */
	void setEvaluator(Evaluator evaluator);
}
