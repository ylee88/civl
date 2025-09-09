/*******************************************************************************
 * Copyright (c) 2013 Stephen F. Siegel, University of Delaware.
 * 
 * This file is part of SARL.
 * 
 * SARL is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SARL is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SARL. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package dev.civl.sarl.reason.common;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.civl.sarl.IF.ModelResult;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SARLException;
import dev.civl.sarl.IF.SARLInternalException;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.reason.IF.ReasonerFactory;

/**
 * A very basic implementation of {@link Reasoner} based on a given
 * {@link Simplifier} and {@link TheoremProverFactory}. The context and other
 * information is already in the {@link Simplifier} that is created before this
 * {@link Reasoner} is created and becomes a field in this {@link Reasoner}. The
 * validity reasoning basically works by attempting to simplify an expression to
 * "true" or "false" and if that doesn't work then applying the prover.
 * 
 * @author Stephen F. Siegel
 */
public class CommonReasoner extends SimpleReasoner {

	/**
	 * The factory that was used to produce this {@link CommonReasoner}. It may
	 * be used again to produce new instances of {@link CommonReasoner} with
	 * different contexts if the need arises.
	 */
	private ReasonerFactory reasonerFactory;

	/**
	 * The theorem prover is created only if needed and once created will be
	 * re-used for subsequence queries. It is stored in this variable.
	 */
	private TheoremProver prover = null;

	/**
	 * The cached results of previous validity queries, i.e., calls to method
	 * {@link #valid(BooleanExpression)},
	 * {@link #validOrModel(BooleanExpression)}.
	 */
	private Map<BooleanExpression, ValidityResult> validityCache = new ConcurrentHashMap<>();

	/**
	 * The cached results of previous unsatisfiability queries, i.e., calls to
	 * method {@link #unsat(BooleanExpression)}.
	 */
	private Map<BooleanExpression, ValidityResult> unsatCache = new ConcurrentHashMap<>();

	/**
	 * @param reasonerFactory
	 *            the factory that created this {@link Reasoner}, and can be
	 *            used to create more {@link Reasoner}s if they are needed by
	 *            this one
	 * @param simplifier
	 *            a {@link Simplifier} formed from the context that undergirds
	 *            this {@link Reasoner}; can be used by this {@link Reasoner}
	 *            for simplifying expressions
	 * @param factory
	 *            a factory for producing new {@link TheoremProver}s
	 */
	public CommonReasoner(PreUniverse universe, IdealFactory idealFactory,
			TheoremProverFactory proverFactory, ReasonerFactory reasonerFactory,
			List<BooleanExpression> assumptionStack) {
		super(universe, idealFactory, proverFactory, assumptionStack);
		this.reasonerFactory = reasonerFactory;
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		boolean showQuery = universe().getShowQueries();

		if (showQuery) {
			PrintStream out = universe().getOutputStream();
			int id = universe().numValidCalls();

			out.println("Query " + id + " assumption: "
					+ getFullCollapsedContext());
			out.println("Query " + id + " predicate:  " + predicate);
		}
		if (predicate == null)
			throw new SARLException("Argument to Reasoner.valid is null.");
		else {
			ValidityResult result = null;
			BooleanExpression fullContext = getFullCollapsedContext();

			universe().incrementValidCount();
			if (fullContext.isTrue()) {
				ResultType resultType = predicate.getValidity();

				if (resultType != null) {
					switch (resultType) {
						case MAYBE :
							result = Prove.RESULT_MAYBE;
							break;
						case NO :
							result = Prove.RESULT_NO;
							break;
						case YES :
							result = Prove.RESULT_YES;
							break;
						default :
							throw new SARLInternalException("unrechable");
					}
				}
			}
			if (result == null) {
				BooleanExpression simplifiedPredicate = (BooleanExpression) simplify(
						predicate);

				if (simplifiedPredicate.isTrue())
					result = Prove.RESULT_YES;
				else if (simplifiedPredicate.isFalse())
					result = Prove.RESULT_NO;
				else {
					result = validityCache.get(simplifiedPredicate);
					if (result == null) {
						result = getProver().valid(simplifiedPredicate);
						validityCache.putIfAbsent(predicate, result);
					}
				}
			}
			if (showQuery) {
				int id = universe().numValidCalls() - 1;
				PrintStream out = universe().getOutputStream();

				out.println("Query " + id + " result:     " + result);
			}
			if (fullContext.isTrue()) {
				predicate.setValidity(result.getResultType());
			}
			return result;
		}
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		BooleanExpression simplifiedPredicate = (BooleanExpression) simplify(
				predicate);
		ValidityResult result;

		universe().incrementValidCount();
		if (simplifiedPredicate.isTrue())
			result = Prove.RESULT_YES;
		else {
			result = validityCache.get(simplifiedPredicate);
			if (result != null && result instanceof ModelResult)
				return result;
			result = getProver().validOrModel(simplifiedPredicate);
			validityCache.putIfAbsent(predicate, result);
		}
		return result;
	}

	@Override
	public boolean isValid(BooleanExpression predicate) {
		return valid(predicate).getResultType() == ResultType.YES;
	}

	@Override
	public Number extractNumber(NumericExpression expression) {
		NumericExpression simple = (NumericExpression) simplify(expression);

		return universe().extractNumber(simple);
	}

	@Override
	public boolean checkBigOClaim(BooleanExpression indexConstraint,
			NumericExpression lhs, NumericSymbolicConstant[] limitVars,
			int[] orders) {
		// strategy: create new context and add index constraint to the
		// assumption. Perform Taylor expansions where appropriate.
		// TODO: rename the indexConstraint and the limitVars if they conflict
		// with any free variables.
		PreUniverse universe = universe();
		BooleanExpression oldContext = getFullCollapsedContext();
		BooleanExpression newContext = universe.and(oldContext,
				indexConstraint);
		Reasoner newReasoner = reasonerFactory.getReasoner(newContext, true,
				new ProverFunctionInterpretation[0]);
		UnaryOperator<SymbolicExpression> taylorSubstituter = new TaylorSubstituter(
				universe, universe.objectFactory(), universe.typeFactory(),
				newReasoner, limitVars, orders);
		NumericExpression newLhs = (NumericExpression) taylorSubstituter
				.apply(lhs);

		return newReasoner
				.isValid(universe.equals(newLhs, universe.zeroReal()));
		// throw new UnsupportedOperationException();
	}

	private synchronized TheoremProver getProver() {
		return prover == null
				? (prover = proverFactory
						.newProver(getReducedCollapsedContext()))
				: prover;
	}

	/**
	 * @author ziqingluo
	 */
	@Override
	public ValidityResult unsat(BooleanExpression predicate) {
		boolean showQuery = universe().getShowQueries();

		if (showQuery) {
			PrintStream out = universe().getOutputStream();
			int id = universe().numValidCalls();

			out.println("Unsat-Query " + id + " assumption: "
					+ getFullCollapsedContext());
			out.println("Unsat-Query  " + id + " predicate :  " + predicate);
		}
		if (predicate == null)
			throw new SARLException("Argument to Reasoner.valid is null.");
		else {
			ValidityResult result = null;
			BooleanExpression formula = universe()
					.and(getFullCollapsedContext(), predicate);

			universe().incrementValidCount();
			BooleanExpression simplifiedFormula = (BooleanExpression) simplify(
					formula);

			result = unsatCache.get(simplifiedFormula);
			if (result == null) {
				if (simplifiedFormula.isFalse())
					result = Prove.RESULT_YES;
				else if (simplifiedFormula.isTrue())
					result = Prove.RESULT_NO;
				else
					result = proverFactory
							.newProver(universe().trueExpression())
							.unsat(simplifiedFormula);
			}
			unsatCache.putIfAbsent(predicate, result);
			if (showQuery) {
				int id = universe().numValidCalls() - 1;
				PrintStream out = universe().getOutputStream();

				out.println("UNSAT Query " + id + " result:     " + result);
			}
			if (getFullCollapsedContext().isTrue())
				predicate.setUnsatisfiability(result.getResultType());
			return result;
		}
	}
}
