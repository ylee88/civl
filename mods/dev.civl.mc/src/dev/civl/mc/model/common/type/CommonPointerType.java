/**
 * 
 */
package dev.civl.mc.model.common.type;

import java.util.Set;

import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.type.CIVLPointerType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * 
 * @author zirkel
 * 
 */
public class CommonPointerType extends CommonType implements CIVLPointerType {

	private CIVLType baseType;

	public CommonPointerType(CIVLType baseType, SymbolicType pointerType) {
		super();
		this.dynamicType = pointerType;
		this.baseType = baseType;
	}

	@Override
	public CIVLType baseType() {
		return baseType;
	}

	@Override
	public String toString() {
		return "(" + baseType + ")*";
	}

	@Override
	public boolean isPointerType() {
		return true;
	}

	@Override
	public Scope getRegion() {
		return null;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		return dynamicType;
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.POINTER;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof CIVLPointerType) {
			CIVLPointerType that = (CIVLPointerType) obj;

			return this.baseType.equals(that.baseType());
		}
		return false;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this))
			((CommonType) baseType).addFreeVariables(result, seenTypes);
	}

	@Override
	public boolean hasReferences() {
		return true;
	}

	@Override
	public boolean analyze() {
		return true;
	}
}
