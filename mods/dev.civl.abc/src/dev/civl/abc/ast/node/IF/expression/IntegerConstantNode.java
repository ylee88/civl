package dev.civl.abc.ast.node.IF.expression;

import dev.civl.abc.ast.value.IF.IntegerValue;

/**
 * An integer constant node represents the occurrence of a literal integer
 * constant in a program. It encodes a concrete integer value. See C11 Sec.
 * 6.4.4.1.
 * 
 * @author siegel
 * 
 */
public interface IntegerConstantNode extends ConstantNode {

	@Override
	IntegerValue getConstantValue();

	@Override
	IntegerConstantNode copy();

}
