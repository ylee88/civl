package edu.udel.cis.vsl.civl.model.IF.statement;

import java.util.Set;

import edu.udel.cis.vsl.civl.model.IF.variable.Variable;

public interface AtomicLockAssignStatement extends Statement {
	boolean enterAtomic();

	boolean leaveAtomic();

	/**
	 * <p>
	 * Returns the set of all external variables that could be accessed in an
	 * execution of an atomic block. Could return {@code null} if this set
	 * cannot be estimated or if this is an atomic exit statement.
	 * </p>
	 * 
	 * <p>
	 * More precisely, an atomic enter statement in a function graph defines an
	 * atomic block as follows: consider all paths in the graph that start with
	 * the atomic enter statement and have the following property: for any
	 * prefix of the path, the number of atomic enter statements is greater than
	 * the number of atomic exit statements. All locations along such paths
	 * comprise the atomic construct. Furthermore, consider all function
	 * calls/spawns that occur within that construct, and all functions called
	 * by those functions, etc. These function bodies, together with the
	 * original atomic construct, comprise the atomic region. This method
	 * returns the set of variables which exist in the scope of the location
	 * which is the source of the original atomic enter statement and which are
	 * accessed (either by read or write) in the atomic region.
	 * </p>
	 * 
	 * <p>
	 * Reasons for a return of {@code null}: a function is called through a
	 * function pointer, so it is not possible to determine the exact function
	 * called statically. A system function is called and it is not known which
	 * variables will be accessed.
	 * </p>
	 * 
	 * @return the set of external variables accessed within the atomic block,
	 *         or null
	 */
	Set<Variable> getVariables();
}
