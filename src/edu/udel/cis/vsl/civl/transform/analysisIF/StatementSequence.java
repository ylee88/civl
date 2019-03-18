package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;

/**
 * <p>
 * Abstract an AST to a sequence of statements for flow-insensitive analysis.
 * </p>
 * 
 * <p>
 * The basic idea is to convert a branch <code>if (c) then a else b</code> to
 * <code>a;b</code>, other statements are naturally converted. Declarations are
 * no needed but declared memory locations are stored in
 * {@link #memoryLocation}s.
 * </p>
 * 
 * @author ziqing
 *
 */
public interface StatementSequence {

	/**
	 * 
	 * @return the statement sequence
	 */
	public Iterable<StatementNode> getAll();

	/**
	 * 
	 * @return the next statement if available, otherwise null
	 */
	public StatementNode next();

	/**
	 * 
	 * @return the previous statement if available, otherwise null
	 */
	public StatementNode prev();

	/**
	 * reset iterator
	 */
	public void reset();

	/**
	 * 
	 * @return the set of memory locations included by the sequence of
	 *         statements
	 */
	public Set<Entity> memoryLocations();
}
