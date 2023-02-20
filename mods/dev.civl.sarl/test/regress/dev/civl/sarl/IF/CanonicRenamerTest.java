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
package dev.civl.sarl.IF;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;

public class CanonicRenamerTest {
	private SymbolicUniverse universe = SARL.newStandardUniverse();
	private SymbolicType integerType = universe.integerType();

	private SymbolicConstant intConstant(String name) {
		return universe.symbolicConstant(universe.stringObject(name),
				integerType);
	}

	/**
	 * This method tests that a canonical renamer with root "Y" should never
	 * change any symobol that does NOT start with "Y".
	 */
	@Test
	public void renamerY() {
		SymbolicExpression xa = intConstant("Xa"), Yb = intConstant("Yb"), newXa;
		CanonicalRenamer renamer = universe.canonicalRenamer("Y");

		newXa = renamer.apply(xa);
		Yb = renamer.apply(Yb);
		assertEquals(xa, newXa);
	}
}
