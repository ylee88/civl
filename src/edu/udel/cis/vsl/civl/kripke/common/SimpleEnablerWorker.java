package edu.udel.cis.vsl.civl.kripke.common;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import edu.udel.cis.vsl.civl.config.IF.CIVLConfiguration;
import edu.udel.cis.vsl.civl.dynamic.IF.SymbolicUtility;
import edu.udel.cis.vsl.civl.model.IF.CIVLFunction;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLSource;
import edu.udel.cis.vsl.civl.model.IF.CIVLTypeFactory;
import edu.udel.cis.vsl.civl.model.IF.Scope;
import edu.udel.cis.vsl.civl.model.IF.contract.DependsEvent;
import edu.udel.cis.vsl.civl.model.IF.contract.FunctionBehavior;
import edu.udel.cis.vsl.civl.model.IF.contract.FunctionContract;
import edu.udel.cis.vsl.civl.model.IF.contract.MemoryEvent;
import edu.udel.cis.vsl.civl.model.IF.contract.NamedFunctionBehavior;
import edu.udel.cis.vsl.civl.model.IF.expression.AbstractFunctionCallExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.AddressOfExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ArrayLambdaExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ArrayLiteralExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.BinaryExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.CastExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ConditionalExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DereferenceExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DerivativeCallExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DifferentiableExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DomainGuardExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DotExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DynamicTypeOfExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.Expression;
import edu.udel.cis.vsl.civl.model.IF.expression.ExtendedQuantifiedExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.FunctionCallExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.FunctionGuardExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.LHSExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.LambdaExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.MPIContractExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.MemoryUnitExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.QuantifiedExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.RecDomainLiteralExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.RegularRangeExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ScopeofExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.SizeofExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.SizeofTypeExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.SubscriptExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.SystemGuardExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.UnaryExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ValueAtExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.VariableExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.reference.ArraySliceReference;
import edu.udel.cis.vsl.civl.model.IF.expression.reference.MemoryUnitReference;
import edu.udel.cis.vsl.civl.model.IF.location.Location;
import edu.udel.cis.vsl.civl.model.IF.statement.AssignStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.AtomicLockAssignStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.CallOrSpawnStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.CivlParForSpawnStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.DomainIteratorStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.MallocStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.ParallelAssignStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.ReturnStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.Statement;
import edu.udel.cis.vsl.civl.model.IF.statement.Statement.StatementKind;
import edu.udel.cis.vsl.civl.model.IF.statement.UpdateStatement;
import edu.udel.cis.vsl.civl.model.IF.statement.WithStatement;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLArrayType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLCompleteArrayType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLFunctionType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLPointerType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLSetType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLStructOrUnionType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLType;
import edu.udel.cis.vsl.civl.model.IF.type.StructOrUnionField;
import edu.udel.cis.vsl.civl.model.IF.variable.Variable;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluation;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluator;
import edu.udel.cis.vsl.civl.semantics.IF.Transition;
import edu.udel.cis.vsl.civl.state.IF.DynamicScope;
import edu.udel.cis.vsl.civl.state.IF.ProcessState;
import edu.udel.cis.vsl.civl.state.IF.StackEntry;
import edu.udel.cis.vsl.civl.state.IF.State;
import edu.udel.cis.vsl.civl.state.IF.StateFactory;
import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.civl.util.IF.Pair;
import edu.udel.cis.vsl.civl.util.IF.SeqSet;
import edu.udel.cis.vsl.civl.util.IF.Triple;
import edu.udel.cis.vsl.sarl.IF.Reasoner;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.ValidityResult.ResultType;
import edu.udel.cis.vsl.sarl.IF.expr.ArrayElementReference;
import edu.udel.cis.vsl.sarl.IF.expr.BooleanExpression;
import edu.udel.cis.vsl.sarl.IF.expr.NTReferenceExpression;
import edu.udel.cis.vsl.sarl.IF.expr.NumericExpression;
import edu.udel.cis.vsl.sarl.IF.expr.ReferenceExpression;
import edu.udel.cis.vsl.sarl.IF.expr.ReferenceExpression.ReferenceKind;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import edu.udel.cis.vsl.sarl.IF.expr.TupleComponentReference;
import edu.udel.cis.vsl.sarl.IF.number.IntegerNumber;
import edu.udel.cis.vsl.sarl.IF.object.SymbolicObject;
import edu.udel.cis.vsl.sarl.IF.object.SymbolicSequence;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicArrayType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicCompleteArrayType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicFunctionType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicMapType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicSetType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicTupleType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicTypeSequence;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicUnionType;

/*
  how to make this data persistent? extra state for stack only (not stored)
  DerivedData. PersistentSeqSet?  Share nodes.  transform() in seq set given
  new mapping [3] |-> [1]. etc.
  
  How to deal with entering atomic?  For depends: ....   For reachability: ....
  
  Could store the result of evaluating the guard in the transition.
  Is that the clause?  YES.  Also check if the guard is satisfiable.
  If not, use false for the clause.  The clause just satisfies:
  pc & clause <=> pc & guard(state).   So this should be create new Transitions
  and caching guards.
  
  Could cache the guards as a sequence in the Node for the process.
  Need to match them up with Statements.  Yes, they are numbered from
  0 to numOutgoing.    So for each process, create an array of BooleanExpression
  evaluatedGuards that are satisfiable.
  
  However the new state will not necessarily be the new state of the transition
  since executing one transition yields a big TraceStep???
  
  
 */
public class SimpleEnablerWorker {

	// Fields...

	State theState;

	/**
	 * A reasoner with context the path condition of the state.
	 */
	Reasoner reasoner;

	Evaluator evaluator;

	StateFactory stateFactory;

	SymbolicUtility symbolicUtil;

	CIVLTypeFactory typeFactory;

	SymbolicType heapSymbolicType;

	SymbolicType pointerSymbolicType;

	SymbolicUniverse universe;

	BooleanExpression[][] theGuards;

	/**
	 * After the ample set is computed, this flag tells you whether the ample
	 * set is the full set of enabled transitions.
	 */
	boolean full = false;

	List<Transition> ampleSet = null;

	// Types...

	class Node {
		int pid;
		boolean spoiled = false;
		int lowlink = -1;
		int index = -1;
		SeqSet depend = null;
		SeqSet reach = null;

		Node(int pid) {
			this.pid = pid;
		}

		SeqSet getDependSet() throws UnsatisfiablePathConditionException {
			if (depend == null) {
				depend = computeDepends(pid);
			}
			return depend;
		}

		SeqSet getReachSet() {
			if (reach == null) {
				reach = computeReach(pid);
			}
			return reach;
		}
	}

	// Constructor...

	SimpleEnablerWorker(State state, Evaluator evaluator,
			CIVLConfiguration config) {
		this.typeFactory = evaluator.modelFactory().typeFactory();
		this.theState = state;
		this.evaluator = evaluator;
		this.stateFactory = evaluator.stateFactory();
		this.symbolicUtil = evaluator.symbolicUtility();
		this.heapSymbolicType = typeFactory.heapSymbolicType();
		this.pointerSymbolicType = typeFactory.pointerSymbolicType();
		this.universe = evaluator.universe();
		this.reasoner = universe.reasoner(state.getPathCondition(universe));
		this.theGuards = new BooleanExpression[theState.numProcs()][];
	}

	// Methods...

	private void addVariable(SeqSet result, State state, int pid,
			Variable variable) {
		if (variable == null)
			return;

		int dyid = state.getDyscopeID(pid, variable);

		assert dyid >= 0;
		// if the dyscope is out of range, don't add it. This happens when
		// evaluating contract expressions in the next state, because the
		// next state has a new dyscope corresponding to the function call
		// and the expressions are evaluated in that new dyscope.
		if (dyid >= theState.numDyscopes())
			return;

		int vid = variable.vid();

		assert vid >= 0;
		result.add(dyid, vid);
	}

	/**
	 * Adds to {@code result} an over-approximation to the set of memory
	 * locations pointed to by a pointer value.
	 * 
	 * @param result
	 *            the set to which the memory locations should be added
	 * @param state
	 *            the state in which the pointer exists
	 * @param pointer
	 *            a non-null pointer value
	 */
	private void addPointer(SeqSet result, State state, CIVLSource source,
			SymbolicExpression pointer) throws NoReductionException {
		assert pointer.type() == typeFactory.pointerSymbolicType();
		if (pointer == symbolicUtil.nullPointer()
				|| pointer == symbolicUtil.undefinedPointer())
			return;
		if (pointer.operator() != SymbolicOperator.TUPLE)
			throw new NoReductionException();

		int dyscopeID = stateFactory
				.getDyscopeId(symbolicUtil.getScopeValue(pointer));

		assert (dyscopeID >= 0); // or are constants in dyscope -1 ?
		// as in the case of addVariable, ignore new dyscopes from
		// contracts...
		if (dyscopeID >= theState.numDyscopes())
			return;

		int variableID = symbolicUtil.getVariableId(source, pointer);

		assert (variableID >= 0); // can a variable have a negative ID?
		// note: every dyscope has a heap, which is variable ID 0

		SymbolicExpression object = state.getVariableValue(dyscopeID,
				variableID);

		if (object.type() != heapSymbolicType) {
			result.add(dyscopeID, variableID);
		} else { // ... of arrayElementRef of tupleComponentRef of IdentifyRef
			ReferenceExpression ref = symbolicUtil.getSymRef(pointer);
			ReferenceExpression ref1 = ref, ref2 = null, ref3 = null;

			while (ref1 instanceof NTReferenceExpression) {
				ref3 = ref2;
				ref2 = ref1;
				ref1 = ((NTReferenceExpression) ref1).getParent();
			}
			assert ref1.referenceKind() == ReferenceKind.IDENTITY;
			assert ref2 instanceof TupleComponentReference;
			assert ref3 instanceof ArrayElementReference;

			int mallocIndex = ((TupleComponentReference) ref2).getIndex()
					.getInt();
			NumericExpression objectIndex = ((ArrayElementReference) ref3)
					.getIndex();
			IntegerNumber objectNumber = (IntegerNumber) universe
					.extractNumber(objectIndex);

			if (objectNumber == null)
				result.add(dyscopeID, variableID, mallocIndex);
			else
				result.add(dyscopeID, variableID, mallocIndex,
						objectNumber.intValue());
		}
	}

	/**
	 * Given a symbolic constant X of type type, is there some sequence of
	 * operations that could be performed on X to yield a pointer value? For
	 * example, if type is array-of-pointer-to-int, the X[1] yields a pointer
	 * value.
	 * 
	 * @param type
	 *            any type, the type of the symbolic constant X
	 * @return {@code true} iff it is possible to get a pointer value from X
	 */
	private boolean containsPointer(SymbolicType type) {
		switch (type.typeKind()) {
			case ARRAY :
				return containsPointer(((SymbolicArrayType) type).baseType());
			case FUNCTION : {
				SymbolicFunctionType ftype = (SymbolicFunctionType) type;

				return containsPointer(ftype.outputType());
			}
			case MAP : {
				SymbolicMapType mtype = (SymbolicMapType) type;

				return containsPointer(mtype.keyType())
						|| containsPointer(mtype.valueType());
			}
			case SET :
				return containsPointer(((SymbolicSetType) type).elementType());
			case TUPLE :
				for (SymbolicType ftype : ((SymbolicTupleType) type).sequence())
					if (containsPointer(ftype))
						return true;
				return false;
			case UNION :
				for (SymbolicType ftype : ((SymbolicUnionType) type).sequence())
					if (containsPointer(ftype))
						return true;
				return false;
			case BOOLEAN :
			case CHAR :
			case INTEGER :
			case REAL :
			case UNINTERPRETED :
		}
		return false;
	}

	private void getPointedObjects(Collection<SymbolicExpression> result,
			SymbolicType type) throws NoReductionException {
		switch (type.typeKind()) {
			case ARRAY :
				getPointedObjects(result,
						((SymbolicArrayType) type).baseType());
				if (type instanceof SymbolicCompleteArrayType)
					result.add(((SymbolicCompleteArrayType) type).extent());
				break;
			case FUNCTION : {
				SymbolicFunctionType ftype = (SymbolicFunctionType) type;

				getPointedObjects(result, ftype.outputType());
				for (SymbolicType itype : ftype.inputTypes())
					getPointedObjects(result, itype);
				break;
			}
			case MAP : {
				SymbolicMapType mtype = (SymbolicMapType) type;

				getPointedObjects(result, mtype.keyType());
				getPointedObjects(result, mtype.valueType());
				break;
			}
			case SET :
				getPointedObjects(result,
						((SymbolicSetType) type).elementType());
				break;
			case TUPLE :
				for (SymbolicType ftype : ((SymbolicTupleType) type).sequence())
					getPointedObjects(result, ftype);
				break;
			case UNION :
				for (SymbolicType ftype : ((SymbolicUnionType) type).sequence())
					getPointedObjects(result, ftype);
				break;
			case BOOLEAN :
			case CHAR :
			case INTEGER :
			case REAL :
			case UNINTERPRETED :
				// no pointers, nothing to do
		}
	}

	/**
	 * Computes over-approximation of set of objects that could be pointed to by
	 * some component of the given value.
	 * 
	 * @param result
	 * @param state
	 * @param source
	 * @param expr
	 * @throws NoReductionException
	 */
	private void getPointedObjects(SeqSet result, State state,
			CIVLSource source, SymbolicExpression value)
			throws NoReductionException {
		Stack<SymbolicExpression> worklist = new Stack<>();

		worklist.push(value);
		while (!worklist.isEmpty()) {
			SymbolicExpression expr = worklist.pop();
			SymbolicType type = expr.type();

			if (type == null) {// a NULL object has null type, ignore
			} else if (type == pointerSymbolicType) {
				addPointer(result, state, source, expr);
			} else if (expr.operator() == SymbolicOperator.SYMBOLIC_CONSTANT) {
				if (containsPointer(type))
					throw new NoReductionException();
				// } else if (type == typeFactory.heapSymbolicType()
				// || type == typeFactory.bundleSymbolicType()) { // ignore:
				// WHY??
			} else {
				for (SymbolicObject obj : expr.getArguments()) {
					switch (obj.symbolicObjectKind()) {
						case EXPRESSION :
							worklist.push((SymbolicExpression) obj);
							break;
						case TYPE :
							getPointedObjects(worklist, (SymbolicType) obj);
							break;
						case SEQUENCE :
							for (SymbolicExpression se : (SymbolicSequence<?>) obj)
								worklist.push(se);
							break;
						case TYPE_SEQUENCE :
							for (SymbolicType stype : (SymbolicTypeSequence) obj)
								getPointedObjects(worklist, stype);
							break;
						case BOOLEAN :
						case CHAR :
						case INT :
						case NUMBER :
						case STRING :
							// no pointers, nothing to do
					}
				}
			}
		}
	}

	private SymbolicExpression getValue(State state, int[] objectID) {
		// dyscope, var; or dyscope, var, field, objID
		int dyscopeID = objectID[0];
		int variableID = objectID[1];
		SymbolicExpression value = state.getVariableValue(dyscopeID,
				variableID);

		if (objectID.length == 2) {
			return value;
		} else {
			assert objectID.length == 4;

			int mallocID = objectID[2];
			int objID = objectID[3];
			SymbolicExpression row, result;

			assert value.type() == heapSymbolicType;
			if (value.operator() == SymbolicOperator.TUPLE)
				row = (SymbolicExpression) value.argument(mallocID);
			else
				row = universe.tupleRead(value, universe.intObject(mallocID));
			if (row.operator() == SymbolicOperator.ARRAY)
				result = (SymbolicExpression) row.argument(objID);
			else
				result = universe.arrayRead(row, universe.integer(objID));
			return result;
		}
	}

	// given a set of objects, find all objects reachable from that set
	// through pointer operations (dereference, pointer arithmetic).
	// start with given set S.
	// form a worklist, find all pointed objects, repeat until convergence.

	/**
	 * Give a set of objects S, this method adds to S an over-approximation of
	 * all objects that can be reached from S by pointers.
	 * 
	 * @param objectSet
	 *            a SeqSet representing a set of objects S
	 * @param state
	 *            the state in which all evaluation takes place
	 * @param source
	 *            the source used for error reporting
	 * @throws NoReductionException
	 *             if no good over-approximation can be made
	 */
	private void close(SeqSet objectSet, State state, CIVLSource source)
			throws NoReductionException {
		LinkedList<int[]> workset = objectSet.getLeaves();

		// invariant: workset is a subset of objectSet
		while (!workset.isEmpty()) {
			int[] objId = workset.remove();
			SymbolicExpression value = getValue(state, objId);
			SeqSet pointedObjects = new SeqSet();

			getPointedObjects(pointedObjects, state, source, value);
			for (int[] pObj : pointedObjects.getLeaves())
				if (objectSet.add(pObj))
					workset.add(pObj);
		}
	}

	/**
	 * All objects that can be reached from the given variables. There is an
	 * edge from one object to another if the first contains a pointer value
	 * which references (some part of) the second.
	 * 
	 * @param state
	 * @param pid
	 * @param source
	 * @param vars
	 */
	private SeqSet findReachableObjects(State state, int pid, CIVLSource source,
			Set<Variable> vars) throws NoReductionException {
		SeqSet result = new SeqSet();

		for (Variable var : vars)
			result.add(state.getDyscopeID(pid, var), var.vid());
		close(result, state, source);
		return result;
	}

	private State executeCall(State state, int pid, CIVLFunction function,
			List<Expression> arguments)
			throws UnsatisfiablePathConditionException {
		int numArgs = function.functionType().parameterTypes().length;
		SymbolicExpression[] argumentValues = new SymbolicExpression[numArgs];
		int index = 0;

		for (Expression arg : arguments)
			argumentValues[index++] = evaluator.evaluate(state, pid, arg).value;
		return stateFactory.pushCallStack(state, pid, function, argumentValues);
	}

	/**
	 * Computes the dependencies of a function call in the case where the
	 * function has a contract with a "depends_on" clause. An implemented or
	 * system function may have a function contract, and that contract may
	 * include one or more "depends_on" clauses. These clauses specify "access",
	 * "read" or "write" events, each of which has an argument of pointer type.
	 * These arguments may refer to the formal parameters of the function. The
	 * actual arguments of the function call are evaluated, the formal
	 * parameters are assigned the corresponding values, and then the depends_on
	 * expressions are evaluated to yield a set of pointer values. The objects
	 * pointed to by these pointer values are collected into the set returned.
	 * 
	 * The differences between "access", "read", and "write" are currently
	 * ignored. All three are treated the same.
	 * 
	 * Missing depends clauses: a missing depends clause is interpreted to mean
	 * nothing, i.e., the function could depend on anything. It is equivalent to
	 * specifying the universal set consisting of all memory locations for the
	 * depends clause.
	 * 
	 * Behaviors: for a contract with multiple behaviors, the effective depends
	 * clause is the intersection of the depends sets of the enabled behaviors.
	 * Rationale: similar to the case of assigns clauses in ACSL, to say a
	 * statement S depends on X really means it is independent of all statements
	 * in the complement of X. If multiple behaviors are enabled, then all of
	 * the claims encoded by those behaviors should hold, i.e., S is independent
	 * of all statements in the union of the complements of the X_i, i.e., X
	 * depends on the the intersection of the X_i.
	 * 
	 * @param result
	 *            the set into which the dependent object of the call will be
	 *            added
	 * @param state
	 *            the state from which the function is called
	 * @param pid
	 *            the ID of the process making the call
	 * @param statement
	 *            the call statement
	 * @return <code>true</code> if the function has an enabled depends_on
	 *         clause at state, <code>false</code> otherwise. In the case of
	 *         false being returned, the <code>result</code> is not modified
	 * @throws UnsatisfiablePathConditionException
	 */
	private boolean memFromContract(SeqSet result, State state, int pid,
			CallOrSpawnStatement statement)
			throws UnsatisfiablePathConditionException, NoReductionException {
		CIVLFunction function = statement.function();

		if (function == null) {
			Expression functionExpr = statement.functionExpression();
			Triple<State, CIVLFunction, Integer> triple = evaluator
					.evaluateFunctionIdentifier(state, pid, functionExpr,
							functionExpr.getSource());

			function = triple.second;
		}

		if (function.isPureFunction())
			return true; // no dependencies

		FunctionContract contract = function.functionContract();
		boolean dependsRequired = function.isSystemFunction()
				|| function.isAtomicFunction();

		if (contract == null) {
			if (dependsRequired)
				throw new NoReductionException();
			return false;
		}

		State newState = null; // after executing the call
		SeqSet otherSet = null, dependSet = null;

		/*
		 * otherSet contains ancillary references, such as the variables
		 * occurring in the assumes and depends_on clauses. For these, the union
		 * is taken over all behaviors. dependSet contains the actual objects
		 * pointed to by the depends_on clause. For these, the intersection is
		 * taken over all behaviors. The final result is the union of these two
		 * sets, but the result is only used if there is at least one enabled
		 * depends_on clause.
		 */
		if (contract.hasDependsClause()) { // process the default behavior
			FunctionBehavior behavior0 = contract.defaultBehavior();

			if (behavior0 == null) {
				// nothing known
			} else if (behavior0.dependsNoact()) { // depends_on \nothing
				return true;
			} else if (behavior0.numDependsEvents() == 0) {
				// nothing known
			} else { // there are some depends_on clauses
				dependSet = new SeqSet();
				otherSet = new SeqSet();
				newState = executeCall(state, pid, function,
						statement.arguments());
				for (DependsEvent event : behavior0.dependsEvents()) {
					if (event instanceof MemoryEvent) {
						MemoryEvent memEvent = (MemoryEvent) event;
						Set<Expression> memSet = memEvent.memoryUnits();

						for (Expression expr : memSet) {
							SymbolicExpression pointer = evaluator
									.evaluate(newState, pid, expr).value;

							assert pointer.type() == typeFactory
									.pointerSymbolicType();
							addPointer(dependSet, newState, expr.getSource(),
									pointer);
							findObjects(otherSet, newState, pid, expr);
						}
					}
				}
			}
		}
		for (NamedFunctionBehavior behavior : contract.namedBehaviors()) {
			Expression assumption = behavior.assumptions();

			if (assumption != null) {
				if (otherSet == null)
					otherSet = new SeqSet();
				if (newState == null)
					newState = executeCall(state, pid, function,
							statement.arguments());

				findObjects(otherSet, newState, pid, assumption);

				BooleanExpression assumptionValue = (BooleanExpression) evaluator
						.evaluate(newState, pid, assumption);

				if (reasoner.isValid(assumptionValue)) {
					if (behavior.dependsNoact()) { // depends_on \nothing
						if (dependSet == null)
							dependSet = new SeqSet();
						dependSet.clear();
					} else if (behavior.numDependsEvents() == 0) {
						// nothing known
					} else { // there are some depends_on clauses
						// new depend set will be intersection with old...
						SeqSet newDependSet = new SeqSet();

						if (newState == null)
							newState = executeCall(state, pid, function,
									statement.arguments());
						for (DependsEvent event : behavior.dependsEvents()) {
							if (event instanceof MemoryEvent) {
								MemoryEvent memEvent = (MemoryEvent) event;
								Set<Expression> memSet = memEvent.memoryUnits();

								for (Expression expr : memSet) {
									SymbolicExpression pointer = evaluator
											.evaluate(newState, pid,
													expr).value;
									SeqSet ptrSet = new SeqSet();

									assert pointer.type() == typeFactory
											.pointerSymbolicType();
									addPointer(ptrSet, newState,
											expr.getSource(), pointer);
									if (dependSet == null
											|| dependSet.containsAll(ptrSet))
										newDependSet.addAll(ptrSet);
									findObjects(otherSet, newState, pid, expr);
								}
							}
						}
						dependSet = newDependSet;
					}
				}
			}
		} // end loop over named behaviors
		if (dependSet != null) {
			if (otherSet != null)
				result.addAll(otherSet);
			result.addAll(dependSet);
			return true;
		}
		if (dependsRequired)
			throw new NoReductionException();
		return false;
	}

	private void findObjects(SeqSet result, State state, int pid,
			MemoryUnitReference ref)
			throws UnsatisfiablePathConditionException, NoReductionException {
		if (ref == null)
			return;
		if (ref instanceof ArraySliceReference)
			findObjects(result, state, pid,
					((ArraySliceReference) ref).index());
		findObjects(result, state, pid, ref.child());
	}

	private void computeMem(SeqSet result, State state, int pid,
			Statement statement)
			throws UnsatisfiablePathConditionException, NoReductionException {
		StatementKind kind = statement.statementKind();

		switch (kind) {
			case ASSIGN : {
				if (statement instanceof AtomicLockAssignStatement) {
					AtomicLockAssignStatement as = (AtomicLockAssignStatement) statement;

					if (as.enterAtomic())
						computeMemAtomic(result, state, pid, as);
				} else {
					AssignStatement as = (AssignStatement) statement;

					findObjects(result, state, pid, as.getLhs());
					findObjects(result, state, pid, as.rhs());
				}
				break;
			}
			case CALL_OR_SPAWN : {
				CallOrSpawnStatement cs = (CallOrSpawnStatement) statement;

				findObjects(result, state, pid, cs.functionExpression());
				if (cs.lhs() != null)
					findObjects(result, state, pid, cs.lhs());
				for (Expression arg : cs.arguments())
					findObjects(result, state, pid, arg);
				memFromContract(result, state, pid, cs);
				break;
			}
			case CIVL_PAR_FOR_ENTER : {
				CivlParForSpawnStatement ps = (CivlParForSpawnStatement) statement;

				findObjects(result, state, pid, ps.domain());
				findObjects(result, state, pid, ps.domSizeVar());
				findObjects(result, state, pid, ps.parProcsVar());
				break;
			}
			case DOMAIN_ITERATOR : {
				DomainIteratorStatement ds = (DomainIteratorStatement) statement;

				findObjects(result, state, pid, ds.domain());
				// don't think these are needed...
				// computeObjectsIn(result, pid, ds.getLiteralDomCounter());
				// computeObjectsIn(result, pid, ds.loopVariables());
				break;
			}
			case MALLOC : {
				MallocStatement ms = (MallocStatement) statement;

				findObjects(result, state, pid, ms.getScopeExpression());
				findObjects(result, state, pid, ms.getSizeExpression());
				break;
			}
			case NOOP :
				break; // nothing to do
			case PARALLEL_ASSIGN : {
				ParallelAssignStatement ps = (ParallelAssignStatement) statement;

				for (Pair<LHSExpression, Expression> pair : ps.assignments()) {
					findObjects(result, state, pid, pair.left);
					findObjects(result, state, pid, pair.right);
				}
				break;
			}
			case RETURN : {
				ReturnStatement rs = (ReturnStatement) statement;

				findObjects(result, state, pid, rs.expression());
				break;
			}
			case UPDATE : {
				UpdateStatement us = (UpdateStatement) statement;

				for (Expression arg : us.arguments()) {
					findObjects(result, state, pid, arg);
				}
				findObjects(result, state, pid, us.collator());
				computeMem(result, state, pid, us.call());
				break;
			}
			case WITH : {
				WithStatement ws = (WithStatement) statement;

				findObjects(result, state, pid, ws.collateState());
				break;
			}
			default :
				throw new CIVLInternalException("unknown statement kind",
						statement);
		}
	}

	/**
	 * Computes an over-approximation of the existing memory locations accessed
	 * by executing an atomic statement. (The atomic statement may allocate and
	 * access new memory, but these are not included in this computation.) An
	 * atomic statement may be a compound statement and hence will consist of
	 * many edges ({@link Statement).
	 * 
	 * Current implementation: all objects reachable from variables that occur
	 * within the atomic regions (which includes functions called within the
	 * atomic block, functions called by those functions, etc.).
	 * 
	 * @param result
	 *            the set to which the memory locations should be added
	 * @param pid
	 *            process ID for the process executing the atomic statement
	 * @param as
	 *            the {@link Statement} that marks the entrance to the atomic
	 *            statement by obtaining the atomic lock
	 * @throws NoReductionException
	 */
	private void computeMemAtomic(SeqSet result, State state, int pid,
			AtomicLockAssignStatement as) throws NoReductionException {
		Set<Variable> vars = as.getVariables();

		if (vars == null)
			throw new NoReductionException();
		result.addAll(findReachableObjects(state, pid, as.getSource(), vars));
	}

	/**
	 * Computes an over-approximation of the memory locations specified by a
	 * left-hand-side expressions (or "lexpr").
	 * 
	 * @param result
	 *            the set to which the memory locations should be added
	 * @param pid
	 *            process ID for the process evaluating the expression
	 * @param arg
	 *            the argument to the address-of operator
	 * @throws UnsatisfiablePathConditionException
	 */
	private void findObjectsLHS(SeqSet result, State state, int pid,
			LHSExpression arg)
			throws UnsatisfiablePathConditionException, NoReductionException {
		switch (arg.lhsExpressionKind()) {
			case DEREFERENCE :
				// evaluating &*e accesses the memory locations accessed when
				// evaluating e
				findObjects(result, state, pid,
						((DereferenceExpression) arg).pointer());
				break;
			case DOT : {
				// evaluating &e.f accesses the memory locations accessed when
				// evaluating &e
				LHSExpression struct = (LHSExpression) ((DotExpression) arg)
						.structOrUnion();

				findObjectsLHS(result, state, pid, struct);
				break;
			}
			case SUBSCRIPT : {
				// evaluating &e[f] accesses the memory locations accessed
				// when evaluating &e and f
				SubscriptExpression sub = (SubscriptExpression) arg;
				LHSExpression array = sub.array();
				Expression index = sub.index();

				findObjectsLHS(result, state, pid, array);
				findObjects(result, state, pid, index);
				break;
			}
			case VARIABLE : // evaluating &x does not access any memory location
				break;
			default :
				throw new CIVLInternalException("Unknown kind of LExpression",
						arg);
		}
	}

	// loc: taken lexpr which designates a memory location and returns it.
	// example: loc(x)=the memory location specified by variable x
	// example: loc(*p)=the memory location pointed to by p
	// example: loc(x.f)=the memory location specified by field f of
	// variable x
	// example: loc(a[i])=the memory location specified by element i or
	// array a

	// think of loc as &. & returns a pointer, which specifies a memory
	// location.

	// define mem: LExpr -> Set-of-memory-locations by:
	// mem(x)=loc(x)
	// mem(*p)=mem(p) U loc(*p)
	// mem(e.f)=mem(e) U loc(e.f)
	// mem(a[e])=mem(a) U mem(e) U loc(a[e])

	// other expressions (read-only):
	// p+e: mem(p) U mem(e). Etc.

	// when converting a pointer to a mem object: get the whole object only.

	/**
	 * Computes an over-approximation to the set of memory locations accessed
	 * when evaluating an expression.
	 * 
	 * @param result
	 *            the (non-null) set to which the memory locations referenced in
	 *            {@code expr} will be added
	 * @param pid
	 *            the process ID number for the process that is evaluating
	 *            {@code expr}
	 * @param expr
	 *            the expression being evaluated
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating {@code expr} it is discovered
	 *             that the path condition of the current state is not
	 *             satisfiable
	 */
	private void findObjects(SeqSet result, State state, int pid,
			Expression expr)
			throws UnsatisfiablePathConditionException, NoReductionException {
		if (expr == null)
			return;
		findObjects(result, state, pid, expr.getExpressionType());
		switch (expr.expressionKind()) {
			case ABSTRACT_FUNCTION_CALL :
				for (Expression arg : ((AbstractFunctionCallExpression) expr)
						.arguments())
					findObjects(result, state, pid, arg);
				break;
			case ADDRESS_OF :
				findObjectsLHS(result, state, pid,
						((AddressOfExpression) expr).operand());
				break;
			case ARRAY_LAMBDA : {// (int[n])$lambda (int i,j,... | e) f
				ArrayLambdaExpression ale = (ArrayLambdaExpression) expr;

				for (Pair<List<Variable>, Expression> pair : ale
						.boundVariableList())
					findObjects(result, state, pid, pair.right);
				findObjects(result, state, pid, ale.restriction());
				findObjects(result, state, pid, ale.expression());
				break;
			}
			case ARRAY_LITERAL :
				for (Expression element : ((ArrayLiteralExpression) expr)
						.elements())
					findObjects(result, state, pid, element);
				break;
			case BINARY :
				findObjects(result, state, pid,
						((BinaryExpression) expr).left());
				findObjects(result, state, pid,
						((BinaryExpression) expr).right());
				break;
			case BOOLEAN_LITERAL : // nothing
				break;
			case BOUND_VARIABLE : // nothing
				break;
			case CAST :
				findObjects(result, state, pid,
						((CastExpression) expr).getExpression());
				break;
			case CHAR_LITERAL : // nothing
				break;
			case COND : {
				ConditionalExpression cond = (ConditionalExpression) expr;

				findObjects(result, state, pid, cond.getCondition());
				findObjects(result, state, pid, cond.getTrueBranch());
				findObjects(result, state, pid, cond.getFalseBranch());
				break;
			}
			case DEREFERENCE : {
				Expression pointerArg = ((DereferenceExpression) expr)
						.pointer();
				Evaluation eval = evaluator.evaluate(state, pid, pointerArg);

				findObjects(result, state, pid, pointerArg);
				addPointer(result, state, pointerArg.getSource(), eval.value);
				break;
			}
			case DERIVATIVE : {
				DerivativeCallExpression de = (DerivativeCallExpression) expr;

				for (Expression arg : de.arguments())
					findObjects(result, state, pid, arg);
				break;
			}
			case DIFFERENTIABLE : {
				DifferentiableExpression de = (DifferentiableExpression) expr;

				for (Expression lb : de.lowerBounds())
					findObjects(result, state, pid, lb);
				for (Expression ub : de.upperBounds())
					findObjects(result, state, pid, ub);
				break;
			}
			case DOMAIN_GUARD : {
				DomainGuardExpression dge = (DomainGuardExpression) expr;
				int n = dge.dimension();

				findObjects(result, state, pid, dge.domain());
				for (int i = 0; i < n; i++)
					addVariable(result, state, pid, dge.variableAt(i));
				addVariable(result, state, pid, dge.getLiteralDomCounter());
				break;
			}
			case DOT :
				findObjects(result, state, pid,
						((DotExpression) expr).structOrUnion());
				break;
			case DYNAMIC_TYPE_OF :
				findObjects(result, state, pid,
						((DynamicTypeOfExpression) expr).getType());
				break;
			case EXTENDED_QUANTIFIER : {
				ExtendedQuantifiedExpression eqf = (ExtendedQuantifiedExpression) expr;

				findObjects(result, state, pid, eqf.function());
				findObjects(result, state, pid, eqf.lower());
				findObjects(result, state, pid, eqf.higher());
				break;
			}
			case FUNCTION_GUARD : {
				FunctionGuardExpression fge = (FunctionGuardExpression) expr;

				findObjects(result, state, pid, fge.functionExpression());
				for (Expression arg : fge.arguments())
					findObjects(result, state, pid, arg);
				break;
			}
			case FUNCTION_IDENTIFIER : // nothing
				break;
			case FUNC_CALL :
				computeMem(result, state, pid,
						((FunctionCallExpression) expr).callStatement());
				break;
			case HERE_OR_ROOT : // nothing
				break;
			case INITIAL_VALUE : // nothing - abstract initial value
				break;
			case INTEGER_LITERAL : // nothing
				break;
			case LAMBDA :
				findObjects(result, state, pid,
						((LambdaExpression) expr).lambdaFunction());
				break;
			case MEMORY_UNIT :
				findObjects(result, state, pid,
						((MemoryUnitExpression) expr).reference());
				break;
			case MPI_CONTRACT_EXPRESSION :
				for (Expression arg : ((MPIContractExpression) expr)
						.arguments())
					findObjects(result, state, pid, arg);
				break;
			case NOTHING : // nothing
				break;
			case NULL_LITERAL : // nothing
				break;
			case PROC_NULL : // nothing
				break;
			case QUANTIFIER : {
				QuantifiedExpression qe = (QuantifiedExpression) expr;

				for (Pair<List<Variable>, Expression> pair : qe
						.boundVariableList())
					findObjects(result, state, pid, pair.right);
				findObjects(result, state, pid, qe.expression());
				findObjects(result, state, pid, qe.restriction());
				break;
			}
			case REAL_LITERAL : // nothing
				break;
			case REC_DOMAIN_LITERAL : {
				RecDomainLiteralExpression rdl = (RecDomainLiteralExpression) expr;
				int n = rdl.dimension();

				for (int i = 0; i < n; i++)
					findObjects(result, state, pid, rdl.rangeAt(i));
				break;
			}
			case REGULAR_RANGE : {
				RegularRangeExpression rr = (RegularRangeExpression) expr;

				findObjects(result, state, pid, rr.getLow());
				findObjects(result, state, pid, rr.getHigh());
				findObjects(result, state, pid, rr.getStep());
				break;
			}
			case RESULT : // nothing
				break;
			case SCOPEOF :
				findObjectsLHS(result, state, pid,
						((ScopeofExpression) expr).argument());
				break;
			case SELF : // nothing
				break;
			case SIZEOF_EXPRESSION :
				findObjects(result, state, pid, ((SizeofExpression) expr)
						.getArgument().getExpressionType());
				// this is what the evaluator does
				break;
			case SIZEOF_TYPE :
				findObjects(result, state, pid,
						((SizeofTypeExpression) expr).getTypeArgument());
				break;
			case STATE_NULL : // nothing
				break;
			case STRING_LITERAL : // nothing
				break;
			case STRUCT_OR_UNION_LITERAL :
				// nothing. these have constant values only (see Evaluator)
				break;
			case SUBSCRIPT : {
				SubscriptExpression se = (SubscriptExpression) expr;

				findObjectsLHS(result, state, pid, se.array());
				findObjects(result, state, pid, se.index());
				break;
			}
			case SYSTEM_GUARD :
				for (Expression arg : ((SystemGuardExpression) expr)
						.arguments())
					findObjects(result, state, pid, arg);
				break;
			case UNARY :
				findObjects(result, state, pid,
						((UnaryExpression) expr).operand());
				break;
			case UNDEFINED_PROC : // nothing
				break;
			case VALUE_AT : {
				ValueAtExpression vae = (ValueAtExpression) expr;

				findObjects(result, state, pid, vae.state());
				findObjects(result, state, pid, vae.pid());
				findObjects(result, state, pid, vae.expression());
				break;
			}
			case VARIABLE :
				addVariable(result, state, pid,
						((VariableExpression) expr).variable());
				break;
			case WILDCARD : // nothing to do
				break;
			default :
				break;
		}
	}

	private void findObjects(SeqSet result, State state, int pid, CIVLType type)
			throws UnsatisfiablePathConditionException, NoReductionException {
		switch (type.typeKind()) {
			case ARRAY :
				findObjects(result, state, pid,
						((CIVLArrayType) type).elementType());
				break;
			case COMPLETE_ARRAY : {
				CIVLCompleteArrayType atype = (CIVLCompleteArrayType) type;

				findObjects(result, state, pid, atype.elementType());
				findObjects(result, state, pid, atype.extent());
				break;
			}
			case FUNCTION : {
				CIVLFunctionType ftype = (CIVLFunctionType) type;

				for (CIVLType ptype : ftype.parameterTypes())
					findObjects(result, state, pid, ptype);
				findObjects(result, state, pid, ftype.returnType());
				break;
			}
			case POINTER :
				findObjects(result, state, pid,
						((CIVLPointerType) type).baseType());
				break;
			case SET :
				findObjects(result, state, pid,
						((CIVLSetType) type).elementType());
				break;
			case STRUCT_OR_UNION :
				for (StructOrUnionField field : ((CIVLStructOrUnionType) type)
						.fields())
					findObjects(result, state, pid, field.type());
				break;
			case BUNDLE :
			case DOMAIN :
			case ENUM :
			case HEAP :
			case MEM :
			case PRIMITIVE :
			default :
				break;

		}
	}

	/**
	 * Gets the result of evaluating a guard for a statement. If the result was
	 * previously computed, the cached result is returned. Otherwise, the guard
	 * is evaluated and then the {@link Reasoner} for the path condition of the
	 * current state is used to determine whether the resulting symbolic
	 * expression is satisfiable. If it is not satisfiable, the result is
	 * replaced by the false expression.
	 * 
	 * Should we also check if the guard is valid? Should it be simplified?
	 * 
	 * @param pid
	 *            the process ID
	 * @param sid
	 *            the statement ID, i.e., the index in the list of outgoing
	 *            statements from the current location of the process
	 * @return evaluated guard
	 * @throws UnsatisfiablePathConditionException
	 */
	private BooleanExpression getGuardValue(int pid, int sid)
			throws UnsatisfiablePathConditionException {
		Location location = theState.getProcessState(pid).getLocation();
		int numOutgoing = location.getNumIncoming();
		assert sid < numOutgoing;

		if (theGuards[pid] == null)
			theGuards[pid] = new BooleanExpression[numOutgoing];

		BooleanExpression evaluatedGuard = theGuards[pid][sid];

		if (evaluatedGuard == null) {
			Statement stmt = location.getOutgoing(sid);
			Expression expr = stmt.guard();

			evaluatedGuard = (BooleanExpression) evaluator.evaluate(theState,
					pid, expr);
			if (reasoner.unsat(evaluatedGuard)
					.getResultType() == ResultType.YES)
				evaluatedGuard = universe.falseExpression();
			theGuards[pid][sid] = evaluatedGuard;
		}
		return evaluatedGuard;
	}

	/**
	 * Computes an over-approximation to the set of memory locations associated
	 * to a process's current location. These are: (1) any memory location that
	 * could be read or modified by a currently enabled statement, and (2) all
	 * memory locations that are read by any guard of an (enabled or disabled)
	 * statement emanating from that location.
	 * 
	 * @param pid
	 * @throws UnsatisfiablePathConditionException
	 */
	private SeqSet computeDepends(int pid)
			throws UnsatisfiablePathConditionException {
		Location location = theState.getProcessState(pid).getLocation();
		int numOutgoing = location.getNumOutgoing();
		SeqSet result = new SeqSet();

		try {
			for (int i = 0; i < numOutgoing; i++) {
				Statement statement = location.getOutgoing(i);
				Expression guard = statement.guard();
				BooleanExpression guardValue = getGuardValue(pid, i);

				findObjects(result, theState, pid, guard);
				if (reasoner.unsat(guardValue)
						.getResultType() != ResultType.YES)
					computeMem(result, theState, pid, statement);
			}
		} catch (NoReductionException e) {
			result.makeFull();
		}
		return result;
	}

	/**
	 * Computes an over-approximation of all objects reachable from process pid
	 * in theState.
	 * 
	 * @param pid
	 * @throws NoReductionException
	 */
	private SeqSet computeReach(int pid) {
		// find all dyscopes reachable from the stack and parent relation
		// iterate over all variables in those dyscopes, except for heap
		// add those variables to the result, then find all pointers
		// inside the variable value
		// add the pointers to a workset :
		// take a pointer from the workset: add the object pointed to to
		// result (if it isn't already there). find all pointers in the
		// resulting objects value to the workset, repeat until workset
		// empty.
		ProcessState ps = theState.getProcessState(pid);
		Set<Integer> dyscopeIDs = new HashSet<>();
		SeqSet varSet = new SeqSet();

		for (StackEntry se : ps.getStackEntries()) {
			int dyscopeID = se.scope();

			while (dyscopeID != -1 && dyscopeIDs.add(dyscopeID))
				dyscopeID = theState.getParentId(dyscopeID);
		}
		for (int dyscopeID : dyscopeIDs) {
			DynamicScope ds = theState.getDyscope(dyscopeID);
			Scope scope = ds.lexicalScope();

			for (Variable var : scope.variables())
				if (!var.type().isHeapType())
					varSet.add(dyscopeID, var.vid());
		}
		try {
			close(varSet, theState, ps.getLocation().getSource());
		} catch (NoReductionException e) {
			varSet.add();
		}
		return varSet;
	}

	protected void computeAmpleSet() {
		// perform Tarjan's algorithm to find a small SCC in
		// the POR graph. Return the union of all the transitions
		// in that SCC. There must be at least one, and they must
		// all be "invisible".
		// nodes are processes
		// there is an edge p->q if q reaches something on which p depends,
		// i.e., the depends set of p and the reach set of q are not disjoint.

		// visibility: cannot have a proper ample set with a visible transition.
		// visibility depends on the property being checked (deadlock, potential
		// deadlock)

		// special handling needed for comm???

		// TODO
	}

	protected List<Transition> ampleSet() {
		return ampleSet;
	}

	protected boolean isFull() {
		return full;
	}
}

class NoReductionException extends Exception {
	private static final long serialVersionUID = 1L;
}
