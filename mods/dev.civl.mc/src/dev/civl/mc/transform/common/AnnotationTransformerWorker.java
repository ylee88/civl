package dev.civl.mc.transform.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.SyntaxException;

/**
 * 
 * @author awilton
 *
 */
public class AnnotationTransformerWorker extends BaseWorker {

	public AnnotationTransformerWorker(String transformerName,
			ASTFactory astFactory) {
		super(transformerName, astFactory);
		this.nodeFactory = astFactory.getNodeFactory();
	}

	@Override
	protected AST transformCore(AST ast) throws SyntaxException {
		SequenceNode<BlockItemNode> root = ast.getRootNode();

		ast.release();
		ast = astFactory.newAST(transformSeqChildren(root), ast.getSourceFiles(),
				ast.isWholeProgram());
		return ast;
	}

	private List<BlockItemNode> applyTransforms(BlockItemNode node) throws SyntaxException {
		if (node == null)
			return null;
		//node = (BlockItemNode) transformChildren(node);
		
		node.remove();
		List<BlockItemNode> transNodes = new LinkedList<BlockItemNode>(
				Collections.singletonList(node));

		for (TransformNode transformer : node.transformAnnotations()) {
			transNodes = transformer.transform(transNodes);
		}
		node.transformAnnotations().clear();
		
		List<BlockItemNode> results = new ArrayList<>(transNodes.size());
		for (BlockItemNode transNode : transNodes) {
			results.add((BlockItemNode) transformChildren(transNode));
		}
		
		return results;
	}

	/**
	 * @param seqNode
	 * @return
	 * @throws SyntaxException 
	 */
	@SuppressWarnings("unchecked")
	private <T extends ASTNode> SequenceNode<T> transformSeqChildren(
			SequenceNode<T> seqNode) throws SyntaxException {
		if (seqNode == null)
			return null;
		for (int i = 0; i < seqNode.numChildren();) {
			T child = seqNode.getSequenceChild(i);
			if (child instanceof BlockItemNode) {
				seqNode.shiftRemoveChild(i);
				List<T> transformedChild = (List<T>) applyTransforms((BlockItemNode) child);
				seqNode.insertChildren(i, transformedChild);
				i+=transformedChild.size();
			} else {
				i++;
			}
		}
		return seqNode;
	}
	
	private ASTNode transformChildren(ASTNode node) throws SyntaxException {
		if (node == null)
			return null;
		if (node instanceof SequenceNode<?>) {
			return transformSeqChildren((SequenceNode<?>) node);
		}
		int numChildren = node.numChildren();

		for (int i = 0; i < numChildren; i++) {
			ASTNode child = node.child(i);
			if (child != null) {
				child.remove();
				node.setChild(i, transformChildren(child));
			}
		}
		return node;
	}
}
