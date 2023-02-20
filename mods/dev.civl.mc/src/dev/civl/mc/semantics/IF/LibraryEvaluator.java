package dev.civl.mc.semantics.IF;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;

/**
 * A Library Evaluator provides a method to "evaluate" the guard of each system
 * function call. A new library is implemented in the package named as
 * "dev.civl.mc.library." ( {@link CommonLibraryLoader#CLASS_PREFIX})
 * + library name. And the class name of the enabler is: "Lib" + library name +
 * "Evaluator". For example, the stdio library enabler is implemented as the
 * class dev.civl.mc.library.stdio.LibstdioEnabler.
 * 
 * @author zmanchun
 * 
 */
public interface LibraryEvaluator {

	/**
	 * Evaluates the guard of a system function. This is an extra guard related
	 * to the particular system function, and needs to be checked in addition to
	 * the "regular" guard in the transition system.
	 * 
	 * @param source
	 *            The source code information for error report.
	 * @param state
	 *            The state where the evaluation happens.
	 * @param pid
	 *            The PID of the process that triggers this evaluation.
	 * @param function
	 *            The name of the function.
	 * @param arguments
	 *            The arguments of the function.
	 * @return The result of the guard with a possibly new state caused by side
	 *         effects.
	 * @throws UnsatisfiablePathConditionException
	 */
	Evaluation evaluateGuard(CIVLSource source, State state, int pid,
			String function, Expression[] arguments)
			throws UnsatisfiablePathConditionException;

	/**
	 * @param primaryEvaluator
	 *         the primary {@link Evaluator} that will be used by this library
	 *         evaluator after setting
	 */
	void setPrimaryEvaluator(Evaluator primaryEvaluator);

}
