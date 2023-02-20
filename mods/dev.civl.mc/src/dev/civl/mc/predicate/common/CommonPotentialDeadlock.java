/**
 * 
 */
package dev.civl.mc.predicate.common;

import static dev.civl.sarl.IF.ValidityResult.ResultType.MAYBE;
import static dev.civl.sarl.IF.ValidityResult.ResultType.YES;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.library.comm.LibcommEnabler;
import dev.civl.mc.log.IF.CIVLExecutionException;
import dev.civl.mc.model.IF.CIVLException.Certainty;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.statement.Statement.StatementKind;
import dev.civl.mc.predicate.IF.PotentialDeadlock;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryLoaderException;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.ProcessState;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * An potential deadlock occurs if all of the following hold:
 * 
 * <ol>
 * <li>not every process has terminated</li>
 * <li>the only enabled transitions are sends for which there is no matching
 * receive</li>
 * </ol>
 * 
 * @author Ziqing Luo
 */
public class CommonPotentialDeadlock extends CommonCIVLStatePredicate
		implements
			PotentialDeadlock {

	private Enabler enabler;

	private LibcommEnabler libEnabler;

	private BooleanExpression falseExpr;

	/**
	 * The symbolic analyzer for operations on symbolic expressions and states,
	 * used in this class for printing states.
	 */
	@SuppressWarnings("unused")
	private SymbolicAnalyzer symbolicAnalyzer;

	/**
	 * 
	 * @param symbolicUniverse
	 *            The symbolic universe for creating symbolic expressions.
	 * @param enabler
	 *            The enabler of the system.
	 * @param symbolicAnalyzer
	 *            The symbolic analyzer used in the system.
	 * @throws LibraryLoaderException
	 */
	public CommonPotentialDeadlock(SymbolicUniverse symbolicUniverse,
			Enabler enabler, LibraryEnablerLoader loader, Evaluator evaluator,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer) {
		this.universe = symbolicUniverse;
		this.falseExpr = symbolicUniverse.falseExpression();
		this.enabler = enabler;
		this.symbolicAnalyzer = symbolicAnalyzer;
		try {
			this.libEnabler = (LibcommEnabler) loader.getLibraryEnabler("comm",
					enabler, evaluator, modelFactory, symbolicUtil,
					symbolicAnalyzer);
		} catch (LibraryLoaderException e) {
			throw new CIVLInternalException(
					"PotentialDeadlock loads LibcommEnabler failed",
					(CIVLSource) null);
		}
	}

	/**
	 * Precondition: already know that deadlock is a possibility in this state,
	 * i.e., we cannot show the enabled predicate is valid.
	 * 
	 * @param state
	 *            a state that might have a deadlock
	 * @return a String with a detailed explanation including the locatin of
	 *         each process in the state
	 * @throws UnsatisfiablePathConditionException
	 */
	private String explanationWork(State state)
			throws UnsatisfiablePathConditionException {
		StringBuffer explanation = new StringBuffer();
		boolean first = true;

		for (ProcessState p : state.getProcessStates()) {
			if (p == null)
				continue;

			Location location = null;
			BooleanExpression predicate = null;
			// wait on unterminated function, no outgoing edges:
			// String nonGuardExplanation = null;
			int pid = p.getPid();

			if (first)
				first = false;
			else
				explanation.append("\n");
			if (!p.hasEmptyStack())
				location = p.getLocation();
			explanation.append("ProcessState " + pid + ": ");
			if (location == null) {
				explanation.append("terminated");
			} else {
				CIVLSource source = location.getSource();

				explanation.append("at location " + location.id() + ", ");
				if (source != null)
					explanation.append(source.getSummary(false));
				for (Statement statement : location.outgoing()) {
					BooleanExpression guard;

					guard = enabler.getGuard(statement, pid, state);

					// if (statement instanceof WaitStatement) {
					// // TODO: Check that the guard is actually true, but it
					// // should be.
					// WaitStatement wait = (WaitStatement) statement;
					// Expression waitExpr = wait.process();
					// SymbolicExpression joinProcess = evaluator.evaluate(
					// state, pid, waitExpr).value;
					// int pidValue = modelFactory.getProcessId(
					// waitExpr.getSource(), joinProcess);
					// nonGuardExplanation = "\n Waiting on process "
					// + pidValue;
					// }
					if (predicate == null) {
						predicate = guard;
					} else {
						predicate = universe.or(predicate, guard);
					}
				}
				if (predicate == null) {
					explanation.append("No outgoing transitions.");
					// } else if (nonGuardExplanation != null) {
					// explanation.append(nonGuardExplanation);
				} else {
					explanation.append("\n  Enabling predicate: " + predicate);
				}
			}
		}
		return explanation.toString();
	}

	@Override
	public String explanation() {
		if (violation == null)
			return "No any kind of deadlock";
		return violation.getMessage();
	}

	private boolean allTerminated(State state) {
		for (ProcessState p : state.getProcessStates()) {
			if (p != null && !p.hasEmptyStack())
				return false;
		}
		return true;
	}

	private boolean holdsAtWork(State state)
			throws UnsatisfiablePathConditionException {
		if (allTerminated(state)) // all processes terminated: no deadlock.
			return false;

		BooleanExpression predicate = falseExpr;
		Reasoner reasoner = universe.reasoner(state.getPathCondition(universe));
		int firstPid = -1;
		CIVLSource firstSource = null; // location of first non-term proc

		for (ProcessState p : state.getProcessStates()) {
			if (p == null || p.hasEmptyStack()) // p has terminated
				continue;

			int pid = p.getPid();
			Location location = p.getLocation();

			if (firstPid == -1) {
				firstPid = pid;
				firstSource = location.getSource();
			}
			for (Statement s : location.outgoing()) {
				BooleanExpression guard = enabler.getGuard(s, pid, state);

				if (guard.isFalse())
					continue;
				if (s.statementKind().equals(StatementKind.CALL_OR_SPAWN)) {
					CallOrSpawnStatement call = (CallOrSpawnStatement) s;

					// TODO: function pointer makes call.function() == null
					if (call.function() != null)
						if (call.function().name().name()
								.equals("$comm_enqueue")) {
							String process = p.name();
							BooleanExpression claim;

							claim = libEnabler.hasMatchedDequeue(state, pid,
									process, call, true);
							// TODO change to andTo
							guard = universe.and(guard, claim);
							predicate = universe.or(predicate, claim);
						}
				}
				predicate = universe.or(predicate, guard);
				if (predicate.isTrue())
					return false;
			} // end loop over all outgoing statements
		} // end loop over all processes
		if (firstPid == -1)
			throw new CIVLInternalException("unreachable", firstSource);

		ResultType enabled = reasoner.valid(predicate).getResultType();

		if (enabled == YES)
			return false;
		else {
			String message;
			Certainty certainty;

			if (enabled == MAYBE) {
				certainty = Certainty.MAYBE;
				message = "Cannot prove that potential deadlock is impossible:\n";
			} else {
				certainty = Certainty.PROVEABLE;
				message = "A potential deadlock is possible:\n";
			}
			message += "  Path condition: " + state.getPathCondition(universe)
					+ "\n  Enabling predicate: " + predicate + "\n";
			message += explanationWork(state);
			violation = new CIVLExecutionException(CIVLProperty.DEADLOCK,
					certainty, state.getProcessState(firstPid).name(), message,
					state, firstPid, firstSource);
			return true;
		}
	}

	@Override
	public boolean holdsAt(State state) {
		try {
			return holdsAtWork(state);
		} catch (UnsatisfiablePathConditionException e) {
			return false;
		}
	}

	public String toString() {
		return "PotentialDeadlock";
	}

}
