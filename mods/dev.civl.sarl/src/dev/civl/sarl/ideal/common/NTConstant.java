/*******************************************************************************
 * Copyright (c) 2013 Stephen F. Siegel, University of Delaware.
 * 
 * This file is part of SARL.
 * 
 * SARL is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SARL is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SARL. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package dev.civl.sarl.ideal.common;

import java.util.Set;

import dev.civl.sarl.IF.SARLException;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.IF.number.RationalNumber;
import dev.civl.sarl.IF.object.NumberObject;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicIntegerType;
import dev.civl.sarl.IF.type.SymbolicRealType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.common.HomogeneousExpression;
import dev.civl.sarl.ideal.IF.Constant;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.Monic;
import dev.civl.sarl.ideal.IF.Monomial;
import dev.civl.sarl.ideal.IF.Primitive;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.util.EmptySet;

/**
 * A constant which is not 1.
 * 
 * @author siegel
 * 
 */
public class NTConstant extends HomogeneousExpression<SymbolicObject>
		implements Constant {

	private final static Set<Primitive> emptySet = new EmptySet<Primitive>();

	/**
	 * Constructs new {@link NTConstant} of given type, wrapping given numeric
	 * value.
	 * 
	 * @param type
	 *            either a {@link SymbolicRealType} or
	 *            {@link SymbolicIntegerType}
	 * @param value
	 *            the numeric value to be wrapped; its type must be consistent
	 *            with <code>type</code>
	 */
	protected NTConstant(SymbolicType type, NumberObject value) {
		super(SymbolicOperator.CONCRETE, type, new SymbolicObject[] { value });
		assert !value.isOne();
	}

	public NumberObject value() {
		return (NumberObject) argument(0);
	}

	public Number number() {
		return value().getNumber();
	}

	public boolean isZero() {
		return value().isZero();
	}

	public boolean isOne() {
		return false;
	}

	@Override
	public Constant monomialConstant(IdealFactory factory) {
		return this;
	}

	@Override
	public Monic monic(IdealFactory factory) {
		return (Monic) factory.one(type());
	}

	@Override
	public Monomial numerator(IdealFactory factory) {
		return this;
	}

	@Override
	public Monomial denominator(IdealFactory factory) {
		return factory.one(type());
	}

	@Override
	public Monomial[] termMap(IdealFactory factory) {
		return isZero() ? IdealFactory.emptyTermList : new Monomial[] { this };
	}

	@Override
	public IntegerNumber monomialDegree(NumberFactory factory) {
		return isZero() ? factory.integer(-1) : factory.zeroInteger();
	}

	@Override
	public Monomial[] expand(IdealFactory factory) {
		return termMap(factory);
	}

	@Override
	public IntegerNumber totalDegree(NumberFactory factory) {
		return isZero() ? factory.integer(-1) : factory.zeroInteger();
	}

	@Override
	public boolean hasNontrivialExpansion(IdealFactory factory) {
		return false;
	}

	@Override
	public int monomialOrder(IdealFactory factory) {
		return 0;
	}

	@Override
	public Monomial[] lower(IdealFactory factory) {
		return termMap(factory);
	}

	@Override
	public RationalExpression powerRational(IdealFactory factory,
			RationalExpression exponent) {
		NumberFactory numFactory = factory.numberFactory();
		Number exp = factory.extractNumber(exponent);

		if (exp == null) {
			return factory.expression(SymbolicOperator.POWER, type(), this,
					exponent);
		}

		Number base = number();
		IntegerNumber exp_num, exp_den, base_num, base_den;

		if (exp instanceof IntegerNumber) {
			exp_num = (IntegerNumber) exp;
			exp_den = numFactory.oneInteger();
		} else {
			exp_num = numFactory.integer(((RationalNumber) exp).numerator());
			exp_den = numFactory.integer(((RationalNumber) exp).denominator());
		}
		if (base instanceof IntegerNumber) {
			base_num = (IntegerNumber) base;
			base_den = numFactory.oneInteger();
		} else {
			base_num = numFactory.integer(((RationalNumber) base).numerator());
			base_den = numFactory
					.integer(((RationalNumber) base).denominator());
		}

		IntegerNumber result_num = null;
		IntegerNumber result_den = null;
		IntegerNumber tmp_num = null;
		IntegerNumber tmp_den = null;

		if (exp_num.signum() < 0) {
			IntegerNumber tmp = base_den;

			exp_num = numFactory.negate(exp_num);
			if (base_num.signum() < 0) {
				base_den = numFactory.negate(base_num);
				base_num = numFactory.negate(tmp);
			} else {
				base_den = base_num;
				base_num = tmp;
			}
		}
		assert exp_num.signum() >= 0;
		result_num = numFactory.power(base_num, exp_num);
		result_den = numFactory.power(base_den, exp_num);
		tmp_num = numFactory.nthRootInt(result_num, exp_den);
		tmp_den = numFactory.nthRootInt(result_den, exp_den);
		if (tmp_num == null || tmp_den == null) {
			return factory.expression(SymbolicOperator.POWER, type(), this,
					exponent);
		} else if (type.isInteger()) {
			if (!numFactory.mod(tmp_num, tmp_den).isZero())
				throw new SARLException(
						"Result of power is not integer:\nbase = " + this
								+ "\nexponent=" + exponent);
			return factory.constant(numFactory.divide(tmp_num, tmp_den));
		} else {
			return factory.constant(numFactory.fraction(tmp_num, tmp_den));
		}
	}

	@Override
	public Constant powerInt(IdealFactory factory, IntegerNumber n) {
		assert n.signum() >= 0;
		return factory.constant(
				factory.numberFactory().power(number(), (IntegerNumber) n));
	}

	@Override
	public IntegerNumber maxDegreeOf(NumberFactory factory,
			Primitive primitive) {
		return factory.zeroInteger();
	}

	@Override
	public Set<Primitive> getTruePrimitives() {
		return emptySet;
	}
}
