package dev.civl.mc.predicate.common;

import dev.civl.mc.predicate.IF.TrivialPredicate;
import dev.civl.mc.state.IF.State;

public class CommonTrivialPredicate extends CommonCIVLStatePredicate
		implements
			TrivialPredicate {

	@Override
	public boolean holdsAt(State state) {
		return false;
	}

	@Override
	public String explanation() {
		return null;
	}

}
