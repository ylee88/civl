package dev.civl.sarl.simplify.simplification;

import java.util.HashSet;
import java.util.Set;

import dev.civl.sarl.IF.SARLConstants;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;

/**
 * Some things that should happen:
 * 
 * all clauses involving inequalities/equations on the same monic should be
 * combined and unified.
 * 
 * p||!p should be reduced to true.
 * 
 * 
 * 
 * @author siegel
 *
 */
public class OrSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression x) {
		if (x.operator() != SymbolicOperator.OR)
			return x;

		BooleanExpression expr = (BooleanExpression) x;
		BooleanExpression[] args = util().getBooleanFactory()
				.getArgumentsAsArray(expr);
		int n = args.length;
		Set<BooleanExpression> nots = new HashSet<>();

		for (BooleanExpression arg : args)
			nots.add(universe.not(arg));
		for (int i = 0; i < n; i++) {
			BooleanExpression arg = args[i];

			if (nots.contains(arg)) {
				return universe.trueExpression();
			}
		}
		if (SARLConstants.useDoubleOrNegation) {
			BooleanExpression result = universe.not(
					(BooleanExpression) simplify(universe.not(expr)));
			return result;
		} else {
			return expr;
		}
	}
}
