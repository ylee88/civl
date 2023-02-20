package dev.civl.mc.library.collate;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.mc.model.IF.type.CIVLStateType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;

/**
 * This class reserves the structure information for datatypes in the collate
 * library
 * 
 * @author ziqing
 *
 */
public class LibcollateConstantsAndUtils {
	/**
	 * Constants for field indices of structs defined in collate library.
	 * Constants are named as TYPENAME_FILEDNAME (all in capital cases).
	 */
	static public final int GCOLLATOR_NPROCS = 0;
	static public final int GCOLLATOR_PROCS = 1;
	static public final int GCOLLATOR_QUEUE_LENGTH = 2;
	static public final int GCOLLATOR_QUEUE = 3;
	static public final int COLLATOR_PLACE = 0;
	static public final int COLLATOR_GCOLLATOR = 1;
	static public final int GCOLLATE_STATE_STATUS = 0;
	static public final int GCOLLATE_STATE_STATE = 1;
	static public final int COLLATE_STATE_PLACE = 0;
	static public final int COLLATE_STATE_GSTATE = 1;

	static public CIVLType gcollate_state(CIVLTypeFactory typeFactory) {
		return typeFactory.systemType(ModelConfiguration.GCOLLATOR_TYPE);
	}

	static public CIVLType collate_state(CIVLTypeFactory typeFactory) {
		return typeFactory.systemType(ModelConfiguration.COLLATOR_TYPE);
	}

	/**
	 * Extract the object of $gcollate_state type from an object of
	 * $collate_state type. $collate_state is designed to be the process-local
	 * handle of $gcollate_state.
	 * 
	 * @param evaluator
	 *            a reference to {@link Evaluator}
	 * @param universe
	 *            a reference to {@link SymbolicUniverse}
	 * @param state
	 *            the current {@link State}
	 * @param pid
	 *            the PID of the process who owns the $collate_state object
	 * @param collateState
	 *            the value of the $collate_state object
	 * @param collateSource
	 *            the {@link CIVLSource} that is associated with the
	 *            $collate_state object
	 * @return an {@link Evaluation} for the extracted $gcollate_state object
	 * @throws UnsatisfiablePathConditionException
	 *             when path condition becomes UNSAT during dereferencing
	 *             pointers in the $collate_state objects
	 */
	static public Evaluation getGcollateStateFromCollateState(
			Evaluator evaluator, SymbolicUniverse universe, State state,
			int pid, SymbolicExpression collateState, CIVLSource collateSource)
			throws UnsatisfiablePathConditionException {
		String process = state.getProcessState(pid).name();
		SymbolicExpression gcollateStateHandle = universe.tupleRead(
				collateState, universe.intObject(COLLATE_STATE_GSTATE));

		return evaluator.dereference(collateSource, state, pid, process,
				gcollateStateHandle, false, true);
	}

	/**
	 * Extract the integral "place" of the given $collate_state object in its
	 * corresponding $gcollate_state object. $collate_state is designed to be
	 * the process-local handle of $gcollate_state and occupies a "place" in
	 * $gcollate_state.
	 * 
	 * @param universe
	 *            a reference to {@link SymbolicUniverse}
	 * @param collateState
	 *            the value of an object of $collate_state type
	 * @return an integer representing the "place"
	 */
	static public int getPlaceFromCollateState(SymbolicUniverse universe,
			SymbolicExpression collateState) {
		IntegerNumber placeNum = (IntegerNumber) universe.extractNumber(
				(NumericExpression) universe.tupleRead(collateState,
						universe.intObject(COLLATE_STATE_PLACE)));

		assert placeNum != null : "the place where the given collate state "
				+ "belongs to should always be concrete";
		return placeNum.intValue();
	}

	/**
	 * Extract the object of {@link CIVLStateType} from a $gcollate_state object
	 * 
	 * @param universe
	 *            a reference to {@link SymbolicUniverse}
	 * @param gcollateState
	 *            the value of an object of $gcollate_state type
	 * @return the value of an object of {@link CIVLStateType}
	 */
	static public SymbolicExpression getStateFromGcollateState(
			SymbolicUniverse universe, SymbolicExpression gcollateState) {
		return universe.tupleRead(gcollateState,
				universe.intObject(GCOLLATE_STATE_STATE));
	}
}

