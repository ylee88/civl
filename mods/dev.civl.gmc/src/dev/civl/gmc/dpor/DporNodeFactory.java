package dev.civl.gmc.dpor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.civl.gmc.GetIdFunction;
import dev.civl.gmc.TraceStepIF;
import dev.civl.gmc.seq.SequentialNode;
import dev.civl.gmc.seq.StackEntry;
import dev.civl.gmc.seq.StateManager;

public class DporNodeFactory<STATE, TRANSITION> implements GetIdFunction<STATE> {
	private Map<STATE, DporNode<STATE>> nodeMap = new HashMap<>();
	
	/**
	 * The counter used to count the # of {@link SequentialNode} cached in
	 * {@link #nodeMap}.
	 */
	private int nodeCounter = 0;

	/**
	 * A {@link StateManager} can be used to compute the next state and
	 * normalize a state.
	 */
	private StateManager<STATE, TRANSITION> stateManager;

	private boolean saveStates = true;

	private static int NOT_SAVED = -1;

	public DporNodeFactory(StateManager<STATE, TRANSITION> stateManager,
			boolean saveStates) {
		this.stateManager = stateManager;
		this.saveStates = saveStates;
	}

	/**
	 * <p>
	 * Implements the fly-weight pattern and normalize a state. Note that only
	 * normalized and canonical state will have id.
	 * </p>
	 * 
	 * @param state
	 * @return the existing {@link SequentialNode} mapped to the state, or a new
	 *         {@link SequentialNode} mapped to the state if there was no entry
	 *         in the map for the key state. Note that the
	 *         {@link SequentialNode} will always store the normalized or
	 *         simplified version of {@code state}.
	 */
	public DporNode<STATE> getNode(TraceStepIF<STATE> traceStep) {
		STATE state = traceStep.getFinalState();

		if (saveStates) {
			DporNode<STATE> result = nodeMap.get(state);

			if (result == null) {
				stateManager.normalize(traceStep);

				STATE normalizedState = traceStep.getFinalState();

				if (normalizedState != state) {
					result = nodeMap.get(normalizedState);
					if (result == null) {
						result = new DporNode<STATE>(normalizedState,
								nodeCounter++);
						nodeMap.put(normalizedState, result);
					}
				} else {
					result = new DporNode<STATE>(state, nodeCounter++);
				}
				nodeMap.put(state, result);
			}
			return result;
		} else
			return new DporNode<STATE>(state, NOT_SAVED);
	}

	/**
	 * Get the node associated to the given state, null there is no such a node.
	 * 
	 * @param state
	 *            The state whose associated node will be returned.
	 * @return the node associated to the given state, null there is no such a
	 *         node.
	 */
	public DporNode<STATE> getNode(STATE state) {
		return nodeMap.get(state);
	}

	/**
	 * @return the number of search nodes saved.
	 */
	public int numOfSearchNodeSaved() {
		return nodeMap.size();
	}

	/**
	 * Get the {@link SequentialNode} associated with the initial state.
	 * 
	 * @param initState
	 * @return
	 */
	public DporNode<STATE> getInitialNode(STATE initState) {
		DporNode<STATE> initNode;

		if (saveStates) {
			initNode = new DporNode<STATE>(initState, nodeCounter++);
		} else
			initNode = new DporNode<STATE>(initState, NOT_SAVED);

		nodeMap.put(initState, initNode);
		return initNode;
	}

	/**
	 * Look up if there exists a {@link SequentialNode} associated with a
	 * non-initial state, if there is, return the {@link SequentialNode},
	 * otherwise return null.
	 * 
	 * @param state
	 * @return
	 */
	DporNode<STATE> lookup(STATE state) {
		return nodeMap.get(state);
	}

	@Override
	public int getId(STATE state) {
		DporNode<STATE> node = getNode(state);
		return node == null ? -1 : node.getId();
	}
}
