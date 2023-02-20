package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.expression.IntegerConstantNode;
import dev.civl.abc.ast.value.IF.IntegerValue;
import dev.civl.abc.token.IF.Source;

public class CommonIntegerConstantNode extends CommonConstantNode implements
		IntegerConstantNode {

	public CommonIntegerConstantNode(Source source, String representation,
			IntegerValue value) {
		super(source, representation, value.getType());
		setConstantValue(value);
	}

	@Override
	public IntegerValue getConstantValue() {
		return (IntegerValue) super.getConstantValue();
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print(toString());
	}

	@Override
	public String toString() {
		return "IntegerConstantNode[value="
				+ getConstantValue().getIntegerValue() + "]";
	}

	@Override
	public IntegerConstantNode copy() {
		return new CommonIntegerConstantNode(getSource(),
				getStringRepresentation(), getConstantValue());
	}

	@Override
	public ConstantKind constantKind() {
		return ConstantKind.INT;
	}
}
