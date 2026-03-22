package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.FocusOrderedTransformNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.expression.RegularRangeNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;

public class CommonFocusOrderedTransformNode extends CommonFocusTransformNode
		implements
			FocusOrderedTransformNode {
	private String focusTag;

	public CommonFocusOrderedTransformNode(Source source,
			NodeFactory nodeFactory, TokenFactory tokenFactory, String focusTag,
			OperatorNode operator, RegularRangeNode range,
			ExpressionNode expr) {
		super(source, nodeFactory, tokenFactory,
				Arrays.asList(operator, range, expr));
		this.focusTag = focusTag;
	}

	@Override
	public FocusKind getFocusKind() {
		return FocusKind.ORDERED;
	}

	@Override
	public List<BlockItemNode> transform(List<BlockItemNode> items)
			throws SyntaxException {
		String focusVarName = focusData.getVarNameFromTag(focusTag);
		String altFocusVarName = focusData.getAltVarNameFromTag(focusTag);
		List<BlockItemNode> result = new LinkedList<>();
		result.add(
				nodeFactory.newExpressionStatementNode(functionCall("$assert",
						Arrays.asList(implies(
								genOrderContext(focusVarName, 0, 1),
								genOrderExpression(focusVarName, 0, 1))))));
		SortedSet<Integer> offsets = focusData.getFocusOffsets(focusTag);
		int firstOffset = offsets.first();
		int lastOffset = firstOffset - 1;
		for (Integer offset : offsets) {
			if (lastOffset != firstOffset-1) {
				result.add(nodeFactory.newExpressionStatementNode(functionCall(
						"$assume",
						Arrays.asList(implies(
								genOrderContext(focusVarName, lastOffset,
										offset),
								genOrderExpression(focusVarName, lastOffset,
										offset))))));
				result.add(nodeFactory.newExpressionStatementNode(functionCall(
						"$assume",
						Arrays.asList(implies(
								genOrderContext(altFocusVarName, lastOffset,
										offset),
								genOrderExpression(altFocusVarName, lastOffset,
										offset))))));
			}

			lastOffset = offset;
		}
		result.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assume", Arrays
						.asList(implies(
								genOrderContext(getRangeLow().copy(),
										genVarWithOffset(focusVarName,
												firstOffset),
										true),
								genOrderExpression(getRangeLow().copy(),
										genVarWithOffset(focusVarName,
												firstOffset)))))));
		result.add(
				nodeFactory.newExpressionStatementNode(functionCall("$assume",
						Arrays.asList(implies(
								genOrderContext(
										genVarWithOffset(focusVarName,
												lastOffset),
										getRangeHigh().copy(), true),
								genOrderExpression(
										genVarWithOffset(focusVarName,
												lastOffset),
										getRangeHigh().copy()))))));
		result.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assume", Arrays
						.asList(implies(
								genOrderContext(getRangeLow().copy(),
										genVarWithOffset(altFocusVarName,
												firstOffset),
										true),
								genOrderExpression(getRangeLow().copy(),
										genVarWithOffset(altFocusVarName,
												firstOffset)))))));
		result.add(
				nodeFactory.newExpressionStatementNode(functionCall("$assume",
						Arrays.asList(implies(
								genOrderContext(
										genVarWithOffset(altFocusVarName,
												lastOffset),
										getRangeHigh().copy(), true),
								genOrderExpression(
										genVarWithOffset(altFocusVarName,
												lastOffset),
										getRangeHigh().copy()))))));
		result.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assume",
				Arrays.asList(implies(genOrderContext(
						genVarWithOffset(altFocusVarName, lastOffset),
						genVarWithOffset(focusVarName, firstOffset), true),
						genOrderExpression(
								genVarWithOffset(altFocusVarName, lastOffset),
								genVarWithOffset(focusVarName,
										firstOffset)))))));
		result.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assume", Arrays.asList(implies(genOrderContext(
						genVarWithOffset(focusVarName, lastOffset),
						genVarWithOffset(altFocusVarName, firstOffset), true),
						genOrderExpression(
								genVarWithOffset(focusVarName, lastOffset),
								genVarWithOffset(altFocusVarName,
										firstOffset)))))));
		
		
		return result;
	}
	
	private ExpressionNode genOrderContext(String focusVarName, int offset1,
			int offset2) {
		return genOrderContext(genVarWithOffset(focusVarName, offset1),
				genVarWithOffset(focusVarName, offset2), false);
	}
	
	private ExpressionNode genOrderContext(ExpressionNode lowExprNode, ExpressionNode highExprNode, boolean orderExprs) {
		String thisFuncName = "genOrderContext";
		Source thisSource = newSource(thisFuncName,
				lowExprNode.toString()+" and "+highExprNode.toString());
		ExpressionNode lowerBoundNode = nodeFactory.newOperatorNode(thisSource,
				Operator.LTE, getRangeLow().copy(), lowExprNode);
		ExpressionNode upperBoundNode = nodeFactory.newOperatorNode(thisSource,
				Operator.LTE, highExprNode,
				getRangeHigh().copy());

		return orderExprs
				? andExpr(lowerBoundNode,
						nodeFactory.newOperatorNode(thisSource, Operator.LT,
								lowExprNode.copy(), highExprNode.copy()),
						upperBoundNode)
				: andExpr(lowerBoundNode, upperBoundNode);
	}

	private ExpressionNode genOrderExpression(String focusVarName, int offset1,
			int offset2) {
		return genOrderExpression(genVarWithOffset(focusVarName, offset1),
				genVarWithOffset(focusVarName, offset2));
	}

	private ExpressionNode genOrderExpression(
			ExpressionNode lowReplacement,
			ExpressionNode highReplacement) {
		String thisFuncName = "genOrderExpression";
		Source thisSource = newSource(thisFuncName,
				"(" + lowReplacement.toString() + ", "
						+ highReplacement.toString() + ") ordered result");
		return nodeFactory.newOperatorNode(thisSource, getOperator(),
				replaceIdent(getOrderedExpression(), focusTag, lowReplacement),
				replaceIdent(getOrderedExpression(), focusTag,
						highReplacement));
	}
	
	private ExpressionNode genVarWithOffset(String focusVarName, int offset) {
		return addIntExpr(identifierExpression(focusVarName), offset);
	}
	
	public Operator getOperator() {
		return ((OperatorNode) child(0)).getOperator();
	}
	
	public ExpressionNode getRangeLow() {
		return getRange().getLow();
	}
	
	public ExpressionNode getRangeHigh() {
		return getRange().getHigh();
	}
	
	private RegularRangeNode getRange() {
		return (RegularRangeNode) child(1);
	}
	
	public ExpressionNode getOrderedExpression() {
		return (ExpressionNode) child(2);
	}

	@Override
	public ContractNode copy() {
		return new CommonFocusOrderedTransformNode(this.getSource(),
				nodeFactory, tokenFactory, focusTag,
				(OperatorNode) child(0).copy(),
				(RegularRangeNode) child(1).copy(),
				(ExpressionNode) child(2).copy());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("focus ordered");
	}

}
