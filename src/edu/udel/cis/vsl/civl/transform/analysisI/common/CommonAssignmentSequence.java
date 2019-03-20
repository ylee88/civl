package edu.udel.cis.vsl.civl.transform.analysisI.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.IdentifierNode;
import edu.udel.cis.vsl.abc.ast.node.IF.SequenceNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.StatementExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.AtomicNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ChooseStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CivlForNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CompoundStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.DeclarationListNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ExpressionStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.IfNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.LabeledStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.LoopNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.RunNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode.StatementKind;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.SwitchNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.WhenNode;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;

public class CommonAssignmentSequence implements AssignmentSequence {

	public class CommonAssignment implements AssignmentIF {
		private ExpressionNode lhs = null;

		private IdentifierNode declLhs = null;

		private ExpressionNode rhs;

		private boolean isDecl = false;

		CommonAssignment(ExpressionNode lhs, ExpressionNode rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		CommonAssignment(IdentifierNode lhs, ExpressionNode rhs) {
			this.declLhs = lhs;
			this.rhs = rhs;
			this.isDecl = true;
		}

		@Override
		public ASTNode lhs() {
			if (!isDecl)
				return lhs;
			else
				return declLhs;
		}

		@Override
		public ExpressionNode rhs() {
			return rhs;
		}

		@Override
		public boolean isDecl() {
			return isDecl;
		}
	}

	/**
	 * The set of memory locations (i.e. variable, string, heap objects) in the
	 * given program fragment
	 */
	Set<Entity> memoryLocations = null;

	/**
	 * A fragment of a program in the form of an {@link AST}
	 */
	private Iterable<ASTNode> programFragment;

	/**
	 * A sequence of statements which is an abstraction of the "programFragment"
	 */
	private LinkedList<AssignmentIF> sequence = null;

	/**
	 * A iterator of this sequence
	 */
	private ListIterator<AssignmentIF> iter = null;

	/**
	 * @param programFragment
	 *            a sequence of ASTNodes representing a fragment of a program
	 */
	public CommonAssignmentSequence(Iterable<ASTNode> programFragment) {
		this.programFragment = programFragment;
		buildAbstraction();
		assert sequence != null;
		iter = sequence.listIterator();
	}

	@Override
	public Iterable<AssignmentIF> getAll() {
		return sequence;
	}

	@Override
	public AssignmentIF next() {
		return iter.next();
	}

	@Override
	public AssignmentIF prev() {
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

	@Override
	public String toString() {
		String ret = "";

		for (AssignmentIF assign : sequence) {
			ret += assign.lhs() == null
					? "?"
					: assign.lhs().prettyRepresentation();
			ret += " = ";
			ret += assign.rhs().prettyRepresentation() + ";\n";
		}
		return ret;
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

	private List<AssignmentIF> buildForAstNode(ASTNode node) {
		NodeKind kind = node.nodeKind();
		List<AssignmentIF> result = new LinkedList<>();

		switch (kind) {
			case EXPRESSION :
				result.addAll(buildForExpression((ExpressionNode) node));
				break;
			case FUNCTION_DEFINITION :
				// TODO: nested function definition handling
				// return buildForStatement(
				// ((FunctionDefinitionNode) node).getBody());
				throw new CIVLUnimplementedFeatureException(
						"Deal with nested function definition for points-to analysis");
			case SEQUENCE :
				return buildForSequence((SequenceNode<?>) node);
			case STATEMENT :
				return buildForStatement((StatementNode) node);
			case DECLARATION_LIST :
				DeclarationListNode declList = (DeclarationListNode) node;

				for (VariableDeclarationNode varDecl : declList)
					result.addAll(buildForVariableDeclaration(varDecl));
				break;
			case VARIABLE_DECLARATION :
				result.addAll(buildForVariableDeclaration(
						(VariableDeclarationNode) node));
				break;
			default :
				break;

		}
		return result;
	}

	/**
	 * simply call "buildForAstNode" for every sequence element
	 * 
	 * @param seqNode
	 * @return
	 */
	private List<AssignmentIF> buildForSequence(
			SequenceNode<? extends ASTNode> seqNode) {
		List<AssignmentIF> results = new LinkedList<>();

		for (ASTNode node : seqNode)
			results.addAll(buildForAstNode(node));
		return results;
	}

	private List<AssignmentIF> buildForStatement(StatementNode stmt) {
		StatementKind kind = stmt.statementKind();

		switch (kind) {
			case ATOMIC :
				return buildForStatement(((AtomicNode) stmt).getBody());
			case CHOOSE :
				return buildForSequence(((ChooseStatementNode) stmt));
			case CIVL_FOR :
				return buildForStatement(((CivlForNode) stmt).getBody());
			case COMPOUND :
				return buildForSequence((CompoundStatementNode) stmt);
			case EXPRESSION :
				return buildForExpression(
						((ExpressionStatementNode) stmt).getExpression());
			case IF : {
				IfNode ifNode = (IfNode) stmt;
				List<AssignmentIF> result = buildForStatement(
						ifNode.getTrueBranch());

				result.addAll(buildForStatement(ifNode.getFalseBranch()));
			}
			case LABELED :
				return buildForStatement(
						((LabeledStatementNode) stmt).getStatement());
			case LOOP :
				return buildForStatement(((LoopNode) stmt).getBody());
			case RUN :
				return buildForStatement(((RunNode) stmt).getStatement());
			case SWITCH : {
				Iterator<LabeledStatementNode> caseIter = ((SwitchNode) stmt)
						.getCases();
				List<AssignmentIF> results = new LinkedList<>();

				while (caseIter.hasNext())
					results.addAll(buildForStatement(caseIter.next()));
				return results;
			}
			case WHEN :
				return buildForStatement(((WhenNode) stmt).getBody());
			case WITH :
				throw new CIVLUnimplementedFeatureException(
						"points to analysis for $with statement");
			default :
				return new LinkedList<>();
		}
	}

	private List<AssignmentIF> buildForExpression(ExpressionNode expression) {
		ExpressionKind kind = expression.expressionKind();
		ExpressionNode rhs = null;
		ExpressionNode lhs = null;
		List<AssignmentIF> ret = new LinkedList<>();

		switch (kind) {
			case OPERATOR :
				return buildForOperator((OperatorNode) expression);
			case STATEMENT_EXPRESSION : {
				return buildForStatement(((StatementExpressionNode) expression)
						.getCompoundStatement());
			}
			default :
				rhs = expression;
		}
		ret.add(new CommonAssignment(lhs, rhs));
		return ret;
	}

	private List<AssignmentIF> buildForOperator(OperatorNode opNode) {
		Operator op = opNode.getOperator();
		ExpressionNode rhs = null;
		ExpressionNode lhs = null;
		List<AssignmentIF> ret = new LinkedList<>();

		switch (op) {
			case ASSIGN :
				rhs = opNode.getArgument(1);
				lhs = opNode.getArgument(0);
				ret.add(new CommonAssignment(lhs, rhs));
				break;
			case COMMA :
				return buildForExpression(opNode.getArgument(1));
			default :
				ret.add(new CommonAssignment(lhs, rhs));
				break;
		}
		return ret;
	}

	private List<AssignmentIF> buildForVariableDeclaration(
			VariableDeclarationNode varDecl) {
		InitializerNode initializer = varDecl.getInitializer();
		List<AssignmentIF> result = new LinkedList<>();

		if (initializer == null)
			return result;
		else {
			result.add(new CommonAssignment(varDecl.getIdentifier(),
					(ExpressionNode) initializer));
			return result;
		}
	}
}
