package dev.civl.abc.ast.node.common.omp;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.omp.OmpFunctionReductionNode;
import dev.civl.abc.token.IF.Source;

public class CommonOmpFunctionReductionNode extends CommonOmpReductionNode
		implements
			OmpFunctionReductionNode {

	public CommonOmpFunctionReductionNode(Source source,
			IdentifierExpressionNode identifier,
			SequenceNode<IdentifierExpressionNode> variables) {
		super(source);
		this.addChild(identifier);
		this.addChild(variables);
	}

	@Override
	public OmpReductionNodeKind ompReductionOperatorNodeKind() {
		return OmpReductionNodeKind.FUNCTION;
	}

	@Override
	public ASTNode copy() {
		return null;
	}

	@Override
	public IdentifierExpressionNode function() {
		return (IdentifierExpressionNode) this.child(0);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("OmpFunctionReductionNode");
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<IdentifierExpressionNode> variables() {
		return (SequenceNode<IdentifierExpressionNode>) this.child(1);
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 2)
			throw new ASTException(
					"CommonOmpFunctionReductionNode has two children, but saw index "
							+ index);
		if (index == 0 && !(child == null
				|| child instanceof IdentifierExpressionNode))
			throw new ASTException(
					"Child of CommonOmpFunctionReductionNode at index " + index
							+ " must be a IdentifierExpressionNode, but saw "
							+ child + " with type " + child.nodeKind());
		if (index == 1 && !(child == null || child instanceof SequenceNode))
			throw new ASTException(
					"Child of CommonOmpFunctionReductionNode at index " + index
							+ " must be a SequenceNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
