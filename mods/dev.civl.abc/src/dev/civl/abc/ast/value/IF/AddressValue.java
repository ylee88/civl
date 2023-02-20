package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.PointerType;

/**
 * A value of the form "&(lhs)", where lhs is a left hand side expression. For
 * example "&(x)" or "&(a[i].vel)".
 * 
 * @author siegel
 * 
 */
public interface AddressValue extends Value {

	@Override
	PointerType getType();

}
