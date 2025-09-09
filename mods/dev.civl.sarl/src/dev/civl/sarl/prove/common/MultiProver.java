package dev.civl.sarl.prove.common;

import dev.civl.sarl.IF.TheoremProverException;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProver;

/**
 * An implementation of {@link TheoremProver} which wraps a sequence of
 * underlying {@link TheoremProver}s. To determine validity of a formula, this
 * prover invokes the underlying provers in sequence, until a conclusive result
 * is obtained.
 * 
 * @author Stephen F. Siegel
 */
public class MultiProver implements TheoremProver {

	private TheoremProver[] provers;

	public MultiProver(TheoremProver[] provers) {
		this.provers = provers;
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		for (TheoremProver prover : provers) {
			ValidityResult result = prover.valid(predicate);

			if (result.getResultType() != ResultType.MAYBE)
				return result;
		}
		return Prove.RESULT_MAYBE;
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		for (TheoremProver prover : provers) {
			ValidityResult result = prover.validOrModel(predicate);

			if (result.getResultType() != ResultType.MAYBE)
				return result;
		}
		return Prove.RESULT_MAYBE;
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate)
			throws TheoremProverException {
		for (TheoremProver prover : provers) {
			ValidityResult result = prover.unsat(predicate);

			if (result.getResultType() != ResultType.MAYBE)
				return result;
		}
		return Prove.RESULT_MAYBE;
	}
}
