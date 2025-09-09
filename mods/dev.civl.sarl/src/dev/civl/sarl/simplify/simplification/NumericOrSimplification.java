package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.SymbolicExpression;

@Deprecated
public class NumericOrSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression x) {
		return x;
		/*
		if (x.operator() != SymbolicOperator.OR)
			return x;

		BooleanExpression expr = (BooleanExpression) x, result;
		Context subContext = newSubContext();
		ContextExtractor extractor = new ContextExtractor(subContext,
				new HashSet<>());
		boolean success;

		try {
			success = extractor.extractNumericOr(expr);
		} catch (InconsistentContextException e) {
			return info().falseExpr();
		}
		if (success)
			result = subContext.getFullAssumption();
		else
			result = expr;
		return result;
		*/
	}

}
