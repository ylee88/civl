package dev.civl.abc.ast.node.IF.type;

/**
 * An atomic type, specified by "_Atomic ( type-name )". See C11 Sec. 6.7.2.4.
 * 
 * @author siegel
 * 
 */
public interface AtomicTypeNode extends TypeNode {

	TypeNode getBaseType();

	@Override
	AtomicTypeNode copy();

}
