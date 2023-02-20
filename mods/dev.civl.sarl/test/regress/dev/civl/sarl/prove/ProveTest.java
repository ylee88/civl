package dev.civl.sarl.prove;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.config.Configurations;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.config.SARLConfig;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUninterpretedType;
import dev.civl.sarl.SARL;
import dev.civl.sarl.TestConstants;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.prove.z3.RobustZ3TheoremProverFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ProveTest {

	PreUniverse universe;

	private TheoremProverFactory proverFactory;

	@Before
	public void setUp() throws Exception {
		universe = PreUniverses
				.newPreUniverse(PreUniverses.newIdealFactorySystem());
		proverFactory = Prove.newMultiProverFactory(universe,
				Configurations.getDefaultConfiguration());
	}

	@Test
	public void testValidityResult() {
		ResultType r = ValidityResult.ResultType.YES;
		ValidityResult v = Prove.validityResult(r);
		assertEquals(Prove.RESULT_YES, v);

		r = ValidityResult.ResultType.NO;
		v = Prove.validityResult(r);
		assertEquals(Prove.RESULT_NO, v);

		r = ValidityResult.ResultType.MAYBE;
		v = Prove.validityResult(r);
		assertEquals(Prove.RESULT_MAYBE, v);
	}

	/**
	 * Test translation of uninterpreted type objects
	 */
	@Test
	public void testUninterpretedTypeNCCompare() {
		SymbolicUninterpretedType type = universe
				.symbolicUninterpretedType("test");
		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), type);
		SymbolicConstant Z = universe
				.symbolicConstant(universe.stringObject("Z"), type);
		BooleanExpression context = universe.and(universe.equals(X, Y),
				universe.equals(Z, Y));
		BooleanExpression comparison = universe.equals(X, Z);

		universe.setShowProverQueries(true);
		assertEquals(proverFactory.newProver(context).valid(comparison)
				.getResultType(), ResultType.YES);
	}

	/**
	 * Test translation of uninterpreted type objects
	 */
	@Test
	public void testUninterpretedTypeNCCompare2() {
		SymbolicUninterpretedType type = universe
				.symbolicUninterpretedType("test");
		SymbolicExpression k0 = universe.concreteValueOfUninterpretedType(type,
				universe.intObject(0));
		SymbolicExpression k1 = universe.concreteValueOfUninterpretedType(type,
				universe.intObject(1));
		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		BooleanExpression comparison = universe.and(universe.equals(X, k0),
				universe.equals(X, k1));

		universe.setShowProverQueries(true);
		assertEquals(
				proverFactory.newProver(universe.trueExpression())
						.valid(universe.not(comparison)).getResultType(),
				ResultType.YES);
	}

	/**
	 * Test translation of uninterpreted type objects
	 */
	@Test
	public void testUninterpretedTypeNCCompare3() {
		SymbolicUninterpretedType type = universe
				.symbolicUninterpretedType("test");
		SymbolicExpression k0 = universe.concreteValueOfUninterpretedType(type,
				universe.intObject(0));
		SymbolicExpression k1 = universe.concreteValueOfUninterpretedType(type,
				universe.intObject(1));
		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), type);
		BooleanExpression context = universe.and(universe.equals(Y, k0),
				universe.equals(X, k1));
		BooleanExpression comparison = universe.neq(X, Y);

		universe.setShowProverQueries(true);
		assertEquals(proverFactory.newProver(context).valid(comparison)
				.getResultType(), ResultType.YES);

		context = universe.and(universe.equals(Y, k0), universe.equals(X, k0));
		comparison = universe.equals(X, Y);
		assertEquals(proverFactory.newProver(context).valid(comparison)
				.getResultType(), ResultType.YES);
	}

	/**
	 * Test summation expression
	 * {@link PreUniverse#sigma(dev.civl.sarl.IF.expr.NumericExpression, dev.civl.sarl.IF.expr.NumericExpression, SymbolicExpression)}
	 */
	@Test
	public void testSummationExpansion() {
		SymbolicUniverse su = SARL.newStandardUniverse();

		SymbolicType intType = su.integerType();
		NumericExpression x = (NumericExpression) su
				.symbolicConstant(su.stringObject("X"), intType);
		NumericExpression y = (NumericExpression) su
				.symbolicConstant(su.stringObject("Y"), intType);
		SymbolicExpression f = su.symbolicConstant(su.stringObject("f"), su
				.functionType(Arrays.asList(su.integerType()), su.realType()));
		SymbolicConstant i = su.symbolicConstant(su.stringObject("i"),
				su.integerType());

		f = su.lambda(i, su.apply(f, Arrays.asList(i)));

		NumericExpression f_x_y = (NumericExpression) su.sigma(x, y, f);
		NumericExpression f_x_yPLUS1 = (NumericExpression) su.sigma(x,
				su.add(y, su.oneInt()), f);
		NumericExpression f_xMINUS1_y = (NumericExpression) su
				.sigma(su.subtract(x, su.oneInt()), y, f);

		BooleanExpression rightExpansion = su.equals(f_x_yPLUS1, su.add(f_x_y,
				(NumericExpression) su.apply(f, Arrays.asList(y))));
		BooleanExpression leftExpansion = su.equals(f_xMINUS1_y,
				su.add(f_x_y, (NumericExpression) su.apply(f,
						Arrays.asList(su.subtract(x, su.oneInt())))));
		Reasoner reasoner = su.reasoner(su.lessThanEquals(x, y));

		su.setShowProverQueries(true);
		assertEquals(ResultType.YES,
				reasoner.valid(leftExpansion).getResultType());
		assertEquals(ResultType.YES,
				reasoner.valid(rightExpansion).getResultType());
	}

	@Test
	public void testSummationTransitive() {
		SymbolicUniverse su = SARL.newStandardUniverse();

		SymbolicType intType = su.integerType();
		NumericExpression x = (NumericExpression) su
				.symbolicConstant(su.stringObject("X"), intType);
		NumericExpression y = (NumericExpression) su
				.symbolicConstant(su.stringObject("Y"), intType);
		NumericExpression z = (NumericExpression) su
				.symbolicConstant(su.stringObject("Z"), intType);
		SymbolicExpression f = su.symbolicConstant(su.stringObject("f"), su
				.functionType(Arrays.asList(su.integerType()), su.realType()));
		SymbolicConstant i = su.symbolicConstant(su.stringObject("i"),
				su.integerType());

		f = su.lambda(i, su.apply(f, Arrays.asList(i)));

		NumericExpression f_x_y = (NumericExpression) su.sigma(x, y, f);
		NumericExpression f_y_z = (NumericExpression) su.sigma(y, z, f);
		NumericExpression f_x_z = (NumericExpression) su.sigma(x, z, f);

		ResultType result = su
				.reasoner(su.and(su.lessThanEquals(x, y),
						su.lessThanEquals(y, z)))
				.valid(su.equals(su.add(f_x_y, f_y_z), f_x_z)).getResultType();

		assertEquals(ResultType.YES, result);
	}

	// (X.0).0 != 0 ==> (X.0).0 == 0
	// Expected: NO
	@Test
	public void testProverTranslationForSymbolicTuple() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo z3 = config.getProverWithKind(ProverKind.Z3);

		assertEquals("Z3 must be installed for passing this " + "test", true,
				z3 != null);

		SymbolicUniverse su = SARL.newStandardUniverse(config, z3);
		SymbolicTupleType innerTupleType = su.tupleType(
				su.stringObject("tuple_inn"), Arrays.asList(su.integerType()));
		SymbolicTupleType tupleType = su.tupleType(su.stringObject("tuple"),
				Arrays.asList(innerTupleType));
		SymbolicConstant myTuple = su.symbolicConstant(su.stringObject("X"),
				tupleType);

		BooleanExpression equation = su
				.equals(su.tupleRead(su.tupleRead(myTuple, su.intObject(0)),
						su.intObject(0)), su.zeroInt());
		BooleanExpression assumption = su.not(equation);
		ResultType result = new RobustZ3TheoremProverFactory((PreUniverse) su,
				z3).newProver(assumption).valid(equation).getResultType();

		assertEquals(ResultType.NO, result);
	}

	/**
	 * @author ziqingluo
	 */
	// (X.0).0 == 1 ==> 0 <= (X.0).0 <= 2
	// Expected: YES
	@Test
	public void testProverTranslationForSymbolicTuple2() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo z3 = config.getProverWithKind(ProverKind.Z3);

		assertEquals("Z3 must be installed for passing this " + "test", true,
				z3 != null);

		SymbolicUniverse su = SARL.newStandardUniverse(config, z3);
		SymbolicTupleType innerTupleType = su.tupleType(
				su.stringObject("tuple_inn"), Arrays.asList(su.integerType()));
		SymbolicTupleType tupleType = su.tupleType(su.stringObject("tuple"),
				Arrays.asList(innerTupleType));
		SymbolicConstant myTuple = su.symbolicConstant(su.stringObject("X"),
				tupleType);

		NumericExpression read = (NumericExpression) su.tupleRead(
				su.tupleRead(myTuple, su.intObject(0)), su.intObject(0));
		BooleanExpression assumption = su.equals(read, su.oneInt());
		ResultType result = new RobustZ3TheoremProverFactory(
				(PreUniverse) su, z3)
						.newProver(assumption)
						.valid(su.and(su.lessThan(su.zeroInt(), read),
								su.lessThan(read, su.integer(2))))
						.getResultType();

		assertEquals(ResultType.YES, result);
	}

	/**
	 * The same formula as the one in the {@link TestConstants#slowNegationFormula(boolean, SymbolicUniverse)}
	 * . Negating the formula is slow but prover can directly test the
	 * unsatisfiability of it without negating it.
	 */
	@Test
	public void testNoUnsatZ3() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo z3 = config.getProverWithKind(ProverKind.Z3);

		assertEquals("Z3 must be installed for passing this " + "test", true,
				z3 != null);
		SymbolicUniverse su = SARL.newStandardUniverse(config, z3);
		BooleanExpression unsatFormula = TestConstants
				.slowNegationFormula(false, su);

		su.setShowProverQueries(true);
		assertEquals(ResultType.NO, su.reasoner(su.trueExpression())
				.unsat(unsatFormula).getResultType());
	}

	@Test
	public void testUnsatZ3NoSimplify() {
		SARLConfig config = Configurations.getDefaultConfiguration();
		ProverInfo z3 = config.getProverWithKind(ProverKind.Z3);

		assertEquals("Z3 must be installed for passing this " + "test", true,
				z3 != null);
		SymbolicUniverse su = SARL.newStandardUniverse(config, z3);
		BooleanExpression unsatFormula = TestConstants
				.slowNegationFormula(true, su);

		su.setShowProverQueries(true);
		TheoremProver prover = Prove.newProverFactory((PreUniverse) su, z3)
				.newProver(su.trueExpression());

		assertEquals(ResultType.YES,
				prover.unsat(unsatFormula).getResultType());
	}

	@Test
	public void testUnsatSimplify() {
		SymbolicUniverse su = SARL.newStandardUniverse();
		BooleanExpression unsatFormula = TestConstants
				.slowNegationFormula(true, su);

		assertEquals(ResultType.YES, su.reasoner(su.trueExpression())
				.unsat(unsatFormula).getResultType());
		assertEquals(true, su.numProverValidCalls() == 0);
	}
}
