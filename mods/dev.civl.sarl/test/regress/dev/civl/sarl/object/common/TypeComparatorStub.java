package dev.civl.sarl.object.common;

import java.util.Comparator;

import dev.civl.sarl.IF.type.SymbolicType;

/**
 * Test stub for TypeComparator
 * @author jtirrell
 *
 */
public class TypeComparatorStub implements Comparator<SymbolicType> {

	public TypeComparatorStub() {
	}

	@Override
	public int compare(SymbolicType o1, SymbolicType o2) {
		String name1 = (o1).toString();
		String name2 = (o2).toString();

		return name1.compareTo(name2);
	}

}
