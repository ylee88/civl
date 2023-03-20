package dev.civl.abc.analysis.entity;

import dev.civl.abc.ast.type.IF.ObjectType;

/**
 * An abstract representation of an object type, used in the analysis of a
 * compound literal expression. These are not AST nodes.  They may be
 * reachable from an AST node representing a literal expression.
 * 
 * A literal type node represents a type as a tree. A node in this tree
 * represents either a scalar type (a leaf node), an array type, or a
 * struct/union type. An array type node has one child, the element type. A
 * struct/union type has a child for each field.
 * 
 * @author siegel
 *
 */
public abstract class LiteralTypeNode {

	private LiteralTypeNode parent;

	private ObjectType type;

	public LiteralTypeNode(ObjectType type) {
		this.type = type;
	}

	public abstract boolean hasFixedLength();

	public abstract int length();

	public abstract LiteralTypeNode getChild(int index);

	void setParent(LiteralTypeNode parent) {
		this.parent = parent;
	}

	public ObjectType getType() {
		return type;
	}

	public LiteralTypeNode parent() {
		return parent;
	}

}
