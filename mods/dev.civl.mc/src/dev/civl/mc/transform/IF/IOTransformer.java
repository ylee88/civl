package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.IOWorker;

/**
 * The IO transformer transforms<br>
 * <ul>
 * <li>all function calls printf(...) into frpintf(stdout, ...)</li>
 * <li>all function calls scanf(...) into fscanf(stdin, ...)</li>
 * <li>all function calls fopen(...) into $fopen(...)</li>
 * </ul>
 * 
 * @author zmanchun
 * 
 */
public class IOTransformer extends BaseTransformer {

	/*
	 * ************************** Public Static Fields ***********************
	 */
	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "io";

	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "IOTransformer";

	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "transforms C program with IO to CIVL-C";

	private CIVLConfiguration config;

	/**
	 * Creates a new instance of IO transformer.
	 * 
	 * @param astFactory
	 *            The AST factory to be used.
	 * @param inputVariables
	 *            The input variables specified from command line.
	 * @param config
	 *            The CIVL configuration.
	 */
	public IOTransformer(ASTFactory astFactory, CIVLConfiguration config) {
		super(IOTransformer.CODE, IOTransformer.LONG_NAME,
				IOTransformer.SHORT_DESCRIPTION, astFactory);
		this.config = config;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new IOWorker(astFactory, config).transform(ast);
	}

}
