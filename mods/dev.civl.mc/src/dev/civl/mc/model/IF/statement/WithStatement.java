package dev.civl.mc.model.IF.statement;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.expression.Expression;

public interface WithStatement extends Statement {
	boolean isEnter();

	boolean isExit();

	Expression collateState();

	CIVLFunction function();
}
