package dev.civl.mc.state.common.immutable;

import java.util.Set;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * Make the simplification, which is carried out by a {@link Reasoner}, a
 * {@link UnaryOperator<SymbolicExpression>}.
 * 
 * @author ziqing
 *
 */
public class SimplifyOperator implements UnaryOperator<SymbolicExpression> {

	private Reasoner simplifier;
	private Set<SymbolicConstant> aggressiveSet;

	SimplifyOperator(Reasoner reasoner, Set<SymbolicConstant> aggressiveSet) {
		this.simplifier = reasoner;
		this.aggressiveSet = aggressiveSet;
	}

	@Override
	public SymbolicExpression apply(SymbolicExpression x) {
		return simplifier.simplify(x, aggressiveSet);
	}
}
