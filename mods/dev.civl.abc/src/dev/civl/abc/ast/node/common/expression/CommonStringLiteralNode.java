package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.StringLiteralNode;
import dev.civl.abc.ast.value.IF.StringValue;
import dev.civl.abc.token.IF.Source;

public class CommonStringLiteralNode extends CommonConstantNode
		implements
			StringLiteralNode {

	public CommonStringLiteralNode(Source source, String representation,
			StringValue stringValue) {
		super(source, representation, stringValue.getType());
		setConstantValue(stringValue);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("StringLiteralNode[" + getConstantValue() + "]");
	}

	@Override
	public StringValue getConstantValue() {
		return (StringValue) super.getConstantValue();
	}

	@Override
	public CommonStringLiteralNode copy() {
		return new CommonStringLiteralNode(getSource(),
				getStringRepresentation(), getConstantValue());
	}

	@Override
	public ConstantKind constantKind() {
		return ConstantKind.STRING;
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonStringLiteralNode has no child, but saw index " + index);
	}
}
