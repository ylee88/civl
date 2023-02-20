package dev.civl.abc.ast.node.common.statement;

import java.util.List;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.ChooseStatementNode;
import dev.civl.abc.ast.node.IF.statement.LabeledStatementNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.ast.node.common.CommonSequenceNode;
import dev.civl.abc.token.IF.Source;

public class CommonChooseStatementNode extends CommonSequenceNode<StatementNode>
		implements
			ChooseStatementNode {

	private LabeledStatementNode defaultCase = null;

	public CommonChooseStatementNode(Source source,
			List<StatementNode> childList) {
		super(source, "Choose", childList);
	}

	@Override
	public LabeledStatementNode getDefaultCase() {
		return defaultCase;
	}

	@Override
	public void setDefaultCase(LabeledStatementNode statement) {
		this.defaultCase = statement;
	}

	@Override
	public ChooseStatementNode copy() {
		return new CommonChooseStatementNode(getSource(), childListCopy());
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.STATEMENT;
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.CHOOSE;
	}

	@Override
	public BlockItemKind blockItemKind() {
		return BlockItemKind.STATEMENT;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (!(child == null || child instanceof StatementNode))
			throw new ASTException(
					"Child of CommonChooseStatementNode must be a StatementNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
