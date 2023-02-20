package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.Pthread2CIVLWorker;

public class Pthread2CIVLTransformer extends BaseTransformer {

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "pthread";

	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "PthreadTransformer";

	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "transforms C/Pthread program to CIVL-C";

	public Pthread2CIVLTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new Pthread2CIVLWorker(astFactory).transform(ast);
	}
}
