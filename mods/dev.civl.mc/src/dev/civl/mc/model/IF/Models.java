/**
 * 
 */
package dev.civl.mc.model.IF;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.model.common.CommonModelBuilder;
import dev.civl.sarl.IF.SymbolicUniverse;

/**
 * This is the entry point of the module <strong>model</strong>.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public class Models {

	/**
	 * Creates a new instance of model builder.
	 * 
	 * @param universe
	 *            The symbolic universe to be used.
	 * @return The new model builder created.
	 */
	public static ModelBuilder newModelBuilder(SymbolicUniverse universe,
			CIVLConfiguration config) {
		return new CommonModelBuilder(universe, config);
	}

	/**
	 * Creates a new instance of model builder.
	 * 
	 * @param factory
	 *            The model factory to be used.
	 * @return The new model builder created.
	 */
	public static ModelBuilder newModelBuilder(ModelFactory factory) {
		return new CommonModelBuilder(factory.universe(), factory);
	}

}
