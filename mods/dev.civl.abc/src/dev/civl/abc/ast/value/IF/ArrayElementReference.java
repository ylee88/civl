package dev.civl.abc.ast.value.IF;

public interface ArrayElementReference extends AddressValue {

	Value getIndex();

	AddressValue getArrayReference();

}
