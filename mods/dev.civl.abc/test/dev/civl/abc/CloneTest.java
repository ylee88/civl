package dev.civl.abc;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.main.TranslationTask.TranslationStage;

/**
 * Tests the "copy" method in nodes, which performs deep cloning.
 * 
 * @author siegel
 * 
 */
public class CloneTest {

	private static File root = new File("examples");

	private void check(String filenameRoot) throws ABCException {
		AST ast1, ast2;
		SequenceNode<BlockItemNode> root1, root2;
		TranslationTask task = new TranslationTask(
				new File(root, filenameRoot + ".c"));

		task.setStage(TranslationStage.ANALYZE_ASTS);

		ABCExecutor executor = ABCExecutor.execute(task);
		ASTFactory astFactory = executor.getFrontEnd().getASTFactory();

		ast1 = executor.getAST(0);
		root1 = ast1.getRootNode();
		root2 = root1.copy();
		ast2 = astFactory.newAST(root2, ast1.getSourceFiles(),
				ast1.isWholeProgram());
		assertEquals(ast1.getNumberOfNodes(), ast2.getNumberOfNodes());
	}

	@Test
	public void adder_seq() throws ABCException {
		check("adder_seq");
	}

	@Test
	public void useNull() throws ABCException {
		check("parse/useNull");
	}

	@Test
	public void pointer1() throws ABCException {
		check("parse/pointer1");
	}

	@Test
	public void pointer2() throws ABCException {
		check("parse/pointer2");
	}
}
