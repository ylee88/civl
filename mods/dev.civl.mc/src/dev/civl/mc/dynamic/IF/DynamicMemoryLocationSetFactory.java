package dev.civl.mc.dynamic.IF;

import dev.civl.mc.model.IF.type.CIVLMemType;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public interface DynamicMemoryLocationSetFactory {

	/**
	 * @return an empty {@link DynamicMemoryLocationSet}
	 */
	DynamicMemoryLocationSet empty();

	/**
	 * Add references in a "memValue" into the given "writeSet", returns a new
	 * write set
	 * 
	 * @param writeSet
	 *            a DynamicWriteSet
	 * @param memValue
	 *            a symbolic expression of
	 *            {@link CIVLMemType#getDynamicType(dev.civl.sarl.IF.SymbolicUniverse)}
	 *            type
	 * @return
	 */
	DynamicMemoryLocationSet addReference(DynamicMemoryLocationSet writeSet,
			SymbolicExpression memValue);
}
