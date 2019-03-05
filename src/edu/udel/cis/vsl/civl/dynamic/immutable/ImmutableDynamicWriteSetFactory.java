package edu.udel.cis.vsl.civl.dynamic.immutable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import edu.udel.cis.vsl.civl.dynamic.IF.DynamicWriteSet;
import edu.udel.cis.vsl.civl.dynamic.IF.DynamicWriteSetFactory;
import edu.udel.cis.vsl.civl.library.mem.MemoryLocationMap;
import edu.udel.cis.vsl.civl.library.mem.MemoryLocationMap.MemLocMapEntry;
import edu.udel.cis.vsl.civl.model.IF.CIVLTypeFactory;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLMemType;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.UnaryOperator;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

public class ImmutableDynamicWriteSetFactory implements DynamicWriteSetFactory {

	private SymbolicUniverse universe;

	private Function<List<SymbolicExpression[]>, SymbolicExpression> memCreator;

	private Function<SymbolicExpression, Iterable<CIVLMemType.MemoryLocationReference>> memIterator;

	private UnaryOperator<SymbolicExpression> memCollector;

	public ImmutableDynamicWriteSetFactory(SymbolicUniverse universe,
			CIVLTypeFactory typeFactory,
			SymbolicExpression collectedScopeValue) {
		this.universe = universe;
		memCreator = typeFactory.civlMemType().memValueCreator(universe);
		memIterator = typeFactory.civlMemType().memValueIterator();
		memCollector = typeFactory.civlMemType().memValueCollector(universe,
				collectedScopeValue);
	}

	@Override
	public DynamicWriteSet empty() {
		return new ImmutableDynamicWriteSet(memCreator.apply(Arrays.asList()),
				memCollector);
	}

	@Override
	public DynamicWriteSet addReference(DynamicWriteSet writeSet,
			SymbolicExpression memValue) {
		SymbolicExpression oldMemValue = writeSet.getMemValue();
		List<CIVLMemType.MemoryLocationReference> memContents = new LinkedList<>();
		List<SymbolicExpression[]> memInputs;

		for (CIVLMemType.MemoryLocationReference ref : memIterator
				.apply(oldMemValue))
			memContents.add(ref);
		for (CIVLMemType.MemoryLocationReference ref : memIterator
				.apply(memValue))
			memContents.add(ref);
		// group:
		memInputs = addWorker(memContents);
		// convert to dynamic $mem type
		SymbolicExpression newMemValue = memCreator.apply(memInputs);

		if (newMemValue == oldMemValue)
			return writeSet;
		else
			return new ImmutableDynamicWriteSet(newMemValue, memCollector);
	}

	private List<SymbolicExpression[]> addWorker(
			List<CIVLMemType.MemoryLocationReference> rawContents) {
		MemoryLocationMap set = new MemoryLocationMap();
		List<SymbolicExpression[]> results = new LinkedList<>();

		for (CIVLMemType.MemoryLocationReference r : rawContents) {
			SymbolicExpression vst = set.get(r.vid(), r.heapID(), r.mallocID(),
					r.scopeValue());

			if (vst != null)
				vst = universe.valueSetUnion(vst, r.valueSetTemplate());
			else
				vst = r.valueSetTemplate();
			set.put(r.vid(), r.heapID(), r.mallocID(), r.scopeValue(), vst);
		}
		for (MemLocMapEntry e : set.entrySet())
			results.add(new SymbolicExpression[]{universe.integer(e.vid()),
					universe.integer(e.heapID()),
					universe.integer(e.mallocID()), e.scopeValue(),
					e.valueSetTemplate()});
		return results;
	}
}
