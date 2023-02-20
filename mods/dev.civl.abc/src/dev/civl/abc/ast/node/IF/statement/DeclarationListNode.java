package dev.civl.abc.ast.node.IF.statement;

import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;

/**
 * A list of variable declarations, such as might occur as an initializer in a
 * <code>for</code> loop.
 * 
 * @author siegel
 * 
 */
public interface DeclarationListNode extends
		SequenceNode<VariableDeclarationNode>, ForLoopInitializerNode {

	@Override
	DeclarationListNode copy();

}
