package edu.udel.cis.vsl.civl.transform.analysis.common;

import static edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator.ADDRESSOF;
import static edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator.DEREFERENCE;
import static edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind.ARRAY;
import static edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind.POINTER;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity.EntityKind;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.Scope;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.IdentifierNode;
import edu.udel.cis.vsl.abc.ast.node.IF.SequenceNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.OrdinaryDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.OrdinaryDeclarationNode.OrdinaryDeclarationKind;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ArrayLambdaNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ArrowNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.CastNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ConstantNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ConstantNode.ConstantKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.DotNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.FunctionCallNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.StatementExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.AtomicNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode.BlockItemKind;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CivlForNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CompoundStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ExpressionStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.IfNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.JumpNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.JumpNode.JumpKind;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.LabeledStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.LoopNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ReturnNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.RunNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode.StatementKind;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.SwitchNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.WhenNode;
import edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;
import edu.udel.cis.vsl.civl.util.IF.Pair;

public class CommonAssignmentSequence implements AssignmentSequence {

	/**
	 * The generated set of assignments:
	 */
	private List<AssignmentIF> assigns = new LinkedList<>();

	/**
	 * a reference to {@link AssignmentFactory}
	 */
	private AssignmentFactory absFactory;

	/**
	 * a reference to {@link InvocationGraphFactory}
	 */
	private InvocationGraphFactory igFactory;

	/**
	 * The {@link InvocationGraphNode} associated with function whose body
	 * contains the intra-procedural code fragment represented by this
	 * {@link AssignmentSequence}
	 */
	private InvocationGraphNode igNode;

	/**
	 * The scope of the associated function
	 */
	private Scope functionScope;

	CommonAssignmentSequence(Iterable<BlockItemNode> funcBody, Scope scope,
			AssignmentFactory factory, InvocationGraphFactory igFactory,
			InvocationGraphNode igNode) {
		this.absFactory = factory;
		this.igFactory = igFactory;
		this.igNode = igNode;
		this.functionScope = scope;
		assigns = new LinkedList<>();
		for (BlockItemNode node : funcBody)
			processBlockItemNode(node);

	}

	@Override
	public InvocationGraphNode getIGNode() {
		return this.igNode;
	}

	@Override
	public Iterator<AssignmentIF> iterator() {
		return assigns.iterator();
	}

	@Override
	public Pair<AssignExprIF, Boolean> getAbstraction(ExpressionNode expr) {
		TempExprAbstraction abs = processRHSExpressionNode(expr);

		if (abs != null)
			return new Pair<>(abs.assignExpr, abs.op == Operator.DEREFERENCE);
		else
			return new Pair<>(absFactory.assignmentExpression(expr), false);
	}

	/* ************ methods for build ************ */
	/**
	 * process a {@link BlockItemNode}
	 * 
	 * @param node
	 */
	private void processBlockItemNode(BlockItemNode node) {
		BlockItemKind kind = node.blockItemKind();

		switch (kind) {
			case ORDINARY_DECLARATION :
				processOrdDecNode((OrdinaryDeclarationNode) node);
				break;
			case STATEMENT :
				processStatementNode((StatementNode) node);
				break;
			case STRUCT_OR_UNION :
			case TYPEDEF :
				// skip
				break;
			case OMP_DECLARATIVE :
			case PRAGMA :
			case STATIC_ASSERTION :
			default :
				throw new CIVLUnimplementedFeatureException("convert "
						+ node.prettyRepresentation() + " of kind " + kind
						+ " to a set of assignments for points-to analysis");
		}
	}

	/**
	 * process declarations
	 * 
	 * @param node
	 */
	private void processOrdDecNode(OrdinaryDeclarationNode node) {
		OrdinaryDeclarationKind kind = node.ordinaryDeclarationKind();

		switch (kind) {
			case VARIABLE_DECLARATION :
				processVarDecNode((VariableDeclarationNode) node);
				break;
			case ABSTRACT_FUNCTION_DEFINITION :
			case FUNCTION_DECLARATION :
			case FUNCTION_DEFINITION :
			default :
				throw new CIVLUnimplementedFeatureException("convert "
						+ node.prettyRepresentation() + " of kind " + kind
						+ " to a set of assignments for points-to analysis");
		}
	}

	/**
	 * <p>
	 * <code>T var = e;</code> becomes
	 * <code>var(IdentifierNode) = processExpression(e)</code>
	 * </p>
	 * 
	 * <p>
	 * no-op if this declaration has no initializer
	 * </p>
	 * 
	 * @param varDecl
	 */
	private void processVarDecNode(VariableDeclarationNode varDecl) {
		InitializerNode iz = varDecl.getInitializer();
		TempExprAbstraction abs;

		if (iz == null)
			return;
		if (iz.nodeKind() == NodeKind.EXPRESSION) {
			abs = processRHSExpressionNode((ExpressionNode) iz);
			if (abs == null) // not a pointer
				return;
		} else
			throw new CIVLUnimplementedFeatureException(
					"convert compound initializer " + iz.prettyRepresentation()
							+ " to a set of assignments for points-to analysis");

		boolean deref = abs.op == Operator.DEREFERENCE,
				addrof = abs.op == Operator.ADDRESSOF;

		AssignExprIF lhs = absFactory.assignmentExpression(varDecl.getEntity());
		AssignExprIF rhs = abs.assignExpr;

		assigns.add(absFactory.assignment(lhs, false, rhs, deref, addrof));
	}

	/**
	 * process a statement
	 * 
	 * @param stmt
	 */
	private void processStatementNode(StatementNode stmt) {
		StatementKind kind = stmt.statementKind();

		switch (kind) {
			case ATOMIC :
				processStatementNode(((AtomicNode) stmt).getBody());
				break;
			case CHOOSE : {
				@SuppressWarnings("unchecked")
				SequenceNode<BlockItemNode> seq = (SequenceNode<BlockItemNode>) stmt;

				processBlockItemSequence(seq);
			}
				break;
			case CIVL_FOR : {
				CivlForNode civlForNode = (CivlForNode) stmt;

				processStatementNode(civlForNode.getBody());
				break;
			}
			case COMPOUND :
				processBlockItemSequence((CompoundStatementNode) stmt);
				break;
			case EXPRESSION :
				processRHSExpressionNode(
						((ExpressionStatementNode) stmt).getExpression());
				break;
			case IF :
				processStatementNode(((IfNode) stmt).getTrueBranch());
				processStatementNode(((IfNode) stmt).getFalseBranch());
				processRHSExpressionNode(((IfNode) stmt).getCondition());
				break;
			case LABELED :
				processStatementNode(
						((LabeledStatementNode) stmt).getStatement());
				break;
			case LOOP :
				processStatementNode(((LoopNode) stmt).getBody());
				processRHSExpressionNode(((LoopNode) stmt).getCondition());
				break;
			case RUN :
				processStatementNode(((RunNode) stmt).getStatement());
				break;
			case SWITCH :
				processStatementNode(((SwitchNode) stmt).getBody());
				break;
			case WHEN :
				processStatementNode(((WhenNode) stmt).getBody());
				break;
			case JUMP :
				processJumpNode((JumpNode) stmt);
				break;
			case UPDATE :
			case WITH :
			case OMP :
			case PRAGMA :
				throw new CIVLUnimplementedFeatureException("convert statement "
						+ stmt.prettyRepresentation()
						+ " to a set of assignments for points-to analysis");
			default :
				break;
		}
	}

	/**
	 * process a sequence of block item nodes
	 * 
	 * @param bSeq
	 */
	private void processBlockItemSequence(
			SequenceNode<? extends BlockItemNode> bSeq) {
		for (BlockItemNode b : bSeq)
			processBlockItemNode(b);
	}

	/**
	 * process return node. Associates the returned expression with the
	 * {@link #igNode}
	 * 
	 * @param jumpNode
	 */
	private void processJumpNode(JumpNode jumpNode) {
		if (jumpNode.getKind() != JumpKind.RETURN)
			return;

		ExpressionNode retExpr = ((ReturnNode) jumpNode).getExpression();
		TempExprAbstraction tmpAbs = processRHSExpressionNode(retExpr);
		AssignExprIF abs = tmpAbs.assignExpr;

		if (tmpAbs.op != null) {
			AssignExprIF tmp = absFactory.assignmentExpression(retExpr);

			assigns.add(processAuxAssignment(tmp, abs, tmpAbs.op));
			abs = tmp;
		}
		this.igNode.addReturnValue(abs);
	}

	/**
	 * process an expression
	 * 
	 * @param expr
	 * @return an {@link TempExprAbstraction} of the given expression or null if
	 *         the given expression has no impact on points-to analysis
	 */
	private TempExprAbstraction processRHSExpressionNode(ExpressionNode expr) {
		ExpressionKind kind = expr.expressionKind();

		switch (kind) {
			case ARRAY_LAMBDA : {
				TempExprAbstraction result = processRHSExpressionNode(
						((ArrayLambdaNode) expr).expression());
				result.op = null;
				return result;
			}
			case ARROW : {
				// e->id == *e
				TempExprAbstraction result = processRHSExpressionNode(
						((ArrowNode) expr).getStructurePointer());
				result.op = Operator.DEREFERENCE;
				return result;
			}
			case CAST :
				return processCast((CastNode) expr);
			case CONSTANT :
				return processConstant((ConstantNode) expr);
			case DOT :
				// e.id == e
				return processRHSExpressionNode(
						((DotNode) expr).getStructure());
			case IDENTIFIER_EXPRESSION : {
				Entity entity = ((IdentifierExpressionNode) expr)
						.getIdentifier().getEntity();
				Scope parent = functionScope.getParentScope();
				Entity sameNameEntity = parent.getLexicalOrdinaryEntity(false,
						entity.getName());
				AssignExprIF exprAbs = absFactory.assignmentExpression(entity);

				if (entity == sameNameEntity) {
					// If this entity is visible from lexical parent scope of
					// the function, this entity is not declared locally:
					this.igNode.addGlobalAccess(exprAbs);
				}
				// otherwise, this entity is not visible from lexical parent
				// scope, this entity is locally declared. We dont need to
				// specially save local entities in igNode.
				return new TempExprAbstraction(exprAbs, null);
			}
			case OPERATOR :
				return processRHSOperator((OperatorNode) expr);
			case STATEMENT_EXPRESSION :
				processStatementNode(((StatementExpressionNode) expr)
						.getCompoundStatement());
				return null;
			case FUNCTION_CALL :
				TempExprAbstraction alloc = processIfAlloc(
						(FunctionCallNode) expr);
				if (alloc != null)
					return alloc;
				return processFunctionCall((FunctionCallNode) expr);
			case REMOTE_REFERENCE :
			case UPDATE :
			case VALUE_AT :
			case QUANTIFIED_EXPRESSION :
			case LAMBDA :
			case SPAWN :
			case DERIVATIVE_EXPRESSION :
			case COMPOUND_LITERAL :
				throw new CIVLUnimplementedFeatureException(
						"process expression of kind " + kind
								+ " for creating assignments for points-to analysis");
			default :
				return null;
		}
	}

	/**
	 * 
	 * @param expr
	 * @return an {@link TempExprAbstraction} of this cast expression if this
	 *         expression is pointer to array; otherwise, the result of
	 *         processing {@link CastNode#getArgument()};
	 */
	private TempExprAbstraction processCast(CastNode expr) {
		ExpressionNode arg = ((CastNode) expr).getArgument();

		if (expr.getType().kind() == TypeKind.POINTER)
			if (arg.getType().kind() != TypeKind.POINTER) {
				AssignExprIF auxLhs = absFactory.assignmentExpression(expr);
				AssignmentIF auxAssign = processAuxAssignment(auxLhs, null,
						null);

				assigns.add(auxAssign);
				return new TempExprAbstraction(auxAssign.lhs(),
						(Operator) null);
			}
		return processRHSExpressionNode(arg);
	}

	/**
	 * If the given constant is a String, return the abstraction "&String"
	 * 
	 * @param constant
	 * @return an {@link TempExprAbstraction} if this constant is a STRING (a
	 *         pointer to String), or null since other constants have no impact
	 *         on points-to analysis
	 */
	private TempExprAbstraction processConstant(ConstantNode constant) {
		if (constant.constantKind() != ConstantKind.STRING)
			return null;
		AssignExprIF abs = absFactory.assignmentExpression(constant);

		return new TempExprAbstraction(abs, Operator.ADDRESSOF);
	}

	/**
	 * If the given call is an allocation, return the abstraction "&allocation"
	 * 
	 * @param call
	 * @return
	 */
	private TempExprAbstraction processIfAlloc(FunctionCallNode call) {
		ExpressionNode func = call.getFunction();

		if (func.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION)
			return null;

		IdentifierNode funcID = ((IdentifierExpressionNode) func)
				.getIdentifier();

		if (funcID.name().equals("$malloc"))
			return new TempExprAbstraction(
					absFactory.assignmentExpression(call), Operator.ADDRESSOF);
		return null;
	}

	/* *** process function call *** */
	private TempExprAbstraction processFunctionCall(FunctionCallNode funcCall) {
		ExpressionNode funcNode = funcCall.getFunction();
		boolean unimplementedCase = funcNode
				.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION;
		Function functionEntity = null;

		// currently cannot deal with function pointers:
		if (!unimplementedCase) {
			Entity entity = ((IdentifierExpressionNode) funcNode)
					.getIdentifier().getEntity();

			if (entity.getEntityKind() != EntityKind.FUNCTION)
				unimplementedCase = true;
			else
				functionEntity = (Function) entity;
		}
		if (unimplementedCase)
			throw new CIVLUnimplementedFeatureException(
					"Unsupported function call expression "
							+ funcCall.prettyRepresentation()
							+ " for points-to analysis.");
		assert functionEntity != null;

		AssignExprIF callExprAbs = absFactory.assignmentExpression(funcCall);

		// To attach a new IGNode as a child of the node associated with this
		// AssignmentSequence:
		// get actual parameters:
		SequenceNode<ExpressionNode> actualArgSeq = funcCall.getArguments();
		int numArgs = actualArgSeq.numChildren();
		AssignExprIF[] actualArgAbs = new AssignExprIF[numArgs];

		for (int i = 0; i < numArgs; i++) {
			ExpressionNode actualArg = funcCall.getArgument(i);
			TempExprAbstraction abs = processRHSExpressionNode(actualArg);
			AssignExprIF result = abs.assignExpr;

			if (abs.op != null) {
				// make sure actual parameters are not dereference or address-of
				// operations:
				AssignExprIF auxLhs = absFactory
						.assignmentExpression(actualArg);

				result = auxLhs;
				assigns.add(
						processAuxAssignment(auxLhs, abs.assignExpr, abs.op));
			}
			actualArgAbs[i] = result;
		}

		igFactory.newNode(functionEntity, igNode, callExprAbs, actualArgAbs);
		return new TempExprAbstraction(callExprAbs, null);
	}

	/* *** end-of process function call *** */
	/**
	 * 
	 * @param opNode
	 *            a node represents operation
	 * 
	 * @return an {@link TempExprAbstraction} of this operation or null if this
	 *         operation has NO impact to points-to analysis
	 */
	private TempExprAbstraction processRHSOperator(OperatorNode opNode) {
		Operator op = opNode.getOperator();

		switch (op) {
			case ADDRESSOF : {
				TempExprAbstraction exprAbs = processRHSExpressionNode(
						opNode.getArgument(0));

				assert exprAbs != null;
				exprAbs.op = ADDRESSOF;
				return exprAbs;
			}
			case ASSIGN :
				processAssignment(opNode);
				return null;
			case COMMA :
				return processRHSExpressionNode(opNode.getArgument(1));
			case DEREFERENCE : {
				TempExprAbstraction exprAbs = processRHSExpressionNode(
						opNode.getArgument(0));

				assert exprAbs != null;
				exprAbs.op = DEREFERENCE;
				return exprAbs;
			}
			case SUBSCRIPT :
				return processRHSExpressionNode(opNode.getArgument(0));
			default :
				// general operation:
				int numArgs = opNode.getNumberOfArguments();
				AssignExprIF auxLhs = absFactory.assignmentExpression(opNode);

				for (int i = 0; i < numArgs; i++) {
					ExpressionNode arg = opNode.getArgument(i);

					if (!isPointerOrArrayType(arg))
						continue;

					TempExprAbstraction exprAbs = processRHSExpressionNode(arg);
					AssignmentIF assignIF;

					if (exprAbs == null)
						continue;
					assignIF = processAuxAssignment(auxLhs, exprAbs.assignExpr,
							exprAbs.op);
					assigns.add(assignIF);
				}
				return new TempExprAbstraction(auxLhs, null);
		}
	}

	/**
	 * Make the ASSIGN operation an {@link AssignmentIF} in the final result
	 * 
	 * @param assignNode
	 */
	private void processAssignment(OperatorNode assignNode) {
		ExpressionNode lhs = assignNode.getArgument(0);
		ExpressionNode rhs = assignNode.getArgument(1);
		TempExprAbstraction rhsAbs = processRHSExpressionNode(rhs);
		TempExprAbstraction lhsAbs = processRHSExpressionNode(lhs);

		if (rhsAbs == null || lhsAbs == null)
			return; // not pointer

		boolean lhsDeref = lhsAbs.op == Operator.DEREFERENCE;
		boolean rhsDeref = rhsAbs.op == Operator.DEREFERENCE;
		boolean rhsAddrof = rhsAbs.op == Operator.ADDRESSOF;
		AssignmentIF assignment;

		// if it's the case of "*e = *r", convert it to
		// "v = *r"
		// "*e = v"
		if (lhsDeref == true && (rhsDeref == true || rhsAddrof == true)) {
			AssignExprIF aux = absFactory.assignmentExpression(rhs);

			assignment = absFactory.assignment(aux, false, rhsAbs.assignExpr,
					rhsDeref, rhsAddrof);
			assigns.add(assignment);
			assignment = absFactory.assignment(lhsAbs.assignExpr, lhsDeref, aux,
					false, false);
		} else
			assignment = absFactory.assignment(lhsAbs.assignExpr, lhsDeref,
					rhsAbs.assignExpr, rhsDeref, rhsAddrof);
		assigns.add(assignment);
	}

	/**
	 * <p>
	 * given "lhs", "rhs" and operator of "rhs", return an {@link AssignmentIF}
	 * representing <code>lhs = op rhs</code>
	 * </p>
	 * 
	 * <p>
	 * if "rhs == null", create "lhs = FULL"; if "op == null", no operator.
	 * </p>
	 * 
	 * @param var
	 * @return assignment
	 */
	private AssignmentIF processAuxAssignment(AssignExprIF auxLhs,
			AssignExprIF rhs, Operator op) {
		boolean deref = op == Operator.DEREFERENCE;
		boolean addrf = op == Operator.ADDRESSOF;
		if (rhs == null) {
			assert !addrf && !deref;
			rhs = absFactory.full();
			addrf = true;
		}
		return absFactory.assignment(auxLhs, false, rhs, deref, addrf);
	}

	/* *************** Util classes & methods ******************/
	/**
	 * A temporary representation, for points-to analysis, of the abstraction of
	 * one side of an assignment
	 * 
	 * @author ziqing
	 *
	 */
	private class TempExprAbstraction {
		/**
		 * the abstract representation:
		 */
		final AssignExprIF assignExpr;
		/**
		 * dereference, or address-of, or null ?
		 */
		Operator op;

		TempExprAbstraction(AssignExprIF expr, Operator op) {
			assert expr != null;
			this.assignExpr = expr;
			this.op = op;
		}
	}

	/**
	 * 
	 * @param expr
	 * @return true iff the given expression has pointer or array type
	 */
	private boolean isPointerOrArrayType(ExpressionNode expr) {
		TypeKind kind = expr.getType().kind();

		return kind == POINTER || kind == ARRAY;
	}

	/* *************** Printing ******************/
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (AssignmentIF assign : assigns)
			sb.append(assign.toString() + "\n");
		return sb.toString();
	}
}
