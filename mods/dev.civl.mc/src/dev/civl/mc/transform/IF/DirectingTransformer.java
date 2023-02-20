package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.DirectingWorker;

/**
 * This transformer instruments the model so as to direct
 * the symbolic execution down a subset of program paths.
 * 
 * @author dwyer
 * 
 */
public class DirectingTransformer extends BaseTransformer {

	public final static String CODE = "direct";
	public final static String LONG_NAME = "DirectedSymEx";
	public final static String SHORT_DESCRIPTION = "instruments model to direct sybolic execution";
	private CIVLConfiguration config;

	public DirectingTransformer(ASTFactory astFactory, CIVLConfiguration config) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.config = config;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new DirectingWorker(astFactory, config).transform(ast);
	}

}
