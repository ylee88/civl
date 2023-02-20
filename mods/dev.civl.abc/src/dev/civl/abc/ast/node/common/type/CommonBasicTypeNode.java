package dev.civl.abc.ast.node.common.type;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.IF.DifferenceObject.DiffKind;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.type.BasicTypeNode;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.token.IF.Source;

public class CommonBasicTypeNode extends CommonTypeNode
		implements
			BasicTypeNode {

	private BasicTypeKind basicTypeKind;

	public CommonBasicTypeNode(Source source, BasicTypeKind basicTypeKind) {
		super(source, TypeNodeKind.BASIC);

		if (basicTypeKind == null)
			throw new IllegalArgumentException(
					"null basicTypeKind specified for a basic type node");
		this.basicTypeKind = basicTypeKind;
	}

	@Override
	public BasicTypeKind getBasicTypeKind() {
		return basicTypeKind;
	}

	@Override
	protected void printBody(PrintStream out) {
		String qualifiers = qualifierString();

		out.print("BasicType[" + basicTypeKind);
		if (!qualifiers.isEmpty())
			out.print(", " + qualifiers);
		out.print("]");
	}

	@Override
	public BasicTypeNode copy() {
		CommonBasicTypeNode result = new CommonBasicTypeNode(getSource(),
				getBasicTypeKind());

		copyData(result);
		return result;
	}

	@Override
	protected DifferenceObject diffWork(ASTNode that) {
		if (that instanceof BasicTypeNode)
			if (((BasicTypeNode) that).getBasicTypeKind() == this.basicTypeKind)
				return null;
			else
				return new DifferenceObject(this, that,
						DiffKind.BASIC_TYPE_KIND);
		return new DifferenceObject(this, that);
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonBasicTypeNode has no child, but saw index " + index);
	}
}
