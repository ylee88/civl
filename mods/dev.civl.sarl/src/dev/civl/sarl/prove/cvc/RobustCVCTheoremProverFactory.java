package dev.civl.sarl.prove.cvc;

import java.nio.file.Path;

import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;

/**
 * Factory for producing new instances of {@link RobustCVCTheoremProver}.
 * 
 * @author siegel
 *
 */
public class RobustCVCTheoremProverFactory implements TheoremProverFactory {

	/**
	 * The symbolic universe used for managing symbolic expressions. Initialized by
	 * constructor and never changes.
	 */
	private PreUniverse universe;

	/**
	 * Information object for underlying prover, which must have {@link ProverKind}
	 * either {@link ProverKind#CVC3} or {@link ProverKind#CVC4}
	 */
	private ProverInfo prover;

	private Path workingDirectory;

	/**
	 * Constructs new CVC theorem prover factory with the given symbolic universe.
	 * 
	 * @param universe symbolic universe used to manage symbolic expressions
	 * @param prover   information object for underlying prover, which must have
	 *                 {@link ProverKind} either {@link ProverKind#CVC3} or
	 *                 {@link ProverKind#CVC4}
	 */
	public RobustCVCTheoremProverFactory(PreUniverse universe, ProverInfo prover, Path workingDirectory) {
		this.universe = universe;
		this.prover = prover;
		this.workingDirectory = workingDirectory;
	}

	@Override
	public TheoremProver newProver(BooleanExpression context) {
		return new RobustCVCTheoremProver(universe, context, prover, new ProverFunctionInterpretation[0]);
	}

	@Override
	public TheoremProver newProver(BooleanExpression context, ProverFunctionInterpretation[] logicFunctions) {
		return new RobustCVCTheoremProver(universe, context, prover, logicFunctions);
	}

	@Override
	public Path workingDirectory() {
		return workingDirectory;
	}
}
