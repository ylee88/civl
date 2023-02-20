package dev.civl.mc.dynamic.immutable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import dev.civl.mc.dynamic.IF.DynamicMemoryLocationSet;
import dev.civl.mc.dynamic.IF.DynamicMemoryLocationSetFactory;
import dev.civl.mc.library.mem.MemoryLocationMap;
import dev.civl.mc.library.mem.MemoryLocationMap.MemLocMapEntry;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.type.CIVLMemType;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class ImmutableDynamicMemoryLocationSetFactory
		implements
			DynamicMemoryLocationSetFactory {

	private SymbolicUniverse universe;

	private Function<List<SymbolicExpression[]>, SymbolicExpression> memCreator;

	private Function<SymbolicExpression, Iterable<CIVLMemType.MemoryLocationReference>> memIterator;

	private UnaryOperator<SymbolicExpression> memCollector;

	public ImmutableDynamicMemoryLocationSetFactory(SymbolicUniverse universe,
			CIVLTypeFactory typeFactory,
			SymbolicExpression collectedScopeValue) {
		this.universe = universe;
		memCreator = typeFactory.civlMemType().memValueCreator(universe);
		memIterator = typeFactory.civlMemType().memValueIterator();
		memCollector = typeFactory.civlMemType().memValueCollector(universe,
				collectedScopeValue);
	}

	@Override
	public DynamicMemoryLocationSet empty() {
		return new ImmutableDynamicMemoryLocationSet(memCreator.apply(Arrays.asList()),
				memCollector);
	}

	@Override
	public DynamicMemoryLocationSet addReference(
			DynamicMemoryLocationSet writeSet, SymbolicExpression memValue) {
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
			return new ImmutableDynamicMemoryLocationSet(newMemValue, memCollector);
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
