package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.PointerType;

/**
 * This conversion will usually be undefined behavior, but it is still used and
 * we try to support it.
 * 
 * @author zmanchun
 *
 */
public interface Integer2PointerConversion extends Conversion {
	@Override
	IntegerType getOldType();

	@Override
	PointerType getNewType();
}
