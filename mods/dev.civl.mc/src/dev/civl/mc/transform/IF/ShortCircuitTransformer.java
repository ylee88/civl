package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.transform.common.ShortCircuitTransformerWorker;

public class ShortCircuitTransformer extends BaseTransformer {

	public final static String CODE = "short-circuit";
	public final static String LONG_NAME = "shortCircuitTransformer";
	public final static String SHORT_DESCRIPTION = "transforms logical AND, logical OR and logical IMPLIES away to lift short circuit evaluation in CIVL backend";
	public final static String PREFIX = "_shcc_";

	public ShortCircuitTransformer(ASTFactory astFactory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new ShortCircuitTransformerWorker(code, astFactory)
				.transform(ast);
	}
}
