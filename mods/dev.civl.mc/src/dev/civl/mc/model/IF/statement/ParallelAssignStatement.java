package dev.civl.mc.model.IF.statement;

import java.util.List;

import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.util.IF.Pair;

public interface ParallelAssignStatement extends Statement {
	List<Pair<LHSExpression, Expression>> assignments();
}
