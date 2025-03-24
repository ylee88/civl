package dev.civl.mc.model.common.type;

import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLStructOrUnionType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.type.StructOrUnionField;
import dev.civl.sarl.IF.SymbolicUniverse;

public class CommonStructOrUnionField implements StructOrUnionField {

	final private int index;
	final private Identifier name;
	final private CIVLType type;
	final private boolean isAnonymous;
	final private CIVLStructOrUnionType enclosingType;

	public CommonStructOrUnionField(Identifier name, CIVLType type, int index,
			boolean isAnonymous, CIVLStructOrUnionType enclosingType) {
		this.name = name;
		this.type = type;
		this.isAnonymous = isAnonymous;
		this.index = index;
		this.enclosingType = enclosingType;
	}

	@Override
	public Identifier name() {
		return name;
	}

	@Override
	public CIVLType type() {
		return type;
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public String toString() {
		return name + " : " + type;
	}

	@Override
	public StructOrUnionField copyAs(CIVLPrimitiveType pType,
			SymbolicUniverse universe) {
		CIVLType newType = type.copyAs(pType, universe);

		if (newType.equals(type))
			return this;
		return new CommonStructOrUnionField(name, newType, index, isAnonymous,
				enclosingType);
	}

	@Override
	public boolean isAnonymous() {
		return isAnonymous;
	}

	@Override
	public CIVLStructOrUnionType enclosingType() {
		return enclosingType;
	}

}
