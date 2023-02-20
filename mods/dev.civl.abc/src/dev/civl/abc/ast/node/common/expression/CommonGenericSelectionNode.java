/**
 * 
 */
package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.GenericAssociationNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.GenericSelectionNode;
import dev.civl.abc.ast.type.IF.Type;
import dev.civl.abc.token.IF.Source;

/**
 * @author awilton
 *
 */
public class CommonGenericSelectionNode extends CommonExpressionNode
		implements
			GenericSelectionNode {

	/**
	 * @param source
	 *            The source for this node
	 * @param controllingExpression
	 *            The controlling expression of this generic selection
	 * @param defaultExpression
	 *            The default expression to be evaluated to if the controlling
	 *            expression's type is not compatible with any type in
	 *            genericAssociationList
	 * @param genericAssociationList
	 *            The list of (non-default) generic associations
	 */
	public CommonGenericSelectionNode(Source source,
			ExpressionNode controllingExpression,
			ExpressionNode defaultExpression,
			SequenceNode<GenericAssociationNode> genericAssociationList) {
		super(source, controllingExpression, defaultExpression,
				genericAssociationList);
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.GENERIC_SELECTION;
	}

	@Override
	public boolean isConstantExpression() {
		// Taking a conservative approach because the node will get translated
		// away anyways
		return false;
	}

	@Override
	public boolean isSideEffectFree(boolean errorsAreSideEffects) {
		// Taking a conservative approach because the node will get translated
		// away anyways
		return false;
	}

	@Override
	public ExpressionNode getControllingExpression() {
		return (ExpressionNode) child(0);
	}

	@Override
	public ExpressionNode getDefaultAssociation() {
		return (ExpressionNode) child(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<GenericAssociationNode> getAssociationList() {
		return (SequenceNode<GenericAssociationNode>) child(2);
	}

	@Override
	public ExpressionNode getAssociatedExpression(Type typeLabel) {
		ExpressionNode associatedExpression = null;

		for (GenericAssociationNode associationNode : getAssociationList()) {
			if (typeLabel.compatibleWith(associationNode.getTypeNode().getType())) {
				associatedExpression = associationNode.getExpressionNode();
				break;
			}
		}
		return (associatedExpression != null)
				? associatedExpression
				: getDefaultAssociation();
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 3)
			throw new ASTException(
					"CommonGenericSelectionNode has three children, but saw index "
							+ index);
		if ((index == 0 || index == 1)
				&& !(child == null || child instanceof ExpressionNode))
			throw new ASTException(
					"Child of CommonGenericSelectionNode at index " + index
							+ " must be a ExpressionNode, but saw " + child
							+ " with type " + child.nodeKind());
		if (index == 2 && !(child == null || child instanceof SequenceNode))
			throw new ASTException(
					"Child of CommonGenericSelectionNode at index " + index
							+ " must be a SequenceNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}

	@Override
	public GenericSelectionNode copy() {
		return new CommonGenericSelectionNode(getSource(),
				duplicate(getControllingExpression()),
				duplicate(getDefaultAssociation()),
				duplicate(getAssociationList()));
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("GenericSelectionNode");
	}

}
