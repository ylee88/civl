package dev.civl.sarl.reason.common;

import java.util.Arrays;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;

/**
 * The key of cached {@link Reasoner}s which is a pair of a
 * {@link BooleanExpression} which represents the context and an array of
 * {@link ProverFunctionInterpretation}
 * 
 * @author ziqing
 */
public class ReasonerCacheKey {

	final private BooleanExpression context;

	final private ProverFunctionInterpretation[] proverPredicates;

	ReasonerCacheKey(BooleanExpression context,
			ProverFunctionInterpretation[] proverPredicates) {
		this.context = context;
		this.proverPredicates = proverPredicates;
	}

	@Override
	public int hashCode() {
		return context.hashCode() ^ Arrays.hashCode(proverPredicates) ^ 3063907;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReasonerCacheKey) {
			ReasonerCacheKey otherKey = (ReasonerCacheKey) obj;

			if (otherKey.context.equals(context))
				return Arrays.equals(proverPredicates,
						otherKey.proverPredicates);
		}
		return false;
	}
}
