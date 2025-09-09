package dev.civl.abc.analysis.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.FocusAssertTransformNode;
import dev.civl.abc.ast.node.IF.acsl.FocusLoopTransformNode;
import dev.civl.abc.ast.node.IF.acsl.FocusTransformNode;
import dev.civl.abc.ast.node.IF.acsl.InsertTransformNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.ast.node.IF.declaration.InitializerNode;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import dev.civl.abc.ast.node.IF.expression.QuantifiedExpressionNode.Quantifier;
import dev.civl.abc.ast.node.IF.expression.RegularRangeNode;
import dev.civl.abc.ast.node.IF.expression.FunctionCallNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.expression.QuantifiedExpressionNode;
import dev.civl.abc.ast.node.IF.ASTNode.NodeKind;
import dev.civl.abc.ast.node.IF.PairNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.DeclarationListNode;
import dev.civl.abc.ast.node.IF.statement.ExpressionStatementNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopInitializerNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopNode;
import dev.civl.abc.ast.node.IF.statement.LoopNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.ast.node.IF.statement.LoopNode.LoopKind;
import dev.civl.abc.ast.node.IF.statement.StatementNode.StatementKind;
import dev.civl.abc.ast.node.IF.type.BasicTypeNode;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.front.IF.CivlcTokenConstant;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.CivlcToken.TokenVocabulary;

public class FocusTransformAnalyzer {

	private FocusAnalysisData focusData;

	private String wrongNodeErrMsg = "Focus may only be applied to a statement.";
	private String wrongAssertFormErrMsg = "Focus may only be used on an assertion "
			+ "of the form \"assert($forall (...) ...)\"";
	private String wrongLoopFormErrMsg = "Focus may only be used on a for loop of the form "
			+ "\"for(i = a; i < b; i++)\"";
	
	private NodeFactory nodeFactory;
	private TokenFactory tokenFactory;

	public FocusTransformAnalyzer(NodeFactory nodeFactory,
			TokenFactory tokenFactory) {
		this.nodeFactory = nodeFactory;
		this.tokenFactory = tokenFactory;
		focusData = new FocusAnalysisData();
	}

	public void analyze(SequenceNode<BlockItemNode> root)
			throws SyntaxException {
		BlockItemNode firstItemWithFocus = null;
		for (BlockItemNode child : root) {
			if (process(child) && firstItemWithFocus == null) {
				firstItemWithFocus = child;
			}
		}
		if (firstItemWithFocus != null) {
			firstItemWithFocus.addTransformAnnotation(genFocusVarInserter());
		}
	}

	private boolean process(ASTNode node) throws SyntaxException {
		if (node == null)
			return false;

		FocusTransformNode focusNode = getFocus(node);
		boolean hasFocus = focusNode != null;
		if (hasFocus) {
			processFocus(node, focusNode);
		} else {
			for (ASTNode child : node.children()) {
				if(process(child))
					hasFocus = true;
			}
		}
		return hasFocus;
	}

	private void processFocus(ASTNode node, FocusTransformNode focusNode)
			throws SyntaxException {
		focusNode.setFocusAnalysisData(focusData);
		if (node.nodeKind() != NodeKind.STATEMENT) {
			throw new SyntaxException(wrongNodeErrMsg, node.getSource());
		}
		StatementNode statementNode = (StatementNode) node;
		switch (focusNode.getFocusKind()) {
			case LOOP :
				processFocusLoop(statementNode, (FocusLoopTransformNode) focusNode);
				break;
			case ASSERT :
				processFocusAssert(statementNode, (FocusAssertTransformNode) focusNode);
				break;
		}
	}

	// TODO: Check that statementNode has a loop assigns contract
	private void processFocusLoop(StatementNode statementNode, FocusLoopTransformNode focusNode)
			throws SyntaxException {
		if (statementNode.statementKind() != StatementKind.LOOP)
			throw new SyntaxException("Loop focus must be applied to a loop.",
					statementNode.getSource());
		LoopNode loopNode = (LoopNode) statementNode;
		if (loopNode.getKind() != LoopKind.FOR) {
			throw new SyntaxException(
					"Loop focus must be applied to a for loop.",
					loopNode.getSource());
		}
		ForLoopNode forLoopNode = (ForLoopNode) loopNode;
		ForLoopInitializerNode initNode = forLoopNode.getInitializer();
		if (initNode == null) {
			throw new SyntaxException(
					"Focus must be applied to a loop with an initializer.",
					forLoopNode.getSource());
		}
		String loopVarName;

		if (initNode.nodeKind() == NodeKind.EXPRESSION) {
			if (((ExpressionNode) initNode)
					.expressionKind() != ExpressionKind.OPERATOR) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						initNode.getSource());
			}

			OperatorNode assignNode = (OperatorNode) initNode;
			if (assignNode.getOperator() != Operator.ASSIGN) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						assignNode.getSource());
			}

			if (!(assignNode
					.getArgument(0) instanceof IdentifierExpressionNode)) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						assignNode.getSource());
			}
			loopVarName = ((IdentifierExpressionNode) assignNode.getArgument(0))
					.getIdentifier().name();
		} else {
			DeclarationListNode declsNode = (DeclarationListNode) initNode;
			if (declsNode.numChildren() != 1) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						declsNode.getSource());
			}
			VariableDeclarationNode declNode = declsNode.getSequenceChild(0);
			loopVarName = declNode.getName();
			InitializerNode varInitNode = declNode.getInitializer();
			if (varInitNode == null) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						declNode.getSource());
			}
			if (varInitNode.nodeKind() != NodeKind.EXPRESSION) {
				throw new SyntaxException(wrongLoopFormErrMsg,
						declNode.getSource());
			}
		}

		ExpressionNode condNode = forLoopNode.getCondition();
		if (condNode.expressionKind() != ExpressionKind.OPERATOR) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					condNode.getSource());
		}

		OperatorNode compareNode = (OperatorNode) condNode;
		Operator compOp = compareNode.getOperator();
		if (compOp != Operator.LT && compOp != Operator.LTE) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					condNode.getSource());
		}
		if (!(compareNode.getArgument(0) instanceof IdentifierExpressionNode)) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					condNode.getSource());
		}
		if (!loopVarName
				.equals(((IdentifierExpressionNode) compareNode.getArgument(0))
						.getIdentifier().name())) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					forLoopNode.getSource());
		}
		// TODO: Maybe get rid of incrementer checks since we have to check
		// proper incrementing at runtime anyways?
		if (forLoopNode.getIncrementer() == null) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					forLoopNode.getSource());
		}
		if (forLoopNode.getIncrementer()
				.expressionKind() != ExpressionKind.OPERATOR) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					forLoopNode.getIncrementer().getSource());
		}
		OperatorNode incrNode = (OperatorNode) forLoopNode.getIncrementer();
		if (incrNode.getOperator() != Operator.POSTINCREMENT
				&& incrNode.getOperator() != Operator.PREINCREMENT) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					incrNode.getSource());
		}
		if (incrNode.getArgument(0)
				.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					incrNode.getArgument(0).getSource());
		}
		if (!loopVarName
				.equals(((IdentifierExpressionNode) incrNode.getArgument(0))
						.getIdentifier().name())) {
			throw new SyntaxException(wrongLoopFormErrMsg,
					incrNode.getArgument(0).getSource());
		}
		
		focusData.addFocusTag(focusNode.getFocusTag());
		process(loopNode.getBody());
	}

	private void processFocusAssert(StatementNode statementNode,
			FocusAssertTransformNode focusNode) throws SyntaxException {
		if (statementNode.statementKind() != StatementKind.EXPRESSION)
			throw new SyntaxException(
					"Assert focus must be applied to an expression statement.",
					statementNode.getSource());
		ExpressionStatementNode assertNode = (ExpressionStatementNode) statementNode;
		if (assertNode.getExpression()
				.expressionKind() != ExpressionKind.FUNCTION_CALL) {
			throw new SyntaxException("assert focus must be applied to a function call", assertNode.getSource());
		}
		FunctionCallNode funcCall = (FunctionCallNode) assertNode
				.getExpression();
		ExpressionNode func = funcCall.getFunction();

		if (!(func instanceof IdentifierExpressionNode)) {
			throw new SyntaxException(
					"Focus transform can only be applied to a named function call.",
					func.getSource());
		}
		String name = ((IdentifierExpressionNode) func).getIdentifier().name();

		if (!(name.equals("$assert") || name.equals("assert"))) {
			throw new SyntaxException("assert focus must be applied to an assert", assertNode.getSource());
		}
		ExpressionNode arg = funcCall.getArgument(0);

		if (!(arg instanceof QuantifiedExpressionNode)) {
			throw new SyntaxException(wrongAssertFormErrMsg, arg.getSource());
		}
		QuantifiedExpressionNode quantExpr = (QuantifiedExpressionNode) arg;
		if (quantExpr.quantifier() != Quantifier.FORALL) {
			throw new SyntaxException(wrongAssertFormErrMsg,
					quantExpr.getSource());
		}

		List<String> focusTags = focusNode.getFocusTags();
		SequenceNode<PairNode<SequenceNode<VariableDeclarationNode>, ExpressionNode>> decls = quantExpr
				.boundVariableList();
		int outer = 0, outerLen = decls.numChildren();
		int inner = 0,
				innerLen = decls.getSequenceChild(0).getLeft().numChildren();
		for (String tag : focusTags) {
			if (inner == innerLen) {
				inner = 0;
				outer++;
				if (outer == outerLen) {
					throw new SyntaxException(
							"Focus called on assertion with more tags than universally quantified variables.",
							decls.getSource());
				}
				innerLen = decls.getSequenceChild(outer).getLeft()
						.numChildren();
			}

			ExpressionNode domain = decls.getSequenceChild(outer).getRight();
			if (!(domain == null || domain instanceof RegularRangeNode)) {
				throw new SyntaxException(
						"Domain in a focused assertion must be of the form low .. high",
						domain.getSource());
			}
			focusData.addFocusTag(tag);
			inner++;
		}
	}

	private FocusTransformNode getFocus(ASTNode node) {
		if (node.transformAnnotations().size() > 0) {
			TransformNode currentTransform = node.transformAnnotations().get(0);
			if (currentTransform instanceof FocusTransformNode)
				return (FocusTransformNode) currentTransform;
		}
		return null;
	}
	
	private InsertTransformNode genFocusVarInserter() {
		Formation formation = tokenFactory.newTransformFormation(
				"FocusTransformAnalyzer", "genFocusVarInserter");
		Source inserterSource = tokenFactory.newSource(
				tokenFactory.newCivlcToken(CivlcTokenConstant.ANNOTATION, "focus var inserter",
						formation, TokenVocabulary.DUMMY));
		Set<String> focusTags = focusData.getFocusTags();
		List<BlockItemNode> varDecls = new ArrayList<>(focusTags.size());
		for (String focusTag : focusTags) {
			varDecls.add(genFocusVarNode(focusData.getVarNameFromTag(focusTag)));
			varDecls.add(genFocusVarNode(focusData.getAltVarNameFromTag(focusTag)));
		}
		return nodeFactory.newInsertTransformNode(inserterSource,
				varDecls, false);
	}
	
	private VariableDeclarationNode genFocusVarNode(String varName) {
		Formation formation = tokenFactory.newTransformFormation(
				"FocusTransformAnalyzer", "genFocusVarNode");
		Source varSource = tokenFactory.newSource(
				tokenFactory.newCivlcToken(CivlcTokenConstant.IDENTIFIER,
						varName, formation, TokenVocabulary.DUMMY));
		IdentifierNode varIdentNode = nodeFactory.newIdentifierNode(varSource,
				varName);
		Source declSource = tokenFactory.newSource(tokenFactory.newCivlcToken(
				CivlcTokenConstant.DECLARATION, varName + " declaration",
				formation, TokenVocabulary.DUMMY));
		BasicTypeNode varTypeNode = nodeFactory.newBasicTypeNode(declSource,
				BasicTypeKind.INT);
		varTypeNode.setInputQualified(true);
		return nodeFactory
				.newVariableDeclarationNode(declSource, varIdentNode,
						varTypeNode);
	}
}
