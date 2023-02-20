package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.type.CIVLStructOrUnionType;
/**
 * 
 * @author zmanchun
 *
 */
public interface StructOrUnionLiteralExpression extends LiteralExpression {

	CIVLStructOrUnionType structOrUnionType();

	boolean isStruct();
}
