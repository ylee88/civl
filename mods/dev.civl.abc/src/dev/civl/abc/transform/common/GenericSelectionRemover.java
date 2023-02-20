package dev.civl.abc.transform.common;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.GenericSelectionNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.abc.transform.IF.Transformer;

public class GenericSelectionRemover extends BaseTransformer {

	/**
	 * The short code used to identify this {@link Transformer}.
	 */
	public final static String CODE = "gsr";

	/**
	 * The long name used to identify this {@link Transformer}.
	 */
	public final static String LONG_NAME = "GenericSelectionRemover";

	/**
	 * The short description of what this {@link Transformer} does.
	 */
	public final static String SHORT_DESCRIPTION = "Replaces each generic selection node with the expression it selects";

	public GenericSelectionRemover(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		assert this.astFactory == ast.getASTFactory();
		assert this.nodeFactory == astFactory.getNodeFactory();

		SequenceNode<BlockItemNode> root = ast.getRootNode();
		
		ast.release();
		transformNode(root);

		AST newAst = astFactory.newAST(root, ast.getSourceFiles(),
				ast.isWholeProgram());

		return newAst;
	}

	private void transformNode(ASTNode astNode) {
		if (astNode == null) return;
		
		if (astNode instanceof GenericSelectionNode) {
			GenericSelectionNode thisNode = (GenericSelectionNode) astNode;
			ExpressionNode selectedExprNode = thisNode.getAssociatedExpression(
					thisNode.getControllingExpression().getType());
			ASTNode parent = thisNode.parent();
			
			assert selectedExprNode != null;
			assert parent != null;
			
			selectedExprNode.remove();
			parent.setChild(thisNode.childIndex(), selectedExprNode);
			transformNode(selectedExprNode);
		} else {
			for (ASTNode child : astNode.children()) {
				transformNode(child);
			}
		}
	}
}
