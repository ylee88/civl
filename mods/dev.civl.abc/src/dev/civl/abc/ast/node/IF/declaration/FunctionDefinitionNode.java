package dev.civl.abc.ast.node.IF.declaration;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.statement.CompoundStatementNode;
import dev.civl.abc.ast.node.IF.type.FunctionTypeNode;

/**
 * Represents a function definition, i.e., a function declaration which includes
 * the function body.
 * 
 * @author siegel
 * 
 */
public interface FunctionDefinitionNode extends FunctionDeclarationNode {

	/**
	 * Returns the body of the function, a compound statement.
	 * 
	 * @return the function body
	 */
	CompoundStatementNode getBody();

	/**
	 * Sets the body to the given statement. The statement becomes a child of
	 * this node.
	 * 
	 * @param statement
	 *            node to be made the body child of this node
	 */
	void setBody(CompoundStatementNode statement);

	@Override
	FunctionDefinitionNode copy();

	@Override
	FunctionTypeNode getTypeNode();

	/**
	 * <b>Pre-condition: call to {@link #isLogicFunction()} returns true.</b>
	 * 
	 * @return The logic function definition, which is a side-effect free
	 *         expression, if this funciton is a logic function. Otherwise,
	 *         null.
	 */
	ExpressionNode getLogicDefinition();
}
