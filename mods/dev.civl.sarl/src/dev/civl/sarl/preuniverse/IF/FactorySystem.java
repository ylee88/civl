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
package dev.civl.sarl.preuniverse.IF;

import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;

public interface FactorySystem {

	ObjectFactory objectFactory();

	ExpressionFactory expressionFactory();

	SymbolicTypeFactory typeFactory();

	NumberFactory numberFactory();

	BooleanExpressionFactory booleanFactory();

	NumericExpressionFactory numericFactory();

}
