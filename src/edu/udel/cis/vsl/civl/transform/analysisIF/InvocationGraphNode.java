package edu.udel.cis.vsl.civl.transform.analysisIF;

import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

/**
 * <p>
 * The invocation graph node is described in paper "Context-sensitive
 * inter-procedural points-to analysis in the presence of function pointers"
 * </p>
 * 
 * <p>
 * Briefly, a InvocationGraphNode represents a lexical function call.
 * </p>
 * 
 * @author ziqing
 *
 */
public interface InvocationGraphNode {

	/**
	 * Meaning and usage of these kinds can be found in paper "Context-sensitive
	 * inter-procedural points-to analysis in the presence of function
	 * pointers".
	 * 
	 * @author ziqing
	 *
	 */
	public static enum IGNodeKind {
		ORDINARY, APPROXIMATE, RECURSIVE
	}

	/* ********* read section ********** */

	/**
	 * 
	 * @return the InvocationGraphNodes that represents the function calls that
	 *         are lexically in the body of this function
	 */
	Iterable<InvocationGraphNode> children();

	/**
	 * 
	 * @return the parent node whose associated function all contains the
	 *         funcation call associated with this node
	 */
	InvocationGraphNode parent();

	/**
	 * @return the function this node refers to. Note that if the lexical call
	 *         associated with this node is not called through function pointer,
	 *         there is one exact {@link Function} this node refers to.
	 */
	Function function();

	/**
	 * 
	 * @return the set of accessed global objects
	 */
	Iterable<AssignExprIF> accessedGlobals();

	/**
	 * @return an orderd array of actual parameters
	 */
	AssignExprIF[] actualParams();

	/**
	 * 
	 * @return an ordered array of formal parameters
	 */
	AssignExprIF[] formalParams();

	/**
	 * 
	 * @return the receiver expression abstraction for the function call
	 *         represented by this node
	 */
	AssignExprIF returnTo();

	/**
	 * 
	 * @return the set of returning expression abstraction from the function
	 *         body associated with this node
	 */
	Iterable<AssignExprIF> returnings();

	/**
	 * 
	 * @return the kind of the node
	 */
	IGNodeKind kind();

	/**
	 * 
	 * @return the RECURSIVE kind node that this APPROXIMATE node is associated
	 *         with. null if this node is ORDINARY or already RECURSIVE.
	 */
	InvocationGraphNode getRecursive();

	/**
	 * make the given node, who is associated with the same function as this
	 * one, has same formal parameters, global accesses and returning values as
	 * this one.
	 * 
	 * @param node
	 */
	void share(InvocationGraphNode node);

	/* ********* write section ********** */

	/**
	 * Mark this node to be {@link IGNodeKind#RECURSIVE}
	 */
	void markRecursive();

	/**
	 * @param formals
	 *            the abstractions of the formal parameters of this node
	 */
	void setFormalParameters(AssignExprIF[] formals);

	/**
	 * save a returning expression in the associated function body in this node
	 * 
	 * @param returnValue
	 *            an return expression abstraction
	 */
	void addReturnValue(AssignExprIF returnValue);

	/**
	 * save an access to a global object by the associated function body in this
	 * node
	 * 
	 * @param globalVar
	 *            a global object abstraction
	 */
	void addGlobalAccess(AssignExprIF globalAccess);

	/**
	 * Add a child to this node
	 * 
	 * @param child
	 */
	void addChild(InvocationGraphNode child);
}
