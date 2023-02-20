package dev.civl.sarl.IF;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.type.SymbolicFunctionType;
import dev.civl.sarl.IF.type.SymbolicType;

public class BackwardsSubTest {
	
	public final static PrintStream out = System.out;

	@Test
	public void backwardsSubTest0() {
		SymbolicUniverse u = SARL.newStandardUniverse();
		// Symbolic constants:
		NumericSymbolicConstant X_N = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("X_N"), u.integerType());
		// Type:
		SymbolicType arrayType = u.arrayType(u.integerType(),
				u.add((NumericExpression) X_N, u.integer(2)));
		SymbolicFunctionType funcType = u.functionType(
				Arrays.asList(arrayType, u.integerType(), u.integerType(),
						u.integerType(), u.integerType()),
				u.booleanType());
		// Symbolic constants:
		NumericSymbolicConstant Y0 = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("Y0"), u.integerType());
		SymbolicConstant is_duplet = u
				.symbolicConstant(u.stringObject("is_duplet"), funcType);
		SymbolicConstant X_a = u.symbolicConstant(u.stringObject("X_a"),
				arrayType);
		BooleanExpression Y2 = (BooleanExpression) u
				.symbolicConstant(u.stringObject("Y2"), u.booleanType());
		NumericSymbolicConstant Y1 = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("Y1"), u.integerType());
		NumericSymbolicConstant Y3 = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("Y3"), u.integerType());

		// bound vars:
		NumericSymbolicConstant l = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("l"), u.integerType());
		NumericSymbolicConstant k = (NumericSymbolicConstant) u
				.symbolicConstant(u.stringObject("k"), u.integerType());

		// Build predicate:
		BooleanExpression clause = u.forall(k, u.or(Arrays.asList(
				u.lessThanEquals(
						u.subtract(u.subtract((NumericExpression) Y3,
								(NumericExpression) k), u.oneInt()),
						u.zeroInt()),
				u.lessThanEquals(u.add((NumericExpression) k, u.oneInt()),
						u.zeroInt()),
				u.lessThanEquals(u.zeroInt(),
						u.subtract((NumericExpression) k,
								(NumericExpression) l)),
				u.not((BooleanExpression) u.apply(is_duplet,
						Arrays.asList(X_a, u.zeroInt(), X_N, k, l))))));
		clause = u.or(clause,
				u.lessThanEquals(u.subtract(X_N, l), u.zeroInt()));
		clause = u.forall(l, clause);
		clause = u.and(clause, u.lessThanEquals(u.zeroInt(),
				u.subtract(u.subtract(X_N, Y3), u.oneInt())));
		clause = u.and(clause,
				u.lessThanEquals(u.zeroInt(), u.subtract(X_N, u.integer(3))));
		clause = u.and(clause, u.lessThanEquals(u.zeroInt(), Y3));
		clause = u.and(clause,
				u.or((BooleanExpression) u.apply(is_duplet,
						Arrays.asList(X_a, u.zeroInt(), X_N, Y0, Y1)),
						u.not(Y2)));
		clause = u.and(clause, u.or(
				u.equals(u.add(u.subtract(Y0, Y3), u.oneInt()), u.zeroInt()),
				u.not(Y2)));
		clause = u.and(clause, u.or(u.equals(Y0, u.zeroInt()), Y2));

		BooleanExpression clause0 = u.forall(l, u.or(Arrays.asList(
				u.lessThanEquals(u.subtract(X_N, l), u.zeroInt()),
				u.lessThanEquals(l, u.zeroInt()),
				u.lessThanEquals(u.oneInt(), u.subtract(Y3, l)),
				u.not((BooleanExpression) u.apply(is_duplet, Arrays.asList(X_a,
						u.zeroInt(), X_N, u.subtract(Y3, u.oneInt()), l))))));

		clause = u.and(clause, u.or(clause0, Y2));
		clause = u.and(clause,
				u.or(u.lessThanEquals(u.subtract(X_N, Y3), u.oneInt()), Y2));
		clause = u.and(clause,
				u.or(u.lessThanEquals(u.add(Y0, u.oneInt()), Y1), u.not(Y2)));
		clause = u.and(clause, u.or(
				u.lessThanEquals(u.oneInt(), u.subtract(X_N, Y1)), u.not(Y2)));
		clause = u.and(clause, u.or(u.neq(u.oneInt(), Y3), u.not(Y2)));

		out.println(clause);
		u.setUseBackwardSubstitution(true);
		clause = u.reasoner(clause).getReducedContext();
		out.println(clause);
		int numX_a = 0;

		for (SymbolicConstant var : clause.getFreeVars()) {
			if (var.name().getString().equals("X_a")) {
				numX_a++;
				out.println(var.type());
			}
		}
		assertEquals(1, numX_a);
	}
}
