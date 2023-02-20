package dev.civl.abc.ast.node.common.type;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.token.IF.Source;

public class CommonVoidTypeNode extends CommonTypeNode {

	public CommonVoidTypeNode(Source source) {
		super(source, TypeNodeKind.VOID);
	}

	@Override
	protected void printBody(PrintStream out) {
		String qualifiers = qualifierString();

		out.print("VoidType");
		if (!qualifiers.isEmpty())
			out.print("[" + qualifiers + "]");
	}

	@Override
	public TypeNode copy() {
		CommonVoidTypeNode result = new CommonVoidTypeNode(getSource());

		copyData(result);
		return result;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonVoidTypeNode has no child, but saw index " + index);
	}
}
