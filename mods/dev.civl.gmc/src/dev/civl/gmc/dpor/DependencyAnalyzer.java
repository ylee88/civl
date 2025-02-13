package dev.civl.gmc.dpor;

public interface DependencyAnalyzer<STATE, TRANSITION> {
	
	/**
	 * Number of transitions executed from a "cross state" (see
	 * {@link ImmutableStateFactory#crossState}) for the purposes of determining
	 * dependence.
	 */
	public int numCrossTransitions();
	
	/**
	 * Number of trace steps executed from a "cross state" (see
	 * {@link ImmutableStateFactory#crossState}) for the purposes of determining
	 * dependence.
	 */
	public int numCrossTraceSteps();
	
	/**
	 * Checks whether the transition at "stackIndex" in the stack is dependent
	 * with process pid
	 * 
	 * @param stack
	 *            The current search stack
	 * @param stackIndex
	 *            The index on the stack of the transition which we are checking
	 *            dependency with
	 * @param pid
	 *            The process id of the process we want to check dependency
	 *            against
	 * @return
	 */
	public boolean checkDependent(DporSearchStack<STATE, TRANSITION> stack, int stackIndex, int pid);
}
