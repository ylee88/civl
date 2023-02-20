package dev.civl.sarl.object.common;

import java.util.Comparator;

import dev.civl.sarl.IF.type.SymbolicTypeSequence;
/**
 * Test stub for TypeSequenceComparator
 * @author justin
 *
 */
public class TypeSequenceComparatorStub implements Comparator<SymbolicTypeSequence> {

	public TypeSequenceComparatorStub() {
	}

	@Override
	public int compare(SymbolicTypeSequence o1, SymbolicTypeSequence o2) {
		String name1 = (o1).toString();
		String name2 = (o2).toString();

		return name1.compareTo(name2);
	}

}
