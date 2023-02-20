package dev.civl.mc.semantics.IF;

import dev.civl.mc.state.IF.State;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * Represents the result of evaluating something that returns a symbolic type,
 * but in two parts: the (possibly) new state resulting from side-effects
 * arising from the evaluation, and the symbolic type resulting from the
 * evaluation.
 * 
 * @author siegel
 * 
 */
public class TypeEvaluation {

	public State state;

	public SymbolicType type;

	public TypeEvaluation(State state, SymbolicType type) {
		this.state = state;
		this.type = type;
	}

	@Override
	public String toString() {
		return "[" + state + ", " + type + "]";
	}

}
