package edu.udel.cis.vsl.civl.transform.analysis.common;

import static edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind.CONSTANT;
import static edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind.IDENTIFIER_EXPRESSION;
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
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpExecutableNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpNode;
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
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.InsensitiveFlow;
import edu.udel.cis.vsl.civl.transform.analysisIF.InsensitiveFlowFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNodeFactory;

public class CommonInsensitiveFlow implements InsensitiveFlow {

	/**
	 * The generated set of assignments:
	 */
	private List<AssignmentIF> assigns = new LinkedList<>();

	/**
	 * a reference to {@link InsensitiveFlowFactory}
	 */
	private InsensitiveFlowFactory absFactory;

	/**
	 * a reference to {@link InvocationGraphNodeFactory}
	 */
	private InvocationGraphNodeFactory igFactory;

	/**
	 * the {@link InvocationGraphNode} that is associated with function body
	 * represented by this {@link InsensitiveFlow}
	 */
	private InvocationGraphNode igNode;

	/**
	 * the scope of the function body represented by this
	 * {@link InsensitiveFlow}
	 */
	private Scope functionScope;

	CommonInsensitiveFlow(Iterable<BlockItemNode> funcBody, Scope scope,
			InsensitiveFlowFactory factory,
			InvocationGraphNodeFactory igFactory, InvocationGraphNode igNode) {
		this.absFactory = factory;
		this.igFactory = igFactory;
		this.igNode = igNode;
		this.functionScope = scope;
		this.assigns = new LinkedList<>();
		for (BlockItemNode node : funcBody)
			processBlockItemNode(node);

	}

	@Override
	public Iterator<AssignmentIF> iterator() {
		return assigns.iterator();
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
	 * process ordinary declarations
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
				throw new CIVLUnimplementedFeatureException(
						"represent " + node.prettyRepresentation() + " of kind "
								+ kind + " in an insensitive flow");
		}
	}

	/**
	 * <p>
	 * convert <code>T var = e;</codde> to {@link AssignmentIF}s
	 * </p>
	 * 
	 * <p>
	 * no-op if this declaration has no initializer
	 * </p>
	 * 
	 * @param varDecl
	 */
	private void processVarDecNode(VariableDeclarationNode varDecl) {
		InitializerNode init = varDecl.getInitializer();
		TempExprAbstraction abs;

		if (init == null)
			return;
		if (init.nodeKind() == NodeKind.EXPRESSION) {
			abs = processRHSExpressionNode((ExpressionNode) init);
			if (abs == null) // irrelevant
				return;
		} else
			throw new CIVLUnimplementedFeatureException(
					"represent compound initializer "
							+ init.prettyRepresentation()
							+ " in an insensitive flow");

		boolean deref = abs.op == Operator.DEREFERENCE,
				addrof = abs.op == Operator.ADDRESSOF;

		AssignExprIF lhs = absFactory.assignExpr(varDecl.getEntity());
		AssignExprIF rhs = abs.assignExpr;

		assigns.add(absFactory.assignment(lhs, false, rhs, deref, addrof));
	}

	/**
	 * process a statement
	 * 
	 * @param stmt
	 */
	private void processStatementNode(StatementNode stmt) {
		if (stmt == null)
			return;

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
			case OMP :
				processOmpNode((OmpNode) stmt);
				break;
			case UPDATE :
			case WITH :
			case PRAGMA :
				throw new CIVLUnimplementedFeatureException(
						"represent statement " + stmt.prettyRepresentation()
								+ " of kind " + kind
								+ " in an insensitive flow");
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

		if (tmpAbs == null)
			return;

		AssignExprIF abs = tmpAbs.assignExpr;

		if (tmpAbs.op != null) {
			AssignExprIF tmp = absFactory.assignExpr(retExpr);

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
				// func scope -> param scope -> parent (func) scope
				Scope parent = functionScope.getParentScope().getParentScope();
				Entity sameNameEntity = parent.getLexicalOrdinaryEntity(false,
						entity.getName());
				AssignExprIF exprAbs = absFactory.assignExpr(entity);

				if (entity == sameNameEntity) {
					// If this entity is visible from lexical parent scope of
					// the function, this entity is not declared locally:
					this.igNode.addGlobalAccess(exprAbs);
				}
				// otherwise, this entity is locally declared. We dont need to
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
						"represent expression " + expr.prettyRepresentation()
								+ " of kind " + kind
								+ " in an insensitive flow");
			default :
				return null;
		}
	}

	/**
	 * 
	 * @param expr
	 * @return the processed {@link CastNode#getArgument()} expression; or the
	 *         representation of FULL, if this is a cast from non-pointer type
	 *         to pointer type
	 */
	private TempExprAbstraction processCast(CastNode expr) {
		ExpressionNode arg = ((CastNode) expr).getArgument();

		if (expr.getType().kind() == TypeKind.POINTER)
			if (arg.getType().kind() != TypeKind.POINTER) {
				AssignExprIF auxLhs = absFactory.assignExpr(expr);
				AssignmentIF auxAssign = processAuxAssignment(auxLhs, null,
						null);

				assigns.add(auxAssign);
				return new TempExprAbstraction(auxAssign.lhs(),
						(Operator) null);
			}
		return processRHSExpressionNode(arg);
	}

	/**
	 * If the given constant is a String, return the representation for a
	 * pointer to the string
	 * 
	 * @param constant
	 * @return an {@link TempExprAbstraction} if this constant is a STRING (a
	 *         pointer to String), or null since other constants have no impact
	 *         on points-to analysis
	 */
	private TempExprAbstraction processConstant(ConstantNode constant) {
		if (constant.constantKind() != ConstantKind.STRING)
			return null;
		AssignExprIF abs = absFactory.assignExpr(constant);

		return new TempExprAbstraction(abs, Operator.ADDRESSOF);
	}

	/**
	 * If the given call is an allocation, return a representation for the
	 * allocated object
	 * 
	 * @param call
	 *            a function call that is an allocation if it is a call to
	 *            $malloc
	 * @return an {@link TempExprAbstraction} if the given call is not called
	 *         via function pointer and is an allocation; null otherwise
	 */
	private TempExprAbstraction processIfAlloc(FunctionCallNode call) {
		ExpressionNode func = call.getFunction();

		if (func.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION)
			return null;

		IdentifierNode funcID = ((IdentifierExpressionNode) func)
				.getIdentifier();

		if (funcID.name().equals("$malloc"))
			return new TempExprAbstraction(absFactory.assignExpr(call),
					Operator.ADDRESSOF);
		return null;
	}

	/**
	 * <p>
	 * process a function call via a function identifier (instead of a function
	 * pointer)
	 * </p>
	 * 
	 * @param funcCall
	 *            a function call that 1) is NOT an allocation; 2) is made
	 *            through function identifier
	 * @return a {@link TempExprAbstraction}, representing the call expression,
	 *         if the return type of this function is not void.
	 */
	private TempExprAbstraction processFunctionCall(FunctionCallNode funcCall) {
		ExpressionNode funcNode = funcCall.getFunction();
		boolean isUnimplCase = funcNode
				.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION;
		Function functionEntity = null;

		// currently cannot deal with function pointers:
		if (!isUnimplCase) {
			Entity entity = ((IdentifierExpressionNode) funcNode)
					.getIdentifier().getEntity();

			if (entity.getEntityKind() != EntityKind.FUNCTION)
				isUnimplCase = true;
			else
				functionEntity = (Function) entity;
		}
		assert functionEntity != null;
		if (isUnimplCase || functionEntity.getDefinition() == null)
			throw new CIVLUnimplementedFeatureException(
					"Unsupported function call expression "
							+ funcCall.prettyRepresentation()
							+ " for points-to analysis.");

		AssignExprIF callExprAbs = absFactory.assignExpr(funcCall);

		// to attach a new IGNode as a child of this.igNode
		//// get actual parameters:
		SequenceNode<ExpressionNode> actualArgSeq = funcCall.getArguments();
		int numArgs = actualArgSeq.numChildren();
		AssignExprIF[] actualArgAbs = new AssignExprIF[numArgs];

		for (int i = 0; i < numArgs; i++) {
			ExpressionNode actualArg = funcCall.getArgument(i);
			TempExprAbstraction abs = processRHSExpressionNode(actualArg);
			AssignExprIF result = abs.assignExpr;

			if (abs.op != null) {
				// use auxiliary variables if the actual parameter involves
				// dereference or address-of operations:
				AssignExprIF auxLhs = absFactory.assignExpr(actualArg);

				result = auxLhs;
				assigns.add(
						processAuxAssignment(auxLhs, abs.assignExpr, abs.op));
			}
			actualArgAbs[i] = result;
		}
		igFactory.newNode(functionEntity, igNode, callExprAbs, actualArgAbs);
		return new TempExprAbstraction(callExprAbs, null);
	}

	/**
	 * 
	 * @param opNode
	 *            a node represents operation
	 * 
	 * @return an {@link TempExprAbstraction} of this operation or null if this
	 *         operation is irrelevant to points-to analysis
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
				processRHSExpressionNode(opNode.getArgument(0));
				return processRHSExpressionNode(opNode.getArgument(1));
			case DEREFERENCE : {
				TempExprAbstraction exprAbs = processRHSExpressionNode(
						opNode.getArgument(0));

				assert exprAbs != null;
				exprAbs.op = DEREFERENCE;
				return exprAbs;
			}
			case SUBSCRIPT : {
				ExpressionNode arrayNode = opNode.getArgument(0);
				TempExprAbstraction tmpAbs = processRHSExpressionNode(
						arrayNode);

				// a[e] is represented by a, if a is an array variable
				// a[e] is represented by *a, if a is a pointer
				if (arrayNode.getInitialType().isScalar()) {
					if (tmpAbs.op != null) {
						AssignExprIF auxLhs = absFactory.assignExpr(arrayNode);

						assigns.add(processAuxAssignment(auxLhs,
								tmpAbs.assignExpr, tmpAbs.op));
						tmpAbs = new TempExprAbstraction(auxLhs,
								Operator.DEREFERENCE);
					} else
						tmpAbs.op = Operator.DEREFERENCE;
				}
				return tmpAbs;
			}
			default :
				// general operation:
				int numArgs = opNode.getNumberOfArguments();
				AssignExprIF auxLhs = absFactory.assignExpr(opNode);

				for (int i = 0; i < numArgs; i++) {
					ExpressionNode arg = opNode.getArgument(i);

					if (isIrrelaventOperand(arg))
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
	 * process OpenMP node: ignore OpenMP pragma and process statement as usual
	 * 
	 * @param stmt
	 */
	private void processOmpNode(OmpNode stmt) {
		switch (stmt.ompNodeKind()) {
			case EXECUTABLE : {
				OmpExecutableNode execNode = (OmpExecutableNode) stmt;

				processStatementNode(execNode.statementNode());
				break;
			}
			default :
				throw new CIVLUnimplementedFeatureException(
						"represent OpenMP node " + stmt.prettyRepresentation()
								+ " in an insensitive flow");
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

		// special case for array:
		// T *p; T a[n];
		// "p = a;" is suppose to mean "p = &a[0]", i.e., p points to a
		if (rhs.getInitialType().kind() == TypeKind.ARRAY)
			if (lhs.getType().kind() == TypeKind.POINTER) {
				assert !rhsAddrof;
				rhsAddrof = true;
			}

		// if it's the case of "*e = *r", convert it to
		// "v = *r"
		// "*e = v"
		if (lhsDeref == true && (rhsDeref == true || rhsAddrof == true)) {
			AssignExprIF aux = absFactory.assignExpr(rhs);

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
	 * given "lhs", "rhs" and the operator (i.e. dereference or address-of) of
	 * "rhs", return an {@link AssignmentIF} representing
	 * <code>lhs = op rhs</code>
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
	 * A temporary abstract representation of an expression at either side of an
	 * {@link AssignmentIF}
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
		 * dereference, or address-of, or null
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
	 * @return true iff the given expression is NOT a pointer or an array and is
	 *         a trivial expression (i.e. id or constant)
	 */
	private boolean isIrrelaventOperand(ExpressionNode expr) {
		TypeKind kind = expr.getType().kind();
		ExpressionKind exprKind = expr.expressionKind();
		boolean nonStringConst = exprKind == CONSTANT
				&& ((ConstantNode) expr).constantKind() != ConstantKind.STRING;

		return kind != POINTER && kind != ARRAY
				&& (exprKind == IDENTIFIER_EXPRESSION || nonStringConst);
	}

	/* *************** Printing ******************/
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (AssignmentIF assign : assigns)
			sb.append(assign.toString() + "\n");
		return sb.toString();
	}
}
