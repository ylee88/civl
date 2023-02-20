package dev.civl.mc.model.common.type;

import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.type.StructOrUnionField;
import dev.civl.sarl.IF.SymbolicUniverse;

public class CommonStructOrUnionField implements StructOrUnionField {

	private int index = -1;
	private Identifier name;
	private CIVLType type;

	public CommonStructOrUnionField(Identifier name, CIVLType type) {
		this.name = name;
		this.type = type;
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

	void setIndex(int index) {
		this.index = index;
	}

	@Override
	public StructOrUnionField copyAs(CIVLPrimitiveType pType,
			SymbolicUniverse universe) {
		CIVLType newType = type.copyAs(pType, universe);

		if (newType.equals(type))
			return this;
		return new CommonStructOrUnionField(name, newType);
	}

}
