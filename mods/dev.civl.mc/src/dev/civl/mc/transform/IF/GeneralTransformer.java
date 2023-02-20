package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.GeneralWorker;

public class GeneralTransformer extends BaseTransformer {

	public final static String CODE = "general";
	public final static String LONG_NAME = "GeneralTransformer";
	public final static String SHORT_DESCRIPTION = "transforms general features of C programs to CIVL-C";
	public final static String PREFIX = "_civl_";

	public GeneralTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new GeneralWorker(astFactory).transform(ast);
	}

}
