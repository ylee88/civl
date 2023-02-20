package dev.civl.abc.ast.node.IF.expression;

import dev.civl.abc.ast.node.IF.GenericAssociationNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.type.IF.Type;

/**
 * Represents a C11 generic selection construct; see C11 Section 6.5.1.1.
 * 
 * @author awilton
 * 
 */
public interface GenericSelectionNode extends ExpressionNode {

	/**
	 * Returns the controlling expression.
	 * 
	 * @return the controlling expression which is the (unevaluated) expression
	 *         whose type is used to select an expression from the association
	 *         list.
	 */
	ExpressionNode getControllingExpression();

	/**
	 * @return the default association if it exists, null otherwise
	 */
	ExpressionNode getDefaultAssociation();

	/**
	 * @return the sequence node containing all generic associations (which does
	 *         not include the default association)
	 */
	SequenceNode<GenericAssociationNode> getAssociationList();

	/**
	 * Gets the expression node associated to the typeNode representing a type
	 * compatible with typeLabel. If no such type node exists in the sequence of
	 * associations then returns the expression of the default association
	 * (which may be null if there is no default association).
	 * 
	 * @param typeLabel
	 *            the conceptual type that pinpoints the association of interest
	 * @return the expression node associated with typeLabel if it exists;
	 *         returns getDefaultAssociation() otherwise.
	 */
	ExpressionNode getAssociatedExpression(Type typeLabel);

	@Override
	GenericSelectionNode copy();
}
