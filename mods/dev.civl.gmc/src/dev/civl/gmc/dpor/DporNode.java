package dev.civl.gmc.dpor;

import java.util.HashMap;
import java.util.Map;

import dev.civl.gmc.TraceStepIF;

/**
 * Wraps a STATE object with transition caching and persistent data used in the
 * DPOR search
 * 
 * @author Alex Wilton
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public class DporNode<STATE, TRANSITION> {
	private int id;
	
	/**
	 * The state that is associated with this node.
	 */
	private STATE state;
	
	/**
	 * The position index of which this {@link DporNode} is on Stack.
	 * Position 0 will be at the bottom of the stack (initial state). -1 means
	 * not on the stack.
	 */
	private int stackPosition = -1;

	/**
	 * Cache mapping transitions to the trace steps that they entail when
	 * executed.
	 * <p>
	 * Needed because DPOR requires frequently retracing steps.
	 */
	private Map<TRANSITION, TraceStepIF<STATE>> traceStepCache = new HashMap<>();

	/**
	 * @param state
	 *            The state this node will store
	 * @param id
	 *            An identifier unique to this node
	 */
	public DporNode(STATE state, int id) {
		this.state = state;
		this.id = id;
	}

	/**
	 * <p>
	 * Set the stack position field of the {@link DporNode}.
	 * </p>
	 * <p>
	 * The "stack position" field is intended to be used by a depth-first search
	 * algorithm, to mark that a state is currently on the depth-first search
	 * stack.
	 * </p>
	 * <p>
	 * The bottom element in the stack will have an index 0.
	 * </p>
	 * 
	 * @param value
	 *            The value assigned to the stack position filed.
	 */
	public void setStackPosition(int stackPosition) {
		this.stackPosition = stackPosition;
	}

	/**
	 * @return the position of this node as an entry in {@link DporSearchStack}
	 *         or -1 if it is not on the stack.
	 */
	public int getStackPosition() {
		return stackPosition;
	}
	
	/**
	 * @return the cached trace step {@code transition} entails if we have explored it already,
	 *         otherwise {@code null}.
	 */
	public TraceStepIF<STATE> getTraceStepCache(TRANSITION transition) {
		return traceStepCache.getOrDefault(transition, null);
	}

	/**
	 * Accepts a transition and the trace step that it entails and caches this
	 * association.
	 */
	public void cacheTraceStep(TRANSITION transition,
			TraceStepIF<STATE> traceStep) {
		traceStepCache.put(transition, traceStep);
	}

	/**
	 * @return the STATE this {@link DporNode} wraps.
	 */
	public STATE getState() {
		return state;
	}

	/**
	 * @return the node id which is unique to every node.
	 */
	public int getId() {
		return id;
	}
}
