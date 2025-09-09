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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.TheoremProverException;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.simplify.IF.Range;
import dev.civl.sarl.simplify.simplification.Strategy;
import dev.civl.sarl.simplify.simplifier.Context;

/**
 * Very basic reasoner that does not use any theorem prover, only
 * simplification.
 * 
 * @author siegel
 *
 */
public class SimpleReasoner implements Reasoner {

	private Map<BooleanExpression, ValidityResult> validityCache = new ConcurrentHashMap<BooleanExpression, ValidityResult>();

	private Map<BooleanExpression, ValidityResult> unsatCache = new ConcurrentHashMap<BooleanExpression, ValidityResult>();

	protected List<Context> contextStack = new LinkedList<>();

	protected PreUniverse universe;

	protected TheoremProverFactory proverFactory;

	public SimpleReasoner(PreUniverse universe, IdealFactory idealFactory,
			TheoremProverFactory proverFactory,
			List<BooleanExpression> assumptionStack) {
		int stackSize = assumptionStack.size();

		assert stackSize > 0;
		this.universe = universe;
		this.proverFactory = proverFactory;
		UnaryOperator<SymbolicExpression> boundCleaner = universe
				.newMinimalBoundCleaner();
		Context lastContext = Context.newContext(universe, idealFactory,
				proverFactory,
				(BooleanExpression) boundCleaner.apply(assumptionStack.get(0)),
				true, null);

		contextStack.add(lastContext);
		for (int i = 1; i < stackSize; i++) {
			lastContext = lastContext
					.createSubContext((BooleanExpression) boundCleaner
							.apply(assumptionStack.get(i)));
			contextStack.add(lastContext);
		}
	}

	protected Context topContext() {
		return contextStack.get(contextStack.size() - 1);
	}

	public PreUniverse universe() {
		return universe;
	}

	@Override
	public BooleanExpression getReducedCollapsedContext() {
		return universe.and(getReducedContextStack());
	}

	@Override
	public BooleanExpression getFullCollapsedContext() {
		return universe.and(getFullContextStack());
	}

	@Override
	public BooleanExpression getReducedContext(int index) {
		return contextStack.get(index).getReducedAssumption();
	}

	@Override
	public BooleanExpression getFullContext(int index) {
		return contextStack.get(index).getFullAssumption();
	}

	@Override
	public List<BooleanExpression> getReducedContextStack() {
		List<BooleanExpression> reducedContextStack = new ArrayList<>(
				contextStack.size());
		for (int i = 0; i < contextStack.size(); i++) {
			reducedContextStack.add(getReducedContext(i));
		}
		return reducedContextStack;
	}

	@Override
	public List<BooleanExpression> getFullContextStack() {
		List<BooleanExpression> fullContextStack = new ArrayList<>(
				contextStack.size());
		for (int i = 0; i < contextStack.size(); i++) {
			fullContextStack.add(getFullContext(i));
		}
		return fullContextStack;
	}

	@Override
	public void aggressivelySimplifyTopContext(
			Set<SymbolicConstant> aggressiveSet) {
		topContext().simplifyAssumption(
				aggressiveSet == null ? new HashSet<>() : aggressiveSet);
	}

	@Override
	public Interval assumptionAsInterval(SymbolicConstant symbolicConstant) {
		return topContext().assumptionAsInterval(symbolicConstant);
	}

	@Override
	public <T extends SymbolicExpression> T simplify(T expression) {
		return simplify(expression, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SymbolicExpression> T simplify(T expression,
			Set<SymbolicConstant> aggressiveSet) {
		Strategy strategy = aggressiveSet == null
				? Strategy.standardStrategy()
				: Strategy.standardFreeVarStrategy(aggressiveSet);
		return (T) topContext().simplify(expression, strategy);
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		ValidityResult result = validityCache.get(predicate);

		universe().incrementProverValidCount();
		if (result == null) {
			BooleanExpression simple = (BooleanExpression) topContext()
					.simplify(predicate, Strategy.standardStrategy());
			Boolean concrete = universe().extractBoolean(simple);

			if (concrete == null)
				result = Prove.RESULT_MAYBE;
			else if (concrete)
				result = Prove.RESULT_YES;
			else
				result = Prove.RESULT_NO;
			validityCache.putIfAbsent(predicate, result);
		}
		return result;
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		throw new TheoremProverException(
				"SimpleIdealProver cannot be used to find models");
	}

	@Override
	public Map<SymbolicConstant, SymbolicExpression> constantSubstitutionMap() {
		return topContext().getAllSolvedVariables();
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
	public Interval intervalApproximation(NumericExpression expr) {
		Range range = topContext().computeRange((RationalExpression) expr);
		Interval result = range.intervalOverApproximation();

		return result;
	}

	@Override
	public boolean checkBigOClaim(BooleanExpression indexConstraint,
			NumericExpression lhs, NumericSymbolicConstant[] limitVars,
			int[] orders) {
		return false;
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate) {
		ValidityResult result = unsatCache.get(predicate);

		universe().incrementProverValidCount();
		if (result == null) {
			BooleanExpression simple = (BooleanExpression) topContext()
					.simplify(predicate, Strategy.standardStrategy());
			Boolean concrete = universe().extractBoolean(simple);

			if (concrete == null)
				result = Prove.RESULT_MAYBE;
			else if (concrete)
				result = Prove.RESULT_NO;
			else
				result = Prove.RESULT_YES;
			unsatCache.putIfAbsent(predicate, result);
		}
		return result;
	}
}
