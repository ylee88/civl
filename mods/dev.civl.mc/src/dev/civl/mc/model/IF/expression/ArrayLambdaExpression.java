package dev.civl.mc.model.IF.expression;

import java.util.List;

import dev.civl.mc.model.IF.type.CIVLCompleteArrayType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.util.IF.Pair;

/**
 * A CIVL-C quantified expression, including three components, bound variable
 * declaration list, (optional) restriction and expression. It has the following
 * syntax:<br>
 * 
 * <pre>
 * array-lambda: 
 *   $lambda ( variable-decl-list | restrict? ) expression ;
 * 
 * variable-decl-list:
 *   variable-decl-sub-list (; variable-decl-sub-list)* ;
 *   
 * variable-decl-sub-list:
 *   type ID (, ID)* (: domain)?
 * 
 * </pre>
 * 
 * e.g.,
 * 
 * <pre>
 * (int[n]) $lambda (int i) i*4
 * </pre>
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public interface ArrayLambdaExpression extends Expression {

	/**
	 * The list of bound variables.
	 * 
	 * TODO: WHAT IS THE Expression for?
	 * 
	 * @return
	 */
	List<Pair<List<Variable>, Expression>> boundVariableList();

	/**
	 * Boolean-valued expression assumed to hold when evaluating expression.
	 */
	Expression restriction();

	/** The expression e(x). */
	Expression expression();

	@Override
	CIVLCompleteArrayType getExpressionType();

}
