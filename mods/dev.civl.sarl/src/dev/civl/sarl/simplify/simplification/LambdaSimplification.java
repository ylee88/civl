package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;

public class LambdaSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression expr) {
		if (expr.operator() == SymbolicOperator.LAMBDA) {
			// lambda x . e;
			SymbolicConstant boundVar = (SymbolicConstant) expr.argument(0);
			SymbolicExpression body = (SymbolicExpression) expr.argument(1);
			SymbolicExpression body2 = (SymbolicExpression) simplify(body);

			if (body2 == body)
				return expr;
			return universe.lambda(boundVar, body2);
		} else {
			return expr;
		}
	}

}
