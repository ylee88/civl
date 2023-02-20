package dev.civl.abc.ast.type.IF;

import dev.civl.abc.ast.entity.IF.OrdinaryEntity;
import dev.civl.abc.ast.node.IF.declaration.EnumeratorDeclarationNode;
import dev.civl.abc.ast.value.IF.Value;

/**
 * An Enumerator corresponds to one of the identifiers in the list in an
 * enumeration.
 * 
 * @author siegel
 * 
 */
public interface Enumerator extends OrdinaryEntity {

	@Override
	EnumeratorDeclarationNode getDefinition();

	@Override
	EnumerationType getType();

	/**
	 * Returns the optional constant integer value assigned to this enumerator,
	 * or <code>null</code> if this is missing.
	 * 
	 * @return the integer value of this enumerator constant
	 */
	Value getValue();
}
