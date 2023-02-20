/**
 * 
 */
package dev.civl.mc.model.common.type;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.civl.mc.model.IF.type.CIVLBundleType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUnionType;

public class CommonBundleType extends CommonType implements CIVLBundleType {

	private List<CIVLType> types;

	private SymbolicType[] elementTypes = null;

	private Map<SymbolicType, Integer> indexMap = null;

	private boolean isAnalyzed = false;

	private boolean hasReferences = false;

	public CommonBundleType() {
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicUnionType getDynamicType(SymbolicUniverse universe) {
		return (SymbolicUnionType) dynamicType;
	}

	@Override
	public int getNumTypes() {
		return elementTypes.length;
	}

	@Override
	public SymbolicType getElementType(int index) {
		return elementTypes[index];
	}

	@Override
	public List<CIVLType> types() {
		return types;
	}

	@Override
	public boolean isComplete() {
		return elementTypes != null;
	}

	@Override
	public void complete(List<CIVLType> types,
			Collection<SymbolicType> elementTypes,
			SymbolicUnionType dynamicType) {
		int n = elementTypes.size();

		this.types = types;
		this.elementTypes = elementTypes.toArray(new SymbolicType[n]);
		this.dynamicType = dynamicType;
		this.indexMap = new LinkedHashMap<SymbolicType, Integer>(n);
		for (int i = 0; i < n; i++)
			indexMap.put(this.elementTypes[i], i);
		analyze();
	}

	@Override
	public boolean isBundleType() {
		return true;
	}

	@Override
	public String toString() {
		return "$bundle";
	}

	@Override
	public Integer getIndexOf(SymbolicType elementType) {
		return indexMap.get(elementType);
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.BUNDLE;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		// only one bundle type for a model
		if (obj instanceof CIVLBundleType)
			return true;
		return false;
	}

	@Override
	public boolean areSubtypesScalar() {
		return false;
	}

	@Override
	public CIVLType getStaticElementType(int index) {
		return types.get(index);
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this))
			for (CIVLType type : types)
				((CommonType) type).addFreeVariables(result, seenTypes);
	}

	@Override
	public boolean hasReferences() {
		return hasReferences;
	}

	@Override
	public boolean analyze() {
		if (!isAnalyzed && types != null) {
			boolean allTypesAnalyzed = true;

			for (CIVLType type : types) {
				if (!type.analyze())
					allTypesAnalyzed = false;
			}
			if (allTypesAnalyzed) {
				for (CIVLType type : types) {
					if (type.hasReferences()) {
						hasReferences = true;
						break;
					}
				}
				isAnalyzed = true;
			}
		}
		return isAnalyzed;
	}
}
