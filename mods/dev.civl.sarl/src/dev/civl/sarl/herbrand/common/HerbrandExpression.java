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
package dev.civl.sarl.herbrand.common;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.common.HomogeneousExpression;

public class HerbrandExpression extends HomogeneousExpression<SymbolicObject>
		implements NumericExpression {

	protected HerbrandExpression(SymbolicOperator operator, SymbolicType type,
			SymbolicObject... arguments) {
		super(operator, type, arguments);
	}
}
