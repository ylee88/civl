package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;
import java.util.Arrays;

import dev.civl.abc.ast.node.IF.acsl.MPIContractConstantNode;
import dev.civl.abc.token.IF.Source;

public class CommonMPIConstantNode extends CommonMPIContractExpressionNode
		implements
			MPIContractConstantNode {

	private MPIConstantKind kind;

	private ConstantKind constKind;

	private String stringReresentation;

	private String exprName;

	public CommonMPIConstantNode(Source source, String name,
			MPIConstantKind kind, ConstantKind constKind) {
		super(source, Arrays.asList(),
				MPIContractExpressionKind.MPI_INTEGER_CONSTANT, name);
		this.kind = kind;
		this.constKind = constKind;
		this.exprName = name;
	}

	@Override
	public MPIConstantKind getMPIConstantKind() {
		return kind;
	}

	@Override
	public MPIContractConstantNode copy() {
		return new CommonMPIConstantNode(getSource(), exprName, kind,
				constKind);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print(kind);
	}

	@Override
	public ConstantKind constantKind() {
		return constKind;
	}

	@Override
	public String getStringRepresentation() {
		return this.stringReresentation;
	}

	@Override
	public void setStringRepresentation(String representation) {
		this.stringReresentation = representation;
	}
}
