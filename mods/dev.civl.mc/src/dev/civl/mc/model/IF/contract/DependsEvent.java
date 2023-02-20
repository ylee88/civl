package dev.civl.mc.model.IF.contract;

import dev.civl.mc.model.IF.Sourceable;

/**
 * This represents an event which is used as one argument of the
 * <code>depends</code> clause.
 * 
 * @author Manchun Zheng
 *
 */
public interface DependsEvent extends Sourceable {
	public enum DependsEventKind {
		READ, WRITE, REACH, CALL, COMPOSITE, ANYACT, NOACT
	}

	/**
	 * Returns the kind of this event.
	 * 
	 * @return
	 */
	DependsEventKind dependsEventKind();

	/**
	 * Does this event equals to that event?
	 * 
	 * @param that
	 * @return
	 */
	boolean equalsWork(DependsEvent that);
}
