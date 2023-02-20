package dev.civl.mc.state.common.immutable;

import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.sarl.IF.Predicate;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.type.SymbolicType.SymbolicTypeKind;

/**
 * This class implements the SARL predicate of symbolic constants. It provides
 * the method apply which returns true if the given symbolic constant's name is
 * reserved and should never be renamed. The condition is:
 * <ul>
 * <li>the symbolic constant has function type; or</li>
 * <li>the symbolic constant has name UNDEFINED or INVALID, which are special
 * symbolic constant used by CIVL</li>
 * </ul>
 * 
 * @author Manchun Zheng
 *
 */
public class ReservedConstant implements Predicate<SymbolicConstant> {
	@Override
	public boolean apply(SymbolicConstant x) {
		if (x.type().typeKind() == SymbolicTypeKind.FUNCTION)
			return true;
		return ModelConfiguration.RESERVE_NAMES.contains(x.name().getString());
	}

}
