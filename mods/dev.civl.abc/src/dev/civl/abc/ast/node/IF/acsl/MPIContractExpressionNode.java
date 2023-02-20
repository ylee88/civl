package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

public interface MPIContractExpressionNode extends ExpressionNode {
	public enum MPIContractExpressionKind {
		MPI_AGREE, MPI_EMPTY_IN, MPI_EMPTY_OUT, MPI_EQUALS, MPI_EXTENT, MPI_OFFSET,
		MPI_REGION, MPI_VALID, MPI_INTEGER_CONSTANT, MPI_ABSENT, MPI_ABSENT_EVENT
	}

	MPIContractExpressionKind MPIContractExpressionKind();

	/**
	 * Return the number of arguments of this MPI expression
	 *
	 * @return
	 */
	int numArguments();

	/**
	 * Returns the index-th argument
	 *
	 * @param index
	 * @return
	 */
	ExpressionNode getArgument(int index);
}
