package dev.civl.mc.model.IF.type;

import java.util.Collection;
import java.util.List;

import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUnionType;

//TODO: Document!!!

public interface CIVLBundleType extends CIVLType {

	int getNumTypes();

	SymbolicType getElementType(int index);

	CIVLType getStaticElementType(int index);

	// TODO: What does this do if the type isn't found?
	Integer getIndexOf(SymbolicType elementType);

	boolean isComplete();

	void complete(List<CIVLType> types, Collection<SymbolicType> elementTypes,
			SymbolicUnionType dynamicType);

	@Override
	SymbolicUnionType getDynamicType(SymbolicUniverse universe);

	List<CIVLType> types();

}
