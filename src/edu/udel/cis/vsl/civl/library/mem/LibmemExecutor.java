package edu.udel.cis.vsl.civl.library.mem;

import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.civl.config.IF.CIVLConfiguration;
import edu.udel.cis.vsl.civl.dynamic.IF.DynamicWriteSet;
import edu.udel.cis.vsl.civl.dynamic.IF.SymbolicUtility;
import edu.udel.cis.vsl.civl.library.common.BaseLibraryExecutor;
import edu.udel.cis.vsl.civl.library.mem.MemoryLocationMap.MemLocMapEntry;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.CIVLSource;
import edu.udel.cis.vsl.civl.model.IF.ModelFactory;
import edu.udel.cis.vsl.civl.model.IF.expression.Expression;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLMemType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLMemType.MemoryLocationReference;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLStateType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLType.TypeKind;
import edu.udel.cis.vsl.civl.model.IF.variable.Variable;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluation;
import edu.udel.cis.vsl.civl.semantics.IF.Executor;
import edu.udel.cis.vsl.civl.semantics.IF.LibraryEvaluatorLoader;
import edu.udel.cis.vsl.civl.semantics.IF.LibraryExecutor;
import edu.udel.cis.vsl.civl.semantics.IF.LibraryExecutorLoader;
import edu.udel.cis.vsl.civl.semantics.IF.SymbolicAnalyzer;
import edu.udel.cis.vsl.civl.state.IF.State;
import edu.udel.cis.vsl.civl.state.IF.StateValueHelper;
import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.sarl.IF.UnaryOperator;
import edu.udel.cis.vsl.sarl.IF.expr.BooleanExpression;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;
import edu.udel.cis.vsl.sarl.IF.expr.valueSetReference.ValueSetReference;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;

public class LibmemExecutor extends BaseLibraryExecutor
		implements
			LibraryExecutor {

	/**
	 * A unary operator that collects the references in the "memValue", which
	 * are referring to non-alive objects:
	 */
	private UnaryOperator<SymbolicExpression> collector;

	public LibmemExecutor(String name, Executor primaryExecutor,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryExecutorLoader libExecutorLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryExecutor, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libExecutorLoader,
				libEvaluatorLoader);
		collector = typeFactory.civlMemType().memValueCollector(universe,
				stateFactory.nullScopeValue());
	}

	@Override
	protected Evaluation executeValue(State state, int pid, String process,
			CIVLSource source, String functionName, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		Evaluation callEval = null;

		switch (functionName) {
			case "$write_set_push" :
				callEval = executeWriteSetPush(state, pid, arguments,
						argumentValues, source);
				break;
			case "$write_set_pop" :
				callEval = executeWriteSetPop(state, pid, arguments,
						argumentValues, source);
				break;
			case "$write_set_peek" :
				callEval = executeWriteSetPeek(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_contains" :
				callEval = executeMemContains(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_union" :
				callEval = executeMemUnion(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_union_widening" :
				callEval = executeMemUnionWidening(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_havoc" :
				callEval = executeMemHavoc(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_assign_from" :
				callEval = executeMemAssignFrom(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_unary_widening" :
				callEval = executeMemUnaryWidening(state, pid, arguments,
						argumentValues, source);
				break;
			case "$mem_empty" :
				callEval = executeMemNew(state, pid, arguments, argumentValues,
						source);
				break;
			case "$mem_equals" :
				callEval = executeMemEquals(state, pid, arguments,
						argumentValues, source);
				break;
			default :
				throw new CIVLInternalException(
						"Unknown mem function: " + functionName, source);
		}
		return callEval;
	}

	/**
	 * <p>
	 * Executing the system function:<code>$write_set_push()</code>. <br>
	 * <br>
	 * 
	 * Push an empty write set onto write set stack associated with the calling
	 * process.
	 * 
	 * </p>
	 * 
	 * @param state
	 *            The current state.
	 * @param pid
	 *            The ID of the process that the function call belongs to.
	 * @param arguments
	 *            The static representation of the arguments of the function
	 *            call.
	 * @param argumentValues
	 *            The dynamic representation of the arguments of the function
	 *            call.
	 * @param source
	 *            The {@link CIVLSource} associates to the function call.
	 * @return The new state after executing the function call.
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation executeWriteSetPush(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) {
		state = stateFactory.pushEmptyWrite(state, pid);
		return new Evaluation(state, null);
	}

	/**
	 * <p>
	 * Executing the system function:<code>$write_set_pop($mem * m)</code>. <br>
	 * <br>
	 * 
	 * Pop a write set w out of the write set stack associated with the calling
	 * process. Assign write set w' to the object refered by the given reference
	 * m, where w' is a subset of w. <code>w - w'</code> is a set of unreachable
	 * memory locaiton references.
	 * 
	 * </p>
	 * 
	 * @param state
	 *            The current state.
	 * @param pid
	 *            The ID of the process that the function call belongs to.
	 * @param arguments
	 *            The static representation of the arguments of the function
	 *            call.
	 * @param argumentValues
	 *            The dynamic representation of the arguments of the function
	 *            call.
	 * @param source
	 *            The {@link CIVLSource} associates to the function call.
	 * @return The new state after executing the function call.
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation executeWriteSetPop(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression memValue;
		DynamicWriteSet writeSet = stateFactory.peekWriteSet(state, pid);

		state = stateFactory.popWriteSet(state, pid);
		memValue = writeSet.getMemValue();
		return new Evaluation(state, memValue);
	}

	private Evaluation executeWriteSetPeek(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression memValue;
		DynamicWriteSet writeSet = stateFactory.peekWriteSet(state, pid);

		memValue = writeSet.getMemValue();
		return new Evaluation(state, memValue);
	}

	private Evaluation executeMemContains(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression mem0 = collector.apply(argumentValues[0]);
		SymbolicExpression mem1 = collector.apply(argumentValues[1]);
		MemoryLocationMap set0 = memValue2MemoryLocationSet(mem0);
		MemoryLocationMap set1 = memValue2MemoryLocationSet(mem1);
		BooleanExpression result = universe.trueExpression();

		// for each "sub" value set template, there must exist one in "super"
		// mem value that contains it, otherwise false...
		for (MemLocMapEntry entry : set1.entrySet()) {
			SymbolicExpression suuper;

			suuper = set0.get(entry.vid(), entry.heapID(), entry.mallocID(),
					entry.scopeValue());
			if (suuper == null) {
				result = universe.falseExpression();
				break;
			} else
				result = universe.and(result, universe.valueSetContains(suuper,
						entry.valueSetTemplate()));
		}
		return new Evaluation(state, result);
	}

	private Evaluation executeMemUnion(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression mem0 = collector.apply(argumentValues[0]);
		SymbolicExpression mem1 = collector.apply(argumentValues[1]);
		MemoryLocationMap set0 = memValue2MemoryLocationSet(mem0);
		MemoryLocationMap set1 = memValue2MemoryLocationSet(mem1);
		CIVLMemType memType = typeFactory.civlMemType();

		// for each "sub" value set template, there must exist one in "super"
		// mem value that contains it, otherwise false...
		for (MemLocMapEntry entry : set1.entrySet()) {
			SymbolicExpression vst;

			vst = set0.get(entry.vid(), entry.heapID(), entry.mallocID(),
					entry.scopeValue());
			vst = vst == null
					? entry.valueSetTemplate()
					: universe.valueSetUnion(vst, entry.valueSetTemplate());
			set0.put(entry.vid(), entry.heapID(), entry.mallocID(),
					entry.scopeValue(), vst);
		}

		List<SymbolicExpression[]> results = new LinkedList<>();

		for (MemLocMapEntry entry : set0.entrySet())
			results.add(new SymbolicExpression[]{universe.integer(entry.vid()),
					universe.integer(entry.heapID()),
					universe.integer(entry.mallocID()), entry.scopeValue(),
					entry.valueSetTemplate()});
		return new Evaluation(state,
				memType.memValueCreator(universe).apply(results));
	}

	private Evaluation executeMemEquals(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression mem0 = collector.apply(argumentValues[0]);
		SymbolicExpression mem1 = collector.apply(argumentValues[1]);

		// TODO: implement equals for ValueSetReference
		return new Evaluation(state, universe.equals(mem0, mem1));
	}

	private Evaluation executeMemUnionWidening(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression mem0 = collector.apply(argumentValues[0]);
		SymbolicExpression mem1 = collector.apply(argumentValues[1]);
		MemoryLocationMap set0 = memValue2MemoryLocationSet(mem0);
		MemoryLocationMap set1 = memValue2MemoryLocationSet(mem1);
		CIVLMemType memType = typeFactory.civlMemType();

		// for each "sub" value set template, there must exist one in "super"
		// mem value that contains it, otherwise false...
		for (MemLocMapEntry entry : set1.entrySet()) {
			SymbolicExpression vst;

			vst = set0.get(entry.vid(), entry.heapID(), entry.mallocID(),
					entry.scopeValue());
			vst = vst == null
					? entry.valueSetTemplate()
					: universe.valueSetUnion(vst, entry.valueSetTemplate());
			set0.put(entry.vid(), entry.heapID(), entry.mallocID(),
					entry.scopeValue(), vst);
		}

		List<SymbolicExpression[]> results = new LinkedList<>();

		for (MemLocMapEntry entry : set0.entrySet())
			results.add(new SymbolicExpression[]{universe.integer(entry.vid()),
					universe.integer(entry.heapID()),
					universe.integer(entry.mallocID()), entry.scopeValue(),
					universe.valueSetWidening(entry.valueSetTemplate())});
		return new Evaluation(state,
				memType.memValueCreator(universe).apply(results));
	}

	private Evaluation executeMemHavoc(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression memValue = collector.apply(argumentValues[0]);
		Iterable<MemoryLocationReference> memRefs = typeFactory.civlMemType()
				.memValueIterator().apply(memValue);

		for (MemoryLocationReference memRef : memRefs)
			state = havoc(state, pid, memRef, source);
		return new Evaluation(state, universe.nullExpression());
	}

	/*
	 * 
	 * Description: assigns each memory location in "m" its value that is
	 * evaluated in state "s"
	 * 
	 * $atomic_f $system void $mem_assign_from($state s, $mem m);
	 */
	private Evaluation executeMemAssignFrom(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression memValue = collector.apply(argumentValues[1]);
		StateValueHelper stateValHelper = stateFactory.stateValueHelper();
		UnaryOperator<SymbolicExpression> scopeValueSubstituter = stateValHelper
				.scopeSubstituterForReferredState(state, argumentValues[0]);
		CIVLStateType stateType = typeFactory.stateType();
		State preState = stateFactory.getStateByReference(
				stateType.selectStateKey(universe, argumentValues[0]));
		Iterable<MemoryLocationReference> memRefs = typeFactory.civlMemType()
				.memValueIterator().apply(memValue);

		for (MemoryLocationReference memRef : memRefs) {
			SymbolicExpression oldRootValue = getRootValue(memRef, preState,
					scopeValueSubstituter, pid);
			SymbolicExpression rootPointer = getRootPointer(memRef);

			state = primaryExecutor.assign2(source, state, pid, rootPointer,
					oldRootValue, memRef.valueSetTemplate());
		}
		return new Evaluation(state, universe.nullExpression());
	}

	/*
	 * Description: apply a "unary widening" operator to each memory location in
	 * the "m". The result of the operation to a memory location 'a' will be the
	 * memory location of a program variable or a memory heap object that
	 * contains 'a'.
	 * 
	 * $atomic_f $system $mem $mem_unary_widening($mem m);
	 */
	private Evaluation executeMemUnaryWidening(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		SymbolicExpression memValue = collector.apply(argumentValues[0]);
		Iterable<MemoryLocationReference> memRefs = typeFactory.civlMemType()
				.memValueIterator().apply(memValue);
		List<SymbolicExpression[]> components = new LinkedList<>();

		for (MemoryLocationReference memRef : memRefs) {
			SymbolicExpression vid, heapId, mallocId;
			SymbolicType rootValueType = getRootValue(memRef, state, null, pid)
					.type();
			SymbolicExpression rootTemplate;

			if (rootValueType == null) {
				Variable var = state
						.getDyscope(
								stateFactory.getDyscopeId(memRef.scopeValue()))
						.lexicalScope().variable(memRef.vid());

				assert var.type().typeKind() == TypeKind.PRIMITIVE;
				rootValueType = var.type().getDynamicType(universe);
			}
			rootTemplate = universe.valueSetTemplate(rootValueType,
					new ValueSetReference[]{universe.vsIdentityReference()});
			vid = universe.integer(memRef.vid());
			heapId = universe.integer(memRef.heapID());
			mallocId = universe.integer(memRef.mallocID());
			components.add(new SymbolicExpression[]{vid, heapId, mallocId,
					memRef.scopeValue(), rootTemplate});
		}

		SymbolicExpression result = typeFactory.civlMemType()
				.memValueCreator(universe).apply(components);

		return new Evaluation(state, result);
	}

	/**
	 * <p>
	 * Havoc memory locations that are referred by "memRef".
	 * </p>
	 * 
	 * @param state
	 *            the state where the havoc operation will happen
	 * @param pid
	 *            the PID of the running process
	 * @param memRef
	 *            a {@link MemoryLocationReference}
	 * @param source
	 *            the CIVLSource that is related to this operation
	 * @return the state after havoc
	 * @throws UnsatisfiablePathConditionException
	 */
	private State havoc(State state, int pid, MemoryLocationReference memRef,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		int sid = stateFactory.getDyscopeId(memRef.scopeValue());
		SymbolicExpression oldValue = getRootValue(memRef, state, null, pid);
		SymbolicExpression rootPointer = getRootPointer(memRef);
		SymbolicType oldValueType = oldValue.type();
		Evaluation eval;

		// if the referred variable was uninitialized and has a
		// primitive type, its value may be NULL hence type cannot be
		// obtained from its value:
		if (oldValueType == null) {
			Variable var = state.getDyscope(sid).lexicalScope()
					.variable(memRef.vid());

			assert var.type().typeKind() == TypeKind.PRIMITIVE;
			oldValueType = var.type().getDynamicType(universe);
		}
		eval = evaluator.havoc(state, oldValueType);
		state = primaryExecutor.assign2(source, eval.state, pid, rootPointer,
				eval.value, memRef.valueSetTemplate());
		return state;
	}

	private Evaluation executeMemNew(State state, int pid,
			Expression[] arguments, SymbolicExpression[] argumentValues,
			CIVLSource source) throws UnsatisfiablePathConditionException {
		CIVLMemType memType = typeFactory.civlMemType();
		SymbolicExpression empty = memType.memValueCreator(universe)
				.apply(new LinkedList<>());

		return new Evaluation(state, empty);
	}

	/**
	 * Create a {@link MemoryLocationMap} for memory location references in the
	 * given "memValue"
	 */
	private MemoryLocationMap memValue2MemoryLocationSet(
			SymbolicExpression memValue) {
		MemoryLocationMap set = new MemoryLocationMap();
		CIVLMemType memType = typeFactory.civlMemType();

		for (CIVLMemType.MemoryLocationReference memLocRef : memType
				.memValueIterator().apply(memValue))
			set.put(memLocRef.vid(), memLocRef.heapID(), memLocRef.mallocID(),
					memLocRef.scopeValue(), memLocRef.valueSetTemplate());
		return set;
	}

	/**
	 * @param memRef
	 *            a {@link MemoryLocationReference}
	 * @param state
	 *            a state where all memory locations referred by the "memRef"
	 *            are alive
	 * @param scopeValueSubstituter
	 *            a scope value substituter which can change the scope value in
	 *            "memRef" to the corresponding scope value in the given "state"
	 * @param pid
	 *            the PID of the running process
	 * 
	 * @return the value in the given state of the variable or the memory heap
	 *         object that contains all the memory locations referred by the
	 *         given "memRef"
	 */
	private SymbolicExpression getRootValue(MemoryLocationReference memRef,
			State state,
			UnaryOperator<SymbolicExpression> scopeValueSubstituter, int pid) {
		SymbolicExpression scopeVal = memRef.scopeValue();

		if (scopeValueSubstituter != null)
			scopeVal = scopeValueSubstituter.apply(scopeVal);

		int sid = stateFactory.getDyscopeId(scopeVal);
		int vid = memRef.vid();
		SymbolicExpression rootValue = state.getVariableValue(sid, vid);

		if (vid == 0) {
			rootValue = universe.tupleRead(rootValue,
					universe.intObject(memRef.heapID()));
			rootValue = universe.arrayRead(rootValue,
					universe.integer(memRef.mallocID()));
		}
		return rootValue;
	}

	/**
	 * @param memRef
	 *            a {@link MemoryLocationReference}
	 * @return the pointer to the variable or the memory heap object that
	 *         contains the memory locations referred by the given "memRef"
	 */
	private SymbolicExpression getRootPointer(MemoryLocationReference memRef) {
		SymbolicExpression scopeVal = memRef.scopeValue();
		int vid = memRef.vid(), sid = stateFactory.getDyscopeId(scopeVal);

		if (vid == 0)
			// TODO: here the code couples with the definition of the heap
			// type, better there is better way to hide heap structure.
			return symbolicUtil
					.makePointer(sid, memRef.vid(),
							universe.arrayElementReference(
									universe.tupleComponentReference(
											universe.identityReference(),
											universe.intObject(
													memRef.heapID())),
									universe.integer(memRef.mallocID())));
		else
			return symbolicUtil.makePointer(sid, memRef.vid(),
					universe.identityReference());
	}
}
