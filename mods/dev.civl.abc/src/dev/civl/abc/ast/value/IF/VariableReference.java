package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.entity.IF.Variable;

public interface VariableReference extends AddressValue {

	Variable getVariable();

}
