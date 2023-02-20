package dev.civl.abc.ast.node.IF.compound;

import dev.civl.abc.ast.type.IF.Type;

/**
 * A literal object is either a {@link ScalarLiteralObject} or a
 * {@link CompoundLiteralObject}. The elements of a compound initializer
 * designate literal objects in a hierarchical way.
 * 
 * @author siegel
 * 
 */
public interface LiteralObject {
	Type getType();
}
