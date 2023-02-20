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

import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.object.NumberObject;

/**
 * A constant, i.e., a concrete number. Wraps a {@link NumberObject}, which
 * wraps a {@link Number}. It is how a concrete number is represented as a
 * {@link SymbolicExpression}.
 * 
 * @author siegel
 * 
 */
public interface Constant extends Monomial {

	/**
	 * Returns the {@link NumberObject} wrapped by this {@link Constant}.
	 * 
	 * @return the underlying {@link NumberObject}
	 */
	NumberObject value();

	/**
	 * Returns the underlying {@link Number} wrapped by this {@link Constant}.
	 * Convenience method, equivalent to <code>value().getNumber()</code>.
	 * 
	 * @return <code>value().getNumber()</code>
	 */
	Number number();

	@Override
	Constant powerInt(IdealFactory factory, IntegerNumber exponent);

}
