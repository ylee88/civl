package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.PointerType;

/**
 * some sv-comp examples are using conversions between pointers and integers.
 * this conversion would only be used when -svcomp option is on.
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
