package dev.civl.mc.library.common;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.Semantics;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.MemoryUnitFactory;
import dev.civl.mc.state.IF.MemoryUnitSet;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.StateFactory;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * This class provides the common data and operations of library enablers.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public abstract class BaseLibraryEnabler extends LibraryComponent
		implements
			LibraryEnabler {

	/* *************************** Instance Fields ************************* */

	/**
	 * The enabler for normal CIVL execution.
	 */
	protected Enabler primaryEnabler;

	/**
	 * The state factory for state-related computation.
	 */
	protected StateFactory stateFactory;

	protected LibraryEnablerLoader libEnablerLoader;

	protected MemoryUnitFactory memUnitFactory;

	/* ***************************** Constructor *************************** */

	/**
	 * Creates a new instance of library enabler.
	 * 
	 * @param primaryEnabler
	 *            The enabler for normal CIVL execution.
	 * @param modelFactory
	 *            The model factory of the system.
	 * @param symbolicUtil
	 *            The symbolic utility used in the system.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 */
	public BaseLibraryEnabler(String name, Enabler primaryEnabler,
			Evaluator evaluator, ModelFactory modelFactory,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer,
			CIVLConfiguration civlConfig, LibraryEnablerLoader libEnablerLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, evaluator.universe(), symbolicUtil, symbolicAnalyzer,
				civlConfig, libEvaluatorLoader, modelFactory,
				evaluator.errorLogger(), evaluator);
		this.primaryEnabler = primaryEnabler;
		this.stateFactory = evaluator.stateFactory();
		this.memUnitFactory = stateFactory.memUnitFactory();

	}

	/* ********************* Methods from LibraryEnabler ******************* */

	@Override
	public BitSet ampleSet(State state, int pid, CallOrSpawnStatement statement,
			MemoryUnitSet[] setsReachableRead,
			MemoryUnitSet[] setsReachableWrite)
			throws UnsatisfiablePathConditionException {
		return new BitSet(0);
	}

	@Override
	public List<Transition> enabledTransitions(State state,
			CallOrSpawnStatement call, BooleanExpression clause, int pid)
			throws UnsatisfiablePathConditionException {
		List<Transition> localTransitions = new LinkedList<>();

		localTransitions.add(Semantics.newTransition(pid, clause, call));
		return localTransitions;
	}

	/**
	 * Computes the ample set by analyzing the given handle object for the
	 * statement.
	 * 
	 * @param state
	 *            The current state
	 * @param pid
	 *            The pid of the process
	 * @param handleObj
	 *            The expression of the given handle object
	 * @param handleObjValue
	 *            The symbolic expression of the given handle object
	 * @param reachableMemUnitsMap
	 *            The map contains all reachable memory units of all processes
	 * @return
	 */
	protected BitSet computeAmpleSetByHandleObject(State state, int pid,
			Expression handleObj, SymbolicExpression handleObjValue,
			MemoryUnitSet[] setsReachableRead,
			MemoryUnitSet[] setsReachableWrite) {
		MemoryUnitSet handleObjMemUnits = memUnitFactory.newMemoryUnitSet();
		BitSet ampleSet = new BitSet();
		int numProcs = state.numProcs();
		CIVLSource source = handleObj.getSource();

		handleObjMemUnits.add(memUnitFactory.newMemoryUnit(
				stateFactory.getDyscopeId(
						symbolicUtil.getScopeValue(handleObjValue)),
				symbolicUtil.getVariableId(source, handleObjValue),
				symbolicUtil.getSymRef(handleObjValue)));
		for (int otherPid = 0; otherPid < numProcs; otherPid++) {
			if (otherPid == pid || ampleSet.get(otherPid))
				continue;
			else {
				MemoryUnitSet setRead = setsReachableRead[otherPid];
				MemoryUnitSet setWrite = setsReachableWrite[otherPid];

				if (memUnitFactory.isJoint(handleObjMemUnits, setWrite)
						|| memUnitFactory.isJoint(handleObjMemUnits, setRead))
					ampleSet.set(otherPid);
			}
		}
		return ampleSet;
	}
}
