package dev.civl.gmc.dpor;

import java.util.HashMap;
import java.util.Map;

import dev.civl.gmc.GetIdFunction;
import dev.civl.gmc.TraceStepIF;
import dev.civl.gmc.seq.StateManager;
import dev.civl.gmc.util.Pair;

public class DporNodeFactory<STATE, TRANSITION> implements GetIdFunction<STATE> {
	// Maps a state to its canonical DporNode
	private Map<STATE, DporNode<STATE, TRANSITION>> nodeMap = new HashMap<>();
	
	/**
	 * The counter used to count the # of {@link DporNode} cached in
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
	 * @return Left component: the existing {@link DporNode} mapped to the
	 *         state, or a new {@link DporNode} mapped to the state if there was
	 *         no entry in the map for the key state. Note that the
	 *         {@link DporNode} will always store the normalized or simplified
	 *         version of {@code state}.
	 * 
	 *         Right component: whether the returned node has been seen before
	 *         (i.e. true iff we got it from storage)
	 */
	public Pair<DporNode<STATE, TRANSITION>, Boolean> getNode(TraceStepIF<STATE> traceStep) {
		STATE state = traceStep.getFinalState();
		boolean seen = true;

		if (saveStates) {
			DporNode<STATE, TRANSITION> result = nodeMap.get(state);

			if (result == null) {
				stateManager.normalize(traceStep);

				STATE normalizedState = traceStep.getFinalState();

				if (normalizedState != state) {
					result = nodeMap.get(normalizedState);
					if (result == null) {
						result = new DporNode<STATE, TRANSITION>(normalizedState,
								nodeCounter++);
						seen = false;
						nodeMap.put(normalizedState, result);
					}
				} else {
					result = new DporNode<STATE, TRANSITION>(state, nodeCounter++);
					seen = false;
				}
				nodeMap.put(state, result);
			}
			return new Pair<>(result, seen);
		} else
			return new Pair<>(new DporNode<STATE, TRANSITION>(state, NOT_SAVED), false);
	}

	/**
	 * Get the node associated to the given state, null if there is no such a node.
	 * 
	 * @param state
	 *            The state whose associated node will be returned.
	 * @return the node associated to the given state, null there is no such a
	 *         node.
	 */
	public DporNode<STATE, TRANSITION> getAssociatedNode(STATE state) {
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
	public DporNode<STATE, TRANSITION> getInitialNode(STATE initState) {
		DporNode<STATE, TRANSITION> initNode;

		if (saveStates) {
			initNode = new DporNode<STATE, TRANSITION>(initState, nodeCounter++);
		} else
			initNode = new DporNode<STATE, TRANSITION>(initState, NOT_SAVED);

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
	DporNode<STATE, TRANSITION> lookup(STATE state) {
		return nodeMap.get(state);
	}

	@Override
	public int getId(STATE state) {
		DporNode<STATE, TRANSITION> node = getAssociatedNode(state);
		return node == null ? -1 : node.getId();
	}
}
