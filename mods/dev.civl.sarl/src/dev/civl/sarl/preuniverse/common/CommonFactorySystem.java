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
package dev.civl.sarl.preuniverse.common;

import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;

public class CommonFactorySystem implements FactorySystem {

	private ObjectFactory objectFactory;

	private ExpressionFactory expressionFactory;

	private SymbolicTypeFactory typeFactory;

	/**
	 * Creates new instance from the three given factories and initializes the
	 * given factories in this order: first, the object factory, then the type
	 * factory, then the expression factory.
	 * 
	 * @param objectFactory
	 *            factory used to create {@link SymbolicObject}s
	 * @param typeFactory
	 *            factory used to create {@link SymbolicType}s
	 * @param expressionFactory
	 *            factory used to create {@link SymbolicExpression}s
	 */
	public CommonFactorySystem(ObjectFactory objectFactory,
			SymbolicTypeFactory typeFactory,
			ExpressionFactory expressionFactory) {
		this.objectFactory = objectFactory;
		this.typeFactory = typeFactory;
		this.expressionFactory = expressionFactory;

		objectFactory.init();
		typeFactory.init();
		expressionFactory.init();
	}

	@Override
	public ObjectFactory objectFactory() {
		return objectFactory;
	}

	@Override
	public SymbolicTypeFactory typeFactory() {
		return typeFactory;
	}

	@Override
	public ExpressionFactory expressionFactory() {
		return expressionFactory;
	}

	@Override
	public NumberFactory numberFactory() {
		return objectFactory.numberFactory();
	}

	@Override
	public NumericExpressionFactory numericFactory() {
		return expressionFactory.numericFactory();
	}

	@Override
	public BooleanExpressionFactory booleanFactory() {
		return expressionFactory.booleanFactory();
	}

}
