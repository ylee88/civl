package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.Field;

public interface MemberReference extends AddressValue {

	Field getField();

	AddressValue getStructureOrUnionReference();

}
