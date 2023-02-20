package dev.civl.mc.state.common.immutable;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.UnaryOperator;
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

	SimplifyOperator(Reasoner reasoner) {
		this.simplifier = reasoner;
	}

	@Override
	public SymbolicExpression apply(SymbolicExpression x) {
		return simplifier.simplify(x);
	}
}
