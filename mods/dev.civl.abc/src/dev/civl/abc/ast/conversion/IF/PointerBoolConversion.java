package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.type.IF.PointerType;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType;

/**
 * Any pointer type can be explicitly cast to the boolean type: if the pointer
 * is NULL, it maps to the boolean value <code>false</code>, else it maps to
 * <code>true</code>.
 * 
 * @author siegel
 * 
 */
public interface PointerBoolConversion extends Conversion {

	/**
	 * Any pointer type.
	 */
	@Override
	PointerType getOldType();

	/**
	 * The boolean type <code>_Bool</code>.
	 */
	@Override
	StandardUnsignedIntegerType getNewType();

}
