package dev.civl.gmc.smc;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import dev.civl.gmc.TraceStepIF;
import dev.civl.gmc.seq.StateManager;

/**
 * The implementation of the interface {@link StateManager} used by SMC.
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class SimpleStateManager extends StateManager<Integer, String> {
	/**
	 * The {@link MatrixDirectedGraph} represents the state-transition map.
	 */
	MatrixDirectedGraph graph;

	public SimpleStateManager(MatrixDirectedGraph graph) {
		this.graph = graph;
	}

	@Override
	public TraceStepIF<Integer> nextState(Integer state, String transition) {
		return new TraceStep(transition, graph.getDestState(state, transition));
	}
	
	@Override
	public int getId(Integer normalizedState) {
		return normalizedState;
	}
	
	@Override
	public void normalize(TraceStepIF<Integer> traceStep) {
		// Do nothing
	}

	@Override
	public void printStateShort(PrintStream out, Integer state) {
		System.out.println("S:<" + state + ">");
	}

	@Override
	public void printStateLong(PrintStream out, Integer state) {
		System.out.println("State<" + state + ">");
	}

	@Override
	public void printTransitionShort(PrintStream out, String transition) {
		System.out.println("T:'" + transition + "'");
	}

	@Override
	public void printTransitionLong(PrintStream out, String transition) {
		System.out.println("Transition'" + transition + "'");

	}

	@Override
	public void printAllStatesShort(PrintStream out) {
		System.err.println("smc.StateManager.printAllStatesShort method"
				+ " is not implemented yet.");

	}

	@Override
	public void printAllStatesLong(PrintStream out) {
		System.err.println("smc.StateManager.printAllStatesLong method"
				+ " is not implemented yet.");

	}

	@Override
	public void printTraceStep(Integer sourceState,
			TraceStepIF<Integer> traceStep) {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("SourceState<");
		sBuilder.append(sourceState);
		sBuilder.append(">\n TraceStep:");
		sBuilder.append(traceStep);
		System.out.println(sBuilder);
	}

	@Override
	public void printTraceStepFinalState(Integer finalState, int normalizedID) {
		System.out.println(
				"[" + normalizedID + "]FinalState<" + finalState + ">");
	}

	@Override
	public int getPid(String transition) {
		return 0;
	}
	public void debug(Integer state, List<Integer> backtrack) {}
	// TODO: Added these to appease the compiler but are almost surely not right.
	@Override
	public Set<Integer> getEnabledProcesses(Integer state) {
		return null;
	}
	@Override
	public Collection<String> getTransitions(Integer state, int pid) {
		return null;
	}

}
