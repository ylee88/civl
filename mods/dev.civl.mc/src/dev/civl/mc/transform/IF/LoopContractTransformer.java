package dev.civl.mc.transform.IF;

import java.io.File;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.config.IF.CIVLConstants;
import dev.civl.mc.transform.common.LoopContractTransformerWorker;

public class LoopContractTransformer extends BaseTransformer {

	public final static File[] additionalLibraries = {
			new File(CIVLConstants.CIVL_LIB_INCLUDE_PATH, "mem.cvh"),
			new File(CIVLConstants.CIVL_LIB_INCLUDE_PATH, "string.h"),
			new File(CIVLConstants.CIVL_LIB_SRC_PATH, "string.cvl")};

	private final CIVLConfiguration config;
	
	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "loop";

	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "LoopContractTransformer";

	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "transforms loops contracts"
			+ " into CIVL IR.";

	protected LoopContractTransformer(ASTFactory astFactory, CIVLConfiguration civlConfig) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.config = civlConfig;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new LoopContractTransformerWorker(LONG_NAME, astFactory, config)
				.transform(ast);
	}
}
