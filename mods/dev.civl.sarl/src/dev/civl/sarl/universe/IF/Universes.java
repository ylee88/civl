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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.civl.sarl.IF.SARLConstants;
import dev.civl.sarl.IF.SARLException;
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

	/**
	 * Creates a new directory to be used by theorem provers. The new directory will
	 * be created in ./defaultProofDir, i.e., within a directory named
	 * defaultProofDir in the current working directory, where defaultProofDir is a
	 * string constant defined in {@link SARLConstants#defaultProofDir}.
	 * 
	 * The directory ./defaultProofDir will be created if it does not already exist.
	 * 
	 * @return the default working directory for provers
	 */
	public final static Path makeProverDir() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		Path proofPath = Paths.get(tmpDir, SARLConstants.defaultProofDir).toAbsolutePath();
		try {
			if (Files.exists(proofPath)) {
				if (!Files.isDirectory(proofPath))
					throw new SARLException("A file exists at " + proofPath + " but is not a directory.");
			} else {
				Files.createDirectory(proofPath);
			}
			Path result = Files.createTempDirectory(proofPath, "universe");
			return result;
		} catch (IOException e) {
			throw new SARLException(e.toString());
		}
	}

	public static SymbolicUniverse newIdealUniverse(SARLConfig config, ProverInfo prover, Path workingDirectory) {
		FactorySystem system = PreUniverses.newIdealFactorySystem();
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config, workingDirectory)
				: Prove.newProverFactory(universe, prover, workingDirectory);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe, idealFactory, proverFactory);

		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newIdealUniverse(SARLConfig config, ProverInfo prover) {
		return newIdealUniverse(config, prover, makeProverDir());
	}

	public static SymbolicUniverse newIdealUniverse(Path workingDirectory) {
		return newIdealUniverse(Configurations.getDefaultConfiguration(), null, workingDirectory);
	}

	public static SymbolicUniverse newIdealUniverse() {
		return newIdealUniverse(makeProverDir());
	}

	public static SymbolicUniverse newHerbrandUniverse(SARLConfig config, ProverInfo prover, Path workingDirectory) {
		FactorySystem system = PreUniverses.newHerbrandFactorySystem();
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config, workingDirectory)
				: Prove.newProverFactory(universe, prover, workingDirectory);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe, idealFactory, proverFactory);

		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newHerbrandUniverse(SARLConfig config, ProverInfo prover) {
		return newHerbrandUniverse(config, prover, makeProverDir());
	}

	public static SymbolicUniverse newHerbrandUniverse(Path workingDirectory) {
		return newHerbrandUniverse(Configurations.getDefaultConfiguration(), null, workingDirectory);
	}

	public static SymbolicUniverse newHerbrandUniverse() {
		return newHerbrandUniverse(makeProverDir());
	}

	public static SymbolicUniverse newStandardUniverse(SARLConfig config, ProverInfo prover, Path workingDirectory) {
		NumberFactory numberFactory = Numbers.REAL_FACTORY;
		ObjectFactory objectFactory = Objects.newObjectFactory(numberFactory);
		SymbolicTypeFactory typeFactory = Types.newTypeFactory(objectFactory);
		ExpressionFactory expressionFactory = Expressions.newStandardExpressionFactory(numberFactory, objectFactory,
				typeFactory);
		FactorySystem system = PreUniverses.newFactorySystem(objectFactory, typeFactory, expressionFactory);
		IdealFactory idealFactory = getIdealFactory(system);
		CommonSymbolicUniverse universe = new CommonSymbolicUniverse(system);
		TheoremProverFactory proverFactory = prover == null
				? Prove.newMultiProverFactory(universe, config, workingDirectory)
				: Prove.newProverFactory(universe, prover, workingDirectory);
		ReasonerFactory reasonerFactory = Reason.newReasonerFactory(universe, idealFactory, proverFactory);

		universe.setReasonerFactory(reasonerFactory);
		return universe;
	}

	public static SymbolicUniverse newStandardUniverse(SARLConfig config, ProverInfo prover) {
		return newStandardUniverse(config, prover, makeProverDir());
	}

	public static SymbolicUniverse newStandardUniverse(Path workingDirectory) {
		return newStandardUniverse(Configurations.getDefaultConfiguration(), null, workingDirectory);
	}

	public static SymbolicUniverse newStandardUniverse() {
		return newStandardUniverse(makeProverDir());
	}

	/*
	 * Small hack to obtain an IdealFactory regardless if we are working with a
	 * Herbrand FactorySystem or not. Eventually, Ideal and Herbrand universes will
	 * be merged into one and then we can remove this hack.
	 */
	private static IdealFactory getIdealFactory(FactorySystem system) {
		NumericExpressionFactory numericFactory = system.numericFactory();
		return (IdealFactory) (numericFactory instanceof IdealFactory ? numericFactory
				: numericFactory instanceof CommonNumericExpressionFactory
						? ((CommonNumericExpressionFactory) numericFactory).idealFactory()
						: PreUniverses.newIdealFactorySystem().numericFactory());
	}

}
