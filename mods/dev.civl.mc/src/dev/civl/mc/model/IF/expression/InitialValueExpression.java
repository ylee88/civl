package dev.civl.mc.model.IF.expression;

import dev.civl.mc.model.IF.variable.Variable;

/**
 * An expression yielding the initial value of a variable.
 * 
 * @author siegel
 * 
 */
public interface InitialValueExpression extends Expression {

	Variable variable();

}
