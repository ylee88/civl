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
package dev.civl.sarl.ideal.IF;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.IF.number.RationalNumber;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.ideal.common.CommonIdealFactory;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.simplify.IF.SimplifierFactory;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;

/**
 * Entry point for the ideal module, providing static methods to create an
 * {@link IdealFactory} and a {@link SimplifierFactory}.
 * 
 * @author siegel
 *
 */
public class Ideal {

	/**
	 * Creates a new ideal factory based on the given factories.
	 * 
	 * /** Constructs new factory based on the given factories.
	 * 
	 * @param numberFactory
	 *            the number factory used by the ideal factory to create and
	 *            manipulate infinite-precision concrete integer and rational
	 *            numbers, instances of {@link Number}, {@link IntegerNumber},
	 *            and {@link RationalNumber}
	 * @param objectFactory
	 *            the object factory used by the ideal factory to manipulate
	 *            symbolic objects, instances of {@link SymbolicObject}.
	 * @param typeFactory
	 *            the symbolic type factory used by the ideal factory to create
	 *            and manipulate symbolic types, instances of
	 *            {@link SymbolicType}
	 * @param booleanFactory
	 *            the boolean expression factory used by the ideal factory to
	 *            create and manipulate boolean expressions, instances of
	 *            {@link BooleanExpression}
	 * @return a new {@link IdealFactory} based on the given factories
	 */
	public static IdealFactory newIdealFactory(NumberFactory numberFactory,
			ObjectFactory objectFactory, SymbolicTypeFactory typeFactory,
			BooleanExpressionFactory booleanFactory) {
		return new CommonIdealFactory(numberFactory, objectFactory, typeFactory,
				booleanFactory);
	}
}
