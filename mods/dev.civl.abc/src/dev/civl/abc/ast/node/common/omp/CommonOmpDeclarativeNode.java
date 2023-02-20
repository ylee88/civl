package dev.civl.abc.ast.node.common.omp;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.omp.OmpDeclarativeNode;
import dev.civl.abc.token.IF.Source;

public class CommonOmpDeclarativeNode extends CommonOmpNode
		implements
			OmpDeclarativeNode {
	/**
	 * Child 0: variable list
	 * 
	 * @param varList
	 */
	public CommonOmpDeclarativeNode(Source source,
			SequenceNode<IdentifierExpressionNode> varList) {
		super(source);
		this.addChild(varList);// child 0
	}

	@Override
	public OmpNodeKind ompNodeKind() {
		return OmpNodeKind.DECLARATIVE;
	}

	@Override
	public OmpDeclarativeNodeKind ompDeclarativeNodeKind() {
		return OmpDeclarativeNodeKind.THREADPRIVATE;
	}

	@Override
	public void setList(SequenceNode<IdentifierExpressionNode> list) {
		this.setChild(0, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<IdentifierExpressionNode> variables() {
		return (SequenceNode<IdentifierExpressionNode>) this.child(0);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("OmpThreadprivate");
	}

	@Override
	public OmpDeclarativeNode copy() {
		return new CommonOmpDeclarativeNode(getSource(),
				duplicate(this.variables()));
	}

	@Override
	public BlockItemKind blockItemKind() {
		return BlockItemKind.OMP_DECLARATIVE;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 1)
			throw new ASTException(
					"CommonOmpDeclarativeNode has one child, but saw index "
							+ index);
		if (!(child == null || child instanceof SequenceNode))
			throw new ASTException("Child of CommonOmpDeclarativeNode at index "
					+ index + " must be a SequenceNode, but saw " + child
					+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
