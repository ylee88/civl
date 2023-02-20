package dev.civl.abc.ast.conversion.IF;

import dev.civl.abc.ast.conversion.common.CommonConversionFactory;
import dev.civl.abc.ast.type.IF.TypeFactory;

/**
 * Factory class providing static method to produce a {@link ConversionFactory}.
 * The conversion factory can be used to produce any number of conversion
 * objects.
 * 
 * @author siegel
 * 
 */
public class Conversions {

	/**
	 * Produce a new conversion factory which uses the given type factory.
	 * 
	 * @param config
	 *            the configuration of the translation task
	 * @param typeFactory
	 *            a type factory
	 * @return the new conversion factory
	 */
	public static ConversionFactory newConversionFactory(TypeFactory typeFactory) {
		return new CommonConversionFactory(typeFactory);
	}
}
