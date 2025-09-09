package dev.civl.abc.ast.node.IF.acsl;

import java.util.List;

import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.SyntaxException;

public interface TransformNode extends ContractNode {

	/**
	 * Apply the transformation this node is representing to some ast.
	 * 
	 * @param items
	 * @return
	 * @throws SyntaxException 
	 */
	List<BlockItemNode> transform(List<BlockItemNode> items) throws SyntaxException;

}
