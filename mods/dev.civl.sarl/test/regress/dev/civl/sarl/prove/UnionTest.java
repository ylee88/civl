package dev.civl.sarl.prove;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.config.Configurations;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicUnionType;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProverFactory;

/**
 * Tests involving proving things related to union types, including union
 * injection and extraction.
 * 
 * @author siegel
 */
public class UnionTest {

	private static FactorySystem factorySystem = PreUniverses
			.newIdealFactorySystem();

	private static PreUniverse universe = PreUniverses
			.newPreUniverse(factorySystem);

	private static Collection<TheoremProverFactory> proverFactories = new LinkedList<>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		universe.setShowProverQueries(true);
		for (ProverInfo info : Configurations.getDefaultConfiguration()
				.getProvers())
			proverFactories.add(Prove.newProverFactory(universe, info));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		proverFactories = null;
		universe = null;
		factorySystem = null;
	}

	@Test
	public void test() {
		SymbolicUnionType unionType = universe.unionType(
				universe.stringObject("U"),
				Arrays.asList(universe.integerType(), universe.realType()));
		SymbolicExpression zeroI = universe.unionInject(unionType,
				universe.intObject(0), universe.zeroInt());
		SymbolicConstant realX = universe.symbolicConstant(
				universe.stringObject("X"), universe.realType());
		BooleanExpression t = universe.trueExpression();
		BooleanExpression pred1 = universe.equals(
				universe.unionExtract(universe.intObject(1), zeroI), realX);

		for (TheoremProverFactory factory : proverFactories)
			assertEquals(factory.toString(), ResultType.NO,
					factory.newProver(t).valid(pred1).getResultType());

		SymbolicExpression zeroR = universe.unionInject(unionType,
				universe.intObject(1), universe.zeroReal());
		BooleanExpression xeq0 = universe.equals(realX, universe.zeroReal());
		BooleanExpression pred2 = universe.equals(
				universe.unionExtract(universe.intObject(1), zeroR), realX);

		for (TheoremProverFactory factory : proverFactories)
			assertEquals(factory.toString(), ResultType.YES,
					factory.newProver(xeq0).valid(pred2).getResultType());
	}

}
