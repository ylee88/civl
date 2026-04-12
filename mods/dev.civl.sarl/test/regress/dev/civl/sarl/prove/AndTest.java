package dev.civl.sarl.prove;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.config.Configurations;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.universe.IF.Universes;

@RunWith(JUnit4.class)
public class AndTest {

	// Static fields: instantiated once and used for all tests...

	private static FactorySystem factorySystem = PreUniverses.newIdealFactorySystem();

	private static PreUniverse universe = PreUniverses.newPreUniverse(factorySystem);

	private static ExpressionFactory expressionFactory = factorySystem.expressionFactory();

	private static SymbolicType boolType = universe.booleanType();

	private static SymbolicType integerType = universe.integerType();

	private static BooleanExpression boolTrue = universe.trueExpression();

	private static BooleanExpression boolFalse = universe.falseExpression();

	private static NumericExpression five = universe.integer(5);

	private static NumericSymbolicConstant x = (NumericSymbolicConstant) universe
			.symbolicConstant(universe.stringObject("x"), integerType);

	private static BooleanExpression context = universe.lessThan(x, five);

	private static Collection<TheoremProver> provers;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		universe.setShowProverQueries(false);
		provers = new LinkedList<TheoremProver>();
		for (ProverInfo info : Configurations.getDefaultConfiguration().getProvers()) {
			provers.add(Prove.newProverFactory(universe, info, Universes.makeProverDir()).newProver(context));
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Checks that the result of applying the prover to the given predicate is as
	 * expected.
	 * 
	 * @param expected  expected result type (YES, NO, or MAYBE)
	 * @param predicate boolean expression to be checked for validity
	 */
	private void check(ResultType expected, BooleanExpression predicate) {
		for (TheoremProver prover : provers) {
			assertEquals(prover.toString(), expected, prover.valid(predicate).getResultType());
		}
	}

	@Test
	@Ignore
	public void testTranslateAndOneArg() {
		BooleanExpression andExp = (BooleanExpression) expressionFactory.expression(SymbolicOperator.AND, boolType,
				boolFalse, boolTrue);

		for (TheoremProver prover : provers) {
			ValidityResult result = prover.valid(andExp);
			assertEquals(ResultType.NO, result.getResultType());
		}
	}

	@Test
	public void translateTwoArgNotEqual() {
		check(ResultType.NO, universe.and(boolTrue, boolFalse));
	}

	@Test
	public void translateTwoArgEqual() {
		check(ResultType.YES, universe.and(boolTrue, boolTrue));
	}

	@Test
	public void translateBitPred() {
		check(ResultType.NO, universe.lessThan((NumericExpression) universe.bitand(five, five), five));
	}

}
