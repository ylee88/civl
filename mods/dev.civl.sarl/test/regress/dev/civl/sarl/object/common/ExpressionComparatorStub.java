package dev.civl.sarl.object.common;

import java.util.Comparator;

import dev.civl.sarl.IF.expr.SymbolicExpression;

public class ExpressionComparatorStub implements Comparator<SymbolicExpression> {

	@Override
	public int compare(SymbolicExpression o1, SymbolicExpression o2) {
		String name1 = ((ExpressionStub) o1).toString();
		String name2 = ((ExpressionStub) o2).toString();

		return name1.compareTo(name2);
	}

}
