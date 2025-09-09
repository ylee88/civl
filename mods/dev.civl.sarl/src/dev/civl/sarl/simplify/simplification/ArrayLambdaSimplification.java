package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicFunctionType;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;

public class ArrayLambdaSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression expr) {
		assert expr.operator() == SymbolicOperator.ARRAY_LAMBDA;
		SymbolicArrayType arrayType = (SymbolicArrayType) expr.type();
		SymbolicExpression function = (SymbolicExpression) expr.argument(0);
		BooleanExpressionFactory bf = util().getBooleanFactory();
		IdealFactory idf = util().getIdealFactory();
		SymbolicTypeFactory tf = idf.typeFactory();
		PreUniverse universe = util().getUniverse();

		if (arrayType.isComplete()) {
			SymbolicCompleteArrayType completeType = (SymbolicCompleteArrayType) arrayType;
			SymbolicCompleteArrayType newCompleteType = (SymbolicCompleteArrayType) simplify(arrayType);
			NumericExpression length = newCompleteType.extent();
			// function : [0,length-1] -> elementType
			// when simplifying function, can assume domain is [0,length-1].
			// create temporary symbolic constant i
			// create sub-context, add assumption 0<=i<length
			// simplify (APPLY function i) in this context, yielding g.
			// result is lambda(i).g.
			// small optimization: if function is a lambda expression, just
			// use the existing bound variable of that lambda expression,
			// no need to create a new one
			if (function.operator() == SymbolicOperator.LAMBDA) {
				NumericSymbolicConstant boundVar = (NumericSymbolicConstant) function
						.argument(0);
				SymbolicExpression body = (SymbolicExpression) function
						.argument(1);
				BooleanExpression boundAssumption = bf.and(
						idf.lessThanEquals(idf.zeroInt(), boundVar),
						idf.lessThan(boundVar, length));
				pushAssumption(boundAssumption);
				SymbolicExpression newBody = (SymbolicExpression) simplify(body);
				popAssumption();

				if (newBody == body && newCompleteType == completeType)
					return expr;

				SymbolicFunctionType functionType = (SymbolicFunctionType) function
						.type();
				SymbolicFunctionType newFunctionType = tf.functionType(
						functionType.inputTypes(), newBody.type());
				SymbolicExpression newFunction = universe.make(
						SymbolicOperator.LAMBDA, newFunctionType,
						new SymbolicObject[]{boundVar, newBody});
				SymbolicExpression result = universe.make(
						SymbolicOperator.ARRAY_LAMBDA, newCompleteType,
						new SymbolicObject[]{newFunction});

				return result;
			} else {
				// TODO: need a fresh bound variable
			}
		} else {
			// TODO: incomplete array type. // Still know i>=0.
		}
		return genericSimplify(expr);
		// return expr;
	}
}
