package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Map;
import java.util.Set;

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
	 * @return the abstraction of the code fragment that contains the call
	 *         associated with this node
	 */
	// AssignmentSequence containingCodeFragment();

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
	 * @return a map from formal parameters to actual parameters as well as
	 *         affected global variables to themselves.
	 */
	Map<AssignExprIF, AssignExprIF> unmapping();

	/**
	 * 
	 * @return the set of function inputs
	 */
	Set<AssignExprIF> functionInputs();

	/**
	 * 
	 * @return the kind of the node
	 */
	IGNodeKind kind();

	/* ********* write section ********** */

	/**
	 * set the abstraction of the code fragment that contains the call
	 * associated with this node
	 */
	// void setContainingCodeFragment(AssignmentSequence containingCode);

	/**
	 * Mark this node to be {@link IGNodeKind#RECURSIVE}
	 */
	void markRecursive();

	/**
	 * add an un-mapping entry to this node
	 * 
	 * @param umFrom
	 *            the expression abstraction will gone after return from the
	 *            lexical call
	 * @param umTo
	 *            the expression abstraction inherits the information from
	 *            "umFrom" after return from the lexical call
	 */
	void addUnmapping(AssignExprIF umFrom, AssignExprIF umTo);

	/**
	 * add abstract representation of the formal parameters
	 */
	void setFormalParameters(AssignExprIF[] formals);

	/**
	 * 
	 * @param returnValue
	 */
	void addReturnValue(AssignExprIF returnValue);

	/**
	 * Add a child to this node
	 * 
	 * @param child
	 */
	void addChild(InvocationGraphNode child);
}
