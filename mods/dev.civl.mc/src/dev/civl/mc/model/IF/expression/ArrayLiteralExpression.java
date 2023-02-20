package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.type.CIVLArrayType;
import dev.civl.mc.model.IF.type.CIVLType;

public interface ArrayLiteralExpression extends LiteralExpression {

	Expression[] elements();

	void setElements(Expression[] elements);

	CIVLArrayType arrayType();

	CIVLType elementType();

}
