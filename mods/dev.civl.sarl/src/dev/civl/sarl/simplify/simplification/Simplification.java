package dev.civl.sarl.simplify.simplification;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.simplify.simplifier.MutableContext;
import dev.civl.sarl.simplify.simplifier.SimplifierUtility;

/**
 * A {@link Simplification} takes a {@link SymbolicExpression} and returns an
 * equivalent {@link SymbolicExpression} in a simplified form. Each instance of
 * {@link Simplification} does one and only one thing. A typical simplification
 * object may be applicable to only a certain kind of {@link SymbolicExpression}
 * , for example, to those expressions whose operator is
 * {@link SymbolicOperator#OR}. A client can chain together multiple
 * {@link Simplification}s to form a more powerful simplification engine.
 * 
 * This base class provides utility methods that will be commonly used by most
 * instances.
 * 
 * @author siegel
 *
 */
public abstract class Simplification {

	//private List<MutableContext> contextStack = new LinkedList<>();
	private MutableContext context;
	private SimplifierUtility util;
	private Strategy strategy;

	protected PreUniverse universe;

	public SymbolicExpression apply(MutableContext context,
			Strategy strategy, SymbolicExpression expr) {
		this.context = context;
		this.util = context.getInfo();
		this.universe = util.getUniverse();
		this.strategy = strategy;
		
		SymbolicExpression result = apply(expr);
		return result;
	}

	protected abstract SymbolicExpression apply(SymbolicExpression expr);

	protected boolean proveValid(BooleanExpression predicate) {
		return context.isValid(predicate, strategy);
	}

	protected boolean proveUnsat(BooleanExpression predicate) {
		return context.isUnsat(predicate, strategy);
	}
	
	protected SymbolicObject simplify(SymbolicObject expr) {
		return context.simplify(expr, strategy);
	}
	
	protected Interval intervalApproximation(NumericExpression expr) {
		return context.computeRange((RationalExpression) expr)
				.intervalOverApproximation();
	}
	
	protected BooleanExpression getFullAssumption() {
		return context.getFullAssumption();
	}
	
	protected SymbolicExpression genericSimplify(SymbolicExpression expr) {
		return context.genericSimplify(strategy, expr);
	}
	
	protected void pushAssumption(BooleanExpression assumption) {
		context = context.createSubContext(assumption, false);
	}
	
	protected void popAssumption() {
		context = (MutableContext) context.getSuperContext();
	}
	
	protected SimplifierUtility util() {
		return util;
	}
}
