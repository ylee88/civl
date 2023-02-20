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
package dev.civl.sarl.simplify.common;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.simplify.IF.Simplifier;
import dev.civl.sarl.simplify.IF.SimplifierFactory;

/**
 * Factory for producing instances of {@link IdentitySimplifier}.
 * 
 * @author Stephen F. Siegel
 */
public class IdentitySimplifierFactory implements SimplifierFactory {

	private PreUniverse universe;

	public IdentitySimplifierFactory(PreUniverse universe) {
		this.universe = universe;
	}

	@Override
	public Simplifier newSimplifier(BooleanExpression assumption,
			boolean useBackwardSubstitution) {
		return new IdentitySimplifier(universe, assumption);
	}

}
