package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.Cuda2CIVLWorker;

public class Cuda2CIVLTransformer extends BaseTransformer {

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "cuda";

	/**
	 * The long name of the transformer.
	 */
	public static String LONG_NAME = "Cuda2CIVLTransformer";

	/**
	 * The description of this transformer.
	 */
	public static String SHORT_DESCRIPTION = "transforms CUDA/C program to CIVL-C";

	/**
	 * 
	 * 
	 * @param astFactory
	 *            The ASTFactory that will be used to create new nodes.
	 */
	public Cuda2CIVLTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new Cuda2CIVLWorker(astFactory).transform(ast);
	}
}
