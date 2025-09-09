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
package dev.civl.sarl.simplify.IF;

import java.util.List;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.simplify.common.CommonContextPartition;
import dev.civl.sarl.simplify.common.IntervalUnionFactory;

/**
 * Entry point for module "simplify", providing static method to create basic
 * range factories and context partitions
 * 
 * @author Stephen F. Siegel
 */
public class Simplify {

	public static RangeFactory newIntervalUnionFactory() {
		return new IntervalUnionFactory();
	}

	public static ContextPartition newContextPartition(PreUniverse universe,
			List<BooleanExpression> contextStack) {
		return new CommonContextPartition(contextStack, universe);
	}

}
