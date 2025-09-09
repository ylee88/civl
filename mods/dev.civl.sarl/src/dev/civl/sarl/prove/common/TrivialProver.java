package dev.civl.sarl.prove.common;

import dev.civl.sarl.IF.TheoremProverException;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProver;

public class TrivialProver implements TheoremProver {

	public TrivialProver() {}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		return predicate.isTrue() ? Prove.RESULT_YES
				: (predicate.isFalse() ? Prove.RESULT_NO : Prove.RESULT_MAYBE);
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate)
			throws TheoremProverException {
		return predicate.isTrue() ? Prove.RESULT_NO
				: (predicate.isFalse() ? Prove.RESULT_YES : Prove.RESULT_MAYBE);
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		return Prove.RESULT_MAYBE;
	}

}
