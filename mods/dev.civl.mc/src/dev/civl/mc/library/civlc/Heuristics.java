package dev.civl.mc.library.civlc;

import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class Heuristics {

	private SymbolicUniverse universe;
	static double overhead = 0;

	public static class Query {
		public final BooleanExpression context;
		public final BooleanExpression query;

		public Query(BooleanExpression context, BooleanExpression query) {
			this.context = context;
			this.query = query;
		}
	}

	public Heuristics(SymbolicUniverse universe) {
		this.universe = universe;
	}

	public Query applyHeuristicSimplifications(BooleanExpression context,
			BooleanExpression predicate) {
		UnaryOperator<SymbolicExpression> simplifier = new SteppedUniversalCombination(
				universe);

		context = (BooleanExpression) simplifier.apply(context);
		predicate = (BooleanExpression) simplifier.apply(predicate);

		simplifier = new ConditionalSimplification(universe);
		context = (BooleanExpression) simplifier.apply(context);
		predicate = (BooleanExpression) simplifier.apply(predicate);
		return new Query(context, predicate);
	}
}
