package dev.civl.mc.model.IF.type;

import dev.civl.mc.model.IF.Identifier;
import dev.civl.sarl.IF.SymbolicUniverse;

/**
 * A field in a struct has a name and a type.
 * 
 * @author zirkel
 * 
 */
public interface StructOrUnionField {

	Identifier name();

	CIVLType type();

	int index();

	StructOrUnionField copyAs(CIVLPrimitiveType type, SymbolicUniverse universe);
}
