package dev.civl.mc.model.IF.expression;

import java.util.List;

import dev.civl.mc.model.IF.CIVLFunction;

/**
 * A system guard expression is a pseudo guard expression for system function
 * calls. Its evaluation is actually done in the corresponding library executor.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public interface SystemGuardExpression extends Expression {
	/**
	 * The name of the library that the invoked function belongs to.
	 * 
	 * @return
	 */
	String library();

	/**
	 * The invoked function.
	 * 
	 * @return
	 */
	CIVLFunction function();

	/**
	 * The list of arguments that this function call uses.
	 * 
	 * @return
	 */
	List<Expression> arguments();
}
