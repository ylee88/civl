package dev.civl.abc.ast.node.common.expression;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.expression.CharacterConstantNode;
import dev.civl.abc.ast.value.IF.CharacterValue;
import dev.civl.abc.token.IF.Source;

public class CommonCharacterConstantNode extends CommonConstantNode implements
		CharacterConstantNode {

	public CommonCharacterConstantNode(Source source, String representation,
			CharacterValue value) {
		super(source, representation, value.getType());
		setConstantValue(value);
	}

	@Override
	public String toString() {
		return "CharacterConstantNode[" + getConstantValue() + "]";
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print(this);
	}

	@Override
	public CharacterValue getConstantValue() {
		return (CharacterValue) super.getConstantValue();
	}

	@Override
	public CharacterConstantNode copy() {
		return new CommonCharacterConstantNode(getSource(),
				getStringRepresentation(), getConstantValue());
	}

	@Override
	public ConstantKind constantKind() {
		return ConstantKind.CHAR;
	}

}
