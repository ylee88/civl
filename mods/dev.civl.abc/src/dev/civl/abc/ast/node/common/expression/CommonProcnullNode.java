package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.ProcnullNode;
import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.token.IF.Source;

public class CommonProcnullNode extends CommonConstantNode
		implements
			ProcnullNode {
	public CommonProcnullNode(Source source, ObjectType processType) {
		super(source, "$proc_null", processType);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("$proc_null");
	}

	@Override
	public ObjectType getInitialType() {
		return (ObjectType) super.getInitialType();
	}

	@Override
	public ProcnullNode copy() {
		return new CommonProcnullNode(getSource(), getInitialType());
	}

	@Override
	public boolean isSideEffectFree(boolean errorsAreSideEffects) {
		return true;
	}

	@Override
	public ConstantKind constantKind() {
		return ConstantKind.PROCNULL;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonProcnullNode has no child, but saw index " + index);
	}
}
