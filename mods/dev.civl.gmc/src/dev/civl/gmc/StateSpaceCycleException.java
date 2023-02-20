package dev.civl.gmc;

import dev.civl.gmc.seq.DfsSearcher;

/**
 * This is the Exception that will be thrown by the {@link DfsSearcher} if and
 * only if 1) a cycle in state space detected; and 2) cycle violation is set to
 * be reported.
 * 
 * @author ziqing
 *
 */
public class StateSpaceCycleException extends Exception {

	private static final long serialVersionUID = -4766994919501211571L;

	/**
	 * The position in stack of the state where a cycle found
	 */
	private int stackPos;

	public StateSpaceCycleException(int stackPos) {
		super();
		this.stackPos = stackPos;
	}

	/**
	 * 
	 * @return the position in stack of the state where a cycle found
	 */
	public int stackPos() {
		return stackPos;
	}
}
