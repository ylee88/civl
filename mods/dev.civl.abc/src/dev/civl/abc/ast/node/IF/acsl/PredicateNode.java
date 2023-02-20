package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.declaration.FunctionDefinitionNode;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * The ACSL predicate node, which in the view of ABC, is just a function with a
 * boolean return type. An ACSL predicate annotation will become a function
 * which get inserted in the AST tree at the location of the annotation.
 * 
 * @author ziqing
 *
 */
public interface PredicateNode extends ContractNode, FunctionDefinitionNode {
	/**
	 * The name of the predicate
	 * 
	 * @return
	 */
	IdentifierNode getPredicateName();

	/**
	 * The parameters of the predicate
	 * 
	 * @return
	 */
	SequenceNode<VariableDeclarationNode> getParameters();

	/**
	 * the body expression of the predicate
	 * 
	 * @return
	 */
	ExpressionNode getExpressionBody();

	@Override
	PredicateNode copy();
}
