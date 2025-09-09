package dev.civl.sarl.simplify.simplification;

import static dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator.EXISTS;
import static dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator.FORALL;

import dev.civl.sarl.IF.CoreUniverse.ForallStructure;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;

public class QuantifierSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression expr) {
		SymbolicOperator op = expr.operator();

		if (op == FORALL) {
			ForallStructure forall = universe
					.getForallStructure((BooleanExpression) expr);

			if (forall != null) {
				NumericExpression incLow = forall.lowerBound, excUp = universe
						.add(forall.upperBound, universe.oneInt()), excUp2;

				incLow = (NumericExpression) simplify(incLow);
				excUp2 = (NumericExpression) simplify(excUp);

				// restrict:
				BooleanExpression rstc = universe.lessThanEquals(incLow,
						forall.boundVariable);

				rstc = universe.and(rstc,
						universe.lessThan(forall.boundVariable, excUp2));
				rstc = (BooleanExpression) simplify(rstc);

				// TODO: Do we want to change this to a call to proveUnsat?
				if (rstc.isFalse()) {
					return universe.trueExpression();
				}

				pushAssumption(rstc);
				BooleanExpression newBody = (BooleanExpression) simplify(forall.body);
				popAssumption();

				if (newBody == forall.body && incLow == forall.lowerBound
						&& excUp == excUp2)
					return expr;
				else
					return universe.forallInt(forall.boundVariable, incLow,
							excUp2, newBody);
			}
		}
		if (op == FORALL || op == EXISTS) {
			SymbolicConstant boundVar = (SymbolicConstant) expr.argument(0);
			BooleanExpression body = (BooleanExpression) expr.argument(1);
			BooleanExpression body2 = (BooleanExpression) simplify(body);

			if (body == body2)
				return expr;
			return op == SymbolicOperator.FORALL
					? universe.forall(boundVar, body2)
					: universe.exists(boundVar, body2);
		}
		return expr;
	}

}
