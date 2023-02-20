package dev.civl.abc.ast.node.common.omp;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.ConstantNode;
import dev.civl.abc.ast.node.IF.omp.OmpSimdNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

public class CommonOmpSimdNode extends CommonOmpStatementNode
		implements
			OmpSimdNode {

	public CommonOmpSimdNode(Source source, StatementNode statement) {
		super(source, statement);
		// 8th child : safelen argument, children 0-7 are inherited from
		// CommonOmpStatementNode
		this.addChild(null);
		// 9th child : simdlen argument
		this.addChild(null);
	}

	@Override
	public CommonOmpSimdNode copy() {
		CommonOmpSimdNode copied = new CommonOmpSimdNode(getSource(),
				statementNode().copy());

		for (int i = 0; i < numChildren(); i++) {
			ASTNode child = this.child(i);

			if (child != null) {
				copied.setChild(i, child.copy());
			}
		}
		return copied;
	}

	@Override
	public OmpExecutableKind ompExecutableKind() {
		return OmpExecutableKind.SIMD;
	}

	@Override
	public ConstantNode safeLen() {
		return (ConstantNode) child(8);
	}

	@Override
	public ConstantNode simdLen() {
		return (ConstantNode) child(9);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("OmpSimd");
	}

	@Override
	public ASTNode setSafelen(ConstantNode arg) {
		return setChild(8, arg);
	}

	@Override
	public ASTNode setSimdlen(ConstantNode arg) {
		return setChild(9, arg);
	}
}
