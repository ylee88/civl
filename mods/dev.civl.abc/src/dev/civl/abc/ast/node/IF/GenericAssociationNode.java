package dev.civl.abc.ast.node.IF;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;

/**
 * Represents the association between a type and an expression for use in a
 * generic selection expression.
 * 
 * @author awilton
 */
public interface GenericAssociationNode extends ASTNode {
	
	/**
	 * @return type contained in this association
	 */
	TypeNode getTypeNode();
	
	/**
	 * @return expression contained in this association
	 */
	ExpressionNode getExpressionNode();
	
	@Override
	GenericAssociationNode copy();
}
