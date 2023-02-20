package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.SequenceNode;

/**
 * This represents a named behavior of the ACSL specification.
 * 
 * @author Manchun Zheng
 *
 */
public interface BehaviorNode extends ContractNode {

	/**
	 * returns the name of this behavior
	 * 
	 * @return the name of this behavior
	 */
	IdentifierNode getName();

	/**
	 * returns the body of this behavior, which is a sequence of contract nodes
	 * 
	 * @return the body of this behavior
	 */
	SequenceNode<ContractNode> getBody();

	@Override
	BehaviorNode copy();
}
