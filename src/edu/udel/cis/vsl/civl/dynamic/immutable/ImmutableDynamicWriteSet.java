package edu.udel.cis.vsl.civl.dynamic.immutable;

import edu.udel.cis.vsl.civl.dynamic.IF.DynamicWriteSet;
import edu.udel.cis.vsl.sarl.IF.Reasoner;
import edu.udel.cis.vsl.sarl.IF.UnaryOperator;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

/**
 * An immutable pattern implementaion of {@Link DynamicWriteSet}
 * 
 * @author ziqing (Ziqing Luo)
 */
public class ImmutableDynamicWriteSet implements DynamicWriteSet {

	private final SymbolicExpression memValue;

	/**
	 * A unary operator that collects the references in the "memValue", which
	 * are referring to non-alive objects:
	 */
	private UnaryOperator<SymbolicExpression> collector;

	ImmutableDynamicWriteSet(SymbolicExpression memValue,
			UnaryOperator<SymbolicExpression> collector) {
		this.memValue = memValue;
		this.collector = collector;
	}

	/* ***************** public methods from DynamicWriteSet *****************/
	@Override
	public SymbolicExpression getMemValue() {
		return memValue;
	}

	@Override
	public ImmutableDynamicWriteSet apply(
			UnaryOperator<SymbolicExpression> operator) {
		SymbolicExpression newMemValue = operator.apply(memValue);

		newMemValue = collector.apply(newMemValue);
		if (newMemValue != memValue)
			return new ImmutableDynamicWriteSet(newMemValue, collector);
		else
			return this;
	}

	@Override
	public ImmutableDynamicWriteSet simplify(Reasoner reasoner) {
		SymbolicExpression newMemValue = reasoner.simplify(memValue);

		newMemValue = collector.apply(newMemValue);
		if (newMemValue != memValue)
			return new ImmutableDynamicWriteSet(newMemValue, collector);
		else
			return this;
	}

	/* ***************** Public methods from Objects ******************* */

	@Override
	public String toString() {
		return "WriteSet{" + memValue + "}";
	}

	@Override
	public int hashCode() {
		return memValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImmutableDynamicWriteSet) {
			ImmutableDynamicWriteSet other = (ImmutableDynamicWriteSet) obj;

			return other.getMemValue().equals(memValue);
		}
		return false;
	}
}
