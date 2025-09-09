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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.reason.IF.ReasonerFactory;

/**
 * Basic factory for producing instances of {@link CommonReasoner}.
 * 
 * @author Stephen F. Siegel
 */
public class CommonReasonerFactory implements ReasonerFactory {

	/**
	 * Cached results of previous creation of @{link Reasoner}s. The idea is to
	 * have at most one {@link Reasoner} for each boolean expression
	 * ("context").
	 */
	private Map<List<BooleanExpression>, Reasoner> reasonerCache = new ConcurrentHashMap<>();

	private PreUniverse universe;

	private IdealFactory idealFactory;

	/**
	 * The theorem prover factory associated to this reasoner factory. It will
	 * be used by the {@link Reasoner}s produced by this factory to instantiate
	 * new {@link TheoremProver}s as the need arises.
	 */
	private TheoremProverFactory proverFactory;

	/**
	 * Produces a new {@link CommonReasonerFactory} based on the given
	 * <code>simplifierFactory</code> and <code>proverFactory</code>.
	 * 
	 * @param simplifierFactory
	 *            the factory that will be used by {@link Reasoner}s produced by
	 *            this factory to create new {@link Simplifier}s when the need
	 *            arises
	 * @param proverFactory
	 *            the factory that will be used by {@link Reasoner}s produced by
	 *            this factory to create new {@link TheoremProver}s when the
	 *            need arises
	 */
	public CommonReasonerFactory(PreUniverse universe,
			IdealFactory idealFactory, TheoremProverFactory proverFactory) {
		this.universe = universe;
		this.idealFactory = idealFactory;
		this.proverFactory = proverFactory;
	}

	@Override
	public Reasoner getReasoner(BooleanExpression context,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation[] proverPredicates) {
		List<BooleanExpression> contextStack = new ArrayList<>(1);
		contextStack.add(context);
		return getReasoner(contextStack, useBackwardSubstitution,
				proverPredicates);
	}

	@Override
	public Reasoner getReasoner(List<BooleanExpression> assumptionStack,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation[] proverPredicates) {
		Reasoner result = reasonerCache.get(assumptionStack);

		if (result == null) {
			Reasoner newReasoner = new CommonReasoner(universe, idealFactory,
					proverFactory, this, assumptionStack);

			result = reasonerCache.putIfAbsent(assumptionStack, newReasoner);
			return result == null ? newReasoner : result;
		}
		return result;
	}
}
