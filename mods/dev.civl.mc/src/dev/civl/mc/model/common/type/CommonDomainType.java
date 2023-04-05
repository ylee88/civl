package dev.civl.mc.model.common.type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.type.CIVLCompleteDomainType;
import dev.civl.mc.model.IF.type.CIVLDomainType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUnionType;

public class CommonDomainType extends CommonType implements CIVLDomainType {

	public final static int classCode = CommonDomainType.class.hashCode();

	private SymbolicUnionType subtypesUnion;

	private CIVLType rangeType;

	public CommonDomainType(CIVLType rangeType) {
		super();
		this.rangeType = rangeType;
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.DOMAIN;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		if (this.dynamicType == null) {
			List<SymbolicType> tupleComponents = new LinkedList<>();
			SymbolicTupleType domainTupleType;
			SymbolicArrayType recDomainType, literalDomainType;
			SymbolicType integerType = universe.integerType();
			SymbolicType rangeType = this.rangeType.getDynamicType(universe);

			recDomainType = universe.arrayType(rangeType);
			literalDomainType = universe
					.arrayType(universe.arrayType(integerType));
			tupleComponents.add(universe.integerType());
			tupleComponents.add(universe.integerType());
			if (this.subtypesUnion == null)
				this.subtypesUnion = universe.unionType(
						universe.stringObject("domain"),
						Arrays.asList(recDomainType, literalDomainType));
			tupleComponents.add(this.subtypesUnion);
			domainTupleType = universe.tupleType(
					universe.stringObject("$domain"), tupleComponents);
			this.dynamicType = domainTupleType;
		}
		return this.dynamicType;
	}

	@Override
	public SymbolicUnionType getDynamicSubTypesUnion(
			SymbolicUniverse universe) {
		if (this.subtypesUnion == null)
			this.getDynamicType(universe);
		return this.subtypesUnion;
	}

	@Override
	public boolean isDomainType() {
		return true;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return this;
	}

	@Override
	public String toString() {
		return "$domain";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return obj instanceof CommonDomainType
				&& !(obj instanceof CIVLCompleteDomainType);
	}

	@Override
	public int hashCode() {
		return classCode;
	}

	@Override
	public boolean isComplete() {
		return (this instanceof CIVLCompleteDomainType);
	}

	@Override
	public boolean areSubtypesScalar() {
		return false;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this))
			((CommonType) rangeType).addFreeVariables(result, seenTypes);
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
