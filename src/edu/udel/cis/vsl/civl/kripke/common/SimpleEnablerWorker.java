package edu.udel.cis.vsl.civl.kripke.common;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import edu.udel.cis.vsl.civl.config.IF.CIVLConstants.DeadlockKind;
import edu.udel.cis.vsl.civl.dynamic.IF.SymbolicUtility;
import edu.udel.cis.vsl.civl.kripke.IF.Enabler;
import edu.udel.cis.vsl.civl.kripke.IF.LibraryEnabler;
import edu.udel.cis.vsl.civl.model.IF.CIVLFunction;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLSource;
import edu.udel.cis.vsl.civl.model.IF.CIVLTypeFactory;
import edu.udel.cis.vsl.civl.model.IF.Scope;
import edu.udel.cis.vsl.civl.model.IF.SystemFunction;
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
import edu.udel.cis.vsl.civl.model.IF.expression.BinaryExpression.BINARY_OPERATOR;
import edu.udel.cis.vsl.civl.model.IF.expression.BooleanLiteralExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.CastExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.ConditionalExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DereferenceExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DerivativeCallExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DifferentiableExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DomainGuardExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DotExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.DynamicTypeOfExpression;
import edu.udel.cis.vsl.civl.model.IF.expression.Expression;
import edu.udel.cis.vsl.civl.model.IF.expression.Expression.ExpressionKind;
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
import edu.udel.cis.vsl.civl.semantics.IF.LibraryLoaderException;
import edu.udel.cis.vsl.civl.semantics.IF.Semantics;
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

public class SimpleEnablerWorker {

	// Fields...

	/**
	 * The {@link Enabler} that created this worker.
	 */
	SimpleEnabler enabler;

	/**
	 * The state at which we are computing an ample set.
	 */
	State theState;

	/**
	 * The number of processes in the state. Note some of the processes may have
	 * terminated or may be {@code null}.
	 */
	int nprocs;

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

	/**
	 * The guard values of the statements emanating from the current locations
	 * of the processes. Value in position [i][j] is the result of evaluating
	 * the guard of statement j of process i. These are initially {@code null}
	 * but are filled in as values are requested.
	 */
	BooleanExpression[][] theGuards;

	/**
	 * The transitions enabled in each process. Ragged array of length nprocs.
	 */
	Transition[][] enabledTransitions;

	/**
	 * After the ample set is computed, this flag tells you whether the ample
	 * set is the full set of enabled transitions.
	 */
	boolean full = false;

	/**
	 * The set of transitions which form an ample set. The objective of this
	 * class is to compute this set.
	 */
	Transition[] ampleSet = null;

	// Constructor...

	SimpleEnablerWorker(SimpleEnabler enabler, State state) {
		this.evaluator = enabler.evaluator;
		this.typeFactory = evaluator.modelFactory().typeFactory();
		this.stateFactory = evaluator.stateFactory();
		this.symbolicUtil = evaluator.symbolicUtility();
		this.heapSymbolicType = typeFactory.heapSymbolicType();
		this.pointerSymbolicType = typeFactory.pointerSymbolicType();
		this.universe = evaluator.universe();
		this.reasoner = universe.reasoner(state.getPathCondition(universe));
		this.nprocs = state.numProcs();
		this.theGuards = new BooleanExpression[nprocs][];
		this.enabledTransitions = new Transition[nprocs][];
		this.theState = state;
		this.nprocs = theState.numProcs();
	}

	// Methods...

	/**
	 * Adds a variable instance to a set of objects. The set represents a set of
	 * objects that exists at a certain state. A variable instance is
	 * represented as a pair (d,v), where d is the dynamic scope ID and v is the
	 * variable.
	 * 
	 * <p>
	 * A variable can be instantiated multiple (or 0) times because it is
	 * instantiated each time control in some process enters the variable's
	 * scope. Because of recursion and multiple processes, many instances of the
	 * variable may exist in the given state. The particular instance is
	 * specified by the state, pid, and variable. In the given state, there is
	 * at most one "visible" instance of the variable from the specified
	 * process: the top frame of the process's call stack points to a dynamic
	 * scope. If the variable exists in that scope, that is the instance, else
	 * the parent dynamic scope is examined, and so on until the variable is
	 * either found or the root dynamic scope is reached. When a variable is
	 * used in an expression, this is how the instance of the variable is found.
	 * </p>
	 * 
	 * <p>
	 * If an instance of the variable is not found, this will be a no-op. (This
	 * may happen when evaluating contract expressions in the next state (the
	 * state after the call), because the next state has a new dyscope
	 * corresponding to the function call and the expressions are evaluated in
	 * that new dyscope.) If the variable instance is already in the given set,
	 * this will be a no-op.
	 * </p>
	 * 
	 * @param result
	 *            the set to which the variable should be added
	 * @param state
	 *            the state in which this variable instance exists
	 * @param pid
	 *            the ID of the process which references this variable
	 * @param variable
	 *            the (static) variable to search for
	 */
	private void addVariable(SeqSet result, State state, int pid,
			Variable variable) {
		if (variable == null)
			return;

		int dyid = state.getDyscopeID(pid, variable);

		assert dyid >= 0;
		if (dyid >= theState.numDyscopes())
			return;

		int vid = variable.vid();

		assert vid >= 0;
		result.add(dyid, vid);
	}

	/**
	 * Adds to {@code result} an over-approximation to the set of memory
	 * locations pointed to by a pointer value. The pointer value specifies a
	 * sub-object (which may the whole object) of an object that exists in a
	 * state. An object is either a variable instance or the object that is
	 * created by a single call to malloc. This method will find and add the
	 * entire object to the given set {@code result}.
	 * 
	 * <p>
	 * If the object does not exist at the given state (because it is not in
	 * scope), this is a no-op. If the object is already in {@code result}, this
	 * is a no-op. If the pointer value is the NULL pointer or undefined, this
	 * is a no-op.
	 * </p>
	 * 
	 * @param result
	 *            the set to which the memory locations should be added
	 * @param state
	 *            the state in which the pointer is evaluated
	 * @param source
	 *            source information used for error-reporting; should be the
	 *            piece of source code that was evaluated to yield the pointer
	 *            value
	 * @param pointer
	 *            a non-null pointer value
	 * @throws NoReductionException
	 *             if the pointer is not concrete, and therefore no reasonable
	 *             over-estimate of the pointed-to objects can be made
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
	 * Given a symbolic constant X of type {@code type}, is there some sequence
	 * of operations that could be performed on X to yield a pointer value? For
	 * example, if type is array-of-pointer-to-int, then X[1] yields a pointer
	 * value.
	 * 
	 * TODO: is there a way to do this once, when the type is created?
	 * 
	 * @param type
	 *            any type, the type of the symbolic constant X
	 * @return {@code true} iff it is possible to get a pointer value from X
	 */
	private boolean containsPointer(SymbolicType type) {
		if (type == pointerSymbolicType)
			return true;
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

	/**
	 * Finds all symbolic expressions occurring within a symbolic type, adding
	 * them to {@code result}. These symbolic expressions are necessarily of
	 * integer type, occurring as length expressions in array types.
	 * 
	 * @param result
	 *            the collection to which the symbolic expressions will be added
	 * @param type
	 *            the type to be searched for symbolic expressions
	 */
	private void getExpressionsInType(Collection<SymbolicExpression> result,
			SymbolicType type) {
		switch (type.typeKind()) {
			case ARRAY :
				getExpressionsInType(result,
						((SymbolicArrayType) type).baseType());
				if (type instanceof SymbolicCompleteArrayType)
					result.add(((SymbolicCompleteArrayType) type).extent());
				break;
			case FUNCTION : {
				SymbolicFunctionType ftype = (SymbolicFunctionType) type;

				getExpressionsInType(result, ftype.outputType());
				for (SymbolicType itype : ftype.inputTypes())
					getExpressionsInType(result, itype);
				break;
			}
			case MAP : {
				SymbolicMapType mtype = (SymbolicMapType) type;

				getExpressionsInType(result, mtype.keyType());
				getExpressionsInType(result, mtype.valueType());
				break;
			}
			case SET :
				getExpressionsInType(result,
						((SymbolicSetType) type).elementType());
				break;
			case TUPLE :
				for (SymbolicType ftype : ((SymbolicTupleType) type).sequence())
					getExpressionsInType(result, ftype);
				break;
			case UNION :
				for (SymbolicType ftype : ((SymbolicUnionType) type).sequence())
					getExpressionsInType(result, ftype);
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
	 *            set to which the objects will be added
	 * @param state
	 *            the state in which the value exists
	 * @param source
	 *            source code info for error reporting; should correspond to the
	 *            expression that evaluated to {@code value}
	 * @param value
	 *            the value to search for pointers
	 * @throws NoReductionException
	 *             if a pointer found in {@code value} is not concrete
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
							getExpressionsInType(worklist, (SymbolicType) obj);
							break;
						case SEQUENCE :
							for (SymbolicExpression se : (SymbolicSequence<?>) obj)
								worklist.push(se);
							break;
						case TYPE_SEQUENCE :
							for (SymbolicType stype : (SymbolicTypeSequence) obj)
								getExpressionsInType(worklist, stype);
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

	/**
	 * Gets the value of an object in the specified state. The object is
	 * specified by a sequence of integers. If the object is a regular variable,
	 * the sequence consists of two integers: the dynamic scope ID and the
	 * variable ID. If the object is heap-allocated, the sequence consists of 4
	 * integers: ID of the dynamic scope containing the heap, the variable ID of
	 * the heap variable in that scope, the row ID (corresponding to the malloc
	 * statement) in the heap table, and the column ID (corresponding to a
	 * single malloc call) within that row.
	 * 
	 * @param state
	 *            the state in which the value of the object will be found
	 * @param objectID
	 *            the sequence of integers specifying the object
	 * @return the value of the specified object in {@code state}
	 */
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

	/**
	 * Given a set of objects S, this method adds to S an over-approximation of
	 * all objects that can be reached from S by pointers. "Reached" means
	 * through pointer dereferences, pointer arithmetic, and any other operation
	 * that could be performed on a value. This computes the transitive closure
	 * of the binary relation on objects where there is an edge from o1 to o2 if
	 * o1 contains a pointer which points to some part of o2.
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
	 * Finds all objects that can be reached from the given variables. There is
	 * an edge from one object to another if the first contains a pointer value
	 * which references (some part of) the second.
	 * 
	 * @param state
	 *            the state in which specifies the value of all objects
	 * @param pid
	 *            the ID of the process which is referencing the variables; used
	 *            together with {@code state} to determine the variable
	 *            instances and the values stored
	 * @param source
	 *            a source info object used for reporting errors
	 * @param vars
	 *            the set of variables which form the starting point of the
	 *            search
	 * @return the set of reachable objects, represented as a {@link SeqSet}
	 */
	private SeqSet findReachableObjects(State state, int pid, CIVLSource source,
			Set<Variable> vars) throws NoReductionException {
		SeqSet result = new SeqSet();

		for (Variable var : vars)
			result.add(state.getDyscopeID(pid, var), var.vid());
		close(result, state, source);
		return result;
	}

	/**
	 * Gets the function being called or spawned in a call or spawn statement.
	 * Usually this is obvious since the function expression is an identifier so
	 * the function is known statically. However the function expression can be
	 * any expression (think function pointers in C), and in general may only be
	 * known dynamically. This method will figure it out in either case.
	 * 
	 * @param state
	 *            the state in which the function is called or spawned; needed
	 *            in case the function expression is not statically known
	 * @param pid
	 *            the ID of the process performing the call or spawn
	 * @param statement
	 *            the call or spawn statement
	 * @return the function called or spawned
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating the function expression it is
	 *             determined that the path condition is not satisfiable
	 */
	private CIVLFunction getFunction(State state, int pid,
			CallOrSpawnStatement statement)
			throws UnsatisfiablePathConditionException {
		CIVLFunction function = statement.function();

		if (function == null) {
			Expression functionExpr = statement.functionExpression();
			Triple<State, CIVLFunction, Integer> triple = evaluator
					.evaluateFunctionIdentifier(state, pid, functionExpr,
							functionExpr.getSource());

			function = triple.second;
		}
		return function;
	}

	/**
	 * Computes the guard from the contract of the called function, in the case
	 * when the called function is not known statically.
	 * 
	 * <p>
	 * For a call statement for which the function expression is not an
	 * identifier (the usual case), the called function is not known statically.
	 * In this case, the function guard (specified perhaps in the enabled_when
	 * clause of the function contract) was not built into the statement's guard
	 * expression, and must therefore be computed dynamically, at the given
	 * state.
	 * </p>
	 * 
	 * <p>
	 * If the given {@code statement} is not a call statement, {@code null} is
	 * returned. If the function expression of the call statement is an
	 * identifier (the normal case), then
	 * {@link CallOrSpawnStatement#function()} returns something non-null, and
	 * this method will return {@code null}. Otherwise, the function expression
	 * is evaluated at the given {@code state} to determine the called function
	 * f. If f has a contract and that contract specifies a guard, then that
	 * guard is returned, otherwise {@code null} is returned.
	 * </p>
	 * 
	 * @param state
	 *            the state from which the call will take place
	 * @param pid
	 *            the ID of the process in which the call takes place
	 * @param statement
	 *            a call statement
	 * @return the guard expression, or {@code null} (if {@code statement} is
	 *         not a call or if the called function is statically known)
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating the function expression it is
	 *             determined that the path condition is not satisfiable
	 */
	private Expression getDynamicGuard(State state, int pid,
			CallOrSpawnStatement statement)
			throws UnsatisfiablePathConditionException {
		if (!statement.isCall())
			return null;

		CIVLFunction function = statement.function();

		if (function != null)
			return null;

		Expression functionExpr = statement.functionExpression();
		Triple<State, CIVLFunction, Integer> triple = evaluator
				.evaluateFunctionIdentifier(state, pid, functionExpr,
						functionExpr.getSource());

		function = triple.second;

		FunctionContract contract = function.functionContract();

		if (contract == null)
			return null;

		return contract.guard();
	}

	/**
	 * Computes the new state resulting from executing a function call from a
	 * given state.
	 * 
	 * @param state
	 *            the original state
	 * @param pid
	 *            the ID of the process performing the call
	 * @param function
	 *            the function being called
	 * @param arguments
	 *            the actual argument expressions in the call
	 * @return the new state immediately after the call (i.e., just after the
	 *         new frame is pushed onto the call stack and control enters the
	 *         called function)
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating the arguments it is discovered
	 *             that the path condition is unsatisfiable
	 */
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
	 * <p>
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
	 * </p>
	 * 
	 * <p>
	 * The differences between "access", "read", and "write" are currently
	 * ignored. All three are treated the same.
	 * </p>
	 * 
	 * <p>
	 * Missing depends clauses: a missing depends clause is interpreted to mean
	 * nothing, i.e., the function could depend on anything. It is equivalent to
	 * specifying the universal set consisting of all memory locations for the
	 * depends clause.
	 * </p>
	 * 
	 * 
	 * <p>
	 * Behaviors: for a contract with multiple behaviors, the effective depends
	 * clause is the intersection of the depends sets of the enabled behaviors.
	 * Rationale: similar to the case of assigns clauses in ACSL, to say a
	 * statement S depends on X really means it is independent of all statements
	 * in the complement of X. If multiple behaviors are enabled, then all of
	 * the claims encoded by those behaviors should hold, i.e., S is independent
	 * of all statements in the union of the complements of the X_i, i.e., X
	 * depends on the the intersection of the X_i.
	 * </p>
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
	 *         clause at {@code state}, <code>false</code> otherwise. In the
	 *         case of {@code false} being returned, the <code>result</code> is
	 *         not modified
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating some expression it is
	 *             discovered that the path condition of {@code state} is
	 *             unsatisfiable
	 * @throws NoReductionException
	 *             if the called function is a system function or atomic
	 *             function, but no depends_on clause is specified
	 */
	private boolean memFromContract(SeqSet result, State state, int pid,
			CallOrSpawnStatement statement)
			throws UnsatisfiablePathConditionException, NoReductionException {
		CIVLFunction function = getFunction(state, pid, statement);

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

	/**
	 * Finds all objects referenced in a {@link MemoryUnitReference}, adding
	 * them to the specified {@code SeqSet}.
	 * 
	 * @param result
	 *            the set to which the objects should be added
	 * @param state
	 *            the state to which the memory unit reference applies
	 * @param pid
	 *            the ID of the process containing the memory unit reference
	 * @param ref
	 *            the memory unit reference
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating, it is determined that the
	 *             path condition of {@code state} is unsatisfiable
	 * @throws NoReductionException
	 *             if a non-concrete pointer is encountered
	 */
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

	/**
	 * Computes an over-approximation of the set of objects accessed (read or
	 * modified) by executing a statement.
	 *
	 * @param result
	 *            the set of objects to which the computed set of objects will
	 *            be added
	 * @param state
	 *            the state from which the statement is executed
	 * @param pid
	 *            the ID of the process executing the statement
	 * @param statement
	 *            the statement being executed
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of this computation it is discovered that
	 *             {@code state} has an unsatisfiable path condition
	 * @throws NoReductionException
	 *             if a non-concrete pointer is encountered
	 */
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

				if (cs.isSpawn() && enabler.config.getProcBound() > 0)
					throw new NoReductionException();
				if (isYield(cs)) {
					if (stateFactory.processInAtomic(state) != pid) {
						// second part of $yield: this proc re-obtains
						// atomic lock. For now, say depends on everything.
						// TODO: eventually do same thing we do for atomic-enter
						throw new NoReductionException();
					} else {
						// first part of $yield: no dependencies
						break;
					}
				}
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
	 * <p>
	 * Computes an over-approximation of the set of pre-existing objects
	 * accessed by executing an atomic statement. (The atomic statement may
	 * allocate and access new memory, but these are not included in this
	 * computation.)
	 * </p>
	 * 
	 * <p>
	 * Current implementation: all objects reachable from variables that occur
	 * within the atomic region (which includes functions called within the
	 * atomic block, functions called by those functions, etc.).
	 * </p>
	 * 
	 * @param result
	 *            the set to which the objects should be added
	 * @param state
	 *            the state from which the atomic statement is executed
	 * @param pid
	 *            process ID for the process executing the atomic statement
	 * @param as
	 *            the {@link Statement} that marks the entrance to the atomic
	 *            statement by obtaining the atomic lock
	 * @throws NoReductionException
	 *             if no upper bound on the set of objects can be found
	 */
	private void computeMemAtomic(SeqSet result, State state, int pid,
			AtomicLockAssignStatement as) throws NoReductionException {
		Set<Variable> vars = as.getVariables();

		if (vars == null)
			throw new NoReductionException();
		result.addAll(findReachableObjects(state, pid, as.getSource(), vars));
	}

	/**
	 * Computes an over-approximation of the set of objects specified by a
	 * left-hand-side expressions (or "lexpr").
	 * 
	 * @param result
	 *            the set to which the memory locations should be added
	 * @param state
	 *            the state in which the expression {@code arg} occurs
	 * @param pid
	 *            process ID for the process evaluating the expression
	 * @param arg
	 *            the argument to the address-of operator
	 * @throws UnsatisfiablePathConditionException
	 *             if it is discovered that the path condition of {@code state}
	 *             is unsatisfiable
	 * @throws NoReductionException
	 *             if no good over-approximation can be found
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

	/**
	 * Computes an over-approximation to the set of objects accessed when
	 * evaluating an expression.
	 * 
	 * @param result
	 *            the (non-null) set to which the memory locations referenced in
	 *            {@code expr} will be added
	 * @param state
	 *            the state in which the evaluation occurs
	 * @param pid
	 *            the process ID number for the process that is evaluating
	 *            {@code expr}
	 * @param expr
	 *            the expression being evaluated. may be {@code null}, in which
	 *            case this is a no-op
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating {@code expr} it is discovered
	 *             that the path condition of the current state is not
	 *             satisfiable
	 * @throws NoReductionException
	 *             if no good over-approximation can be found
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

	/**
	 * Computes an over-approximation to the set of objects referenced in a CIVL
	 * type. These object references would occur in array length expressions.
	 * Example: in the type {@code int[n]} the object {@code n} is referenced.
	 * 
	 * @param result
	 *            the set to which the objects shall be added
	 * @param state
	 *            the state in which this type is evaluated
	 * @param pid
	 *            the ID of the process performing the evaluation
	 * @param type
	 *            the CIVL type
	 * @throws UnsatisfiablePathConditionException
	 *             if it is discovered that the path condition of {@code state}
	 *             is unsatisfiable
	 * @throws NoReductionException
	 *             if no good over-approximation can be found
	 */
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
	 * Gets the result of evaluating a guard for a statement. The guard is
	 * evaluated at state {@link #theState}. If the result was previously
	 * computed, the cached result is returned. Otherwise, the guard is
	 * evaluated and then the {@link Reasoner} for the path condition of
	 * {@link #theState} is used to determine whether the resulting symbolic
	 * expression is satisfiable. If it is not satisfiable, the result is
	 * replaced by the false expression.
	 * 
	 * <p>
	 * This handles the case where the guard is not explicit in the model, due
	 * to the call of a system or atomic function through a function pointer (so
	 * the function called is not statically known).
	 * </p>
	 * 
	 * <p>
	 * TODO: Should we also check if the guard is valid? Should it be
	 * simplified?
	 * </p>
	 * 
	 * @param pid
	 *            the process ID
	 * @param sid
	 *            the statement ID, i.e., the index in the list of outgoing
	 *            statements from the current location of the process
	 * @return evaluated guard
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating it is discovered that the path
	 *             condition of {@link #theState} is unsatisfiable
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

			if (stmt.statementKind() == StatementKind.CALL_OR_SPAWN) {
				Expression dynamicGuard = getDynamicGuard(theState, pid,
						(CallOrSpawnStatement) stmt);

				if (dynamicGuard != null)
					expr = enabler.modelFactory.binaryExpression(
							stmt.getSource(), BINARY_OPERATOR.AND, expr,
							dynamicGuard);
			}
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
	 * Is the given {@link Statement} the {@code $yield} statement?
	 * 
	 * @param stmt
	 *            a (non-null) {@link Statement}
	 * @return {@code true} iff {@code stmt} is the {@code $yield statement}
	 */
	private boolean isYield(Statement stmt) {
		return stmt.statementKind() == StatementKind.CALL_OR_SPAWN
				&& ((CallOrSpawnStatement) stmt).isCall()
				&& ((CallOrSpawnStatement) stmt)
						.function() == enabler.yieldFunction;
	}

	/**
	 * Is the given {@link Statement} an invocation of the {@code $assume}
	 * statement?
	 * 
	 * @param stmt
	 *            a (non-null) {@link Statement}
	 * @return {@code true} iff {@code stmt} is an invocation of the
	 *         {@code $assume statement}
	 */
	private boolean isAssume(Statement stmt) {
		return stmt.statementKind() == StatementKind.CALL_OR_SPAWN
				&& ((CallOrSpawnStatement) stmt).isCall()
				&& ((CallOrSpawnStatement) stmt)
						.function() == enabler.assumeFunction;
	}

	/**
	 * Is the given {@link Statement} a call of a system function? This method
	 * will produce the correct answer even if the call is through a function
	 * pointer or other complex function expression.
	 * 
	 * @param state
	 *            the state from which the statement is executed
	 * @param pid
	 *            the ID of the process executing {@code stmt}
	 * @param stmt
	 *            the statement being executed
	 * @return {@code true} iff {@code stmt} is a call of a system function
	 * @throws UnsatisfiablePathConditionException
	 *             if in the course of evaluating the function expression it is
	 *             discovered that the path condition of {@code state} is
	 *             unsatisfiable
	 */
	private boolean isSystemCall(State state, int pid, Statement stmt)
			throws UnsatisfiablePathConditionException {
		if (stmt.statementKind() == StatementKind.CALL_OR_SPAWN) {
			CallOrSpawnStatement call = (CallOrSpawnStatement) stmt;

			if (call.isCall()) {
				CIVLFunction function = getFunction(theState, pid, call);

				if (function.isSystemFunction())
					return true;
			}
		}
		return false;
	}

	/**
	 * Computes the set of enabled transitions of a call of a system function.
	 * The set is obtained using the function's library's
	 * {@link LibraryEnabler}.
	 * 
	 * @param pid
	 *            the ID of the process making the call
	 * @param guardValue
	 *            the value of the guard expression of the call statement, in
	 *            state {@link #theState}
	 * @param call
	 *            the call statement
	 * @return the list of transitions enabled by this system call
	 * @throws UnsatisfiablePathConditionException
	 *             if in the process of evaluating the function expression it is
	 *             determined that the path condition of {@link #theState} is
	 *             unsatisfiable
	 */
	private List<Transition> enabledTransitionsOfSystemCall(int pid,
			BooleanExpression guardValue, CallOrSpawnStatement call)
			throws UnsatisfiablePathConditionException {
		SystemFunction sf = (SystemFunction) getFunction(theState, pid, call);

		try {
			LibraryEnabler lib = enabler.libraryEnabler(call.getSource(),
					sf.getLibrary());

			return lib.enabledTransitions(theState, call, guardValue, pid,
					null);
		} catch (LibraryLoaderException e) {
			throw new CIVLInternalException(
					"unable to load library " + sf.getLibrary(), call);
		}
	}

	/**
	 * Computes the set of transitions enabled at {@link #theState} by a given
	 * statement.
	 * 
	 * @param result
	 *            the list to which the enabled transitions will be added
	 * @param pid
	 *            the ID of the process executing the statement
	 * @param location
	 *            the current location of the process at state {@link #theState}
	 *            (this could be determined from {@link #theState} but is an
	 *            argument for efficiency)
	 * @param stmtID
	 *            the ID number of the outgoing statement from {@code location}
	 * @throws UnsatisfiablePathConditionException
	 *             if it is determined that the path condition of
	 *             {@link #theState} is unsatisfiable
	 */
	private void computeEnabledFromStatement(List<Transition> result, int pid,
			Location location, int stmtID)
			throws UnsatisfiablePathConditionException {
		Statement stmt = location.getOutgoing(stmtID);
		BooleanExpression guardValue = getGuardValue(pid, stmtID);

		if (guardValue.isFalse())
			return;
		if (isSystemCall(theState, pid, stmt))
			result.addAll(enabledTransitionsOfSystemCall(pid, guardValue,
					(CallOrSpawnStatement) stmt));
		else {
			boolean simplify = isAssume(stmt);
			boolean noop = stmt.statementKind() == StatementKind.NOOP;
			Transition trans = noop
					? Semantics.newNoopTransition(pid, guardValue, stmt,
							simplify, null)
					: Semantics.newTransition(pid, guardValue, stmt, simplify,
							null);

			result.add(trans);
		}
	}

	/**
	 * Is the given expression the boolean expression "true"?
	 * 
	 * @param expr
	 *            a non-null CIVL {@link Expression}
	 * @return {@code true} iff {@code expr} is the expression "true"
	 */
	private boolean isTrue(Expression expr) {
		return expr.expressionKind() == ExpressionKind.BOOLEAN_LITERAL
				&& ((BooleanLiteralExpression) expr).value();
	}

	/**
	 * Is the given statement a "send" operation, i.e., a call of function
	 * {@code $comm_enqueue}?
	 * 
	 * @param state
	 *            the state from which the statement is executed
	 * @param pid
	 *            the ID of the process executing the statement
	 * @param stmt
	 *            any non-null CIVL {@link Statement}
	 * @return {@code true} iff {@code stmt} is a call of function
	 *         {@code $comm_enqueue}
	 * @throws UnsatisfiablePathConditionException
	 *             if it is determined that the path condition of {@code state}
	 *             is unsatisfiable
	 */
	private boolean isSend(State state, int pid, Statement stmt)
			throws UnsatisfiablePathConditionException {
		if (stmt.statementKind() == StatementKind.CALL_OR_SPAWN) {
			CallOrSpawnStatement call = (CallOrSpawnStatement) stmt;

			if (call.isCall()) {
				CIVLFunction function = getFunction(state, pid, call);

				if (function == enabler.commEnqueueFunction)
					return true;
			}
		}
		return false;
	}

	/**
	 * Computes the set of transitions enabled at {@link #theState} in the
	 * specified process.
	 * 
	 * <p>
	 * Precondition: the atomic lock is not held by another process at
	 * {@link #theState} Hence the atomic lock may be free, or it may be held by
	 * the specified process.
	 * </p>
	 * 
	 * @param result
	 *            the list to which the enabled transitions will be added
	 * @param pid
	 *            ID of the process
	 * @throws UnsatisfiablePathConditionException
	 *             if it is determined that the path condition of
	 *             {@link #theState} is unsatisfiable
	 */
	private void computeEnabledInProcess(List<Transition> result, int pid)
			throws UnsatisfiablePathConditionException {
		Location location = theState.getProcessState(pid).getLocation();
		int numStatements = location.getNumOutgoing();

		for (int i = 0; i < numStatements; i++)
			computeEnabledFromStatement(result, pid, location, i);
	}

	/**
	 * <p>
	 * Computes an over-approximation to the set of objects associated to a
	 * process's current location at state {@link #theState}. These are: (1) any
	 * object that could be read or modified by a currently enabled statement,
	 * and (2) all objects that are read by any guard of an (enabled or
	 * disabled) statement emanating from that location.
	 * </p>
	 * 
	 * <p>
	 * For a system function call: if the system function does not specify a
	 * depends_on contract clause, nothing is assumed about the call, i.e., it
	 * could depend on everything. Example: $wait. I think $wait should
	 * depends_on nothing. Nothing can disable it and it commutes with
	 * everything.
	 * </p>
	 * 
	 * @param pid
	 *            the ID of the process
	 * @return the set of objects, represented as a {@link SeqSet}
	 * @throws UnsatisfiablePathConditionException
	 *             if it is determined that the path condition of
	 *             {@link #theState} is unsatisfiable
	 */
	SeqSet computeDepends(int pid) throws UnsatisfiablePathConditionException {
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
	 * Computes an over-approximation of all objects reachable from a process in
	 * {@link #theState}. Consider the directed graph in which the nodes are the
	 * objects which exist at {@link #theState} and there is an edge from u to v
	 * if u contains a pointer which points to some part of v. The initial nodes
	 * are all variable instances in the dyscopes reachable from the process's
	 * call stack. The reachable dyscopes are those referenced by the call
	 * stack, the parents of those dyscopes, the parents of those, etc.
	 * 
	 * <p>
	 * Heaps are treated specially since a heap is technically a single
	 * variable, but is considered to represent a set of independent objects.
	 * </p>
	 * 
	 * <p>
	 * If no good over-approximation can be found, the universal set (containing
	 * all objects) is returned.
	 * </p>
	 * 
	 * @param pid
	 *            the ID of the process
	 * @return the set of reachable objects represented as a {@link SeqSet}
	 */
	SeqSet computeReach(int pid) {
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
			varSet.add(); // makes it the universal set
		}
		return varSet;
	}

	/**
	 * Does the specified process satisfy the simple invisibility criterion for
	 * the current deadlock predicate at state {@link #theState}?
	 * 
	 * <p>
	 * This means: assuming the current state s does not satisfy the "bad"
	 * property p, on any execution starting from s in which the executing
	 * processes do not access the dependencies of process {@code pid}, p will
	 * not hold. Example:
	 * 
	 * <pre>
	 * $input int X;
	 * p0 : { int x = X; $when x>0 ; }
	 * p1 : { 1; } // 1 is a no-op with guard true
	 * </pre>
	 * </p>
	 * 
	 * <p>
	 * Absolute deadlock is the bad property which holds when the enabling
	 * predicate (the disjunction of guards of statements from current
	 * locations) is not valid, i.e., there exists an assignment of values to
	 * symbolic constants such that the resulting concrete state is deadlocked.
	 * </p>
	 * 
	 * <p>
	 * In the state s where p0 is at the $when and p1 is at the no-op, absolute
	 * deadlock is false, as the enabled predicate is true, because of p1.
	 * However the simple invisibility criterion does not hold for pid=0. That
	 * is because after process 1 executes the no-op, absolute deadlock holds,
	 * as X>0 is not valid. Hence it would be wrong to choose {p0} as an ample
	 * set for s.
	 * </p>
	 * 
	 * <p>
	 * A sufficient condition for the criterion to hold is that the enabling
	 * predicate of the single process pid is valid. By the assumption, further
	 * execution by other processes can only weaken the enabling predicate of
	 * pid, so it must remain valid. That is because the other processes cannot
	 * affect any variable occurring in a guard of an enabled transition in pid.
	 * They may affect variables which enable currently disabled transitions in
	 * pid, but that can only weaken the enabling predicate.
	 * </p>
	 * 
	 * <p>
	 * For potential deadlock: the situation is similar except send transitions
	 * should be considered possibly blocking, so the disjunction of guards of
	 * all transitions departing from pid's location other than send transitions
	 * must be valid. A "send" is actually an "enqueue" operation in the comm
	 * library.
	 * </p>
	 * 
	 * <p>
	 * $wait: $wait is a system function which should be declared as
	 * "depends_on" nothing. It really is independent of any transition from
	 * another process. Its guard is "terminated(p)", where p is the $proc that
	 * is the argument to $wait. If that guard evaluates to true, it will remain
	 * true. So there is no need for any special treatment for $wait.
	 * </p>
	 * 
	 * TODO: make sure the contract for $wait is depends_on nothing.
	 * 
	 * <p>
	 * $spawn is normally always enabled, except for a process-bounded search
	 * (proc_bound > 0). For such a search, $spawn should never be considered
	 * independent. That is because a $spawn can disable another $spawn. Since
	 * this method should only be invoked for a set of transitions that are
	 * independent (of transitions in other processes), this case also requires
	 * no special handling.
	 * </p>
	 * 
	 * @param pid
	 *            process ID
	 * @return {@code true} if it is possible the process has a visible enabled
	 *         transition
	 * @throws UnsatisfiablePathConditionException
	 */
	protected boolean allInvisible(int pid)
			throws UnsatisfiablePathConditionException {
		// optimization: handle the common cases first.
		// TODO: perform this computation statically

		DeadlockKind kind = enabler.config.deadlock();

		if (kind == DeadlockKind.NONE)
			return true;

		Location location = theState.getProcessState(pid).getLocation();

		if (location.isBinaryBranching()
				|| location.isSwitchOrChooseWithDefault())
			return true;

		int numOutgoing = location.getNumOutgoing();

		if (numOutgoing == 0)
			return true;
		if (numOutgoing == 1 && isTrue(location.getOutgoing(0).guard()))
			return true;

		BooleanExpression enabled = universe.falseExpression();

		if (kind == DeadlockKind.ABSOLUTE)
			for (int i = 0; i < numOutgoing; i++)
				enabled = universe.or(enabled, getGuardValue(pid, i));
		else
			for (int i = 0; i < numOutgoing; i++)
				if (!isSend(theState, pid, location.getOutgoing(i)))
					enabled = universe.or(enabled, getGuardValue(pid, i));
		return reasoner.isValid(enabled);
	}

	/**
	 * Returns the set of transitions enabled in the specified process at state
	 * {@link #theState}. If the set has been previously computed, it is
	 * returned immediately from a cache, otherwise it is computed and cached.
	 * This is the method clients should use to get the set of transitions
	 * enabled in a process.
	 * 
	 * @param pid
	 *            the ID of the process
	 * @return the set of enabled transitions, represented as an array
	 * @throws UnsatisfiablePathConditionException
	 *             if it is determined that the path condition of
	 *             {@link #theState} is unsatisfiable
	 */
	protected Transition[] enabledTransitionsInProcess(int pid)
			throws UnsatisfiablePathConditionException {
		Transition[] result = enabledTransitions[pid];

		if (result == null) {
			List<Transition> list = new LinkedList<>();

			computeEnabledInProcess(list, pid);
			enabledTransitions[pid] = result = list
					.toArray(new Transition[list.size()]);
		}
		return result;
	}

	/**
	 * Computes an ample set for state {@link #theState}. This may be the full
	 * set (consisting of all enabled transitions). This method may set
	 * {@link #full} to {@code true}, indicating that the full set was used.
	 * 
	 * @throws UnsatisfiablePathConditionException
	 *             if it is discovered that the path condition of
	 *             {@link #theState} is unsatisfiable
	 */
	protected void computeAmpleSet()
			throws UnsatisfiablePathConditionException {
		StrongConnect sc = new StrongConnect(this);
		LinkedList<Integer> amplePids = sc.findAmple();
		int size = 0, c = 0;

		if (amplePids == null) {
			full = true;
			for (int i = 0; i < nprocs; i++)
				size += enabledTransitionsInProcess(i).length;
			ampleSet = new Transition[size];
			for (int i = 0; i < nprocs; i++)
				for (Transition tran : enabledTransitions[i])
					ampleSet[c++] = tran;
		} else {
			full = false;
			for (int i : amplePids)
				size += enabledTransitionsInProcess(i).length;
			ampleSet = new Transition[size];
			for (int i : amplePids)
				for (Transition tran : enabledTransitions[i])
					ampleSet[c++] = tran;
		}
	}

	/**
	 * Returns the ample set for {@link #theState}. This method should be called
	 * only after {@link #computeAmpleSet()} has been called.
	 * 
	 * @return the ample set
	 */
	protected Transition[] ampleSet() {
		return ampleSet;
	}

	/**
	 * Returns the full bit. This method should be called only after
	 * {@link #computeAmpleSet()} has been called. If this method returns
	 * {@code true}, then the ample set consists of all enabled transitions at
	 * {@link #theState}.
	 * 
	 * @return the full bit
	 */
	protected boolean isFull() {
		return full;
	}
}

/**
 * An exception indicating that no good approximation of a set of dependencies
 * or reachable objects can be determined.
 * 
 * @author siegel
 */
class NoReductionException extends Exception {
	private static final long serialVersionUID = 1L;
}
