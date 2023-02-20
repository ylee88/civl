package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.Field;
import dev.civl.abc.ast.type.IF.StructureOrUnionType;

/**
 * 
 * @author siegel
 * 
 */
public interface StructureValue extends CompoundValue {

	@Override
	StructureOrUnionType getType();

	Value getMember(Field field);

	Value getMember(int fieldIndex);

	void setMember(Field field, Value memberValue);

	void setMember(int index, Value memberValue);
}
