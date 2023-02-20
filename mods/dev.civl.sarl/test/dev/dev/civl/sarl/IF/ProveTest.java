package dev.civl.sarl.IF;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.config.Configurations;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.config.SARLConfig;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProver;
import performance.PerformanceTest;

public class ProveTest {

	PreUniverse universe;

	@Before
	public void setUp() throws Exception {
		universe = PreUniverses
				.newPreUniverse(PreUniverses.newIdealFactorySystem());
	}

	/**
	 * The same formula as the one in the {@link PerformanceTest#slowNegation()}
	 * . Negating the formula is slow but prover can directly test the
	 * unsatisfiability of it without negating it.
	 */
	@Test
	public void testNoUnsatCVC() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo cvc = config.getProverWithKind(ProverKind.CVC4);

		assertEquals("CVC3/CVC4 must be installed for passing this " + "test",
				true, cvc != null);
		SymbolicUniverse su = SARL.newStandardUniverse(config, cvc);
		BooleanExpression unsatFormula = PerformanceTest
				.slowNegationFormula(false, su);

		su.setShowProverQueries(true);
		assertEquals(ResultType.NO, su.reasoner(su.trueExpression())
				.unsat(unsatFormula).getResultType());
	}

	@Test
	public void testUnsatCVCNOSimplify() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo cvc = config.getProverWithKind(ProverKind.CVC4);

		assertEquals("CVC3/CVC4 must be installed for passing this " + "test",
				true, cvc != null);
		SymbolicUniverse su = SARL.newStandardUniverse(config, cvc);
		BooleanExpression unsatFormula = PerformanceTest
				.slowNegationFormula(true, su);

		su.setShowProverQueries(true);

		TheoremProver prover = Prove.newProverFactory((PreUniverse) su, cvc)
				.newProver(su.trueExpression());

		assertEquals(ResultType.YES,
				prover.unsat(unsatFormula).getResultType());
	}
}
