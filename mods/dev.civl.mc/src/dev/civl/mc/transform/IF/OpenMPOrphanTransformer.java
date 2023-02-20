package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.OpenMPOrphanWorker;

public class OpenMPOrphanTransformer extends BaseTransformer {
	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "openmp_orphan";

	/**
	 * The long name of the transformer.
	 */
	public static String LONG_NAME = "OpenMPOrphanTransformer";

	/**
	 * The description of this transformer.
	 */
	public static String SHORT_DESCRIPTION = "transforms away orphan constructs of C/OpenMP programs";

	/**
	 * Creates a new instance of OpenMP2CIVLTransformer.
	 * 
	 * @param astFactory
	 *            The ASTFactory that will be used to create new nodes.
	 */
	public OpenMPOrphanTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new OpenMPOrphanWorker(astFactory).transform(ast);
	}

}
