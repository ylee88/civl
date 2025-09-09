package dev.civl.sarl.reason.common;

import java.util.ArrayList;
import java.util.List;
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
 * A factory for producing instances of {@link ContextMinimizingReasoner}.
 * 
 * @author Stephen F. Siegel
 */
public class ContextMinimizingReasonerFactory implements ReasonerFactory {

	/**
	 * Factory used to produce new {@link TheoremProver}s, which will be used by
	 * the reasoners to check validity.
	 */
	private TheoremProverFactory proverFactory;

	/**
	 * Symbolic universe used to produce new symbolic expressions.
	 */
	private PreUniverse universe;

	private IdealFactory idealFactory;

	/**
	 * Caches the {@link Reasoner}s associated to each boolean expression. In
	 * this way there is at most one {@link Reasoner} associated to each
	 * equivalence class of a {@link ReasonerCacheKey}, where the equivalence
	 * relation is determined by the {@link ReasonerCacheKey#equals(Object)}
	 * method.
	 */
	private Map<ReasonerCacheKey, ContextMinimizingReasoner> reasonerMap = new ConcurrentHashMap<>();

	/**
	 * Creates new factory based on the given symbolic universe, theorem prover
	 * factory, and simplifier factory. Those objects will be used by the
	 * reasoners produced by this factory.
	 * 
	 * @param universe
	 *            symbolic universe used to produce new symbolic expressions
	 * @param proverFactory
	 *            used to produce new {@link TheoremProver}s, which will be used
	 *            by the reasoners to check validity
	 * @param simplifierFactory
	 *            used to produce new {@link Simplifier}s, which will be used by
	 *            the reasoners to simplify expressions
	 */
	public ContextMinimizingReasonerFactory(PreUniverse universe,
			IdealFactory idealFactory, TheoremProverFactory proverFactory) {
		this.universe = universe;
		this.idealFactory = idealFactory;
		this.proverFactory = proverFactory;
		this.universe = universe;
	}

	@Override
	public ContextMinimizingReasoner getReasoner(BooleanExpression context,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		List<BooleanExpression> contextStack = new ArrayList<>(1);
		contextStack.add(context);
		return getReasoner(contextStack, useBackwardSubstitution,
				logicFunctions);
	}

	@Override
	public ContextMinimizingReasoner getReasoner(
			List<BooleanExpression> contextStack,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		for (BooleanExpression subContext : contextStack)
			assert subContext.isCanonic();
		ReasonerCacheKey key = new ReasonerCacheKey(contextStack,
				logicFunctions);
		ContextMinimizingReasoner result = reasonerMap.get(key);

		if (result == null) {
			ContextMinimizingReasoner newContextMinimizingReasoner = new ContextMinimizingReasoner(
					universe, idealFactory, proverFactory, this, contextStack,
					useBackwardSubstitution, logicFunctions);

			result = reasonerMap.putIfAbsent(key, newContextMinimizingReasoner);
			return result == null ? newContextMinimizingReasoner : result;
		}
		return result;
	}
}
