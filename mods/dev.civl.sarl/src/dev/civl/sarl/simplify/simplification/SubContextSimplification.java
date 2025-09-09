package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.simplify.simplifier.MutableContext;

/**
 * A {@link Simplification} that proceeds by creating a {@link SubContext} of
 * the current {@link MutableContext} to process a {@link BooleanSymbolicExpression}.
 * Currently, this is used to process expressions with operator AND, LESS_THAN,
 * LESS_THAN_EQUALS, NEQ and also EQUALS in the case where the arguments are
 * numeric.
 */
public class SubContextSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression expression) {
		if (expression.type().isBoolean()) {
			pushAssumption((BooleanExpression) expression);
			BooleanExpression simplifiedAssumption = getFullAssumption();
			popAssumption();

			return simplifiedAssumption;
		}
		return expression;
	}

}
