/**
 * 
 */
package dev.civl.mc.model.common;

import java.io.PrintStream;

import dev.civl.abc.program.IF.Program;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.model.IF.Model;
import dev.civl.mc.model.IF.ModelBuilder;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.gmc.CommandLineException;
import dev.civl.gmc.GMCSection;
import dev.civl.sarl.IF.SymbolicUniverse;

/**
 * Class to provide translation from an ABC AST to a CIVL model.
 * 
 * @author zirkeltk
 * 
 */
public class CommonModelBuilder implements ModelBuilder {

	// Fields..........................................................

	/**
	 * The factory used to create new Model components.
	 */
	private ModelFactory factory;

	// private boolean mpiMode = false;

	// Constructors....................................................

	/**
	 * Constructs new instance of CommonModelBuilder, creating instance of
	 * ModelFactory in the process, and sets up system functions.
	 * 
	 * @param universe
	 *            The symbolic universe
	 */
	public CommonModelBuilder(SymbolicUniverse universe,
			CIVLConfiguration config) {
		// this.mpiMode = mpiMode;
		// if (!mpiMode)
		factory = new CommonModelFactory(universe, config);
		// else
		// factory = new CommonMPIModelFactory(universe);
	}

	/**
	 * Constructs new instance of CommonModelBuilder and sets up system
	 * functions.
	 * 
	 * @param universe
	 *            The symbolic universe
	 * @param mpiMode
	 *            True iff this is an MPI program.
	 * @param factory
	 *            The model factory to be used for constructing this model.
	 */
	public CommonModelBuilder(SymbolicUniverse universe, ModelFactory factory) {
		// this.mpiMode = mpiMode;
		this.factory = factory;
	}

	// Exported Methods................................................

	@Override
	public Model buildModel(GMCSection config, Program program, String name,
			boolean debugging, PrintStream debugOut)
			throws CommandLineException {
		ModelBuilderWorker worker;

		// if (!this.mpiMode)
		worker = new ModelBuilderWorker(config, factory, program, name,
				debugging, debugOut);
		// else
		// worker = new MPIModelBuilderWorker(config,
		// (MPIModelFactory) factory, program, name, debugging,
		// debugOut);
		worker.buildModel();
		return worker.getModel();
	}

	@Override
	public ModelFactory factory() {
		return factory;
	}

}
