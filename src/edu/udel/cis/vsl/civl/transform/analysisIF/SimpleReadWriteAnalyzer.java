package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity.EntityKind;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.Scope;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.PairNode;
import edu.udel.cis.vsl.abc.ast.node.IF.compound.CompoundInitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.compound.DesignationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ArrowNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.DotNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.FunctionCallNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.DeclarationListNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ExpressionStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.type.IF.ArrayType;
import edu.udel.cis.vsl.abc.ast.type.IF.PointerType;
import edu.udel.cis.vsl.abc.ast.type.IF.Type;
import edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

/**
 * intra-procedural read-write set analyzer
 * 
 * @author ziqing
 *
 */
public class SimpleReadWriteAnalyzer {
	/**
	 * a reference to a flow-insensitive points-to analyzer
	 */
	private FlowInsensePointsToAnalyzer pointsToAnalyzer;

	/**
	 * the {@link Function} where the current analyzing code is in
	 */
	private Function currentFunction;

	/**
	 * the set of entities declared in the given ASTNode of interface:
	 * {@link #collectRWFromStmtDeclExpr(Function, ASTNode, Set)}
	 */
	private Set<Entity> declaredEntities;

	/**
	 * <p>
	 * This class is a union of different kinds of objects that can be in a
	 * read/write set. A read/write set shall have type of
	 * "collection-of-RWSetElement".
	 * </p>
	 * 
	 * <p>
	 * An RWSetElement can be one of the following kind:
	 * <ul>
	 * <li>Array subscript expression: either {@link #entity} and
	 * {@link #arraySubscript} are significant; or {@link #strOrAlloc} and
	 * {@link #arraySubscript} are significant</li>
	 * <li>Entity: Only {@link #entity} is significant</li>
	 * <li>String literal or allocation: Only {@link #strOrAlloc} is
	 * significant</li>
	 * </ul>
	 * </p>
	 * 
	 * @author ziqing
	 */
	public class RWSetElement {
		/**
		 * significant if 1) this element is a kind of entity or 2) the
		 * {@link #arraySubscript} is significant and its base array is an
		 * {@link Entity}
		 */
		public final Entity entity;

		/**
		 * 
		 * significant iff this element is a kind of array-subscript expression.
		 * Note that for an array-subscript expression,
		 * <ul>
		 * <li>the base array is an {@link Entity}, if the base array expression
		 * has array INITIAL type. In this case, the {@link #entity} field is
		 * significant too which refers to the array variable.</li>
		 * <li>the base array is an allocation, if the base array expression has
		 * pointer type. In this case, the {@link #strOrAlloc} is significant
		 * too which refers to the abstraction of the allocation.</li>
		 * </ul>
		 */
		public final OperatorNode arraySubscript;

		/**
		 * significant if this element is 1) a kind of string literal or
		 * allocation or 2) the base array of {@link #arraySubscript} is an
		 * allocation
		 */
		public final AssignExprIF strOrAlloc;

		private RWSetElement(Entity entity) {
			this.entity = entity;
			this.arraySubscript = null;
			this.strOrAlloc = null;
		}

		private RWSetElement(Entity baseArray, OperatorNode arrSubscript) {
			this.arraySubscript = arrSubscript;
			this.entity = baseArray;
			this.strOrAlloc = null;
		}

		private RWSetElement(AssignExprIF baseArray,
				OperatorNode arrSubscript) {
			this.arraySubscript = arrSubscript;
			this.entity = null;
			this.strOrAlloc = baseArray;
		}

		private RWSetElement(AssignExprIF strOrAlloc) {
			this.strOrAlloc = strOrAlloc;
			this.entity = null;
			this.arraySubscript = null;
		}

		/**
		 * 
		 * @param e
		 * @return True iff the given element refers to the same "Object" as
		 *         this element, i.e. both of them are referring to the same
		 *         {@link Entity} or {@link #strOrAlloc}
		 */
		public boolean sameObject(RWSetElement e) {
			if (e.entity != null)
				return e.entity == entity;
			else
				return e.strOrAlloc.nonEntitySource() == strOrAlloc
						.nonEntitySource();
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			if (entity != null)
				sb.append(entity.getName());
			else if (this.strOrAlloc != null)
				sb.append(strOrAlloc.nonEntitySource().prettyRepresentation()
						.toString());
			if (arraySubscript != null)
				sb.append(": "
						+ arraySubscript.prettyRepresentation().toString());
			return sb.toString();
		}

		@Override
		public int hashCode() {
			if (arraySubscript != null)
				if (entity != null)
					return arraySubscript.hashCode() ^ entity.hashCode();
				else
					return arraySubscript.hashCode() ^ strOrAlloc.hashCode();
			else if (entity != null)
				return entity.hashCode();
			else
				return strOrAlloc.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof RWSetElement))
				return false;

			RWSetElement other = (RWSetElement) obj;

			if (arraySubscript != null)
				if (entity != null)
					return arraySubscript == other.arraySubscript
							&& entity.equals(other.entity);
				else
					return arraySubscript == other.arraySubscript
							&& strOrAlloc == other.strOrAlloc;
			else if (entity != null)
				return entity.equals(other.entity);
			else
				return strOrAlloc == other.strOrAlloc;
		}
	}

	// output type---read/write set pair:
	public class RWSet {

		public Set<RWSetElement> reads;

		public Set<RWSetElement> writes;

		RWSet() {
			this.reads = new HashSet<>();
			this.writes = new HashSet<>();
		}

		private void add(RWSet other) {
			this.reads.addAll(other.reads);
			this.writes.addAll(other.writes);
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			sb.append("reads:\n");
			for (RWSetElement e : reads)
				sb.append(e + ", ");
			sb.append("\nwrites\n");
			for (RWSetElement e : writes)
				sb.append(e + ", ");
			return sb.toString();
		}
	}

	// the points-to sets of a RWSet whose elements are all pointers:
	private RWSet dereferenceRWSet(RWSet ptrRw) throws SimpleFullSetException {
		RWSet result = new RWSet();

		try {
			for (RWSetElement ele : ptrRw.reads)
				result.reads.addAll(dereferenceRWElement(ele));
		} catch (SimpleReadWriteFullSetException e) {
			throw new SimpleFullSetException(false);
		}
		try {
			for (RWSetElement ele : ptrRw.writes)
				result.writes.addAll(dereferenceRWElement(ele));
		} catch (SimpleReadWriteFullSetException e) {
			throw new SimpleFullSetException(true);
		}
		return result;
	}

	// return the points-to sets of a RWSet element which is a pointer:
	private List<RWSetElement> dereferenceRWElement(RWSetElement element)
			throws SimpleReadWriteFullSetException {
		List<AssignExprIF> pts;

		if (element.arraySubscript != null) {
			List<RWSetElement> tmp = new LinkedList<>();

			if (isBaseArrayEntity(element.arraySubscript)) {
				pts = pointsToAnalyzer.mayPointsTo(currentFunction,
						element.entity);
				// add subscript info back:
				for (RWSetElement pt : processPointsToSet(pts))
					tmp.add(new RWSetElement(pt.entity, pt.arraySubscript));
			} else {
				pts = pointsToAnalyzer.mayPointsTo(currentFunction,
						element.strOrAlloc);
				// add subscript info back:
				for (RWSetElement pt : processPointsToSet(pts))
					tmp.add(new RWSetElement(pt.strOrAlloc, pt.arraySubscript));
				return tmp;
			}

		} else if (element.entity != null)
			pts = pointsToAnalyzer.mayPointsTo(currentFunction, element.entity);
		else
			pts = pointsToAnalyzer.mayPointsTo(currentFunction,
					element.strOrAlloc);
		return processPointsToSet(pts);
	}

	// combine the reads/writes set of the base array of a subscript expression
	// with the indices information:
	private RWSet subscriptRWSet(RWSet baseArrayRWSet,
			OperatorNode subscriptNode) {
		RWSet result = new RWSet();

		for (RWSetElement element : baseArrayRWSet.reads)
			result.reads.add(subscriptRWSetWorker(element, subscriptNode));
		for (RWSetElement element : baseArrayRWSet.writes)
			result.writes.add(subscriptRWSetWorker(element, subscriptNode));
		return result;
	}

	// combine subscript indices with the read/write set of a base-array.
	/*
	 * Note the abstraction here: the read/write set of a base-array is an
	 * over-approximate set of the base array object because it may contain
	 * other expressions that are part of the baseArray expression. For such an
	 * over-approximate set, a safe subset of it will include all array type
	 * variables and allocations.
	 */
	private RWSetElement subscriptRWSetWorker(
			RWSetElement baseArrayRWSetElement, OperatorNode subscriptNode) {
		if (baseArrayRWSetElement.arraySubscript != null)
			return null;
		if (baseArrayRWSetElement.entity != null) {
			assert baseArrayRWSetElement.entity
					.getEntityKind() == EntityKind.VARIABLE;
			Variable var = (Variable) baseArrayRWSetElement.entity;

			if (var.getType().isScalar())
				return null;
			return new RWSetElement(var, subscriptNode);
		} else
			return new RWSetElement(baseArrayRWSetElement.strOrAlloc,
					subscriptNode);
	}

	/**
	 * 
	 * @param arraySubscript
	 *            an array subscript expression
	 * @return true if and only if the base array of the given array subscript
	 *         expression refers to an {@link Entity}
	 */
	private boolean isBaseArrayEntity(OperatorNode arraySubscript) {
		ExpressionNode array = arraySubscript.getArgument(0);

		if (array.expressionKind() == ExpressionKind.OPERATOR) {
			arraySubscript = (OperatorNode) array;
			if (arraySubscript.getOperator() == Operator.SUBSCRIPT)
				return isBaseArrayEntity(arraySubscript);
		}
		if (array.getInitialType().kind() == TypeKind.ARRAY)
			return true;
		else
			return false;
	}

	/*
	 * convert the points-to set to a set of RWSetElement. Note that an element
	 * in a points-to set is either an entity or string literal or allocation.
	 */
	private List<RWSetElement> processPointsToSet(List<AssignExprIF> pts)
			throws SimpleReadWriteFullSetException {
		List<RWSetElement> result = new LinkedList<>();

		for (AssignExprIF pt : pts) {
			if (pt.isFull())
				throw new SimpleReadWriteFullSetException();
			else if (pt.source() != null)
				result.add(new RWSetElement(pt.source()));
			else
				result.add(new RWSetElement(pt));
		}
		return result;
	}

	/* ************** full set exception ********/
	// once a full set detected, stop analysis immediately
	private class SimpleReadWriteFullSetException extends Exception {
		// full set exception for use inside this Java file
		private static final long serialVersionUID = 1L;
	}

	public class SimpleFullSetException extends Exception {
		// full set exception for informing clients of this Java file
		private static final long serialVersionUID = 1L;
		// is write set full or read set full ?
		final boolean isWriteSetFull;
		SimpleFullSetException(boolean isWriteSetFull) {
			this.isWriteSetFull = isWriteSetFull;
		}
	}

	/* ************** constructor **************/
	public SimpleReadWriteAnalyzer(
			FlowInsensePointsToAnalyzer pointsToAnalyzer) {
		this.pointsToAnalyzer = pointsToAnalyzer;
	}

	/**
	 * <p>
	 * collect read/write set from a statement, a declaration or an expression.
	 * If the input is not a statement, a declaration or an expression, this
	 * method returns null;
	 * </p>
	 * 
	 * <p>
	 * note that write set is always a subset of the read set.
	 * </p>
	 * 
	 * @param function
	 * @param stmtDeclExpr
	 *            an instance of {@link StatementNode}, Declaration or
	 *            {@link ExpressionNode}, otherwise this method is a no-op
	 * @param locallyDeclaredEntities
	 *            OUTPUT argument: the set of entities declared in the given
	 *            "stmtDeclExpr" node
	 * @return the read/write set of the given node
	 * @throws SimpleFullSetException
	 */
	public RWSet collectRWFromStmtDeclExpr(Function function,
			ASTNode stmtDeclExpr, Set<Entity> locallyDeclaredEntities)
			throws SimpleFullSetException {
		this.currentFunction = function;
		this.declaredEntities = new HashSet<>();

		RWSet result = collectReadsWritesWorker(stmtDeclExpr);

		locallyDeclaredEntities.addAll(declaredEntities);
		return result;
	}

	// worker method of "collectReadsWrites" that uses the global field
	// "currentFunction" instead of keeping passing the function as a parameter:
	private RWSet collectReadsWritesWorker(ASTNode stmtDeclExpr)
			throws SimpleFullSetException {
		if (stmtDeclExpr == null)
			return null;
		if (stmtDeclExpr.nodeKind() == NodeKind.STATEMENT)
			return collectStmtReadsWrites((StatementNode) stmtDeclExpr);
		else if (stmtDeclExpr.nodeKind() == NodeKind.EXPRESSION)
			return collectExprReadsWrites((ExpressionNode) stmtDeclExpr);
		else if (stmtDeclExpr.nodeKind() == NodeKind.DECLARATION_LIST) {
			DeclarationListNode list = (DeclarationListNode) stmtDeclExpr;
			RWSet rwset = new RWSet();

			for (VariableDeclarationNode varDecl : list)
				rwset.add(collectVarDeclReadsWrites(varDecl));
			return rwset;
		} else if (stmtDeclExpr.nodeKind() == NodeKind.VARIABLE_DECLARATION)
			return collectVarDeclReadsWrites(
					(VariableDeclarationNode) stmtDeclExpr);
		else
			return null;
	}

	private RWSet collectVarDeclReadsWrites(VariableDeclarationNode varDecl)
			throws SimpleFullSetException {
		InitializerNode init = varDecl.getInitializer();
		RWSet result = new RWSet();

		if (init == null)
			return result;
		result.add(collectInitializerReadsWrites(init));
		declaredEntities.add(varDecl.getEntity());
		return result;
	}

	// collect read/write set from a statement
	private RWSet collectStmtReadsWrites(StatementNode stmt)
			throws SimpleFullSetException {
		switch (stmt.statementKind()) {
			case EXPRESSION :
				return collectExprReadsWrites(
						((ExpressionStatementNode) stmt).getExpression());
			case ATOMIC :
			case CHOOSE :
			case CIVL_FOR :
			case COMPOUND :
			case IF :
			case JUMP :
			case LABELED :
			case LOOP :
			case NULL :
			case PRAGMA :
			case RUN :
			case SWITCH :
			case UPDATE :
			case WHEN :
			case WITH :
				RWSet result = new RWSet();

				for (ASTNode child : stmt.children()) {
					RWSet tmp = collectReadsWritesWorker(child);

					if (tmp != null)
						result.add(tmp);
				}
				return result;
			case OMP :
			default :
				throw new CIVLInternalException(
						"Unexpected statement kind for read/write analysis "
								+ stmt.statementKind(),
						stmt.getSource());
		}
	}

	// collect reads/writes in initializers:
	private RWSet collectInitializerReadsWrites(InitializerNode initNode)
			throws SimpleFullSetException {
		if (initNode.nodeKind() == NodeKind.EXPRESSION)
			return collectExprReadsWrites((ExpressionNode) initNode);
		else {
			CompoundInitializerNode compInitNode = (CompoundInitializerNode) initNode;
			RWSet result = new RWSet();

			for (PairNode<DesignationNode, InitializerNode> pair : compInitNode)
				result.add(collectInitializerReadsWrites(pair.getRight()));
			return result;
		}
	}

	// collect read/write set from an expression
	private RWSet collectExprReadsWrites(ExpressionNode expr)
			throws SimpleFullSetException {
		ExpressionKind kind = expr.expressionKind();

		switch (kind) {
			case ARROW : {
				RWSet ptrRws = collectExprReadsWrites(
						((ArrowNode) expr).getStructurePointer());

				return dereferenceRWSet(ptrRws);
			}
			case DOT :
				return collectExprReadsWrites(((DotNode) expr).getStructure());
			case FUNCTION_CALL :
				return collectCallReadsWrites((FunctionCallNode) expr);
			case IDENTIFIER_EXPRESSION :
				return collectIdentifierExpression(
						(IdentifierExpressionNode) expr, false);
			case OPERATOR :
				return collectOperatorReadsWrites((OperatorNode) expr);
			default :
				RWSet result = new RWSet();

				for (ASTNode child : expr.children()) {
					RWSet tmp = collectReadsWritesWorker(child);

					if (tmp != null)
						result.add(tmp);
				}
				return result;
		}
	}

	private RWSet collectOperatorReadsWrites(OperatorNode opNode)
			throws SimpleFullSetException {
		ExpressionNode lhs;
		List<ExpressionNode> rhs = new LinkedList<>();

		switch (opNode.getOperator()) {
			case SUBSCRIPT :
				return collectSubscriptNode(opNode);
			case DEREFERENCE : {
				RWSet ptrRws = collectExprReadsWrites(opNode.getArgument(0));

				return dereferenceRWSet(ptrRws);
			}
			// all kinds of assignments:
			case ASSIGN :
				lhs = opNode.getArgument(0);
				rhs.add(opNode.getArgument(1));
				break;
			case SHIFTLEFTEQ :
			case SHIFTRIGHTEQ :
			case TIMESEQ :
			case DIVEQ :
			case MINUSEQ :
			case MODEQ :
			case PLUSEQ :
				lhs = opNode.getArgument(0);
				rhs.add(opNode.getArgument(1));
				rhs.add(lhs);
				break;
			case POSTDECREMENT :
			case POSTINCREMENT :
			case PREDECREMENT :
			case PREINCREMENT :
				lhs = opNode.getArgument(0);
				rhs.add(lhs);
				break;
			default :
				RWSet result = new RWSet();
				int numArgs = opNode.getNumberOfArguments();

				for (int i = 0; i < numArgs; i++) {
					RWSet tmp = collectExprReadsWrites(opNode.getArgument(i));

					if (tmp != null)
						result.add(tmp);
				}
				return result;
		}
		// collect for assignments:
		RWSet set = collectLHSExprReadsWrites(lhs);

		for (ExpressionNode rhsExpr : rhs)
			set.add(collectExprReadsWrites(rhsExpr));
		return set;
	}

	// add to read/write set if this identifier refers to a variable:
	private RWSet collectIdentifierExpression(IdentifierExpressionNode id,
			boolean isWrite) {
		Entity entity = id.getIdentifier().getEntity();
		RWSet result = new RWSet();

		if (entity.getEntityKind() == EntityKind.VARIABLE)
			if (isWrite)
				result.writes.add(new RWSetElement(entity));
			else
				result.reads.add(new RWSetElement(entity));
		return result;
	}

	// collect read/write set for subscript expression
	private RWSet collectSubscriptNode(OperatorNode opNode)
			throws SimpleFullSetException {
		// the invariant of the RWSet.arraySubscript field must be satisfied:
		// if the base array "x" of the subscript expression "x[i][...][j]" is a
		// pointer, the read/write set will includes all "y[i][...][j]" where
		// "y" is an element in the points-to set of "x".
		ExpressionNode baseArray = opNode;

		do {
			baseArray = ((OperatorNode) baseArray).getArgument(0);
			if (baseArray instanceof OperatorNode)
				if (((OperatorNode) baseArray)
						.getOperator() == Operator.SUBSCRIPT)
					continue;
			break;
		} while (true);

		RWSet arrRWSet = collectExprReadsWrites(baseArray);

		if (baseArray.getInitialType().kind() == TypeKind.ARRAY)
			arrRWSet = subscriptRWSet(arrRWSet, opNode);
		else
			// dereference pointers:
			arrRWSet = subscriptRWSet(dereferenceRWSet(arrRWSet), opNode);
		arrRWSet.add(collectExprReadsWrites(opNode.getArgument(1)));
		return arrRWSet;
	}

	/*
	 * collect reads/writes in LHS expression.
	 */
	private RWSet collectLHSExprReadsWrites(ExpressionNode lhs)
			throws SimpleFullSetException {
		ExpressionKind kind = lhs.expressionKind();
		RWSet result;

		switch (kind) {
			case ARROW : {
				result = collectExprReadsWrites(
						((ArrowNode) lhs).getStructurePointer());
				result = dereferenceRWSet(result);
				// select safe over-approx subset to the write set:
				for (RWSetElement e : result.reads)
					if (isStructUnionOrArrayOfStructUnion(e))
						result.writes.add(e);
				return result;
			}
			case DOT : {
				result = collectExprReadsWrites(
						((ArrowNode) lhs).getStructurePointer());
				// select safe over-approx subset to the write set:
				for (RWSetElement e : result.reads)
					if (isStructUnionOrArrayOfStructUnion(e))
						result.writes.add(e);
				return result;
			}
			case IDENTIFIER_EXPRESSION :
				return collectIdentifierExpression(
						(IdentifierExpressionNode) lhs, true);
			case OPERATOR :
				OperatorNode opNode = (OperatorNode) lhs;

				switch (opNode.getOperator()) {
					case DEREFERENCE : {
						result = collectExprReadsWrites(
								((OperatorNode) lhs).getArgument(0));

						RWSet tmp = dereferenceRWSet(result);

						tmp.reads.addAll(tmp.writes);
						result.writes.addAll(tmp.reads);
						return result;
					}
					case SUBSCRIPT : {
						result = collectSubscriptNode(opNode);
						// select safe over-approx subset to the write set:
						for (RWSetElement e : result.reads)
							if (e.arraySubscript != null)
								result.writes.add(e);
						return result;
					}
					default :
				}
			default :
				throw new CIVLInternalException(
						"unexpected left-hand side expression "
								+ lhs.prettyRepresentation(),
						lhs.getSource());
		}
	}

	/**
	 * 
	 * @param element
	 * @return true iff the given RWSetElement is an abstraction of struct/union
	 *         or array-of struct/union type
	 */
	private boolean isStructUnionOrArrayOfStructUnion(RWSetElement element) {
		Type type;

		if (element.arraySubscript != null)
			type = element.arraySubscript.getConvertedType();
		else if (element.entity != null)
			type = ((Variable) element.entity).getType();
		else {
			type = element.strOrAlloc.nonEntitySource().getType();
			assert type.kind() == TypeKind.POINTER;
			type = ((PointerType) type).referencedType();
		}
		while (type.kind() == TypeKind.ARRAY)
			type = ((ArrayType) type).getElementType();
		return type.kind() == TypeKind.STRUCTURE_OR_UNION;
	}

	// call can read all actual parameters
	// call can read/write all visible global variables
	// call can all points-to objects
	private RWSet collectCallReadsWrites(FunctionCallNode call)
			throws SimpleFullSetException {
		RWSet result = new RWSet();

		for (ExpressionNode arg : call.getArguments())
			result.add(allPointsTo(arg));

		ExpressionNode funcExpr = call.getFunction();

		if (funcExpr.expressionKind() != ExpressionKind.IDENTIFIER_EXPRESSION)
			throw new CIVLUnimplementedFeatureException(
					"analyzer function call through function pointer: "
							+ call.prettyRepresentation());

		Function func = (Function) ((IdentifierExpressionNode) funcExpr)
				.getIdentifier().getEntity();

		if (func.getDefinition() != null)
			result.add(visibleVariables(func.getDefinition().getScope()));
		return result;
	}

	// the reads/writes set of "nested" points-to sets:
	private RWSet allPointsTo(ExpressionNode ptr)
			throws SimpleFullSetException {
		RWSet result = collectExprReadsWrites(ptr);
		Type type = ptr.getType();

		while (type.kind() == TypeKind.POINTER) {
			type = ((PointerType) type).referencedType();

			RWSet tmp = dereferenceRWSet(result);

			tmp.writes.addAll(tmp.reads);
			result.add(tmp);
		}
		return result;
	}

	private RWSet visibleVariables(Scope scope) {
		RWSet result = new RWSet();

		for (Variable var : scope.getVariables()) {
			RWSetElement e = new RWSetElement(var);

			result.reads.add(e);
			result.writes.add(e);
		}
		if (scope.getParentScope() != null)
			result.add(visibleVariables(scope.getParentScope()));
		return result;
	}
}
