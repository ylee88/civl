package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity.EntityKind;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ArrowNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.CastNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ConstantNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ConstantNode.ConstantKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.DotNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.FunctionCallNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence.AssignmentIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

public class CommonFlowInsensePointsToAnalyzer
		implements
			FlowInsensePointsToAnalyzer {

	private SymbolicUniverse universe;

	public CommonFlowInsensePointsToAnalyzer(SymbolicUniverse universe) {
		this.universe = universe;
	}

	@Override
	public PointsToGraph getGraph(AssignmentSequence stmts) {
		PointsToGraph graph = new CommonPointsToGraph(universe);

		for (AssignmentIF assign : stmts.getAll())
			processAssignmentIF(assign, graph);
		return graph;
	}

	private void processAssignmentIF(AssignmentIF assign, PointsToGraph graph) {
		if (assign.lhs() != null) {
			ASTNode lhs = assign.lhs();

			if (lhs.nodeKind() == NodeKind.VARIABLE_DECLARATION)
				processInitialization((VariableDeclarationNode) lhs, graph);
			else {
				ExpressionNode exprLhs = (ExpressionNode) lhs;

				if (exprLhs.getType().kind() != TypeKind.POINTER)
					return;

				List<SymbolicExpression> lhses = processExpression(exprLhs,
						graph);
				List<SymbolicExpression> rhses = processExpression(assign.rhs(),
						graph);

				processAssign(lhses, rhses, graph);
			}
		}
	}

	private void processInitialization(VariableDeclarationNode varDecl,
			PointsToGraph graph) {
		Variable var = varDecl.getEntity();
		InitializerNode initializer = varDecl.getInitializer();

		assert initializer != null
				&& initializer.nodeKind() == NodeKind.EXPRESSION;

		List<SymbolicExpression> lhses = new LinkedList<>();
		List<SymbolicExpression> rhses = processExpression(
				(ExpressionNode) initializer, graph);

		lhses.add(graph.addVariable(var));
		processAssign(lhses, rhses, graph);
	}

	private void processAssign(List<SymbolicExpression> tauLhs,
			List<SymbolicExpression> tauRhs, PointsToGraph graph) {
		for (SymbolicExpression lhs : tauLhs) {
			for (SymbolicExpression rhs : tauRhs) {
				graph.addSubsetRelation(graph.getPointsTo(rhs),
						graph.getPointsTo(lhs));
			}
		}
	}

	private List<SymbolicExpression> processExpression(ExpressionNode expr,
			PointsToGraph graph) {
		ExpressionKind kind = expr.expressionKind();
		List<SymbolicExpression> results = new LinkedList<>();

		switch (kind) {
			case ARROW : {
				// "e->id : T", if "*e : T"
				List<SymbolicExpression> tmps;

				results = new LinkedList<>();
				tmps = processExpression(
						((ArrowNode) expr).getStructurePointer(), graph);
				for (SymbolicExpression tmp : tmps)
					results.add(graph.getPointsTo(tmp));
				return results;
			}
			case CAST :
				return processCast((CastNode) expr, graph);
			case CONSTANT :
				return processConstant((ConstantNode) expr, graph);
			case DOT :
				// "e.id : T", if "e : T"
				return processExpression(((DotNode) expr).getStructure(),
						graph);
			case FUNCTION_CALL : {
				FunctionCallNode callNode = (FunctionCallNode) expr;

				if (isAllocation(callNode)) {
					SymbolicExpression tau = graph.addAllocation(callNode);

					results.add(tau);
					return results;
				}
			}
			case SPAWN :
				throw new CIVLUnimplementedFeatureException(
						"points-to analysis for function call or spawn: "
								+ expr);
			case IDENTIFIER_EXPRESSION : {
				Entity var = ((IdentifierExpressionNode) expr).getIdentifier()
						.getEntity();

				if (var != null && var.getEntityKind() == EntityKind.VARIABLE) {
					results.add(graph.addVariable((Variable) var));
					return results;
				}
				break;
			}
			case OPERATOR :
				return processOperator((OperatorNode) expr, graph);
			case REGULAR_RANGE :
				// no need to analyze, skip
				break;
			case ARRAY_LAMBDA :
				// TODO:lambda expression
				throw new CIVLUnimplementedFeatureException(
						"points-to analysis for array lambda expression: "
								+ expr);
			default :
				throw new CIVLInternalException(
						"Unexpected expression kind for poitns-to analysis "
								+ expr.prettyRepresentation(),
						expr.getSource());
		}
		return results;
	}

	private List<SymbolicExpression> processCast(CastNode expr,
			PointsToGraph graph) {
		if (expr.getType().kind() == TypeKind.POINTER)
			if (expr.getArgument().getType().kind() != TypeKind.POINTER) {
				List<SymbolicExpression> results = new LinkedList<>();

				results.add(graph.getPointsToFull());
				return results;
			}
		return processExpression(expr.getArgument(), graph);
	}

	private List<SymbolicExpression> processConstant(ConstantNode expr,
			PointsToGraph graph) {
		List<SymbolicExpression> results = new LinkedList<>();

		if (expr.constantKind() == ConstantKind.STRING) {
			SymbolicExpression tau = graph.addAllocation(expr);

			tau = graph.makePointsTo(tau);
			results.add(tau);
		}
		return results;
	}

	private List<SymbolicExpression> processOperator(OperatorNode expr,
			PointsToGraph graph) {
		Operator op = expr.getOperator();
		List<SymbolicExpression> results = new LinkedList<>();

		switch (op) {
			case ADDRESSOF : {
				List<SymbolicExpression> tmps = processExpression(
						expr.getArgument(0), graph);

				for (SymbolicExpression tmp : tmps)
					results.add(graph.makePointsTo(tmp));
				break;
			}
			case COMMA :
				return processExpression(expr.getArgument(1), graph);
			case DEREFERENCE : {
				List<SymbolicExpression> tmps = processExpression(
						expr.getArgument(0), graph);

				for (SymbolicExpression tmp : tmps)
					results.add(graph.getPointsTo(tmp));
				break;
			}
			case SUBSCRIPT :
				return processExpression(expr.getArgument(0), graph);
			default : {
				int numArgs = expr.getNumberOfArguments();

				for (int i = 0; i < numArgs; i++) {
					ExpressionNode arg = expr.getArgument(i);

					if (arg.getType().kind() == TypeKind.POINTER)
						results.addAll(processExpression(arg, graph));
				}
			}
		}
		return results;
	}

	private boolean isAllocation(FunctionCallNode call) {
		ExpressionNode func = call.getFunction();

		if (func.expressionKind() == ExpressionKind.IDENTIFIER_EXPRESSION)
			return ((IdentifierExpressionNode) func).getIdentifier().name()
					.equals("$malloc");
		return false;
	}
}
