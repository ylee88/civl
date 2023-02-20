package dev.civl.abc.ast.type.IF;

import dev.civl.abc.ast.node.IF.expression.LambdaNode;

/**
 * A lambda type represents the type of an lambda expression (see
 * {@link LambdaNode} . A lambda type consists of at most one free variable type
 * v and a lambda term type t. Both v and t are {@link UnqualifiedObjectType}s
 * 
 * @author ziqing
 *
 */
public interface LambdaType extends UnqualifiedObjectType {
	/**
	 * 
	 * @return The type the free variable v. null if v is absent.
	 */
	UnqualifiedObjectType freeVariableType();

	/**
	 * 
	 * @return The return type of the lambda function
	 */
	UnqualifiedObjectType lambdaFunctionReturnType();
}
