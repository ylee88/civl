package dev.civl.mc.model.common.type;

import java.util.ArrayList;
import java.util.List;

import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.type.CIVLRegularRangeType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.type.StructOrUnionField;
import dev.civl.mc.model.common.CommonIdentifier;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.object.StringObject;

public class CommonRegularRangeType extends CommonStructOrUnionType
		implements
			CIVLRegularRangeType {

	private final CIVLType elementType;

	public CommonRegularRangeType(Identifier name, SymbolicUniverse universe,
			CIVLType integerType) {
		super(name, true);
		List<StructOrUnionField> myfields = new ArrayList<>(3);

		myfields.add(new CommonStructOrUnionField(
				new CommonIdentifier(name.getSource(),
						(StringObject) universe.stringObject("low")),
				integerType));
		myfields.add(new CommonStructOrUnionField(
				new CommonIdentifier(name.getSource(),
						(StringObject) universe.stringObject("high")),
				integerType));
		myfields.add(new CommonStructOrUnionField(new CommonIdentifier(
				name.getSource(), universe.stringObject("step")), integerType));
		this.complete(myfields);
		elementType = integerType;
	}

	@Override
	public boolean isRangeType() {
		return true;
	}

	@Override
	public boolean isSetType() {
		return true;
	}

	@Override
	public boolean isSetTypeOf(CIVLType elementType) {
		return elementType.isIntegerType();
	}

	@Override
	public CIVLType elementType() {
		return elementType;
	}
}
