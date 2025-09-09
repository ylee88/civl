package dev.civl.sarl.simplify.simplification;

import java.util.Arrays;
import java.util.List;

import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.ideal.IF.Polynomial;
import dev.civl.sarl.ideal.IF.RationalExpression;

public class StandardSimplificationSelector implements SimplificationSelector {

	private LambdaSimplification lambdaSimp() {
		return new LambdaSimplification();
	}
	private ArrayLambdaSimplification arrayLambdaSimp() {
		return new ArrayLambdaSimplification();
	}
	private ArrayReadSimplification arrayReadSimp() {
		return new ArrayReadSimplification();
	}
	private SubContextSimplification subContextSimp() {
		return new SubContextSimplification();
	}
	private GenericSimplification genericSimp() {
		return new GenericSimplification();
	}
	private QuantifierSimplification quantifierSimp() {
		return new QuantifierSimplification();
	}
	private PowerSimplification powerSimp() {
		return new PowerSimplification();
	}
	private RationalPowerSimplification rationalPowerSimp() {
		return new RationalPowerSimplification();
	}
	private ConditionalSimplification2 conditionalSimp() {
		return new ConditionalSimplification2();
	}
	private OrSimplification orSimp() {
		return new OrSimplification();
	}
	private ComputerModuloSimplification computerModuloSimp() {
		return new ComputerModuloSimplification();
	}
	private PolynomialSimplification polynomialSimp() {
		return new PolynomialSimplification();
	}

	@Override
	public List<Simplification> select(SymbolicExpression symbExpr) {
		SymbolicOperator op = symbExpr.operator();

		switch (op) {
			case LAMBDA :
				return Arrays.asList(lambdaSimp());
			case ARRAY_LAMBDA :
				return Arrays.asList(arrayLambdaSimp());
			case ARRAY_READ :
				return Arrays.asList(arrayReadSimp());
			case AND :
			case LESS_THAN :
			case LESS_THAN_EQUALS :
			case NEQ :
				return Arrays.asList(subContextSimp());
			case EQUALS :
				if (((SymbolicExpression) symbExpr.argument(0)).type()
						.isNumeric())
					return Arrays.asList(subContextSimp());
				else
					return Arrays.asList(genericSimp());
			case FORALL :
			case EXISTS :
				return Arrays.asList(quantifierSimp());
			case POWER :
				return Arrays.asList(genericSimp(), powerSimp(), rationalPowerSimp());
			case COND : {
				// struggling to find the "right" way to simplify p?a:b...
				// return Arrays.asList(conditionalSimplification());
				return Arrays.asList(conditionalSimp());
				// return Arrays.asList(genericSimplification());
			}
			case OR :
				return Arrays.asList(genericSimp(), orSimp(), subContextSimp());// numericOrSimplification());
			case MODULO :
				return Arrays.asList(genericSimp(), rationalPowerSimp(),
						computerModuloSimp());
			default :
		}
		if (symbExpr instanceof Polynomial)
			return Arrays.asList(genericSimp(), polynomialSimp(),
					rationalPowerSimp());
		if (symbExpr instanceof RationalExpression)
			return Arrays.asList(genericSimp(), rationalPowerSimp());
		return Arrays.asList(genericSimp());
	}

}
