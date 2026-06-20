package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.ComplexWorker;

public class ComplexTransformer extends BaseTransformer {

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "complex";

	/**
	 * The long name of the transformer.
	 */
	public static String LONG_NAME = "ComplexTransformer";

	/**
	 * The description of this transformer.
	 */
	public static String SHORT_DESCRIPTION = "transforms C complex numbers to structs";

	/**
	 * 
	 * 
	 * @param astFactory The ASTFactory that will be used to create new nodes.
	 */
	public ComplexTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new ComplexWorker(LONG_NAME, astFactory).transform(ast);
	}
}
