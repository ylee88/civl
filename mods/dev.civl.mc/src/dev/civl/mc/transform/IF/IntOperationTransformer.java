package dev.civl.mc.transform.IF;

import java.util.Map;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.transform.common.IntOperationWorker;

/**
 * This transformer is used to
 * <ul>
 * <li>replace integer division ('/') and integer modulo ('%') in the program
 * with $int_div(int, int) and $int_mod(int, int) functions respectively</li>
 * <li>replace unsigned integer arithmetic operations with corresponding library
 * functions: int $unsigned_add, int $unsigned_subtract, int $unsigned_multiply
 * </li>
 * </ul>
 * 
 * @author yanyihao
 *
 */
public class IntOperationTransformer extends BaseTransformer {
	/**
	 * The code (short name) of this transformer.
	 */
	public final static String CODE = "int_operation";
	/**
	 * The long name of the transformer.
	 */
	public final static String LONG_NAME = "IntOperationTransformer";
	/**
	 * The description of this transformer.
	 */
	public final static String SHORT_DESCRIPTION = "integer arithmetic operations to its corresponding library functions.";

	private Map<String, String> macros;

	private CIVLConfiguration civlConfig;

	public IntOperationTransformer(ASTFactory astFactory,
			Map<String, String> macros, CIVLConfiguration config) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, astFactory);
		this.macros = macros;
		this.civlConfig = config;
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		IntOperationWorker worker = new IntOperationWorker(astFactory, macros,
				civlConfig);

		return worker.transform(ast);
	}

}
