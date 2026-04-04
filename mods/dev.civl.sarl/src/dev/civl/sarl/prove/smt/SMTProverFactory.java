package dev.civl.sarl.prove.smt;

import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;

/**
 * Factory for producing new instances of {@link SMTProver}.
 * 
 * @author Stephen F. Siegel
 */
public class SMTProverFactory implements TheoremProverFactory {

	/**
	 * The symbolic universe used for managing symbolic expressions. Initialized by
	 * constructor and never changes.
	 */
	private PreUniverse universe;

	/**
	 * Information object for underlying SMT prover.
	 */
	private ProverInfo prover;

	/**
	 * Constructs new SMT theorem prover factory with the given symbolic universe.
	 * 
	 * @param universe symbolic universe used to manage symbolic expressions
	 * @param prover   information object for underlying prover
	 */
	public SMTProverFactory(PreUniverse universe, ProverInfo prover) {
		this.universe = universe;
		this.prover = prover;
	}

	@Override
	public TheoremProver newProver(BooleanExpression context) {
		return new SMTProver(universe, context, prover, new ProverFunctionInterpretation[0]);
	}

	@Override
	public TheoremProver newProver(BooleanExpression context, ProverFunctionInterpretation[] logicFunctions) {
		return new SMTProver(universe, context, prover, logicFunctions);
	}
}
