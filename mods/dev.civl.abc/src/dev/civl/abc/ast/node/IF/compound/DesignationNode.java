package dev.civl.abc.ast.node.IF.compound;

import dev.civl.abc.ast.node.IF.SequenceNode;

/**
 * <p>
 * A designation node specifies a sequence of designators. Each designator is
 * either an array designator or field designator. The sequence navigates to a
 * point within a compound structure. Here is an example from C17:6.7.9,
 * paragraph 35, Example 11:
 * 
 * <pre>
 *  struct { int a[3], b; } w[] = { [0].a = {1}, [1].a[0] = 2 };
 * </pre>
 * 
 * "[0].a" is represented by a DesignationNode wrapping a sequence of length 2
 * of DesignatorNodes. The first DesignatorNode in this sequence represents
 * "[0]" (an array element designator) and the second DesignatorNode represents
 * ".a" (a field designator).
 * </p>
 * <p>
 * The methods inherited from {@link SequenceNode} provide all that is necessary
 * to read and modify the sequence of designators.
 * </p>
 * 
 * @see SequenceNode#numChildren()
 * @see SequenceNode#child(int)
 * @see SequenceNode#addSequenceChild(dev.civl.abc.ast.node.IF.ASTNode)
 * 
 * @author siegel
 */
public interface DesignationNode extends SequenceNode<DesignatorNode> {

	@Override
	DesignationNode copy();
}
