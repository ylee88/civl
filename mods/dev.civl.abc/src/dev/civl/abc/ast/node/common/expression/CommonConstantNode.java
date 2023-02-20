package dev.civl.abc.ast.node.common.expression;

import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.IF.DifferenceObject.DiffKind;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.expression.ConstantNode;
import dev.civl.abc.ast.type.IF.Type;
import dev.civl.abc.ast.value.IF.Value;
import dev.civl.abc.token.IF.Source;

public abstract class CommonConstantNode extends CommonExpressionNode implements
		ConstantNode {

	private String representation;

	public CommonConstantNode(Source source, String representation) {
		super(source);
		this.representation = representation;
	}

	public CommonConstantNode(Source source, String representation, Type type) {
		super(source);
		this.representation = representation;
		setInitialType(type);
	}

	@Override
	public String getStringRepresentation() {
		return representation;
	}

	@Override
	public void setStringRepresentation(String representation) {
		this.representation = representation;
	}

	@Override
	public boolean isConstantExpression() {
		return true;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.CONSTANT;
	}

	@Override
	public boolean isSideEffectFree(boolean errorsAreSideEffects) {
		return true;
	}

	@Override
	protected DifferenceObject diffWork(ASTNode that) {
		if (that instanceof ConstantNode) {
			ConstantNode thatConst = (ConstantNode) that;
			Value thisValue = this.getConstantValue(), thatValue = ((ConstantNode) that)
					.getConstantValue();

			if (thatConst.constantKind() != this.constantKind())
				return new DifferenceObject(this, that);
			if (thisValue != null)
				if (thisValue.equals(thatValue))
					return null;
				else
					return new DifferenceObject(this, that,
							DiffKind.CONSTANT_VALUE);
			else if (thatValue != null)
				return new DifferenceObject(this, that, DiffKind.CONSTANT_VALUE);
			return null;
		}
		return new DifferenceObject(this, that);
	}
}
