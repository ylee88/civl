package dev.civl.abc.ast.node.common.acsl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.analysis.entity.FocusAnalysisData;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.acsl.FocusLoopTransformNode;
import dev.civl.abc.ast.node.IF.acsl.FocusTransformNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.FunctionCallNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.RegularRangeNode;
import dev.civl.abc.front.IF.CivlcTokenConstant;
import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.CivlcToken.TokenVocabulary;

public abstract class CommonFocusTransformNode extends CommonTransformNode
		implements FocusTransformNode {
	protected NodeFactory nodeFactory;
	protected TokenFactory tokenFactory;
	protected FocusAnalysisData focusData = null;

	public CommonFocusTransformNode(Source source, NodeFactory nodeFactory,
			TokenFactory tokenFactory, Iterable<? extends ASTNode> children) {
		super(source, children);
		this.nodeFactory = nodeFactory;
		this.tokenFactory = tokenFactory;
	}
	
	public CommonFocusTransformNode(Source source, NodeFactory nodeFactory,
			TokenFactory tokenFactory, ASTNode child0) {
		this(source, nodeFactory, tokenFactory, Arrays.asList(child0));
	}
	
	public CommonFocusTransformNode(Source source, NodeFactory nodeFactory,
			TokenFactory tokenFactory) {
		this(source, nodeFactory, tokenFactory, Arrays.asList(new ASTNode[] {}));
	}
	
	public void setFocusAnalysisData(FocusAnalysisData focusData) {
		this.focusData = focusData;
	}
	
	protected boolean removeAllFocusTransforms(ASTNode node) {
		boolean hadFocusTransforms = false;
		List<TransformNode> transforms = node.transformAnnotations();
		for (int i = 0; i < transforms.size(); i++) {
			TransformNode transform = transforms.get(i);
			if (transform instanceof FocusTransformNode) {
				hadFocusTransforms = true;
				node.removeTransformAnnotation(i);
				i--;
			}
		}
		return hadFocusTransforms;
	}
	
	protected ExpressionNode genVarRestriction(String varName, RegularRangeNode range) {
		String methodName = "genVarRestriction";
		Source varSource = newSource(methodName, varName);
		IdentifierExpressionNode varNode = nodeFactory
				.newIdentifierExpressionNode(varSource,
						nodeFactory.newIdentifierNode(varSource, varName));
		ExpressionNode loBoundNode = nodeFactory.newOperatorNode(
				range.getLow().getSource(), OperatorNode.Operator.LTE,
				range.getLow().copy(), varNode);
		ExpressionNode hiBoundNode = nodeFactory.newOperatorNode(
				range.getHigh().getSource(), OperatorNode.Operator.LTE,
				varNode.copy(), range.getHigh().copy());
		return nodeFactory.newOperatorNode(range.getSource(), OperatorNode.Operator.LAND, loBoundNode, hiBoundNode);
	}
	
	protected FunctionCallNode functionCall(String name,
			List<ExpressionNode> args) {
		return nodeFactory.newFunctionCallNode(newSource("functionCall", name),
				identifierExpression(name), args, null);
	}

	protected FunctionCallNode functionCall(String name) {
		return functionCall(name, new LinkedList<>());
	}

	protected IdentifierExpressionNode identifierExpression(String name) {
		Source source = newSource("identifierExpression", name);
		return nodeFactory.newIdentifierExpressionNode(source,
				identifier(name));
	}

	protected IdentifierNode identifier(String name) {
		return nodeFactory.newIdentifierNode(newSource("identifier", name),
				name);
	}

	protected Source newSource(String callingFuncName, String sourceText) {
		Formation formation = tokenFactory.newTransformFormation(
				"CommonFocusTransformNode", callingFuncName);
		CivlcToken token = tokenFactory.newCivlcToken(
				CivlcTokenConstant.DECLARATION, sourceText, formation,
				TokenVocabulary.DUMMY);
		return tokenFactory.newSource(token);
	}
}
