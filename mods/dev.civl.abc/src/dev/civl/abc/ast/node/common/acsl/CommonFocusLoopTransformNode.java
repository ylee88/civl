package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.AssignsOrReadsNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.FocusLoopTransformNode;
import dev.civl.abc.ast.node.IF.acsl.InvariantNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.DeclarationListNode;
import dev.civl.abc.ast.node.IF.statement.ExpressionStatementNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopInitializerNode;
import dev.civl.abc.ast.node.IF.statement.ForLoopNode;
import dev.civl.abc.ast.node.IF.statement.LoopNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.util.IF.Pair;

public class CommonFocusLoopTransformNode extends CommonFocusTransformNode
		implements
			FocusLoopTransformNode {
	private String focusTag;

	private final String FOCUS_TMP_VAR_NAME_PREFIX = "$_focus_tmp_";

	private static int numFocusTmpVars = 0;

	public CommonFocusLoopTransformNode(Source source, NodeFactory nodeFactory,
			TokenFactory tokenFactory, String focusTag, SequenceNode<ExpressionNode> tagWindow,
			SequenceNode<ExpressionNode> memoryList) {
		super(source, nodeFactory, tokenFactory, Arrays.asList(tagWindow, memoryList));
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
		String focusSourceLocation = loopNode.getSource().getLocation(false)
				.replace("\\", "\\\\").replace("\"", "\\\"");
		String focusVarName = focusData.getVarNameFromTag(focusTag);
		String altFocusVarName = focusData.getAltVarNameFromTag(focusTag);
		String loopVarName = ((IdentifierExpressionNode) ((OperatorNode) loopNode
				.getCondition()).getArgument(0)).getIdentifier().name();
		Pair<ExpressionNode, Boolean> initInfo = getInitInfo(loopNode);
		ExpressionNode initExpr = initInfo.left.copy();
		Pair<Operator, ExpressionNode> boundInfo = getBoundInfo(loopNode);
		boolean inclusive = boundInfo.left == Operator.LTE;
		ExpressionNode boundExpr = boundInfo.right.copy();
		ExpressionNode loopInvars = getLoopInvariants(loopNode).copy();
		List<ExpressionNode> assignsList = getLoopAssigns(loopNode);
		ExpressionNode windowUpper = getWindowUpper();

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
		items.add(nodeFactory.newExpressionStatementNode(
				nodeFactory.newOperatorNode(initExpr.getSource(),
						OperatorNode.Operator.ASSIGN,
						identifierExpression(loopVarName),
						identifierExpression(loopStartVarName))));
		
		ExpressionNode loopLowerBoundExpr = nodeFactory.newOperatorNode(
				newSource(thisFuncName, "loopLowerBoundExpr"), Operator.LTE,
				identifierExpression(loopStartVarName),
				identifierExpression(loopVarName));
		ExpressionNode loopVarUpperBound = inclusive
				? nodeFactory.newOperatorNode(
						newSource(thisFuncName, boundExpr.toString() + " + 1"),
						Operator.PLUS, boundExpr.copy(),
						nodeFactory.newIntConstantNode(
								newSource(thisFuncName, "1"), 1))
				: boundExpr.copy();
		ExpressionNode loopUpperBoundExpr = nodeFactory.newOperatorNode(
				newSource(thisFuncName, "loopUpperBoundExpr"), Operator.LTE,
				identifierExpression(loopVarName), loopVarUpperBound);
		ExpressionNode newLoopInvars = andExpr(loopInvars.copy(),
				loopLowerBoundExpr, loopUpperBoundExpr);

		items.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assert", Arrays.asList(
						newLoopInvars,
						stringLiteralExpression(focusSourceLocation
								+ ": Focus loop invariant check failed"
								+ " at start of focused iteration for tag "
								+ focusTag + " = %d\\n"),
						identifierExpression(focusVarName)))));

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

		ExpressionNode ifCondition = genWindowCondition(focusVarName,
				loopStartVarName, oldBoundVarName,
				inclusive);
		List<BlockItemNode> trueBranchItems = new ArrayList<BlockItemNode>();

		String focusMemVarName = getNewTmpVarName();
		trueBranchItems.addAll(
				genFocusedMemVar(focusMemVarName, focusTag, focusVarName));
		trueBranchItems.add(
				memDiff(approxMemName, identifierExpression(focusMemVarName)));
		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$mem_havoc",
						Arrays.asList(identifierExpression(approxMemName)))));
		
		trueBranchItems.addAll(genFocusIterAssignment(loopVarName, focusVarName,
				loopStartVarName));
		
		String focusLoopBoundVarName = null;
		if (windowUpper != null) {
			focusLoopBoundVarName = getNewTmpVarName();
			Source focusLoopBoundVarSource = newSource(thisFuncName,
					focusLoopBoundVarName + " declaration");
			ExpressionNode lastFocusValExpr = nodeFactory.newOperatorNode(
					focusLoopBoundVarSource, Operator.PLUS,
					identifierExpression(focusVarName), windowUpper.copy());
			ExpressionNode lastLoopIterValExpr = inclusive
					? identifierExpression(oldBoundVarName)
					: nodeFactory.newOperatorNode(focusLoopBoundVarSource,
							Operator.MINUS,
							identifierExpression(oldBoundVarName),
							nodeFactory.newIntConstantNode(
									newSource(thisFuncName, "1"), 1));
			
			trueBranchItems.add(nodeFactory
					.newVariableDeclarationNode(focusLoopBoundVarSource,
							identifier(focusLoopBoundVarName),
							nodeFactory.newBasicTypeNode(
									focusLoopBoundVarSource, BasicTypeKind.INT),
							lastFocusValExpr));
			StatementNode focusLoopBoundVarMinAssignment = nodeFactory
					.newExpressionStatementNode(nodeFactory.newOperatorNode(
							focusLoopBoundVarSource, Operator.ASSIGN,
							identifierExpression(focusLoopBoundVarName),
							lastLoopIterValExpr));
			trueBranchItems.add(nodeFactory.newIfNode(focusLoopBoundVarSource,
					nodeFactory.newOperatorNode(focusLoopBoundVarSource,
							Operator.GT, lastFocusValExpr.copy(),
							lastLoopIterValExpr.copy()),
					focusLoopBoundVarMinAssignment));
		}

		trueBranchItems.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assume", Arrays.asList(newLoopInvars.copy()))));
		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$write_set_push")));

		List<BlockItemNode> bodyItems = new ArrayList<BlockItemNode>();
		String oldLoopVarName = getNewTmpVarName();
		Source oldLoopVarSource = newSource(thisFuncName,
				oldLoopVarName + " declaration");
		bodyItems.add(nodeFactory.newVariableDeclarationNode(oldLoopVarSource,
				identifier(oldLoopVarName), nodeFactory
						.newBasicTypeNode(oldLoopVarSource, BasicTypeKind.INT),
				identifierExpression(loopVarName)));
		bodyItems.add(loopNode.getBody().copy());
		ExpressionNode incrementer = loopNode.getIncrementer().copy();
		incrementer.remove();
		bodyItems.add(nodeFactory.newExpressionStatementNode(incrementer));

		ExpressionNode boundIsSameExpr = nodeFactory
				.newOperatorNode(
						newSource(thisFuncName,
								oldBoundVarName + " == "
										+ boundExpr.toString()),
						Operator.EQUALS, identifierExpression(oldBoundVarName),
						boundExpr.copy());
		ExpressionNode loopIncrementAssertion = nodeFactory.newOperatorNode(
				newSource(thisFuncName,
						loopVarName + " == " + oldLoopVarName + " + 1"),
				Operator.EQUALS, identifierExpression(loopVarName),
				nodeFactory.newOperatorNode(
						newSource(thisFuncName, oldLoopVarName + " + 1"),
						Operator.PLUS, identifierExpression(oldLoopVarName),
						nodeFactory.newIntConstantNode(
								newSource(thisFuncName, "1"), 1)));
		bodyItems.add(nodeFactory.newExpressionStatementNode(functionCall(
				"$assert", Arrays.asList(
						andExpr(newLoopInvars.copy(), loopIncrementAssertion,
								boundIsSameExpr),
						stringLiteralExpression(focusSourceLocation
								+ ": Focus loop increment check failed"
								+ " for tag " + focusTag + " = %d\\n"),
						identifierExpression(focusVarName)))));
		
		StatementNode focusWorkStatement = nodeFactory
				.newCompoundStatementNode(loopNode.getSource(), bodyItems);
		if (windowUpper != null) {
			Source focusLoopCondSource = newSource(thisFuncName,
					loopVarName + " <= " + focusLoopBoundVarName);
			ExpressionNode focusLoopCondExpr = nodeFactory.newOperatorNode(
					focusLoopCondSource, Operator.LTE,
					identifierExpression(loopVarName),
					identifierExpression(focusLoopBoundVarName));
			focusWorkStatement = nodeFactory.newWhileLoopNode(
					newSource(thisFuncName, "Focus loop over window"),
					focusLoopCondExpr, focusWorkStatement, null);
		}
		trueBranchItems.add(focusWorkStatement);

		String writeSetVarName = getNewTmpVarName();
		Source writeSetVarSource = newSource(thisFuncName,
				"$mem " + writeSetVarName);
		trueBranchItems.add(nodeFactory.newVariableDeclarationNode(
				writeSetVarSource, identifier(writeSetVarName),
				nodeFactory.newMemTypeNode(writeSetVarSource),
				functionCall("$write_set_pop")));

		trueBranchItems.add(nodeFactory
				.newExpressionStatementNode(functionCall("$assume_push",
						Arrays.asList(genAltFocusAssumption(focusVarName,
								altFocusVarName, loopStartVarName,
								oldBoundVarName, inclusive)))));

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
		trueBranchItems.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assert", Arrays.asList(
						functionCall("$mem_contains",
								Arrays.asList(identifierExpression(altApproxMemName),
										identifierExpression(writeSetVarName))),
						stringLiteralExpression(focusSourceLocation
								+ ": Focus $mem-set check failed"
								+ " for tag " + focusTag + " = %d.\\n"
								+ "Expected write set to be a subset"
								+ " of %s \\\\ %s,\\n"
								+ "  where %d != %d and %d in [%d, %d"
								+ (inclusive ? "]" : ")") + "\\n"
								+ "Write set: %s\\n"),
						identifierExpression(focusVarName),
						identifierExpression(assignsMemName),
						identifierExpression(altMemVarName),
						identifierExpression(altFocusVarName),
						identifierExpression(focusVarName),
						identifierExpression(altFocusVarName),
						identifierExpression(loopStartVarName),
						identifierExpression(oldBoundVarName),
						identifierExpression(writeSetVarName)))));
		
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
						Operator.EQUALS, identifierExpression(loopVarName),
						inclusive
								? addIntExpr(
										identifierExpression(oldBoundVarName),
										1)
								: identifierExpression(oldBoundVarName))))));
		items.add(nodeFactory.newExpressionStatementNode(
				functionCall("$assume", Arrays.asList(loopInvars.copy()))));

		return Arrays.asList(nodeFactory.newCompoundStatementNode(
				newSource(thisFuncName, "focus block"), items));
	}

	private ExpressionNode genWindowCondition(String focusVarName,
			String loopStartVarName, String oldBoundVarName,
			boolean inclusive) {
		String thisFuncName = "genWindowCondition";
		Source windowCondSource = newSource(thisFuncName, focusVarName +" window condition");
		ExpressionNode lowerExpr = identifierExpression(focusVarName);
		ExpressionNode windowLower = getWindowLower();
		if (windowLower != null)
			lowerExpr = nodeFactory.newOperatorNode(windowCondSource,
					Operator.PLUS, lowerExpr, windowLower.copy());
		ExpressionNode result = genExprInBoundsCondition(lowerExpr,
				identifierExpression(loopStartVarName),
				identifierExpression(oldBoundVarName), inclusive);
		ExpressionNode windowUpper = getWindowUpper();
		if (windowUpper != null) {
			ExpressionNode upperExpr = nodeFactory.newOperatorNode(
					windowCondSource, Operator.PLUS,
					identifierExpression(focusVarName), windowUpper.copy());
			result = nodeFactory.newOperatorNode(windowCondSource, Operator.LOR,
					result,
					genExprInBoundsCondition(upperExpr,
							identifierExpression(loopStartVarName),
							identifierExpression(oldBoundVarName), inclusive));
		}
		return result;
	}
	
	private ExpressionNode genExprInBoundsCondition(ExpressionNode expr, ExpressionNode lowerBound, ExpressionNode upperBound, boolean inclusive) {
		String thisFuncName = "genExprInBoundsCondition";
		Source condSource = newSource(thisFuncName,
				expr.toString() + "focus bound");
		OperatorNode.Operator upperOp = inclusive
				? OperatorNode.Operator.LTE
				: OperatorNode.Operator.LT;
		return nodeFactory.newOperatorNode(condSource,
				OperatorNode.Operator.LAND,
				nodeFactory.newOperatorNode(condSource,
						OperatorNode.Operator.LTE, lowerBound, expr),
				nodeFactory.newOperatorNode(condSource, upperOp, expr.copy(),
						upperBound));
	}
	
	private List<BlockItemNode> genFocusIterAssignment(String loopVarName, String focusVarName,
			String loopStartVarName) {
		String thisFuncName = "genFocusIterAssignment";
		Source thisSource = newSource(thisFuncName, focusVarName+" iteration assignment");
		List<BlockItemNode> items = new LinkedList<>();
		ExpressionNode focusValueExpr = identifierExpression(focusVarName);
		ExpressionNode windowLower = getWindowLower();
		if (windowLower != null)
			focusValueExpr = nodeFactory.newOperatorNode(thisSource, Operator.PLUS,
					focusValueExpr, windowLower.copy());
		items.add(nodeFactory
				.newExpressionStatementNode(nodeFactory.newOperatorNode(
						newSource(thisFuncName,
								loopVarName + " = " + focusVarName),
						Operator.ASSIGN, identifierExpression(loopVarName),
						focusValueExpr)));

		StatementNode trueBranchItem = nodeFactory.newExpressionStatementNode(
				nodeFactory.newOperatorNode(thisSource, Operator.ASSIGN,
						identifierExpression(loopVarName),
						identifierExpression(loopStartVarName)));
		items.add(nodeFactory.newIfNode(thisSource,
				nodeFactory.newOperatorNode(thisSource, Operator.LT,
						focusValueExpr.copy(),
						identifierExpression(loopStartVarName)),
				trueBranchItem));
		return items;
	}

	private ExpressionNode genAltFocusAssumption(String focusVarName, String altFocusVarName,
			String loopStartVarName, String oldBoundVarName,
			boolean inclusive) {
		String thisFuncName = "genAltFocusAssumption";
		Source altFocusAssumeSource = newSource(thisFuncName, altFocusVarName + " condition");
		Operator upperBoundOp = inclusive ? Operator.LTE : Operator.LT;
		andExpr(nodeFactory.newOperatorNode(altFocusAssumeSource,
				OperatorNode.Operator.LTE,
				identifierExpression(loopStartVarName),
				identifierExpression(altFocusVarName)),
				nodeFactory.newOperatorNode(altFocusAssumeSource,
						upperBoundOp, identifierExpression(altFocusVarName),
						identifierExpression(oldBoundVarName)),
				nodeFactory.newOperatorNode(altFocusAssumeSource,
						OperatorNode.Operator.NEQ,
						identifierExpression(focusVarName),
						identifierExpression(altFocusVarName)));
		ExpressionNode windowCondition = genWindowCondition(altFocusVarName, loopStartVarName, oldBoundVarName, inclusive);
		ExpressionNode windowLower = getWindowLower();
		ExpressionNode windowUpper = getWindowUpper();
		ExpressionNode disjointCondition = null;
		if (windowUpper == null) {
			disjointCondition = nodeFactory.newOperatorNode(altFocusAssumeSource,
					OperatorNode.Operator.NEQ,
					identifierExpression(focusVarName),
					identifierExpression(altFocusVarName));
		} else {
			ExpressionNode altLowerExpr = nodeFactory.newOperatorNode(
					altFocusAssumeSource, Operator.PLUS,
					identifierExpression(altFocusVarName), windowLower.copy()),
					altUpperExpr = nodeFactory.newOperatorNode(altFocusAssumeSource,
							Operator.PLUS,
							identifierExpression(altFocusVarName),
							windowUpper.copy()),
					focusLowerExpr = nodeFactory.newOperatorNode(
							altFocusAssumeSource, Operator.PLUS,
							identifierExpression(focusVarName),
							windowLower.copy()),
					focusUpperExpr = nodeFactory.newOperatorNode(
							altFocusAssumeSource, Operator.PLUS,
							identifierExpression(focusVarName),
							windowUpper.copy());
			disjointCondition = nodeFactory.newOperatorNode(
					altFocusAssumeSource, Operator.LOR,
					nodeFactory.newOperatorNode(altFocusAssumeSource,
							Operator.LT, focusUpperExpr, altLowerExpr),
					nodeFactory.newOperatorNode(altFocusAssumeSource,
							Operator.LT, altUpperExpr, focusLowerExpr));
		}
		return andExpr(windowCondition, disjointCondition);
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
	
	private ExpressionNode getWindowLower() {
		SequenceNode<ExpressionNode> focusWindow = getFocusWindow();
		return focusWindow == null ? null : focusWindow.getSequenceChild(0);
	}
	
	private ExpressionNode getWindowUpper() {
		SequenceNode<ExpressionNode> focusWindow = getFocusWindow();
		return focusWindow == null ? null : focusWindow.getSequenceChild(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<ExpressionNode> getFocusWindow() {
		return (SequenceNode<ExpressionNode>) child(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SequenceNode<ExpressionNode> getMemoryList() {
		return (SequenceNode<ExpressionNode>) this.child(1);
	}

	@Override
	public FocusKind getFocusKind() {
		return FocusKind.LOOP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContractNode copy() {
		return new CommonFocusLoopTransformNode(this.getSource(), nodeFactory,
				tokenFactory, focusTag, (SequenceNode<ExpressionNode>) child(0).copy(),
				(SequenceNode<ExpressionNode>) child(1).copy());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("focusLoop");
	}

}
