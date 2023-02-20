package dev.civl.mc.dynamic.IF;

import dev.civl.mc.dynamic.common.CommonSymbolicUtility;
import dev.civl.mc.dynamic.immutable.ImmutableDynamicMemoryLocationSetFactory;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * Entry point of the module <strong>dynamic</strong>.
 * 
 * @author Manchun Zheng, Ziqing Luo
 * 
 */
public class Dynamics {

	/**
	 * Creates a new instance of symbolic utility.
	 * 
	 * @param universe
	 *            The symbolic universe to be used.
	 * @param modelFactory
	 *            The model factory to be used.
	 * @return The new symbolic utility created.
	 */
	public static SymbolicUtility newSymbolicUtility(SymbolicUniverse universe,
			ModelFactory modelFactory, StateFactory stateFactory) {
		return new CommonSymbolicUtility(universe, modelFactory, stateFactory);
	}

	/**
	 * Creates a new instance of {@link DynamicMemoryLocationSetFactory}.
	 * 
	 * @param universe
	 *            A reference to a {@link SymbolicUniverse}
	 * @param typeFactory
	 *            a reference to a {@link CIVLTypeFactory}
	 * @param collectedScopeValue
	 *            the unique scope value that represents a scope has been
	 *            collected.
	 * @return a new instance of {@link DynamicMemoryLocationSetFactory}.
	 */
	public static DynamicMemoryLocationSetFactory newDynamicMemoryLocationSetFactory(
			SymbolicUniverse universe, CIVLTypeFactory typeFactory,
			SymbolicExpression collectedScopeValue) {
		return new ImmutableDynamicMemoryLocationSetFactory(universe, typeFactory,
				collectedScopeValue);
	}
}
