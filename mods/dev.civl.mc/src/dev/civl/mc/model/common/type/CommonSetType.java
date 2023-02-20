package dev.civl.mc.model.common.type;

import java.util.Set;

import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLSetType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonSetType extends CommonType implements CIVLSetType {

	private CIVLType elementType;

	private boolean isAnalyzed = false;

	private boolean hasReferences = false;

	public CommonSetType(CIVLType elementType) {
		super();
		this.elementType = elementType;
		assert elementType.typeKind() != TypeKind.SET;
		analyze();
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.SET;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		throw new CIVLUnimplementedFeatureException(
				"Dynamic type of CIVLSetType");
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CIVLSetType) {
			CIVLSetType that = (CIVLSetType) obj;

			return this.elementType().equals(that.elementType());
		}
		return false;
	}

	@Override
	public CIVLType elementType() {
		return elementType;
	}

	@Override
	public boolean isSetTypeOf(CIVLType element) {
		return elementType.equals(element);
	}

	@Override
	public boolean isSetType() {
		return true;
	}

	@Override
	public boolean isSuperTypeOf(CIVLType subtype) {
		return this.elementType.equals(subtype);
	}

	@Override
	public String toString() {
		return "set-of-(" + elementType.toString() + ")";
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this))
			((CommonType) elementType).addFreeVariables(result, seenTypes);
	}

	@Override
	public boolean hasReferences() {
		return hasReferences;
	}

	@Override
	public boolean analyze() {
		if (!isAnalyzed && elementType.analyze()) {
			hasReferences = elementType.hasReferences();
			isAnalyzed = true;
		}
		return isAnalyzed;
	}
}
