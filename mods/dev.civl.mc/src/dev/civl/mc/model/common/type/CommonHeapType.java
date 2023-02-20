/**
 * 
 */
package dev.civl.mc.model.common.type;

import java.util.Collection;
import java.util.Set;

import dev.civl.mc.model.IF.statement.MallocStatement;
import dev.civl.mc.model.IF.type.CIVLHeapType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonHeapType extends CommonType implements CIVLHeapType {

	private String name;

	private MallocStatement[] mallocs = null;

	private SymbolicExpression initialValue = null;

	private SymbolicExpression undefinedValue = null;

	public CommonHeapType(String name) {
		this.name = name;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		return dynamicType;
	}

	@Override
	public int getNumMallocs() {
		return mallocs.length;
	}

	@Override
	public MallocStatement getMalloc(int index) {
		return mallocs[index];
	}

	@Override
	public boolean isComplete() {
		return mallocs != null;
	}

	@Override
	public void complete(Collection<MallocStatement> mallocs,
			SymbolicType dynamicType, SymbolicExpression initialValue,
			SymbolicExpression undefinedValue) {
		this.mallocs = mallocs.toArray(new MallocStatement[mallocs.size()]);
		this.dynamicType = dynamicType;
		this.initialValue = initialValue;
		this.undefinedValue = undefinedValue;
	}

	@Override
	public boolean isHeapType() {
		return true;
	}

	@Override
	public String toString() {
		return "__heap__";
	}

	@Override
	public SymbolicExpression getInitialValue() {
		return initialValue;
	}

	@Override
	public SymbolicExpression getUndefinedValue() {
		return undefinedValue;
	}

	@Override
	public String getName() {
		return name;
	}

	// @Override
	// public boolean isHandleObjectType() {
	// return true;
	// }

	@Override
	public TypeKind typeKind() {
		return TypeKind.HEAP;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return this;
	}

	@Override
	public boolean areSubtypesScalar() {
		return false;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
	}

	@Override
	public boolean hasReferences() {
		return true;
	}

	@Override
	public boolean analyze() {
		return true;
	}
}
