package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.CompletenessNode;
import dev.civl.abc.token.IF.Source;

public class CommonCompletenessNode extends CommonContractNode
		implements
			CompletenessNode {

	/**
	 * true if this is complete clause, otherwise it is a disjoint clause
	 */
	private boolean isComplete;

	public CommonCompletenessNode(Source source, boolean isComplete,
			SequenceNode<IdentifierNode> idList) {
		super(source, (ASTNode) idList);
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.COMPLETENESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<IdentifierNode> getIDList() {
		return (SequenceNode<IdentifierNode>) this.child(0);
	}

	@Override
	public CompletenessNode copy() {
		return new CommonCompletenessNode(getSource(), isComplete,
				duplicate(this.getIDList()));
	}

	@Override
	protected void printBody(PrintStream out) {
		out.println("completeness");
	}

	@Override
	public boolean isDisjoint() {
		return !this.isComplete;
	}

	@Override
	public boolean isComplete() {
		return this.isComplete;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index != 0)
			throw new ASTException(
					"CommonCompletenessNode has only one child, but saw index "
							+ index);
		if (!(child == null || child instanceof SequenceNode))
			throw new ASTException(
					"Child of CommonCompletenessNode must be a SequenceNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
