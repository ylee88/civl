package dev.civl.sarl.simplify.simplifier;

import java.util.Map;
import java.util.Set;

import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.simplify.IF.Range;
import dev.civl.sarl.simplify.simplification.ProverHeuristic;
import dev.civl.sarl.simplify.simplification.Strategy;

public interface Context {

	/**
	 * Gets the variables that have been "solved" by this or some super context,
	 * i.e., have an expression in terms of other (unsolved) variables. These
	 * variables can be entirely eliminated from the state.
	 * 
	 * @return mapping from solved variables to their values
	 */
	Map<SymbolicConstant, SymbolicExpression> getAllSolvedVariables();

	/**
	 * If this assumption is exactly equivalent to the claim that the given
	 * symbolic constant lies in some interval, returns that interval.
	 * Otherwise, returns {@code null}.
	 * 
	 * @param symbolicConstant
	 *            the symbolic constant
	 * @return the interval or {@code null}
	 */
	Interval assumptionAsInterval(SymbolicConstant symbolicConstant);

	Context createSubContext(BooleanExpression assumption);

	void simplifyAssumption(Set<SymbolicConstant> aggressiveSet);

	/**
	 * Computes an (over-)estimate of the possible values of a
	 * {@link RationalExpression} based on the current assumptions of this
	 * {@link MutableContext}. Points at which this rational expression are
	 * undefined (because, e.g., the denominator is 0) are ignored.
	 * 
	 * @param expression
	 *            a non-{@code null} {@link RationalExpression}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code rat} at any point satisfying the assumptions of
	 *         this context will lie in that range
	 */
	Range computeRange(NumericExpression expression);

	SymbolicObject simplify(SymbolicObject object, Strategy strategy);

	SymbolicExpression genericSimplify(Strategy strategy, SymbolicExpression x);

	/**
	 * Looks for cached result of validity (or unsatisfiability) of predicate.
	 * For the context "true", results are cached directly in the predicate.
	 * Otherwise, internal caches are checked.
	 * 
	 * @param predicate
	 *            boolean expression whose validity is being checked
	 * @param getModel
	 * @param checkUnsat
	 *            specifies whether to search for unsatisfiability or validity
	 *            result in caches
	 * @return cached result from previous check on this predicate or
	 *         <code>null</code> if no such result is cached
	 */
	ValidityResult checkProverCache(BooleanExpression expr, boolean getModel,
			boolean checkUnsat);

	/**
	 * Updates the validity (or unsatisfiability) cache with the specified
	 * result.
	 * 
	 * @param predicate
	 *            boolean expression whose validity was checked
	 * @param result
	 *            the (non-<code>null</code>) result of the validity check on
	 *            <code>predicate</code>
	 * @param updateUnsatCache
	 *            if <code>true</code>, update unsatCache. Otherwise, update
	 *            validityCache.
	 */
	void updateCache(BooleanExpression predicate, ValidityResult result,
			boolean updateUnsatCache);

	/*
	 * All 3 methods use collapsed reduced context for assumption given to
	 * provers. Solved variables are substituted into predicate. No call to
	 * simplify is made so is probably a good idea to do beforehand.
	 */
	ValidityResult unsat(BooleanExpression predicate,
			ProverHeuristic heuristic);
	ValidityResult valid(BooleanExpression predicate,
			ProverHeuristic heuristic);
	ValidityResult validOrModel(BooleanExpression predicate,
			ProverHeuristic heuristic);

	// Convenience functions
	boolean isUnsat(BooleanExpression predicate, ProverHeuristic heuristic);
	boolean isValid(BooleanExpression predicate, ProverHeuristic heuristic);

	BooleanExpression getReducedAssumption();

	BooleanExpression getFullAssumption();

	boolean contextIsTrivial();
	boolean contextStackIsTrivial();

	public static Context newContext(PreUniverse universe,
			IdealFactory idealFactory, TheoremProverFactory proverFactory,
			BooleanExpression assumption, boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		return new MutableContext(universe, idealFactory, proverFactory,
				assumption, useBackwardSubstitution, logicFunctions);
	}

}