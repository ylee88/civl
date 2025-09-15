package dev.civl.sarl.expr.common;

import java.util.Arrays;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicRange;
import dev.civl.sarl.IF.expr.SymbolicRange.RangeKind;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.expr.IF.SymbolicRangeFactory;

public class CommonSymbolicRangeFactory implements SymbolicRangeFactory {
	private NumericExpressionFactory numericFactory;
	private BooleanExpressionFactory booleanFactory;

	CommonSymbolicRangeFactory(NumericExpressionFactory numericFactory,
			Reasoner reasoner) {
		this.numericFactory = numericFactory;
		this.booleanFactory = numericFactory.booleanFactory();
	}

	CommonSymbolicRangeFactory(NumericExpressionFactory numericFactory) {
		this(numericFactory, null);
	}

	public SymbolicRange symbolicRange(NumericExpression element) {
		return symbolicRange(RangeKind.SINGLETON, element,
				numericFactory.add(element, numericFactory.oneInt()),
				numericFactory.oneInt());
	}

	public SymbolicRange symbolicRange(NumericExpression lower,
			NumericExpression upper) {
		RangeKind kind = RangeKind.INTERVAL;
		if (upper.equals(numericFactory.add(lower, numericFactory.oneInt())))
			kind = RangeKind.SINGLETON;
		return symbolicRange(kind, lower, upper, numericFactory.oneInt());
	}

	public SymbolicRange symbolicRange(NumericExpression lower,
			NumericExpression upper, NumericExpression step) {
		RangeKind kind = RangeKind.REGULAR;
		if (upper.equals(numericFactory.add(lower, numericFactory.oneInt())))
			kind = RangeKind.SINGLETON;
		else if (step.equals(numericFactory.oneInt()))
			kind = RangeKind.INTERVAL;
		return symbolicRange(kind, lower, upper, step);
	}

	/**
	 * Construct an arbitrary symbolicRange without checking {@link RangeKind}.
	 */
	private SymbolicRange symbolicRange(RangeKind kind, NumericExpression lower,
			NumericExpression upper, NumericExpression step) {
		return new CommonSymbolicRange(kind, lower, upper, step);
	}

	/**
	 * Construct an arbitrary symbolicRange with trivial step and without
	 * checking {@link RangeKind}.
	 */
	private SymbolicRange symbolicRange(RangeKind kind, NumericExpression lower,
			NumericExpression upper) {
		return symbolicRange(kind, lower, upper, numericFactory.oneInt());
	}

	public BooleanExpression strictlyBelow(SymbolicRange range0,
			SymbolicRange range1) {
		assertTrivialStep(range0, range1, "Checking ordering of regular ranges"
				+ " is not yet supported");
		// TODO: consider step in the expression.

		return numericFactory.lessThanEquals(range0.getUpper(),
				range1.getLower());
	}

	public BooleanExpression disjoint(SymbolicRange range0,
			SymbolicRange range1) {
		assertTrivialStep(range0, range1,
				"Checking disjointness of regular ranges"
						+ " is not yet supported");
		// TODO: consider step in the expression.

		if (range0.getRangeKind() == RangeKind.SINGLETON
				&& range1.getRangeKind() == RangeKind.SINGLETON) {
			return numericFactory.neq(range0.getLower(), range1.getLower());
		}
		return booleanFactory.or(strictlyBelow(range0, range1),
				strictlyBelow(range1, range0));
	}

	public BooleanExpression subset(SymbolicRange range0,
			SymbolicRange range1) {
		assertTrivialStep(range0, range1,
				"Checking subset relationships of regular ranges"
						+ " is not yet supported");
		if (range0.getRangeKind() == RangeKind.SINGLETON
				&& range1.getRangeKind() == RangeKind.SINGLETON) {
			return numericFactory.equals(range0.getLower(), range1.getLower());
		}
		return booleanFactory.and(
				numericFactory.lessThanEquals(range1.getLower(),
						range0.getLower()),
				numericFactory.lessThanEquals(range0.getUpper(),
						range1.getUpper()));
	}

	public SymbolicRange[] diff(SymbolicRange range0, SymbolicRange range1) {
		assertTrivialStep(range0, range1,
				"Taking diff of regular ranges is not yet supported");
		
		/*
		BooleanExpression lowerContained = booleanFactory.and(
				numericFactory.lessThanEquals(range1.getLower(), range0.getLower()),
				numericFactory.lessThan(range0.getLower(), range1.getUpper()));
		NumericExpression lower = numericFactory.expression(
				SymbolicOperator.COND,
				numericFactory.typeFactory().integerType(), lowerContained,
				range1.getUpper(), range0.getLower());
		BooleanExpression upperContained = booleanFactory.and(
				numericFactory.lessThanEquals(range1.getLower(), range0.getUpper()),
				numericFactory.lessThan(range0.getUpper(), range1.getUpper()));
		NumericExpression upper = numericFactory.expression(
				SymbolicOperator.COND,
				numericFactory.typeFactory().integerType(), upperContained,
				range1.getLower(), range0.getUpper());
				*/

		SymbolicRange lowerRange = symbolicRange(range0.getLower(),
				numericFactory.min(
						Arrays.asList(range0.getUpper(), range1.getLower())));
		SymbolicRange upperRange = symbolicRange(
				numericFactory.max(
						Arrays.asList(range1.getUpper(), range0.getLower())),
				range0.getUpper());
		return new SymbolicRange[]{lowerRange, upperRange};
	}
	
	public BooleanExpression equals(SymbolicRange range0,
			SymbolicRange range1) {
		assertTrivialStep(range0, range1, "Checking equality of regular ranges"
				+ " is not yet supported");
		BooleanExpression eqExpr = numericFactory.equals(range0.getLower(),
				range1.getLower());
		if (range0.getRangeKind() != RangeKind.SINGLETON
				|| range1.getRangeKind() != RangeKind.SINGLETON) {
			eqExpr = booleanFactory.and(eqExpr, numericFactory
					.equals(range0.getUpper(), range1.getUpper()));
		}
		return eqExpr;
	}

	public BooleanExpression neq(SymbolicRange range0, SymbolicRange range1) {
		return booleanFactory.not(equals(range0, range1));
	}

	public BooleanExpression inRange(NumericExpression expr,
			SymbolicRange range) {
		BooleanExpression result = numericFactory
				.lessThanEquals(range.getLower(), expr);

		result = booleanFactory.and(result,
				numericFactory.lessThan(expr, range.getUpper()));
		if (!range.getStep().isOne()) {
			NumericExpression mod = numericFactory.modulo(
					numericFactory.subtract(expr, range.getLower()),
					range.getStep());

			result = booleanFactory.and(result,
					numericFactory.equals(mod, numericFactory.zeroInt()));
		}
		return result;
	}

	public SymbolicRange tryUnion(Reasoner reasoner, SymbolicRange range0,
			SymbolicRange range1) {
		if (!range0.getStep().isOne() || !range0.getStep().isOne()) {
			return null;
		}

		if (reasoner == null) {
			if (range0.getUpper().equals(range1.getLower())) {
				return symbolicRange(range0.getLower(), range1.getUpper());
			} else if (range1.getUpper().equals(range0.getLower())) {
				return symbolicRange(range1.getLower(), range0.getUpper());
			} else if (range0.equals(range1)) {
				return range0;
			}
			return null;
		}

		if (range1.getRangeKind() == RangeKind.SINGLETON) {
			SymbolicRange tmp = range0;
			range0 = range1;
			range1 = tmp;
		}

		if (range0.getRangeKind() == RangeKind.SINGLETON) {
			if (reasoner.isValid(subset(range0, range1)))
				return range1;
			if (reasoner.isValid(numericFactory.equals(range1.getUpper(),
					range0.getLower())))
				return symbolicRange(RangeKind.INTERVAL, range1.getLower(),
						range0.getUpper());
			if (reasoner.isValid(numericFactory.equals(range0.getUpper(),
					range1.getLower())))
				return symbolicRange(RangeKind.INTERVAL, range0.getLower(),
						range1.getUpper());
			return null;
		}

		if (reasoner.isValid(booleanFactory
				.and(inRange(range1.getLower(), range0), numericFactory
						.lessThanEquals(range0.getUpper(), range1.getUpper()))))
			return symbolicRange(range0.getLower(), range1.getUpper());
		if (reasoner.isValid(booleanFactory
				.and(inRange(range0.getLower(), range1), numericFactory
						.lessThanEquals(range1.getUpper(), range0.getUpper()))))
			return symbolicRange(range1.getLower(), range0.getUpper());
		if (reasoner.isValid(subset(range0, range1)))
			return range1;
		if (reasoner.isValid(subset(range1, range0)))
			return range0;
		return null;
	}

	private void assertTrivialStep(SymbolicRange r0, SymbolicRange r1,
			String failMessage) {
		assert r0.getStep().isOne() && r1.getStep().isOne() : failMessage;
	}
}
