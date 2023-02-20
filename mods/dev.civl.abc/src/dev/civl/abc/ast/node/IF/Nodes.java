package dev.civl.abc.ast.node.IF;

import dev.civl.abc.ast.node.common.CommonNodeFactory;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.IF.ValueFactory;
import dev.civl.abc.config.IF.Configuration;

/**
 * A factory class providing a static method to produce a new
 * {@link NodeFactory}.
 * 
 * @author siegel
 * 
 */
public class Nodes {

	/**
	 * Create a new node factory that uses the given type factory and the given
	 * value factory.
	 * 
	 * @param configuration
	 *            the configuration associated with this translation task
	 * @param typeFactory
	 *            the factory for producing types
	 * @param valueFactory
	 *            the factory for producing values (constants)
	 * @return the new node factory
	 */
	public static NodeFactory newNodeFactory(Configuration configuration,
			TypeFactory typeFactory, ValueFactory valueFactory) {
		return new CommonNodeFactory(configuration, typeFactory, valueFactory);
	}

}
