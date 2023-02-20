package dev.civl.mc.model.common.type;

import java.util.HashSet;
import java.util.Set;

import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * Root of CIVLType class hierarchy.
 * 
 * @author siegel
 * 
 */
public abstract class CommonType implements CIVLType {

	protected SymbolicType dynamicType = null;

	/**
	 * CIVL associates a single dynamic type to every CIVL type and does this
	 * once at compile time. All the dynamic types which occur as dynamic types
	 * of CIVL types are numbered from 0. This is used in particular to
	 * construct the bundle type which is the union of all of the dynamic types.
	 * This field is the dynamic type index to this one and it's initially be
	 * minus one and can be set later by calling
	 * {@link #setDynamicTypeIndex(int)} and the getter is
	 * {@link #getDynamicTypeIndex()}
	 */
	private int dynamicTypeIndex = -1;

	private Variable stateVariable = null;

	public CommonType() {
	}

	@Override
	public boolean isNumericType() {
		return false;
	}

	@Override
	public boolean isIntegerType() {
		return false;
	}

	@Override
	public boolean isRealType() {
		return false;
	}

	@Override
	public boolean isPointerType() {
		return false;
	}

	@Override
	public boolean isProcessType() {
		return false;
	}

	@Override
	public boolean isStateType() {
		return false;
	}

	@Override
	public boolean isScopeType() {
		return false;
	}

	@Override
	public Variable getStateVariable() {
		return stateVariable;
	}

	@Override
	public void setStateVariable(Variable variable) {
		stateVariable = variable;
	}

	@Override
	public boolean isVoidType() {
		return false;
	}

	@Override
	public boolean isHeapType() {
		return false;
	}

	@Override
	public boolean isBundleType() {
		return false;
	}

	@Override
	public boolean isArrayType() {
		return false;
	}

	@Override
	public boolean isStructType() {
		return false;
	}

	@Override
	public boolean isUnionType() {
		return false;
	}

	@Override
	public boolean isCharType() {
		return false;
	}

	@Override
	public int getDynamicTypeIndex() {
		return dynamicTypeIndex;
	}

	/**
	 * 
	 * Sets the dynamic type index for this CIVL type. CIVL associates a single
	 * dynamic type to every CIVL type and does this once at compile time. All
	 * the dynamic types which occur as dynamic types of CIVL types are numbered
	 * from 0. This is used in particular to construct the bundle type which is
	 * the union of all of the dynamic types. This field is the dynamic type
	 * index to this one and it's initially be minus one and can be set later by
	 * calling this method and the getter is {@link #getDynamicTypeIndex()}.
	 * 
	 * @param index
	 *            the dynamic type index of this CIVL type
	 */
	public void setDynamicTypeIndex(int index) {
		this.dynamicTypeIndex = index;
	}

	@Override
	public boolean isEnumerationType() {
		return false;
	}

	@Override
	public boolean isBoolType() {
		return false;
	}

	@Override
	public boolean isDomainType() {
		return false;
	}

	@Override
	public boolean isRangeType() {
		return false;
	}

	@Override
	public boolean isSetTypeOf(CIVLType element) {
		return false;
	}

	@Override
	public boolean isSetType() {
		return false;
	}

	@Override
	public boolean isIncompleteArrayType() {
		return false;
	}

	@Override
	public boolean isSuperTypeOf(CIVLType subtype) {
		return this.equals(subtype);
	}

	@Override
	public boolean isScalar() {
		return false;
	}

	@Override
	public boolean areSubtypesScalar() {
		return true;
	}

	@Override
	public boolean isFunction() {
		return false;
	}

	protected abstract void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes);

	@Override
	public Set<Variable> freeVariables() {
		HashSet<Variable> result = new HashSet<>();
		HashSet<CIVLType> seenTypes = new HashSet<>();

		addFreeVariables(result, seenTypes);
		return result;
	}
}
