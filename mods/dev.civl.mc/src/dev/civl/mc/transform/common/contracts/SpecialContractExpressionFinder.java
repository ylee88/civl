package dev.civl.mc.transform.common.contracts;

import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.ASTNode.NodeKind;
import dev.civl.abc.ast.node.IF.acsl.MPIContractExpressionNode;
import dev.civl.abc.ast.node.IF.acsl.MPIContractExpressionNode.MPIContractExpressionKind;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;

/**
 * <p>
 * This class scans an expression in a contract and returns an instance of
 * {@link SpecialContractHub}, which is a collection of all references to
 * special constructs in the given expression. A special construct is a contract
 * construct that needs special handling during code transformation.
 * </p>
 * <p>
 * This class only contains static methods hence no runtime instance of this
 * class is needed.
 * </p>
 * 
 * @author ziqingluo
 *
 */
public class SpecialContractExpressionFinder {

	/**
	 * This class is a collection of all references to special constructs in
	 * some contract expressions
	 * 
	 * @author ziqingluo
	 */
	static class SpecialContractHub {

		List<ExpressionNode> nonderefPointers;

		List<ExpressionNode> remoteExpressions;

		List<ExpressionNode> acslOldExpressions;

		List<ExpressionNode> mpiDatatypes;

		List<ExpressionNode> mpiExtents;

		List<ExpressionNode> acslValidExpressions;

		List<ExpressionNode> mpiValidExpressions;

		List<ExpressionNode> acslResults;

		SpecialContractHub() {
			nonderefPointers = new LinkedList<>();
			remoteExpressions = new LinkedList<>();
			acslOldExpressions = new LinkedList<>();
			mpiDatatypes = new LinkedList<>();
			mpiExtents = new LinkedList<>();
			acslValidExpressions = new LinkedList<>();
			mpiValidExpressions = new LinkedList<>();
			acslResults = new LinkedList<>();
		}
	}

	/**
	 * finds and returns all references to special constructs in the given
	 * expression.
	 * 
	 * @param expression
	 * @return a {@link SpecialContractHub} which is a collection of all
	 *         references to special constructs in the given expression.
	 */
	static SpecialContractHub findSpecialExpressions(
			ExpressionNode expression) {
		SpecialContractHub specials = new SpecialContractHub();

		return findSpecialExpressions(expression, specials);
	}

	static SpecialContractHub findSpecialExpressions(ExpressionNode expression,
			SpecialContractHub specials) {
		specials.remoteExpressions.addAll(findRemoteExpressions(expression));
		specials.acslOldExpressions.addAll(findOldExpressions(expression));
		specials.mpiDatatypes
				.addAll(findMPIDatatypesInMPIExpressions(expression));
		specials.mpiExtents.addAll(findMPIExtents(expression));
		specials.acslValidExpressions.addAll(findAcslValid(expression));
		specials.mpiValidExpressions.addAll(findMpiValid(expression));
		specials.acslResults.addAll(findAcslResult(expression));
		return specials;
	}

	private static List<ExpressionNode> findRemoteExpressions(
			ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.REMOTE_REFERENCE) {
			results.add(expr);
		}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findRemoteExpressions((ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findOldExpressions(
			ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.OPERATOR)
			if (((OperatorNode) expr).getOperator() == Operator.OLD) {
				results.add(expr);
				// nested old ? I think it should not happen
				return results;
			}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findOldExpressions((ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findMPIDatatypesInMPIExpressions(
			ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.MPI_CONTRACT_EXPRESSION) {
			MPIContractExpressionNode mpiexpr = (MPIContractExpressionNode) expr;
			MPIContractExpressionKind mpiExprKind = mpiexpr
					.MPIContractExpressionKind();

			if (mpiExprKind == MPIContractExpressionKind.MPI_REGION
					|| mpiExprKind == MPIContractExpressionKind.MPI_OFFSET
					|| mpiExprKind == MPIContractExpressionKind.MPI_VALID) {
				results.add(mpiexpr.getArgument(2));
				// return results;
			}
		}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findMPIDatatypesInMPIExpressions(
						(ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findMPIExtents(ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.MPI_CONTRACT_EXPRESSION) {
			MPIContractExpressionNode mpiexpr = (MPIContractExpressionNode) expr;
			MPIContractExpressionKind mpiExprKind = mpiexpr
					.MPIContractExpressionKind();

			if (mpiExprKind == MPIContractExpressionKind.MPI_EXTENT) {
				results.add(mpiexpr);
				return results;
			}
		}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findMPIExtents((ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findAcslValid(ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.OPERATOR)
			if (((OperatorNode) expr).getOperator() == Operator.VALID) {
				results.add(expr);
				return results;
			}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findAcslValid((ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findMpiValid(ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.MPI_CONTRACT_EXPRESSION) {
			MPIContractExpressionNode mpiexpr = (MPIContractExpressionNode) expr;
			MPIContractExpressionKind mpiExprKind = mpiexpr
					.MPIContractExpressionKind();

			if (mpiExprKind == MPIContractExpressionKind.MPI_VALID) {
				results.add(mpiexpr);
				return results;
			}
		}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findMpiValid((ExpressionNode) child));
		}
		return results;
	}

	private static List<ExpressionNode> findAcslResult(ExpressionNode expr) {
		List<ExpressionNode> results = new LinkedList<>();

		if (expr.expressionKind() == ExpressionKind.RESULT) {
			results.add(expr);
			return results;
		}

		int numChildren = expr.numChildren();

		for (int i = 0; i < numChildren;) {
			ASTNode child = expr.child(i++);

			if (child != null && child.nodeKind() == NodeKind.EXPRESSION)
				results.addAll(findAcslResult((ExpressionNode) child));
		}
		return results;
	}
}
