package dev.civl.sarl.simplify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.BooleanSymbolicConstant;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.common.CommonNumericExpressionFactory;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.Monic;
import dev.civl.sarl.universe.common.CommonSymbolicUniverse;

public class SimplifyExpressionTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	private static PrintStream out = System.out;

	private static CommonSymbolicUniverse universe = (CommonSymbolicUniverse) SARL
			.newStandardUniverse();

	private static SymbolicType boolType = universe.booleanType();

	private static SymbolicType intType = universe.integerType();

	private static SymbolicType realType = universe.realType();

	private static NumericExpression zero = universe.integer(0);

	private static NumericExpression two = universe.integer(2);

	@BeforeClass
	public static void setUpBeforeClass() {
		universe.setUseBackwardSubstitution(true);
	}

	@Test
	public void conditionalExpr() {
		// given X?Y:Y, it should be simplified to be Y
		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), boolType);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), intType);
		SymbolicExpression cond = universe.cond((BooleanExpression) X, Y, Y);
		Reasoner reasoner = universe.reasoner(universe.trueExpression());
		SymbolicExpression symplified = reasoner.simplify(cond);

		out.println("original expression: " + cond);
		out.println("symplified expression: " + symplified);
		assertTrue(universe.equals(Y, symplified).isTrue());
	}

	@Test
	public void embeddedRational() {
		// want p?x/y:0 --> [0,+infty) in the RangeMap.
		// Then p becomes true. Hence the new range map entry should be
		// x/y --> [0,+infty).
		NumericExpression zero = universe.zeroReal();
		NumericSymbolicConstant X = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("X"), realType);
		NumericSymbolicConstant Y = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("Y"), realType);
		BooleanSymbolicConstant p = (BooleanSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("p"), boolType);
		NumericExpression rat = universe.divide(X, Y);
		NumericExpression ternary = (NumericExpression) universe.cond(p, rat,
				zero);
		BooleanExpression a1 = universe.lessThanEquals(zero, ternary);
		BooleanExpression and = universe.and(a1, p);
		Reasoner reasoner = universe.reasoner(and);
		BooleanExpression context1 = reasoner.getFullCollapsedContext();

		out.println("context: " + context1);
		// whatever you get, should be same as assuming x/y>=0 and p.

		Reasoner reasoner2 = universe
				.reasoner(universe.and(p, universe.lessThanEquals(zero, rat)));
		BooleanExpression context2 = reasoner2.getFullCollapsedContext();

		assertEquals(context2, context1);
	}

	@Test
	public void simplifyOpenRange() {
		// When simplify following expression:
		// (x + 1 <= 0) || (x + 2 <= 0) || (0 <= x - 2) || (0 <= x - 1)
		// an error will happen.
		NumericSymbolicConstant x = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("X"),
						universe.integerType());
		BooleanExpression predicate, clause0, clause1, clause2, clause3;

		clause0 = universe.lessThanEquals(x, universe.minus(universe.oneInt()));

		clause1 = universe.lessThanEquals(x,
				universe.minus(universe.integer(2)));
		clause2 = universe.lessThanEquals(universe.oneInt(), x);
		clause3 = universe.lessThanEquals(universe.integer(2), x);
		predicate = universe
				.or(Arrays.asList(clause0, clause1, clause2, clause3));
		predicate = universe.reasoner(universe.trueExpression())
				.simplify(predicate);
		out.println(predicate);
	}

	// context : X_N - 1*Y3 <= 0 && 0 <= X_N - 1*Y3 && 0 <= X_N - 1 && 0 <= Y3
	// simplified : 0 <= Y3 - 1
	// query: 0 <= X_N
	// expected result: YES
	@Test
	public void backwradsSubstitutionTest() {
		universe.setUseBackwardSubstitution(true);

		SymbolicUniverse u = universe;
		NumericSymbolicConstant X_N = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("X_N"),
						universe.integerType());
		NumericSymbolicConstant Y3 = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("Y3"), intType);
		BooleanExpression context = u.and(Arrays.asList(
				u.lessThanEquals(X_N, Y3), u.lessThanEquals(Y3, X_N),
				u.lessThanEquals(u.oneInt(), X_N),
				u.lessThanEquals(u.zeroInt(), Y3)));

		Reasoner reasoner = u.reasoner(context);

		out.println(context);
		out.println(reasoner.getReducedCollapsedContext());
		assertTrue(reasoner.isValid(u.lessThanEquals(u.zeroInt(), X_N)));
	}

	@Test
	// context:
	// forall i0 : int . ((Y7[i0] == Y6[i0]) || (_uf_$mpi_sizeof(Y9)*Y8 - 1*i0
	// <= 0) || (i0 + 1 <= 0))
	// forall i0 : int . ((Y7[i0] == Y11[i0]) || (2*_uf_$mpi_sizeof(Y9)*Y8 -
	// 1*i0 <= 0) || (i0 + 1 <= 0))
	public void backwardsSubstitutionWithForall() {
		NumericSymbolicConstant i = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("i"), intType);
		SymbolicConstant Y6, Y7, Y11;
		NumericSymbolicConstant N, X;

		N = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("N"), intType);
		X = (NumericSymbolicConstant) universe
				.symbolicConstant(universe.stringObject("X"), intType);
		Y6 = universe.symbolicConstant(universe.stringObject("Y6"),
				universe.arrayType(intType, N));
		Y7 = universe.symbolicConstant(universe.stringObject("Y7"),
				universe.arrayType(intType, N));
		Y11 = universe.symbolicConstant(universe.stringObject("Y11"),
				universe.arrayType(intType, X));

		BooleanExpression pred0 = universe.equals(universe.arrayRead(Y6, i),
				universe.arrayRead(Y7, i));
		BooleanExpression pred1 = universe.equals(universe.arrayRead(Y11, i),
				universe.arrayRead(Y7, i));
		BooleanExpression context = universe.forallInt(i, universe.zeroInt(), N,
				pred0);

		context = universe.and(context,
				universe.forallInt(i, universe.zeroInt(), N, pred1));
		context = universe.and(context, universe.equals(N, X));

		Reasoner reasoner = universe.reasoner(context);

		out.println("full context    : " + reasoner.getFullCollapsedContext());
		out.println("reduced context : " + reasoner.getReducedCollapsedContext());
		out.println("SubMap: " + reasoner.constantSubstitutionMap());
		out.println();

		SymbolicExpression arrayLambdaY7 = universe.arrayLambda(
				universe.arrayType(intType, N),
				universe.lambda(i, universe.arrayRead(Y7, i)));
		SymbolicExpression arrayLambdaY6 = universe.arrayLambda(
				universe.arrayType(intType, N),
				universe.lambda(i, universe.arrayRead(Y6, i)));
		SymbolicExpression arrayLambdaY11 = universe.arrayLambda(
				universe.arrayType(intType, N),
				universe.lambda(i, universe.arrayRead(Y11, i)));

		out.println(arrayLambdaY7);
		out.println(reasoner.simplify(arrayLambdaY7));
		out.println(arrayLambdaY6);
		out.println(reasoner.simplify(arrayLambdaY6));
		out.println(arrayLambdaY11);
		out.println(reasoner.simplify(arrayLambdaY11));
		assertTrue(reasoner.simplify(arrayLambdaY7)
				.equals(reasoner.simplify(arrayLambdaY6)));
		assertTrue(reasoner.simplify(arrayLambdaY7)
				.equals(reasoner.simplify(arrayLambdaY11)));
	}

	/**
	 * Tests a specific termination bug that occurred due to GaussianNormalizer
	 * not maintaining the invariant of a Context which states that a key of its
	 * submap cannot appear as a subexpression of any other key or value in it,
	 * or any subcontext's submap.
	 * 
	 * In rare cases, invariant can be broken if K -> T is in super context's
	 * submap, and we try to simplify an expression E containing T as a subterm.
	 * The circumstances that needed to occur were that K would get ordered
	 * after T when forming the columns of the matrix to be solved. If this
	 * happened, then K was not guaranteed to be chosen as a "pivot" column
	 * which would mean that the submatrix of E could end up with a non-zero
	 * coefficient for K, which meant that K would appear as a subterm of the
	 * simplified expression. This would then result in an entry into the submap
	 * that contained K as a subexpression, breaking the invariant.
	 * 
	 * Non-termination would then occur because SubstitutionNormalizer would
	 * replace this instance of K with T, but then the GaussianNormalizer would
	 * resolve the matrix in the same way as before, placing K back into the
	 * substitution map. This would repeat ad infinitum.
	 */
	@Test
	public void gaussTerminationTest() {
		IdealFactory idealFactory = (IdealFactory) ((CommonNumericExpressionFactory) universe
				.numericExpressionFactory()).idealFactory();
		Monic[] symbols = new Monic[]{
				(Monic) universe.symbolicConstant(universe.stringObject("X"),
						intType),
				(Monic) universe.symbolicConstant(universe.stringObject("Y"),
						intType),
				(Monic) universe.symbolicConstant(universe.stringObject("Z"),
						intType)};
		// Sort symbols so that we can purposefully construct our expressions so
		// that the bug gets triggered.
		Arrays.sort(symbols, idealFactory.monicComparator());

		// Need K -> T in sub map with K > T. Multiplying by 2 does this.
		NumericExpression symb1Expr = universe.multiply(symbols[1], two);
		BooleanExpression superAssumption = universe.equals(symbols[2],
				symb1Expr);
		Reasoner reasoner = universe.reasoner(superAssumption);

		NumericExpression yMinusZ = universe.subtract(symb1Expr, symbols[0]);
		// Using a conjunction so that we use SubContextSimplification to
		// simplify our expression.
		BooleanExpression expr = universe.and(
				universe.lessThanEquals(yMinusZ, zero),
				universe.lessThanEquals(zero, yMinusZ));
		BooleanExpression expectedExpr = universe.equals(symb1Expr, symbols[0]);

		assertEquals(reasoner.simplify(expectedExpr), reasoner.simplify(expr));
	}
}
