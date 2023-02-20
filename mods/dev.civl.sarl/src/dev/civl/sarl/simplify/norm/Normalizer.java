package dev.civl.sarl.simplify.norm;

import java.util.Set;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.simplify.simplifier.InconsistentContextException;

public interface Normalizer {

	void normalize(Set<SymbolicConstant> dirtyIn,
			Set<SymbolicConstant> dirtyOut) throws InconsistentContextException;
}
