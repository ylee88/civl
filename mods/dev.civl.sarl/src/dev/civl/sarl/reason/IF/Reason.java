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
package dev.civl.sarl.reason.IF;

import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.reason.common.ContextMinimizingReasonerFactory;

/**
 * Provides a static method for producing a new {@link ReasonerFactory}.
 * 
 * @author siegel
 *
 */
public class Reason {

	/**
	 * Create a reasoner factory
	 * 
	 * @param universe
	 *            A reference to a {@link PreUniverse}
	 * @param simplifierFactory
	 *            A reference to a {@link SimplifierFactory}
	 * @param proverFactory
	 *            A reference to a {@link TheoremProverFactory}
	 * @return
	 */
	public static ReasonerFactory newReasonerFactory(PreUniverse universe,
			IdealFactory idealFactory, TheoremProverFactory proverFactory) {
		ReasonerFactory result = new ContextMinimizingReasonerFactory(universe,
				idealFactory, proverFactory);

		return result;
	}
}
