/**
 * 
 */
package dev.civl.mc.model.common.type;

import java.util.Set;

import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.Expression.ExpressionKind;
import dev.civl.mc.model.IF.type.CIVLCompleteArrayType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;

/**
 * @author zirkel
 * 
 */
public class CommonCompleteArrayType extends CommonArrayType
		implements
			CIVLCompleteArrayType {

	private Expression extent;

	/**
	 * @param baseType
	 */
	public CommonCompleteArrayType(CIVLType baseType, Expression extent) {
		super(baseType);
		this.extent = extent;
	}

	@Override
	public Expression extent() {
		return extent;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CIVLCompleteArrayType) {
			CIVLCompleteArrayType that = (CIVLCompleteArrayType) obj;

			return this.elementType().equals(that.elementType())
					&& this.extent.equals(that.extent());
		}
		return false;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean isIncompleteArrayType() {
		return false;
	}

	@Override
	public boolean hasState() {
		if (super.hasState())
			return true;
		return extent.expressionKind() != ExpressionKind.INTEGER_LITERAL;
	}

	public String toString() {
		return elementType() + "[" + extent() + "]";
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.COMPLETE_ARRAY;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		CIVLType newElementType = this.elementType().copyAs(type, universe);

		if (newElementType.equals(this.elementType()))
			return this;
		return new CommonCompleteArrayType(newElementType, extent);
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this))
			super.addFreeVariables(result, seenTypes);
	}

	@Override
	public boolean hasConstantLength() {
		return extent.expressionKind() == ExpressionKind.INTEGER_LITERAL;
	}
}
