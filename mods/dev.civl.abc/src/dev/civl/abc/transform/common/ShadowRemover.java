package dev.civl.abc.transform.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.entity.IF.Entity;
import dev.civl.abc.ast.entity.IF.OrdinaryEntity;
import dev.civl.abc.ast.entity.IF.Scope;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.ASTNode.NodeKind;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.transform.IF.BaseTransformer;

/**
 * Renames any variable not in file scope that has the same name as a variable
 * in file scope.
 * 
 * @author siegel
 *
 */
public class ShadowRemover extends BaseTransformer {

	public final static String CODE = "shadow";
	public final static String LONG_NAME = "ShadowRemover";
	public final static String SHORT_DESCRIPTION = "renames entities that shadow others";

	private Map<Entity, String> nameMap = new HashMap<>();

	private Map<String, Integer> countMap = new HashMap<>();

	public ShadowRemover(ASTFactory factory) {
		super(CODE, LONG_NAME, SHORT_DESCRIPTION, factory);
	}

	/**
	 * Iterate over ordinary entities in this scope. For each entity x, if x has
	 * the same name as another ordinary entity in a higher scope, choose a new
	 * name for x and add it to the map. If the old name is "X" the new name
	 * will be "_X_n", where n is an integer.
	 * 
	 * Then invokes itself on children scopes.
	 * 
	 * @param scope
	 */
	private void pass1(Scope scope) {
		Iterable<OrdinaryEntity> entities = scope.getOrdinaryEntities();

		for (OrdinaryEntity entity : entities) {
			String name = entity.getName();

			for (Scope scope2 = scope
					.getParentScope(); scope2 != null; scope2 = scope2
							.getParentScope()) {
				Entity entity2 = scope2.getOrdinaryEntity(false, name);

				if (entity2 != null) {
					Integer count = countMap.get(name);

					if (count == null)
						count = 1;
					else
						count++;

					String newName = "_" + name + "_" + count;

					nameMap.put(entity, newName);
					countMap.put(name, count);
					break;
				}
			}
		}

		Iterator<Scope> childIter = scope.getChildrenScopes();

		while (childIter.hasNext())
			pass1(childIter.next());
	}

	/**
	 * Go over all nodes, updating names.
	 */
	private void pass2(AST ast) {
		int numNodes = ast.getNumberOfNodes();

		for (int i = 0; i < numNodes; i++) {
			ASTNode node = ast.getNode(i);
			NodeKind kind = node.nodeKind();

			if (kind == NodeKind.IDENTIFIER) {
				IdentifierNode identNode = (IdentifierNode) node;
				Entity entity = identNode.getEntity();

				if (entity != null) {
					String newName = nameMap.get(entity);

					if (newName != null)
						identNode.setName(newName);
				}
			}
		}
	}

	@Override
	public AST transform(AST ast) throws SyntaxException {
		SequenceNode<BlockItemNode> rootNode = ast.getRootNode();
		Scope rootScope = rootNode.getScope();
		Iterator<Scope> scopeIter = rootScope.getChildrenScopes();

		while (scopeIter.hasNext())
			pass1(scopeIter.next());
		if (nameMap.isEmpty())
			return ast;
		pass2(ast);
		ast.release();

		AST result = astFactory.newAST(rootNode, ast.getSourceFiles(),
				ast.isWholeProgram());

		countMap = new HashMap<>();
		nameMap = new HashMap<>();
		return result;
	}
}
