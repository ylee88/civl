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
package dev.civl.sarl.universe.IF;

import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.config.Configurations;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.SARLConfig;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.expr.IF.Expressions;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.expr.common.CommonNumericExpressionFactory;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.number.IF.Numbers;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.object.IF.Objects;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.reason.IF.Reason;
import dev.civl.sarl.reason.IF.ReasonerFactory;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;
import dev.civl.sarl.type.IF.Types;
import dev.civl.sarl.universe.common.CommonSymbolicUniverse;

/**
 * This class provides static methods for the creation of new
 * {@link SymbolicUniverse}s.
 * 
 * @author siegel
 */
public class Universes {

	public static SymbolicUniverse newIdealUniverse(SARLConfig config,
			ProverInfo prover) {
		FactorySystem system = PreUniverses.newIdealFactorySystem();
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config)
				: Prove.newProverFactory(universe, prover);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe,
				idealFactory, proverFactory);
//		if (config.getWhy3ProvePlatform() != null) {
//			Why3ReasonerFactory why3ReasonerFactory = Reason
//					.newWhy3ReasonerFactory(config, universe, simplifierFactory,
//							Prove.newWhy3ProvePlatformFactory(universe,
//									config.getWhy3ProvePlatform(), config));
//
//			universe.setWhy3ReasonerFactory(why3ReasonerFactory);
//		}
		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newIdealUniverse() {
		return newIdealUniverse(Configurations.getDefaultConfiguration(), null);
	}

	public static SymbolicUniverse newHerbrandUniverse(SARLConfig config,
			ProverInfo prover) {
		FactorySystem system = PreUniverses.newHerbrandFactorySystem();
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config)
				: Prove.newProverFactory(universe, prover);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe,
				idealFactory, proverFactory);

//		if (config.getWhy3ProvePlatform() != null) {
//			Why3ReasonerFactory why3ReasonerFactory = Reason
//					.newWhy3ReasonerFactory(config, universe, simplifierFactory,
//							Prove.newWhy3ProvePlatformFactory(universe,
//									config.getWhy3ProvePlatform(), config));
//
//			universe.setWhy3ReasonerFactory(why3ReasonerFactory);
//		}

		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newHerbrandUniverse() {
		return newHerbrandUniverse(Configurations.getDefaultConfiguration(),
				null);
	}

	public static SymbolicUniverse newStandardUniverse(SARLConfig config,
			ProverInfo prover) {
		NumberFactory numberFactory = Numbers.REAL_FACTORY;
		ObjectFactory objectFactory = Objects.newObjectFactory(numberFactory);
		SymbolicTypeFactory typeFactory = Types.newTypeFactory(objectFactory);
		ExpressionFactory expressionFactory = Expressions
				.newStandardExpressionFactory(numberFactory, objectFactory,
						typeFactory);

		FactorySystem system = PreUniverses.newFactorySystem(objectFactory,
				typeFactory, expressionFactory);
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config)
				: Prove.newProverFactory(universe, prover);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe,
				idealFactory, proverFactory);
//		if (config.getWhy3ProvePlatform() != null) {
//			Why3ReasonerFactory why3ReasonerFactory = Reason
//					.newWhy3ReasonerFactory(config, universe, simplifierFactory,
//							Prove.newWhy3ProvePlatformFactory(universe,
//									config.getWhy3ProvePlatform(), config));
//
//			universe.setWhy3ReasonerFactory(why3ReasonerFactory);
//		}
		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newStandardUniverse() {
		return newStandardUniverse(Configurations.getDefaultConfiguration(),
				null);
	}

	/*
	 * Small hack to obtain an IdealFactory regardless if we are working with a
	 * Herbrand FactorySystem or not. Eventually, Ideal and Herbrand universes
	 * will be merged into one and then we can remove this hack.
	 */
	private static IdealFactory getIdealFactory(FactorySystem system) {
		NumericExpressionFactory numericFactory = system.numericFactory();
		return (IdealFactory) (numericFactory instanceof IdealFactory
				? numericFactory
				: numericFactory instanceof CommonNumericExpressionFactory ?
						((CommonNumericExpressionFactory) numericFactory).idealFactory() :
							PreUniverses.newIdealFactorySystem().numericFactory());
	}

}
