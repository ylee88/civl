package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.type.IF.DomainType;
import dev.civl.abc.ast.type.IF.ObjectType;

/**
 * Converts a regular range to a (one-dimension) domain.
 * 
 * @author zmanchun
 *
 */
public interface RegularRangeToDomainConversion extends Conversion {
	/**
	 * The range type <code>$range</code>.
	 */
	@Override
	ObjectType getOldType();

	/**
	 * The domain type <code>$domain</code>.
	 */
	@Override
	DomainType getNewType();
}
