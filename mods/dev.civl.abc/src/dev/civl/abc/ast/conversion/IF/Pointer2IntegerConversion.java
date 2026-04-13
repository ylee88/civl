package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.PointerType;

/**
 * Not normally defined behavior, but commonly used and we try to support it.
 * 
 * @author zmanchun
 *
 */
public interface Pointer2IntegerConversion extends Conversion {
	@Override
	PointerType getOldType();

	@Override
	IntegerType getNewType();
}
