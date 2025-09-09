package dev.civl.sarl.simplify.simplification;

import java.util.List;
import java.util.Set;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * A simple wrapper class which combines a {@link SimplificationSelector} and a
 * {@link ProverHeuristic}.
 * 
 * @author awilton
 *
 */
public class Strategy implements SimplificationSelector, ProverHeuristic {

	private SimplificationSelector selector;
	private ProverHeuristic heuristic;

	public Strategy(SimplificationSelector selector,
			ProverHeuristic heuristic) {
		this.selector = selector;
		this.heuristic = heuristic;
	}

	@Override
	public boolean attemptValid(BooleanExpression predicate) {
		return heuristic.attemptValid(predicate);
	}

	@Override
	public boolean attemptUnsat(BooleanExpression predicate) {
		return heuristic.attemptUnsat(predicate);
	}

	@Override
	public List<Simplification> select(SymbolicExpression symbExpr) {
		return selector.select(symbExpr);
	}

	public static Strategy standardStrategy() {
		return new Strategy(new StandardSimplificationSelector(),
				new EmptyProverHeuristic());
	}

	public static Strategy standardFreeVarStrategy(
			Set<SymbolicConstant> freeVars) {
		return new Strategy(new StandardSimplificationSelector(),
				new FreeVarProverHeuristic(freeVars));
	}
}
