package dev.civl.mc.model.IF.type;

import java.util.Collection;

import dev.civl.mc.model.IF.statement.MallocStatement;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;


//TODO: Document!!

public interface CIVLHeapType extends CIVLType {

	int getNumMallocs();

	MallocStatement getMalloc(int index);

	boolean isComplete();

	void complete(Collection<MallocStatement> mallocs,
			SymbolicType dynamicType, SymbolicExpression initialValue,
			SymbolicExpression undefinedValue);

	SymbolicExpression getInitialValue();

	SymbolicExpression getUndefinedValue();

	String getName();

}
