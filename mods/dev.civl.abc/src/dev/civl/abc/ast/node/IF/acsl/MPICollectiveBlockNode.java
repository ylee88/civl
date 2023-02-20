package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;

/**
 * An contract block introduced by the
 * <code>mpi_collective(MPI_Comm, Kind):</code> contract constructor.
 * 
 * @author ziqing
 *
 */
public interface MPICollectiveBlockNode extends ContractNode {
	public enum MPICommunicatorMode {
		COL, P2P, BOTH
	};

	/**
	 * Returns the node corresponding to the specific MPI_Comm
	 * 
	 * @return
	 */
	ExpressionNode getMPIComm();

	/**
	 * Returns the node corresponding to the specific MPI collective kind
	 * 
	 * @return
	 */
	MPICommunicatorMode getCollectiveKind();

	/**
	 * Get the body of a MPI collective block
	 * 
	 * @return
	 */
	SequenceNode<ContractNode> getBody();

	@Override
	MPICollectiveBlockNode copy();
}
