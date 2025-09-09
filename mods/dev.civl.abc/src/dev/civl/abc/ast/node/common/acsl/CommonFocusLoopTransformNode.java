package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.analysis.entity.FocusAnalysisData;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.PairNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.AssignsOrReadsNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.FocusLoopTransformNode;
import dev.civl.abc.ast.node.IF.acsl.InvariantNode;
import dev.civl.abc.ast.node.IF.acsl.TransformNode;
import dev.civl.abc.ast.node.IF.acsl.FocusTransformNode.FocusKind;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.FunctionCallNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.QuantifiedExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.expression.QuantifiedExpressionNode.Quantifier;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.CompoundStatementNode;
import dev.civl.abc.ast.node.IF.statement.DeclarationListNode;
import dev.civl.abc.ast.node.IF.statement.ExpressionStatementNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopInitializerNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopNode;
import dev.civl.abc.ast.node.IF.statement.LoopNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.front.IF.CivlcTokenConstant;
import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.util.IF.Pair;
import dev.civl.abc.token.IF.CivlcToken.TokenVocabulary;

public class CommonFocusLoopTransformNode extends CommonFocusTransformNode
		implements
			FocusLoopTransformNode {
	private String focusTag;

	private final String FOCUS_TMP_VAR_NAME_PREFIX = "$_focus_tmp_";

	private static int numFocusTmpVars = 0;

	public CommonFocusLoopTransformNode(Source source, NodeFactory nodeFactory,
			TokenFactory tokenFactory, String focusTag,
			SequenceNode<ExpressionNode> memoryList) {
		super(source, nodeFactory, tokenFactory, (ASTNode) memoryList);
		this.focusTag = focusTag;
	}

	@Override
	public List<BlockItemNode> transform(List<BlockItemNode> blockItems)
			throws SyntaxException {
		if (blockItems == null || blockItems.size() != 1) {
			throw new SyntaxException(
					"Focus can only be applied to exactly one BlockItemNode.",
					getSource());
		}

		BlockItemNode root = blockItems.get(0);
		if (!(root instanceof ForLoopNode)) {
			throw new SyntaxException(
					"Loop focus must be applied to a ForLoopNode.",
					getSource());
		}

		ForLoopNode loopNode = (ForLoopNode) root;
		String thisFuncName = "transform";
		String focusVarName = focusData.getVarNameFromTag(focusTag);
		String altFocusVarName = focusData.getAltVarNameFromTag(focusTag);
		String loopVarName = ((IdentifierExpressionNode) ((OperatorNode) loopNode
				.getCondition()).getArgument(0)).getIdentifier().name();
		Pair<ExpressionNode, Boolean> initInfo = getInitInfo(loopNode);
		ExpressionNode initExpr = initInfo.left.copy();
		Pair<Operator, ExpressionNode> boundInfo = getBoundInfo(loopNode);
		ExpressionNode boundExpr = boundInfo.right.copy();
		ExpressionNode loopInvars = getLoopInvariants(loopNode).copy();
		List<ExpressionNode> assignsList = getLoopAssigns(loopNode);

		initExpr.remove();
		boundExpr.remove();
		loopInvars.remove();

		List<BlockItemNode> items = new ArrayList<BlockItemNode>();

		if (!initInfo.right) {
			items.add(nodeFactory.newVariableDeclarationNode(
					newSource(thisFuncName, "int " + loopVarName + ";"),
					identifier(loopVarName),
					nodeFactory.newBasicTypeNode(newSource(thisFuncName, "int"),
							BasicTypeKind.INT)));
		}

		String loopStartVarName = getNewTmpVarName();
		items.add(nodeFactory.newVariableDeclarationNode(
				newSource(thisFuncName,
						"int " + loopStartVarName + " = "
								+ initExpr.toString()),
				identifier(loopStartVarName),
				nodeFactory.newBasicTypeNode(newSource(thisFuncName, "int"),
						BasicTypeKind.INT),
				initExpr.copy()));

		String oldBoundVarName = getNewTmpVarName();
		items.add(nodeFactory.newVariableDeclarationNode(
				newSource(thisFuncName,
						"int " + oldBoundVarName + " = "
								+ boundExpr.toString()),
				identifier(oldBoundVarName),
				nodeFactory.newBasicTypeNode(newSource(thisFuncName, "int"),
						BasicTypeKind.INT),
				boundExpr.copy()));

		ExpressionNode loopLowerBoundExpr = nodeFactory.newOperatorNode(
				newSource(thisFuncName, "loopLowerBoundExpr"), Operator.LTE,
				identifierExpression(loopStartVarName),
				identifierExpression(loopVarName));
		ExpressionNode loopVarUpperBound = boundInfo.left == Operator.LT
				? boundExpr.copy()
				: nodeFactory.newOperatorNode(
						newSource(thisFuncName, boundExpr.toString() + " + 1"),
						Operator.PLUS, boundExpr.copy(),
						nodeFactory.newIntConstantNode(
								newSource(thisFuncName, "1"), 1));
		ExpressionNode loopUpperBoundExpr = nodeFactory.newOperatorNode(
				newSource(thisFuncName, "loopUpperBoundExpr"), Operator.LTE,
				identifierExpression(loopVarName), loopVarUpperBound);
		ExpressionNode newLoopInvars = andExpr(loopInvars.copy(),
				loopLowerBoundExpr, loopUpperBoundExpr);

		items.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assert", Arrays.asList(newLoopInvars))));

		String assignsMemName = getNewTmpVarName();
		Source assignsMemSource = newSource(thisFuncName,
				"$mem " + assignsMemName + " = &" + loopVarName);
		items.add(nodeFactory.newVariableDeclarationNode(assignsMemSource,
				identifier(assignsMemName),
				nodeFactory.newMemTypeNode(assignsMemSource),
				nodeFactory.newOperatorNode(assignsMemSource,
						OperatorNode.Operator.ADDRESSOF,
						identifierExpression(loopVarName))));
		for (ExpressionNode memAssignExpr : assignsList) {
			items.add(memUnion(assignsMemName,
					nodeFactory.newOperatorNode(memAssignExpr.getSource(),
							OperatorNode.Operator.ADDRESSOF,
							memAssignExpr.copy())));
		}
		
		String approxMemName = getNewTmpVarName();
		Source approxMemSource = newSource(thisFuncName,
				"$mem " + approxMemName + " = " + assignsMemName);
		items.add(nodeFactory.newVariableDeclarationNode(approxMemSource,
				identifier(approxMemName),
				nodeFactory.newMemTypeNode(approxMemSource),
				identifierExpression(assignsMemName)));

		ExpressionNode ifCondition = andExpr(
				nodeFactory.newOperatorNode(
						newSource(thisFuncName, "if lower condition"),
						Operator.LTE, identifierExpression(loopStartVarName),
						identifierExpression(focusVarName)),
				nodeFactory.newOperatorNode(
						newSource(thisFuncName, "if upper condition"),
						boundInfo.left, identifierExpression(focusVarName),
						identifierExpression(oldBoundVarName)));
		List<BlockItemNode> trueBranchItems = new ArrayList<BlockItemNode>();

		String focusMemVarName = getNewTmpVarName();
		trueBranchItems.addAll(
				genFocusedMemVar(focusMemVarName, focusTag, focusVarName));
		trueBranchItems.add(
				memDiff(approxMemName, identifierExpression(focusMemVarName)));
		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$mem_havoc",
						Arrays.asList(identifierExpression(approxMemName)))));
		trueBranchItems
				.add(nodeFactory
						.newExpressionStatementNode(
								nodeFactory.newOperatorNode(
										newSource(thisFuncName,
												loopVarName + " = "
														+ focusVarName),
										Operator.ASSIGN,
										identifierExpression(loopVarName),
										identifierExpression(focusVarName))));

		trueBranchItems.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assume", Arrays.asList(newLoopInvars.copy()))));
		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$write_set_push")));

		List<BlockItemNode> bodyItems = new ArrayList<BlockItemNode>();
		bodyItems.add(loopNode.getBody().copy());
		ExpressionNode incrementer = loopNode.getIncrementer().copy();
		incrementer.remove();
		bodyItems.add(nodeFactory.newExpressionStatementNode(incrementer));

		trueBranchItems.add(nodeFactory
				.newCompoundStatementNode(loopNode.getSource(), bodyItems));

		ExpressionNode boundIsSameExpr = nodeFactory
				.newOperatorNode(
						newSource(thisFuncName,
								oldBoundVarName + " == "
										+ boundExpr.toString()),
						Operator.EQUALS, identifierExpression(oldBoundVarName),
						boundExpr.copy());
		ExpressionNode loopIncrementAssertion = nodeFactory.newOperatorNode(
				newSource(thisFuncName,
						loopVarName + " == " + focusVarName + " + 1"),
				Operator.EQUALS, identifierExpression(loopVarName),
				nodeFactory.newOperatorNode(
						newSource(thisFuncName, focusVarName + " + 1"),
						Operator.PLUS, identifierExpression(focusVarName),
						nodeFactory.newIntConstantNode(
								newSource(thisFuncName, "1"), 1)));
		trueBranchItems.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assert", Arrays.asList(andExpr(newLoopInvars.copy(),
						loopIncrementAssertion, boundIsSameExpr)))));

		String writeSetVarName = getNewTmpVarName();
		Source writeSetVarSource = newSource(thisFuncName,
				"$mem " + writeSetVarName);
		trueBranchItems.add(nodeFactory.newVariableDeclarationNode(
				writeSetVarSource, identifier(writeSetVarName),
				nodeFactory.newMemTypeNode(writeSetVarSource),
				functionCall("$write_set_pop")));

		Source altFocusAssumeSource = newSource(thisFuncName, "$assume_push");
		trueBranchItems.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assume_push",
				Arrays.asList(andExpr(
						nodeFactory.newOperatorNode(altFocusAssumeSource,
								OperatorNode.Operator.LTE,
								identifierExpression(loopStartVarName),
								identifierExpression(altFocusVarName)),
						nodeFactory.newOperatorNode(altFocusAssumeSource,
								boundInfo.left,
								identifierExpression(altFocusVarName),
								identifierExpression(oldBoundVarName)),
						nodeFactory.newOperatorNode(altFocusAssumeSource,
								OperatorNode.Operator.NEQ,
								identifierExpression(focusVarName),
								identifierExpression(altFocusVarName)))))));
		String altMemVarName = getNewTmpVarName();
		trueBranchItems.addAll(
				genFocusedMemVar(altMemVarName, focusTag, altFocusVarName));
		String altApproxMemName = getNewTmpVarName();
		Source altApproxMemSource = newSource(thisFuncName,
				"$mem " + altApproxMemName);
		trueBranchItems.add(nodeFactory.newVariableDeclarationNode(
				altApproxMemSource, identifier(altApproxMemName),
				nodeFactory.newMemTypeNode(altApproxMemSource),
				functionCall("$mem_diff",
						Arrays.asList(identifierExpression(assignsMemName),
								identifierExpression(altMemVarName)))));
		trueBranchItems.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assert",
				Arrays.asList(functionCall("$mem_contains",
						Arrays.asList(identifierExpression(altApproxMemName),
								identifierExpression(writeSetVarName)))))));
		
		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$assume_pop")));

		items.add(nodeFactory.newIfNode(ifCondition.getSource(), ifCondition,
				nodeFactory.newCompoundStatementNode(
						newSource(thisFuncName, "focus if true branch"),
						trueBranchItems)));

		items.add(nodeFactory
				.newExpressionStatementNode(functionCall("$mem_havoc",
						Arrays.asList(identifierExpression(approxMemName)))));
		items.add(nodeFactory.newExpressionStatementNode(functionCall("$assume",
				Arrays.asList(nodeFactory.newOperatorNode(
						newSource(thisFuncName,
								loopVarName + " == " + oldBoundVarName),
						OperatorNode.Operator.EQUALS,
						identifierExpression(loopVarName),
						identifierExpression(oldBoundVarName))))));
		items.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assume", Arrays.asList(loopInvars.copy()))));

		return Arrays.asList(nodeFactory.newCompoundStatementNode(
				newSource(thisFuncName, "focus block"), items));
	}

	private List<BlockItemNode> genFocusedMemVar(String memVarName,
			String tagName, String replacementName) {
		String thisFuncName = "genFocusedMemVar";
		List<BlockItemNode> items = new LinkedList<>();
		Source memVarSource = newSource(thisFuncName,
				"$mem " + memVarName + " = $mem_empty()");
		items.add(nodeFactory.newVariableDeclarationNode(memVarSource,
				identifier(memVarName),
				nodeFactory.newMemTypeNode(memVarSource),
				functionCall("$mem_empty")));
		SequenceNode<ExpressionNode> memList = getMemoryList();
		for (ExpressionNode memExpr : memList) {
			items.add(memUnion(memVarName,
					nodeFactory.newOperatorNode(memExpr.getSource(),
							OperatorNode.Operator.ADDRESSOF,
							replaceIdent(memExpr, tagName, replacementName))));
		}

		return items;
	}

	private ExpressionStatementNode memUnion(String memName,
			ExpressionNode memExpr) {
		Source memUnionSource = newSource("memUnion", memName + " = $mem_union("
				+ memName + ", " + memExpr.toString() + ")");
		return nodeFactory.newExpressionStatementNode(nodeFactory
				.newOperatorNode(memUnionSource, OperatorNode.Operator.ASSIGN,
						identifierExpression(memName),
						functionCall("$mem_union", Arrays.asList(
								identifierExpression(memName), memExpr))));
	}
	private ExpressionStatementNode memDiff(String memName,
			ExpressionNode memExpr) {
		Source memDiffSource = newSource("memDiff", memName + " = $mem_diff("
				+ memName + ", " + memExpr.toString() + ")");
		return nodeFactory.newExpressionStatementNode(nodeFactory
				.newOperatorNode(memDiffSource, OperatorNode.Operator.ASSIGN,
						identifierExpression(memName),
						functionCall("$mem_diff", Arrays.asList(
								identifierExpression(memName), memExpr))));
	}

	private ExpressionNode andExpr(ExpressionNode... args) {
		Source source = newSource("andExpr", "&&");
		if (args.length == 0)
			return nodeFactory.newBooleanConstantNode(source, true);

		ExpressionNode result = args[0];
		for (int i = 1; i < args.length; i++) {
			result = nodeFactory.newOperatorNode(source,
					OperatorNode.Operator.LAND, result, args[i]);
		}
		return result;
	}

	private ExpressionNode replaceIdent(ExpressionNode expr, String origName,
			String newName) {
		ExpressionNode result = expr.copy();
		replaceIdentHelper(result, origName, newName);
		return result;
	}

	private void replaceIdentHelper(ASTNode node, String origName,
			String newName) {
		if (node == null)
			return;

		if (node.nodeKind() == ASTNode.NodeKind.EXPRESSION) {
			ExpressionNode expr = (ExpressionNode) node;
			if (expr.expressionKind() == ExpressionNode.ExpressionKind.IDENTIFIER_EXPRESSION) {
				IdentifierNode ident = ((IdentifierExpressionNode) expr)
						.getIdentifier();
				if (ident.name().equals(origName))
					ident.setName(newName);
				return;
			}
		}
		for (ASTNode child : node.children()) {
			replaceIdentHelper(child, origName, newName);
		}
	}

	/**
	 * @param loopNode
	 * @return the initializer expression of the loop as well as a boolean which
	 *         is true iff the loop variable was already declared outside of the
	 *         loop.
	 */
	private Pair<ExpressionNode, Boolean> getInitInfo(ForLoopNode loopNode) {
		ForLoopInitializerNode initNode = loopNode.getInitializer();
		if (initNode.nodeKind() == NodeKind.EXPRESSION) {
			return new Pair<ExpressionNode, Boolean>(
					((OperatorNode) initNode).getArgument(1), true);
		} else {
			return new Pair<ExpressionNode, Boolean>(
					(ExpressionNode) ((DeclarationListNode) initNode)
							.getSequenceChild(0).getInitializer(),
					false);
		}
	}

	private Pair<Operator, ExpressionNode> getBoundInfo(ForLoopNode loopNode) {
		OperatorNode comp = (OperatorNode) loopNode.getCondition();
		return new Pair<Operator, ExpressionNode>(comp.getOperator(),
				comp.getArgument(1));
	}

	/*
	 * TODO: This is basically copied from LoopContractBlock in CIVL. How to
	 * remove code duplication without creating extra dependency on CIVL?
	 */
	private ExpressionNode getLoopInvariants(LoopNode loop) {
		if (loop.loopContracts() == null) {
			return nodeFactory.newBooleanConstantNode(loop.getSource(), true);
		}
		List<ExpressionNode> invariants = new LinkedList<>();
		for (ContractNode contract : loop.loopContracts()) {
			if (contract.contractKind() == ContractKind.INVARIANT) {
				invariants.add(((InvariantNode) contract).getExpression());
			}
		}

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

	private List<ExpressionNode> getLoopAssigns(LoopNode loop) {
		List<ExpressionNode> assignsList = new LinkedList<ExpressionNode>();
		if (loop.loopContracts() != null) {
			for (ContractNode contract : loop.loopContracts()) {
				if (contract.contractKind() == ContractKind.ASSIGNS_READS) {
					for (ExpressionNode memExpr : ((AssignsOrReadsNode) contract)
							.getMemoryList()) {
						assignsList.add(memExpr);
					}
				}
			}
		}
		return assignsList;
	}

	private String getNewTmpVarName() {
		return FOCUS_TMP_VAR_NAME_PREFIX + numFocusTmpVars++;
	}

	@Override
	public String getFocusTag() {
		return focusTag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<ExpressionNode> getMemoryList() {
		return (SequenceNode<ExpressionNode>) this.child(0);
	}

	@Override
	public FocusKind getFocusKind() {
		return FocusKind.LOOP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContractNode copy() {
		return new CommonFocusLoopTransformNode(this.getSource(), nodeFactory,
				tokenFactory, focusTag,
				(SequenceNode<ExpressionNode>) child(0).copy());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("focusLoop");
	}

}
