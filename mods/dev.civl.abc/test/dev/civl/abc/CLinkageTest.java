package dev.civl.abc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.entity.IF.OrdinaryEntity;
import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.declaration.FunctionDeclarationNode;
import dev.civl.abc.ast.node.IF.declaration.FunctionDefinitionNode;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.main.TranslationTask.TranslationStage;

/**
 * Tests linkage issues: internal, external, or "none".
 * 
 * @author siegel
 * 
 */
public class CLinkageTest {

	private File root = new File("examples");

	private AST getAST(File file) throws ABCException {
		TranslationTask task = new TranslationTask(file);

		task.setStage(TranslationStage.ANALYZE_ASTS);
		return ABCExecutor.execute(task).getAST(0);
	}

	@Test
	public void inner_func() throws ABCException {
		File file = new File(root, "inner_func.c");
		AST ast = getAST(file);
		OrdinaryEntity entity = ast.getInternalOrExternalEntity("f");
		FunctionDeclarationNode prototype = null;
		FunctionDefinitionNode fDefn = null;
		FunctionDeclarationNode innerPrototype = null;

		// check there is an entity in the AST with internal
		// linkage called "f". It should be a function...
		assertNotNull(entity);
		assertTrue(entity instanceof Function);
		assertEquals(ProgramEntity.LinkageKind.EXTERNAL, entity.getLinkage());
		for (ASTNode exDef : ast.getRootNode().children()) {
			switch (exDef.nodeKind()) {
			case FUNCTION_DECLARATION: {
				FunctionDeclarationNode decl = (FunctionDeclarationNode) exDef;

				if ("f".equals(decl.getName()))
					prototype = decl;
				break;
			}
			case FUNCTION_DEFINITION: {
				FunctionDefinitionNode defn = (FunctionDefinitionNode) exDef;
				String name = defn.getName();

				if ("f".equals(name))
					fDefn = defn;
				else if ("main".equals(name)) {
					for (ASTNode stmt : defn.getBody().children()) {
						if (stmt instanceof FunctionDeclarationNode
								&& ((FunctionDeclarationNode) stmt).getName()
										.equals("f")) {
							innerPrototype = (FunctionDeclarationNode) stmt;
							break;
						}
					}
				}
				break;
			}
			default:
			}
		}
		// check the function prototype which is an external defn
		// for f has that as its entity...
		assertNotNull(prototype);
		assertEquals(entity, prototype.getEntity());
		// check the function definition which is an external defn
		// for f has f as its entity...
		assertNotNull(fDefn);
		assertEquals(entity, fDefn.getEntity());
		// get the body of main and find within it a declaration
		// for a function named f. Check that it has the same entity.
		assertNotNull(innerPrototype);
		assertEquals(entity, innerPrototype.getEntity());
	}
}
