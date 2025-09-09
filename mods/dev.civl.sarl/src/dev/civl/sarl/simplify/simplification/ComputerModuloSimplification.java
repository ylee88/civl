package dev.civl.sarl.simplify.simplification;

import java.util.Arrays;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;

/**
 * <p>
 * Attempt to transform the expression <code>
 * e := (a<sub>0</sub> + a<sub>1</sub> + ..., a<sub>n-1</sub>) % x
 * </code> to its equivalence <code>
 * e' := (a<sub>0</sub>%x + a<sub>1</sub>%x + ..., a<sub>n-1</sub>%x) % x
 * </code> iff<code>e'</code> has a smaller size than <code>e</code> AND all
 * <code>
 * a<sub>0</sub>, a<sub>1</sub>, ..., a<sub>n-1</sub>
 * </code> are non-negative
 * </p>
 * 
 * @author ziqing
 *
 */
public class ComputerModuloSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression x) {
		if (x.operator() != SymbolicOperator.MODULO)
			return x;

		NumericExpression dividen = (NumericExpression) x.argument(0);
		NumericExpression divisor = (NumericExpression) x.argument(1);
		int preSize = x.size();
		NumericExpression zero = universe.zeroInt();
		// addens and divisor must be non-negative
		BooleanExpression nonNeg;

		dividen = (NumericExpression) simplify(dividen);
		divisor = (NumericExpression) simplify(divisor);
		nonNeg = universe.lessThanEquals(zero, divisor);
		if (dividen.operator() == SymbolicOperator.ADD) {
			int numArgs = dividen.numArguments();
			NumericExpression addens[] = new NumericExpression[numArgs];

			for (int i = 0; i < numArgs; i++) {
				addens[i] = (NumericExpression) dividen.argument(i);
				nonNeg = universe.and(nonNeg,
						universe.lessThanEquals(zero, addens[i]));
			}
			// check for non-negative:
			if (proveValid((BooleanExpression) simplify(nonNeg))) {
				for (int i = 0; i < numArgs; i++) {
					addens[i] = universe.modulo(addens[i], divisor);
					addens[i] = (NumericExpression) simplify(addens[i]);
				}

				NumericExpression newX = universe.add(Arrays.asList(addens));

				newX = universe.modulo(newX, divisor);
				if (newX.size() < preSize)
					x = newX;
			}
		}
		
		return x;
	}
}
