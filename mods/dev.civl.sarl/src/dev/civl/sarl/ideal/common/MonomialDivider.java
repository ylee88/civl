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

import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.ideal.IF.Monomial;

class MonomialDivider implements UnaryOperator<Monomial> {
	private CommonIdealFactory factory;
	private Number scalar;
	private NumberFactory numberFactory;

	public MonomialDivider(CommonIdealFactory factory, Number scalar) {
		this.factory = factory;
		this.scalar = scalar;
		this.numberFactory = factory.numberFactory();
	}

	@Override
	public Monomial apply(Monomial arg) {
		return factory.monomial(
				factory.constant(numberFactory.divide(
						arg.monomialConstant(factory).number(), scalar)),
				arg.monic(factory));
	}
}
