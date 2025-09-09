package dev.civl.sarl.simplify.common;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;

/**
 * Transforms some special SARL expresisons (e.g. array lambda, sigma
 * expression) to forms that can be accepted by all provers.
 * 
 * @author ziqing
 */
public class SARLProverAdaptor {

	private PreUniverse pu;

	/* **** Specific Transformers for Adapting Prover Languages *****/
	private StatefulArrayLambdaAdaptor arrayLambdaAdaptor;

	private StatefulSigmaAdaptor sigmaAdaptor;

	public SARLProverAdaptor(PreUniverse pu) {
		this.pu = pu;
		this.arrayLambdaAdaptor = new StatefulArrayLambdaAdaptor(pu);
		this.sigmaAdaptor = new StatefulSigmaAdaptor(pu);
	}

	public BooleanExpression apply(BooleanExpression formula) {
		formula = (BooleanExpression) arrayLambdaAdaptor.apply(formula);
		return (BooleanExpression) sigmaAdaptor.apply(formula);
	}

	public BooleanExpression getAxioms() {
		return pu.and(arrayLambdaAdaptor.getIndependentArrayLambdaAxioms(),
				pu.and(sigmaAdaptor.getAxioms()));
	}
}
