package dev.civl.gmc.dpor;

/**
 * A simple structure to bundle data that is associated to a transition in an
 * execution explored by DPOR.
 * 
 * @author Alex Wilton
 *
 */
public class DporTransitionData {
	DporTransitionData() {}

	/**
	 * The previous position on the stack of an entry with a transition from
	 * this process
	 */
	public int prevStackPosition = -1;
	
	/**
	 * The {@link DporHbSet} of stack entries with transitions that
	 * happen-before this transition
	 */
	public DporHbSet hbSet = new DporHbSet();
}
