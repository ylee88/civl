package dev.civl.mc.model.common.type;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.type.CIVLEnumType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonEnumType extends CommonType implements CIVLEnumType {

	private String name;
	private Map<String, BigInteger> valueMap;

	public CommonEnumType(String name, Map<String, BigInteger> valueMap,
			SymbolicType dynamicType) {
		this.name = name;
		this.dynamicType = dynamicType;
		this.valueMap = valueMap;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		if (dynamicType == null)
			throw new CIVLInternalException(
					"no dynamic type specified for primitive enum " + name,
					(CIVLSource) null);
		return dynamicType;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public BigInteger valueOf(String member) {
		if (!valueMap.containsKey(member))
			throw new CIVLInternalException(
					"no enumerator " + member
							+ " defined in the enumeration type " + name,
					(CIVLSource) null);
		return valueMap.get(member);
	}

	@Override
	public boolean isEnumerationType() {
		return true;
	}

	@Override
	public String toString() {
		String result = "enum ";

		if (name != null)
			result += (name + " ");
		result += "{";
		for (String member : valueMap.keySet()) {
			result += (member + "=" + valueMap.get(member) + ", ");
		}
		result = result.substring(0, result.length() - 2);
		result += "}";
		return result;
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.ENUM;
	}

	@Override
	public BigInteger firstValue() {
		for (String key : this.valueMap.keySet()) {
			return this.valueMap.get(key);
		}
		return BigInteger.ZERO;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CIVLEnumType)
			return true;
		return false;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
	}

	@Override
	public boolean hasReferences() {
		return false;
	}

	@Override
	public boolean analyze() {
		return true;
	}
}
