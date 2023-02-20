package dev.civl.mc.semantics.common;

import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.sarl.IF.expr.BooleanExpression;

public interface CIVLUnaryOperator<SymbolicExpression> {
	SymbolicExpression apply(BooleanExpression context,
			SymbolicExpression value, CIVLType type);
}
