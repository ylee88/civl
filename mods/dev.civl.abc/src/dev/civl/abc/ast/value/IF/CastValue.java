package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.Type;

/**
 * A value obtained by casting a value to a new type.
 * 
 * @author siegel
 * 
 */
public interface CastValue extends Value {

	Type getCastType();

	Value getArgument();

}
