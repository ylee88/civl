package dev.civl.sarl.simplify;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.type.SymbolicType;

public class SimplifyCharTest {

	private boolean debug = false;

	private static SymbolicUniverse universe = SARL.newStandardUniverse();

	private static SymbolicType charType = universe.characterType();

	private static SymbolicExpression a = universe.character('a');

	private static SymbolicConstant X = universe
			.symbolicConstant(universe.stringObject("X"), charType);

	@Test
	public void simplifyChar() {
		BooleanExpression context = universe.equals(X, a);
		Reasoner reasoner = universe.reasoner(context);
		SymbolicExpression result = reasoner.simplify(X);

		if (debug)
			System.out.println(result);
		assertEquals(a, result);
	}
}
