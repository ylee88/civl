package dev.civl.sarl.simplify.simplifier;

import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.common.MapSubstituter;

/**
 * A {@link MapSubstituter} that doesn't perform substitution on types.
 * 
 * I wrote this awhile ago and can't remember the exact reason for this, but I
 * think I recall there being some subtle issue that arises when simplifying
 * types (at least at certain points in the simplification process). I'm almost
 * certain the issue was either a soundness bug, an infinite loop issue, or
 * precision loss issue. A soundness bug seems most likely but not sure.
 * 
 * @author awilton
 *
 */
public class ContextSubstituter extends MapSubstituter {

	public ContextSubstituter(PreUniverse universe,
			UnaryOperator<SymbolicExpression> operator) {
		super(universe, universe.objectFactory(), universe.typeFactory(),
				operator);
	}

	@Override
	protected SymbolicType substituteType(SymbolicType type,
			SubstituterState state) {
		return type;
	}
}
