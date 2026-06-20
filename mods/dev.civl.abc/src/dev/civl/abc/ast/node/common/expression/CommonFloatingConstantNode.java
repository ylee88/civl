package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.expression.FloatingConstantNode;
import dev.civl.abc.ast.value.IF.ComplexFloatingValue;
import dev.civl.abc.ast.value.IF.FloatingValue;
import dev.civl.abc.token.IF.Source;

public class CommonFloatingConstantNode extends CommonConstantNode implements FloatingConstantNode {

	private String wholePart;

	private String fractionPart;

	private String exponent;

	public CommonFloatingConstantNode(Source source, String representation, String wholePart, String fractionPart,
			String exponent, FloatingValue value) {
		super(source, representation, value.getType());
		this.wholePart = wholePart;
		this.fractionPart = fractionPart;
		this.exponent = exponent;
		setConstantValue(value);
	}

	@Override
	public FloatingValue getConstantValue() {
		return (FloatingValue) super.getConstantValue();
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print(toString());
	}

	@Override
	public String toString() {
		return "FloatingConstantNode[significand=" + wholePart + "." + fractionPart + ", exponent=" + exponent
				+ ", value=" + getConstantValue() + "]";
	}

	@Override
	public String wholePart() {
		return wholePart;
	}

	@Override
	public String fractionPart() {
		return fractionPart;
	}

	@Override
	public String exponent() {
		return exponent;
	}

	@Override
	public FloatingConstantNode copy() {
		return new CommonFloatingConstantNode(getSource(), getStringRepresentation(), wholePart(), fractionPart(),
				exponent(), getConstantValue());
	}

	@Override
	public ConstantKind constantKind() {
		return ConstantKind.FLOAT;
	}

	@Override
	public boolean isComplex() {
		return getConstantValue() instanceof ComplexFloatingValue;
	}
}
