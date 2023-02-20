package dev.civl.abc.ast.node.common.statement;

import java.util.List;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.CompoundStatementNode;
import dev.civl.abc.ast.node.common.CommonSequenceNode;
import dev.civl.abc.token.IF.Source;

public class CommonCompoundStatementNode
		extends
			CommonSequenceNode<BlockItemNode>
		implements
			CompoundStatementNode {

	public CommonCompoundStatementNode(Source source,
			List<BlockItemNode> childList) {
		super(source, "CompoundStatement", childList);
	}

	@Override
	public CompoundStatementNode copy() {
		return new CommonCompoundStatementNode(getSource(), childListCopy());
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.STATEMENT;
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.COMPOUND;
	}

	@Override
	public BlockItemKind blockItemKind() {
		return BlockItemKind.STATEMENT;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (!(child == null || child instanceof BlockItemNode))
			throw new ASTException(
					"Child of CommonCompoundStatementNode must be a BlockItemNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
