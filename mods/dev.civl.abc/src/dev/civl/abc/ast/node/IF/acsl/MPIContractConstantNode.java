package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.expression.ConstantNode;

public interface MPIContractConstantNode extends ConstantNode {
	public enum MPIConstantKind {
		MPI_COMM_RANK, MPI_COMM_SIZE
	}

	public MPIConstantKind getMPIConstantKind();

	@Override
	MPIContractConstantNode copy();
}
