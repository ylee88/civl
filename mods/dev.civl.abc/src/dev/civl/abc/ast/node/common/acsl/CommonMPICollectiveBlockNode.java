package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.MPICollectiveBlockNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.token.IF.Source;

public class CommonMPICollectiveBlockNode extends CommonContractNode
		implements
			MPICollectiveBlockNode {
	private SequenceNode<ContractNode> body;

	private MPICommunicatorMode kind;

	public CommonMPICollectiveBlockNode(Source source, ExpressionNode mpiComm,
			MPICommunicatorMode kind, SequenceNode<ContractNode> body) {
		super(source, mpiComm, body);
		this.kind = kind;
		this.body = body;
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.MPI_COLLECTIVE;
	}

	@Override
	public ExpressionNode getMPIComm() {
		return (ExpressionNode) this.child(0);
	}

	@Override
	public MPICommunicatorMode getCollectiveKind() {
		return kind;
	}

	@Override
	public SequenceNode<ContractNode> getBody() {
		return this.body;
	}

	@Override
	public MPICollectiveBlockNode copy() {
		return new CommonMPICollectiveBlockNode(this.getSource(),
				duplicate(getMPIComm()), kind, this.body);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("\\mpi_collective");
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index >= 2)
			throw new ASTException(
					"CommonMPICollectiveBlockNode has only two children, but saw index "
							+ index);
		if (index == 0 && !(child == null || child instanceof ExpressionNode))
			throw new ASTException(
					"Child of CommonMPICollectiveBlockNode at index " + index
							+ " must be a ExpressionNode, but saw " + child
							+ " with type " + child.nodeKind());
		if (index == 1 && !(child == null || child instanceof SequenceNode))
			throw new ASTException(
					"Child of CommonMPICollectiveBlockNode at index " + index
							+ " must be a SequenceNode, but saw " + child
							+ " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}
