package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.OpenMP2CIVLWorker2;

/**
 * OpenMP2CIVLTransformer transforms an AST of an OpenMP program into an AST of
 * an equivalent CIVL-C program. See {@linkplain #transform(AST)}.
 * 
 * @author Michael Rogers
 * 
 */
public class OpenMP2CIVLTransformer extends BaseTransformer {

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "openmp";

	/**
	 * The long name of the transformer.
	 */
	public static String LONG_NAME = "OpenMPTransformer";

	/**
	 * The description of this transformer.
	 */
	public static String SHORT_DESCRIPTION = "transforms C/OpenMP program to CIVL-C";

	private CIVLConfiguration config;

	/**
	 * Creates a new instance of OpenMP2CIVLTransformer.
	 * 
	 * @param astFactory
	 *            The ASTFactory that will be used to create new nodes.
	 */
	public OpenMP2CIVLTransformer(ASTFactory astFactory,
			CIVLConfiguration config) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.config = config;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new OpenMP2CIVLWorker2(astFactory, this.config).transform(ast);
	}

}
