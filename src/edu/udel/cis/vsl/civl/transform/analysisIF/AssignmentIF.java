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
	 * an abstract representation of an expression at left or right hand side of
	 * an assignment. An abstraction can be auxiliary (i.e. no connection to the
	 * original program), representing an entity (connects to an entity of the
	 * original program) or an non-entity location (i.e. allocation or string)
	 * 
	 * @author ziqing
	 *
	 */
	public interface AssignExprIF {
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
		 * @return the ASTNode associated with this abstraction if this
		 *         abstraction represents an allocation or a string literal
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
