package dev.civl.mc.model.IF.statement;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.expression.Expression;

public interface UpdateStatement extends Statement {

	Expression collator();

	CallOrSpawnStatement call();

	CIVLFunction function();

	Expression[] arguments();
}
