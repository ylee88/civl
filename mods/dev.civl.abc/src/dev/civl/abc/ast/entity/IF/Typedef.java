package dev.civl.abc.ast.entity.IF;

import dev.civl.abc.ast.node.IF.declaration.TypedefDeclarationNode;

/**
 * An abstract representation of a <code>typedef</code> construct: binds a name
 * to a type. The method {@link #getName()} in {@link Entity} returns the name;
 * the method {@link #getType()} returns the type.
 * 
 * @author siegel
 * 
 */
public interface Typedef extends OrdinaryEntity {

	@Override
	TypedefDeclarationNode getDefinition();

}
