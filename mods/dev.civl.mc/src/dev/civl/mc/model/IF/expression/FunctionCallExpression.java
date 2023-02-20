package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.type.CIVLType;

public interface FunctionCallExpression extends Expression {

	CallOrSpawnStatement callStatement();

	void setExpressionType(CIVLType returnType);
}
