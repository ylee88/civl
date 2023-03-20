package dev.civl.abc.ast.node.IF.compound;

import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.type.IF.Field;

/**
 * A field designator is used in an initializer for a struct or union. It is a
 * reference to a particular member (field) of that struct or union.
 * 
 * @author siegel
 * 
 */
public interface FieldDesignatorNode extends DesignatorNode {

	/**
	 * The name of the field being designated for initialization.
	 * 
	 * @return the field name
	 * @see #setField(IdentifierNode)
	 */
	IdentifierNode getField();

	/**
	 * Sets the name of the field being designated for initialization.
	 * 
	 * @param name
	 *            the field name
	 * @see #getField()
	 */
	void setField(IdentifierNode name);
	
	Field[] getNavigationSequence();

	void setNavigationSequence(Field[] sequence);

	@Override
	FieldDesignatorNode copy();
}
