package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * An abstract representation of one of the following assignments:
 * 
 * <ul>
 * <li>BASE: {@link #lhs()} = &{@link #rhs()}</li>
 * <li>SIMPLE: {@link #lhs()} = {@link #rhs()}</li>
 * <li>COMPLEX_LD: *{@link #lhs()} = {@link #rhs()}</li>
 * <li>COMPLEX_RD: {@link #lhs()} = *{@link #rhs()}</li>
 * </ul>
 * 
 * @author ziqing
 *
 */
public interface AssignmentIF {

	/**
	 * <p>
	 * an abstract representation of an expression at left or right hand side of
	 * an {@link AssignmentIF}.
	 * </p>
	 * 
	 * <p>
	 * An abstraction is associated with an entity, an non-trivial expression
	 * (e.g. allocation , string...) or the worst-case EVERYTHING
	 * </p>
	 * 
	 * @author ziqing
	 *
	 */
	public interface AssignExprIF {
		/**
		 * @return the unique identifier of this abstraction
		 */
		int id();

		/**
		 * requires {@link #nonEntitySource()} == null
		 * 
		 * @return the {@link Entity} associated with this abstraction, non-null
		 *         iff NOT {@link #isFull()}
		 */
		Entity source();

		/**
		 * 
		 * @return true iff this is an abstraction of a right-hand side and
		 *         represents FULL
		 */
		boolean isFull();

		/**
		 * requires {@link #source()} == null
		 * 
		 * @return the non-trivial expression associated with this abstraction
		 *         if this abstraction does not represent an entity
		 */
		ExpressionNode nonEntitySource();
	}

	/**
	 * <p>
	 * BASE: p = &a
	 * 
	 * SIMPLE: p = a
	 * 
	 * COMPLEX_LD: *p = a
	 *
	 * COMPLEX_RD: p = *a
	 * </p>
	 */
	public static enum AssignmentKind {
		BASE, SIMPLE, COMPLEX_LD, COMPLEX_RD
	}

	/**
	 * @return: the left-hand side abstraction
	 * 
	 */
	public AssignExprIF lhs();

	/**
	 * @return the right-hand side abstraction
	 * 
	 */
	public AssignExprIF rhs();

	/**
	 * @return kind
	 */
	public AssignmentKind kind();
}
