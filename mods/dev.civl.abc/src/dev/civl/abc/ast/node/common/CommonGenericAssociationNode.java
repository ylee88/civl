/**
 * 
 */
package dev.civl.abc.ast.node.common;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.GenericAssociationNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.token.IF.Source;

/**
 * @author awilton
 *
 */
public class CommonGenericAssociationNode extends CommonASTNode
		implements
			GenericAssociationNode {

	/**
	 * @param source
	 *            The source for this node
	 * @param typeLabel
	 *            The type node acting as the "key" or "label" for this
	 *            expression
	 * @param associatedExpression
	 *            The expression associated with the type of typeLabel
	 */
	public CommonGenericAssociationNode(Source source, TypeNode typeLabel,
			ExpressionNode associatedExpression) {
		super(source, typeLabel, associatedExpression);
	}

	@Override
	public NodeKind nodeKind() {
		return NodeKind.GENERIC_ASSOCIATION;
	}

	@Override
	public TypeNode getTypeNode() {
		return (TypeNode) child(0);
	}

	@Override
	public ExpressionNode getExpressionNode() {
		return (ExpressionNode) child(1);
	}

	@Override
	public GenericAssociationNode copy() {
		return new CommonGenericAssociationNode(getSource(),
				duplicate(getTypeNode()), duplicate(getExpressionNode()));
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 2)
			throw new ASTException(
					"CommonGenericAssociationNode has two children, but saw index "
							+ index);
		if (index == 0 && !(child == null || child instanceof TypeNode))
			throw new ASTException(
					"Child of CommonGenericAssociationNode at index " + index
							+ " must be a TypeNode, but saw " + child
							+ " with type " + child.nodeKind());
		if (index == 1 && !(child == null || child instanceof ExpressionNode))
			throw new ASTException(
					"Child of CommonGenericAssocationNode at index " + index
							+ " must be a ExpressionNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("GenericAssociationNode");
	}

}
