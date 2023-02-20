package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.Scope;

public interface FunctionIdentifierExpression extends Expression {
	/**
	 * 
	 * @return The scope that the function is declared
	 */
	Scope scope();
	
	CIVLFunction function();
}
