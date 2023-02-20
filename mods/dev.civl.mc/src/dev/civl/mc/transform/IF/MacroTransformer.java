package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.MacroWorker;

public class MacroTransformer extends BaseTransformer {

	public final static String CODE = "macro";
	public final static String LONG_NAME = "MacroTransformer";
	public final static String SHORT_DESCRIPTION = "recovers macros from C programs to CIVL-C";

	private CIVLConfiguration config;

	public MacroTransformer(ASTFactory astFactory, CIVLConfiguration config) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.config = config;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new MacroWorker(astFactory, config).transform(ast);
	}
}
