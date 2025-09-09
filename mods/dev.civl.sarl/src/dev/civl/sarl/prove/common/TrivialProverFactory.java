package dev.civl.sarl.prove.common;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;

public class TrivialProverFactory implements TheoremProverFactory {

	public TrivialProverFactory() {}
	
	@Override
	public TheoremProver newProver(BooleanExpression context) {
		return new TrivialProver();
	}

	@Override
	public TheoremProver newProver(BooleanExpression context,
			ProverFunctionInterpretation[] logicFunctions) {
		return newProver(context);
	}

}
