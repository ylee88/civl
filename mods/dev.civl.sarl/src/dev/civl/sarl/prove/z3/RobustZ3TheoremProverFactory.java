package dev.civl.sarl.prove.z3;

import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.prove.cvc.RobustCVCTheoremProver;

/**
 * Factory for producing new instances of {@link RobustCVCTheoremProver}.
 * 
 * @author Stephen F. Siegel
 */
public class RobustZ3TheoremProverFactory implements TheoremProverFactory {

	/**
	 * The symbolic universe used for managing symbolic expressions. Initialized
	 * by constructor and never changes.
	 */
	private PreUniverse universe;

	/**
	 * Information object for underlying prover, which must have
	 * {@link ProverKind} {@link ProverKind#Z3}.
	 */
	private ProverInfo prover;

	/**
	 * Constructs new Z3 theorem prover factory with the given symbolic
	 * universe.
	 * 
	 * @param universe
	 *            symbolic universe used to manage symbolic expressions
	 * @param prover
	 *            information object for underlying prover, which must have
	 *            {@link ProverKind} {@link ProverKind#Z3}
	 */
	public RobustZ3TheoremProverFactory(PreUniverse universe,
			ProverInfo prover) {
		this.universe = universe;
		this.prover = prover;
	}

	@Override
	public TheoremProver newProver(BooleanExpression context) {
		return new RobustZ3TheoremProver(universe, context, prover,
				new ProverFunctionInterpretation[0]);
	}

	@Override
	public TheoremProver newProver(BooleanExpression context,
			ProverFunctionInterpretation[] logicFunctions) {
		return new RobustZ3TheoremProver(universe, context, prover,
				logicFunctions);
	}
}
