package dev.civl.mc.transform.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.AssignsOrReadsNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.InvariantNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.statement.LoopNode;

/**
 * This class represents a block of annotations which serve as contracts of a
 * loop statement. A loop contract block may have following kinds of clauses:
 * <ul>
 * <li>loop invariants</li>
 * <li>loop variants</li>
 * </ul>
 * 
 * <p>
 * Note that behavior clause is temporarily not supported in loop contracts.
 * </p>
 * 
 * @author ziqing
 *
 */
class LoopContractBlock {
	/**
	 * Loop invariants specified in this loop contract block
	 */
	private List<ExpressionNode> invariants;

	/**
	 * A set of assigned memory locations specified in this loop contract block.
	 */
	private List<ExpressionNode> assignClauses;

	private List<TransformNode> transformations;

	/**
	 * A reference to the associated {@link LoopNode}
	 */
	private LoopNode loop;

	LoopContractBlock(LoopNode loop) {
		assert loop != null;
		this.loop = loop;
		this.invariants = new LinkedList<>();
		this.assignClauses = new LinkedList<>();
		this.transformations = new LinkedList<>();
		this.parseLoopContracts(loop);
	}

	/**
	 * Add a loop invariant expression specified by a loop invariant clause.
	 * 
	 * @param invariants
	 */
	void addLoopInvariants(ExpressionNode invariants) {
		this.invariants.add(invariants);
	}

	/**
	 * Add a location set expression specified by a loop assign clause
	 * 
	 * @param writeLoc
	 */
	void addLoopAssigns(SequenceNode<ExpressionNode> writeSets) {
		for (ExpressionNode writeSet : writeSets)
			this.assignClauses.add(writeSet);
	}
	
	void addLoopTransformation(TransformNode transformation) {
		transformations.add(transformation);
	}
	
	void addAllLoopTransformations(List<TransformNode> transformList) {
		transformations.addAll(transformList);
	}

	/**
	 * Returns all loop invariant expressions in a conjunction form.
	 * 
	 * @param nodeFactory
	 * @return
	 */
	ExpressionNode getLoopInvariants(NodeFactory nodeFactory) {
		ExpressionNode result;
		Iterator<ExpressionNode> iter = invariants.iterator();

		if (iter.hasNext())
			result = iter.next().copy();
		else
			result = nodeFactory.newBooleanConstantNode(loop.getSource(), true);

		while (iter.hasNext()) {
			ExpressionNode next = iter.next();

			result = nodeFactory.newOperatorNode(result.getSource(),
					Operator.LAND, result, next.copy());
		}
		return result;
	}

	/**
	 * Return the write set specified by the loop assign clauses.
	 * 
	 * @param nodeFactory
	 *            A reference to a {@link NodeFactory}
	 * @return
	 */
	List<ExpressionNode> getLoopAssignSet() {
		return assignClauses;
	}
	
	List<TransformNode> getTransformations() {
		return transformations;
	}

	/**
	 * Returns the corresponding {@link LoopNode}
	 * 
	 * @return
	 */
	LoopNode getLoopNode() {
		return loop;
	}

	/**
	 * Parse an annotated {@link LoopNode} to a {@link LoopContractBlock}
	 * 
	 * @param loopNode
	 * @return
	 */
	private void parseLoopContracts(LoopNode loopNode) {
		SequenceNode<ContractNode> loopContracts = loopNode.loopContracts();

		for (ContractNode contract : loopContracts) {
			switch (contract.contractKind()) {
				case INVARIANT :
					ExpressionNode invariant = ((InvariantNode) contract)
							.getExpression();

					addLoopInvariants(invariant);
					break;
				case ASSIGNS_READS :
					SequenceNode<ExpressionNode> assignedSet = ((AssignsOrReadsNode) contract)
							.getMemoryList();

					addLoopAssigns(assignedSet);
					break;
				case TRANSFORM:
					addLoopTransformation((TransformNode) contract);
					break;
				default:
					// Skip
			}
		}
		addAllLoopTransformations(loopNode.transformAnnotations());
	}
}
