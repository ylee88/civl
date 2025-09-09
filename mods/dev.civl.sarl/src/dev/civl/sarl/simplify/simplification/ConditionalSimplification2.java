package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;

/**
 * Used to simplify a conditional symbolic expression p?a:b.
 * 
 * First, p is simplified. Then a is simplified under the context p. Then b is
 * simplified under the context !p.
 * 
 * @author siegel
 */
public class ConditionalSimplification2 extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression x) {
		if (x.operator() != SymbolicOperator.COND)
			return x;

		BooleanExpression condition = (BooleanExpression) x.argument(0);
		SymbolicExpression trueValue = (SymbolicExpression) x.argument(1),
				falseValue = (SymbolicExpression) x.argument(2);

		BooleanExpression condition2 = (BooleanExpression) simplify(
				condition);

		if (proveUnsat(condition2))
			return (SymbolicExpression) simplify(falseValue);
		if (proveValid(condition2))
			return (SymbolicExpression) simplify(trueValue);

		pushAssumption(condition2);
		SymbolicExpression trueValue2 = (SymbolicExpression) simplify(trueValue);
		popAssumption();
		
		pushAssumption(universe.not(condition2));
		SymbolicExpression falseValue2 = (SymbolicExpression) simplify(falseValue);
		popAssumption();

		if (condition2 == condition && trueValue2 == trueValue
				&& falseValue2 == falseValue)
			return x;

		SymbolicExpression result = universe.cond(condition2, trueValue2,
				falseValue2);

		return result;
	}
}
