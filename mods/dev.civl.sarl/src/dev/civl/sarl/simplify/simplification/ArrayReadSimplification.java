package dev.civl.sarl.simplify.simplification;

import static dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator.ARRAY;
import static dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator.ARRAY_READ;
import static dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator.DENSE_ARRAY_WRITE;

import java.util.Iterator;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * <p>
 * Simplifies a symbolic expression of the form <code>ARRAY_READ(a, i)</code>:
 * <ul>
 * <li>if the array has the form <code>a = ARRAY_WRITE(a', i', v)</code>
 * <ul>
 * <li>if <code>i == i'</code>, <code>ARRAY_READ(a, i) = v</code></li>
 * <li>if <code>i != i'</code>,
 * <code>ARRAY_READ(a, i) = ARRAY_READ(a', i)</code></li>
 * <li>otherwise, no simplification</li>
 * </ul>
 * </li>
 * 
 * 
 * <li>if the array has the form <code>a = DENSE_ARRAY_WRITE(a', {v<sub>0</sub>,
 * v<sub>1</sub>, ..., v<sub>n-1</sub>})</code>
 * <ul>
 * <li>if <code>i == j, 0 &lt= j &lt n</code>,
 * <code>ARRAY_READ(a, i) = v<sub>j</sub></code>.</li>
 * <li>if <code> n &lt= i,</code>,
 * <code>ARRAY_READ(a, i) = ARRAY_READ(a', i)</code></li>
 * <li>otherwise, no simplification</li>
 * </ul>
 * </li>
 * 
 * 
 * <li>if the array has the form <code>a = ARRAY(c, c, ..., c)</code> where
 * <code>c</code> is a constant and <code>0 &lt= i &lt length(a)</code>,
 * <code>ARRAY_READ(a, i) = c</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>note that the ARRAY_READ on ARRAY_WRITE case and the first sub-case of
 * ARRAY_READ on DENSE_ARRAY_WRITE have already been taken care of at the time
 * of creating an ARRAY_READ expression, hence this class doesn't deal with
 * them</b>
 * </p>
 * 
 * @author ziqing
 *
 */
public class ArrayReadSimplification extends Simplification {

	@Override
	protected SymbolicExpression apply(SymbolicExpression x) {
		if (x.operator() != ARRAY_READ)
			return x;

		SymbolicExpression array = (SymbolicExpression) x.argument(0);
		NumericExpression idx = (NumericExpression) x.argument(1);

		array = (SymbolicExpression) simplify(array);
		idx = (NumericExpression) simplify(idx);

		SymbolicExpression result = simplifyArrayReadWorker(array, idx);

		if (result == null && array == x.argument(0) && idx == x.argument(1))
			return x;
		else if (result != null)
			return result;
		else
			return universe.arrayRead(array, idx);
	}

	/**
	 * <p>
	 * Attempts to simplify an array read expression in three different cases.
	 * Returns null if no simplification can be applied.
	 * </p>
	 */
	private SymbolicExpression simplifyArrayReadWorker(SymbolicExpression array,
			NumericExpression readIdx) {

		// if (array.operator() == ARRAY_WRITE)
		// return simplifyArrayReadOnArrayWrite(array, readIdx);
		if (array.operator() == DENSE_ARRAY_WRITE)
			return simplifyArrayReadOnDenseArrayWrite(array, readIdx);
		else if (array.operator() == ARRAY)
			return simplifyArrayReadOnConcreteArray(array, readIdx);
		return null;
	}

	/**
	 * 
	 * simplify
	 * <code>ARRAY_READ(DENSE_ARRAY_WRITE(v<sub>0</sub>, v<sub>1</sub>, ...), i)</code>
	 */
	private SymbolicExpression simplifyArrayReadOnDenseArrayWrite(
			SymbolicExpression denseArrayWrite, NumericExpression readIdx) {
		@SuppressWarnings("unchecked")
		Iterable<SymbolicExpression> writeVals = (Iterable<SymbolicExpression>) denseArrayWrite
				.argument(1);
		SymbolicExpression ret;
		int count = 0;
		Iterator<SymbolicExpression> iter = writeVals.iterator();

		while (iter.hasNext() && iter.next() != null)
			count++;
		BooleanExpression newRead = universe
				.lessThanEquals(universe.integer(count), readIdx);
		if (proveValid((BooleanExpression) simplify(newRead))) {
			ret = universe.arrayRead(
					(SymbolicExpression) denseArrayWrite.argument(0), readIdx);
			return (SymbolicExpression) simplify(ret);
		}
		return null;
	}

	/**
	 * 
	 * simplify <code>ARRAY_READ({v<sub>0</sub>, v<sub>1</sub>, ...}, i)</code>
	 */
	private SymbolicExpression simplifyArrayReadOnConcreteArray(
			SymbolicExpression array, NumericExpression readIdx) {
		int numArgs = array.numArguments();

		if (numArgs < 1)
			return null;

		SymbolicExpression element = (SymbolicExpression) array.argument(0);

		for (int i = 1; i < numArgs; i++)
			if (array.argument(i) != element)
				return null;

		BooleanExpression readIdxInRange = universe.and(
				universe.lessThanEquals(universe.zeroInt(), readIdx),
				universe.lessThan(readIdx, universe.integer(numArgs)));

		if (proveValid(
				(BooleanExpression) simplify(readIdxInRange)))
			return element;
		else
			return null;
	}
}
