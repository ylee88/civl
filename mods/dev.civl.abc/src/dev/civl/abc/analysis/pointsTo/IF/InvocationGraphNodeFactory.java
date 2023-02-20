package dev.civl.abc.analysis.pointsTo.IF;

import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.node.IF.expression.FunctionCallNode;

/**
 * creating invocation graph nodes
 * 
 * @author ziqing
 *
 */
public interface InvocationGraphNodeFactory {

	/**
	 * creates a new invocation graph node
	 * 
	 * @param function
	 *            the {@link Function} that is associated with the node
	 * @param parent
	 *            the parent node of the generated node, null if this node is
	 *            associated with the "main" function
	 * @param call
	 *            the {@link FunctionCallNode} representing the lexical call
	 *            that is associated with this node
	 * @param returnTo
	 *            the expression abstraction that will take the returned value
	 *            after returning from the associated function call
	 * @param actualParams
	 *            the sequence (as a java array) of actual parameter
	 *            abstractions
	 * @return
	 */
	InvocationGraphNode newNode(Function function, InvocationGraphNode parent,
			AssignExprIF returnTo, AssignExprIF... actualParams);
}
