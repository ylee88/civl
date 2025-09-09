package dev.civl.sarl.expr.common;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicRange;

public class CommonSymbolicRange implements SymbolicRange {
	final private NumericExpression lower;
	final private NumericExpression upper;
	final private NumericExpression step;
	final private RangeKind rangeKind;

	public CommonSymbolicRange(RangeKind rangeKind, NumericExpression lower,
			NumericExpression upper, NumericExpression step) {
		this.lower = lower;
		this.upper = upper;
		this.step = step;
		this.rangeKind = rangeKind;
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this.getClass() != other.getClass())
			return false;
		CommonSymbolicRange castOther = (CommonSymbolicRange) other;
		boolean result = rangeKind.equals(castOther.rangeKind)
				&& lower.equals(castOther.lower);
		if (rangeKind == RangeKind.INTERVAL)
			result = result && upper.equals(castOther.upper);
		if (rangeKind == RangeKind.REGULAR)
			result = result && upper.equals(castOther.upper) && step.equals(castOther.step);
		return result;
	}

	public NumericExpression getLower() {
		return lower;
	}

	public NumericExpression getUpper() {
		return upper;
	}

	public NumericExpression getStep() {
		return step;
	}

	public RangeKind getRangeKind() {
		return rangeKind;
	}
	
	public String toString() {
		String str = ""+lower;
		if(rangeKind != RangeKind.SINGLETON) {
			str += ".."+upper;
			if (rangeKind != RangeKind.INTERVAL) {
				str += "#"+step;
			}
		}
		return rangeKind+":["+str+"]";
	}
}
