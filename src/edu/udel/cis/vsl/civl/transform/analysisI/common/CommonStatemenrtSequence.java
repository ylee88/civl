package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.StatementSequence;

public class CommonStatemenrtSequence implements StatementSequence {

	/**
	 * The set of memory locations (i.e. variable, string, heap objects) in the
	 * given program fragment
	 */
	Set<Entity> memoryLocations = null;

	/**
	 * A fragment of a program in the form of an {@link AST}
	 */
	private List<ASTNode> programFragment;

	/**
	 * A sequence of statements which is an abstraction of the "programFragment"
	 */
	private LinkedList<StatementNode> sequence = null;

	/**
	 * A iterator of this sequence
	 */
	private ListIterator<StatementNode> iter = null;

	/**
	 * @param programFragment
	 *            a sequence of ASTNodes representing a fragment of a program
	 */
	CommonStatemenrtSequence(List<ASTNode> programFragment) {
		this.programFragment = programFragment;
		buildAbstraction();
		assert sequence != null;
		iter = sequence.listIterator();
	}

	@Override
	public Iterable<StatementNode> getAll() {
		return sequence;
	}

	@Override
	public StatementNode next() {
		return iter.next();
	}

	@Override
	public StatementNode prev() {
		return iter.previous();
	}

	@Override
	public void reset() {
		iter = sequence.listIterator();
	}

	@Override
	public Set<Entity> memoryLocations() {
		return memoryLocations;
	}

	/* ******** converting program fragment to statement sequence *********/
	private void buildAbstraction() {
		assert sequence == null : "cannot be built twice";
		sequence = new LinkedList<>();
		memoryLocations = new HashSet<>();
		for (ASTNode astNode : programFragment) {
			sequence.addAll(buildForAstNode(astNode));
		}
	}

	private List<StatementNode> buildForAstNode(ASTNode node) {
		NodeKind kind = node.nodeKind();
		List<StatementNode> result = new LinkedList<>();

		switch (kind) {
			case EXPRESSION :
				// return buildForExpression(node);
			case FUNCTION_DEFINITION :
				// return buildForStatement(node);
			case GENERIC_SELECTION :
				break;
			case PRAGMA :
				break;
			case SEQUENCE :
				break;
			case STATEMENT :
				// return buildForStatement(node);
				// ignores:
			case COLLECTIVE :
			case CONTRACT :
			case DEPENDS_EVENT :
			case STATIC_ASSERTION : // no side-effect
			case SWITCH_LABEL :
			case TYPE :
			case TYPEDEF :
			case RESULT :
			case PAIR :
			case IDENTIFIER :
			case OMP_NODE :
			case OMP_REDUCTION_OPERATOR :
			case ORDINARY_LABEL :
			case FIELD_DECLARATION :
			case FIELD_DESIGNATOR :
			case FUNCTION_DECLARATION :
				// this is post-semantics-analysis analysis, no need to process
				// decls
			case ARRAY_DESIGNATOR :
			case DECLARATION_LIST :
			case DESIGNATION :
			case ENUMERATOR_DECLARATION :
			case SCOPE_PARAMETERIZED_DECLARATION :
			case VARIABLE_DECLARATION :
				break;
			default :
				break;
		}
		return result;
	}
}
