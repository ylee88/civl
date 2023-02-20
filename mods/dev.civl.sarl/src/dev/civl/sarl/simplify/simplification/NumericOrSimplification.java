package dev.civl.sarl.simplify.simplification;

import java.util.HashSet;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.simplify.simplifier.Context;
import dev.civl.sarl.simplify.simplifier.ContextExtractor;
import dev.civl.sarl.simplify.simplifier.IdealSimplifierWorker;
import dev.civl.sarl.simplify.simplifier.InconsistentContextException;

public class NumericOrSimplification extends Simplification {

	public NumericOrSimplification(IdealSimplifierWorker worker) {
		super(worker);
	}

	@Override
	public SymbolicExpression apply(SymbolicExpression x) {
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
	}

	@Override
	public SimplificationKind kind() {
		return SimplificationKind.NUMERIC_OR;
	}

}
