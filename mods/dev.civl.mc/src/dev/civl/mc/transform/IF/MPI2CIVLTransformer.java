package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.MPI2CIVLWorker;

/**
 * MPI2CIVLTransformer transforms an AST of an MPI program into an AST of an
 * equivalent CIVL-C program. See {@linkplain #transform(AST)}. TODO: copy
 * output files only for the mpi process with rank 0.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public class MPI2CIVLTransformer extends BaseTransformer {

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "mpi";

	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "MPITransformer";

	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "transforms C/MPI program to CIVL-C";

	/**
	 * Creates a new instance of MPITransformer.
	 * 
	 * @param astFactory
	 *            The ASTFactory that will be used to create new nodes.
	 */
	public MPI2CIVLTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new MPI2CIVLWorker(astFactory).transform(ast);
	}

}
