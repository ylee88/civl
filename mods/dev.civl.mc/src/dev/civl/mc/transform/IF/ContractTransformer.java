package dev.civl.mc.transform.IF;

import java.io.File;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.contracts.ContractTransformerWorker;

public class ContractTransformer extends BaseTransformer {

	private final static String MEM_HEADER = "/include/abc/mem.cvh";

	public final static File[] additionalLibraries = {new File(MEM_HEADER)};

	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "contract";

	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "ContractTransformer";

	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "transforms C/MPI program "
			+ "to a set of programs each of which verifies a single function";

	private String targetFunction;

	/**
	 * A reference to {@link CIVLConfiguration}
	 */
	private CIVLConfiguration civlConfig;

	public ContractTransformer(ASTFactory astFactory, String targetFunction,
			CIVLConfiguration civlConfig) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.targetFunction = targetFunction;
		this.civlConfig = civlConfig;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		return new ContractTransformerWorker(astFactory, targetFunction,
				civlConfig).transform(ast);
	}
}
