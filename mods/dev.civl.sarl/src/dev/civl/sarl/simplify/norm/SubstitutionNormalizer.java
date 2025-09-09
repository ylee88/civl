package dev.civl.sarl.simplify.norm;

import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.simplify.simplification.Strategy;
import dev.civl.sarl.simplify.simplifier.MutableContext;
import dev.civl.sarl.simplify.simplifier.ContextExtractor;
import dev.civl.sarl.simplify.simplifier.InconsistentContextException;
import dev.civl.sarl.simplify.simplifier.SimplifierUtility;
import dev.civl.sarl.util.Pair;

/**
 * Simplifies the context's substitution map. When this method returns, each
 * key-value pair in the subMap will be simplified using all other entries. It
 * may also modify the context's range map, because simplified formulas may
 * result in range restrictions that are added to the range map.
 */
public class SubstitutionNormalizer implements Normalizer {

	/**
	 * The context being simplified.
	 */
	private MutableContext context;

	/**
	 * Creates new {@link SubstitutionNormalizer} for simplifying the given
	 * {@link MutableContext}.
	 * 
	 * @param context
	 *            the context to be simplified
	 */
	public SubstitutionNormalizer(MutableContext context) {
		this.context = context;
	}

	/**
	 * Finds all entries in the context's substitution map (but note: not in its
	 * ancestors' substitution maps) that involve a variable in the given dirty
	 * set. An entry involves a variable if that variable occurs in the key or
	 * value of the entry (or both).
	 * 
	 * @param dirtSet
	 *            a set of {@link SymbolicConstant}s (variables) of interest
	 * @return the list of keys from the entries in the substitution map which
	 *         involve at least one of those dirty variables.
	 */
	private LinkedList<SymbolicExpression> buildSubmapWorklist(
			Set<SymbolicConstant> dirtSet) {
		LinkedList<SymbolicExpression> result = new LinkedList<>();
		Set<Entry<SymbolicExpression, SymbolicExpression>> entrySet;

		entrySet = context.getSubEntries();
		for (Entry<SymbolicExpression, SymbolicExpression> entry : entrySet) {
			SymbolicExpression key = entry.getKey(), value = entry.getValue();

			if (SimplifierUtility.intersects(key, dirtSet)
					|| SimplifierUtility.intersects(value, dirtSet))
				result.add(key);
		}
		return result;
	}

	/**
	 * Simplifies the context's substitution map. When this method returns, each
	 * key-value pair in the subMap will be simplified using all other entries.
	 * 
	 * Given the original dirty set which determines the initial set of entries
	 * that must be simplified; upon return, dirtySet will contain all symbolic
	 * constants ...
	 * 
	 * @param dirtyIn
	 *            set of symbolic constants considered dirty, used to determine
	 *            the initial set of entries in the subMap which will be
	 *            simplified. More entries can be added as more variables become
	 *            dirty. This set is not modified by this method.
	 * 
	 * @param dirtyOut
	 *            the actual set of symbolic constants occurring in entries
	 *            which are modified or added to the subMap
	 *
	 */
	@Override
	public void normalize(Set<SymbolicConstant> dirtyIn,
			Set<SymbolicConstant> dirtyOut)
			throws InconsistentContextException {
		Set<SymbolicConstant> dirtyNow = SimplifierUtility
				.cloneDirtySet(dirtyIn);
		ContextExtractor extractor = new ContextExtractor(context, dirtyNow);

		while (!dirtyNow.isEmpty()) {
			LinkedList<SymbolicExpression> worklist = buildSubmapWorklist(
					dirtyNow);

			dirtyNow.clear();
			while (!worklist.isEmpty()) {
				SymbolicExpression key1 = worklist.remove(),
						value1 = context.getSub(key1);

				if (value1 == null)
					continue;
				context.removeSubkey(key1);

				SymbolicExpression key2 = key1, value2 = value1;
				
				/* If context is trivial and value1 is true then we may infinitely recurse since
				 * a call to simplify on key1 may use SubContextSimplification which may lead back
				 * to here, causing infinite recursion.
				 */
				if (!(context.contextIsTrivial() && value1.isTrue())) {
					key2 = (SymbolicExpression) context.simplify(key1, Strategy.standardStrategy());
					value2 = (SymbolicExpression) context.simplify(value1, Strategy.standardStrategy());
				}
				Pair<SymbolicExpression, SymbolicExpression> pair = new Pair<>(
						key2, value2);

				context.standardizePair(pair);

				SymbolicExpression newKey = pair.left;

				if (newKey == null)
					continue; // a trivial substitution

				SymbolicExpression newValue = pair.right;
				SymbolicExpression oldValue = context.getSub(newKey);

				if (oldValue == newValue) {
					// do nothing: the new sub is already in the subMap
				} else if (newKey == key1 && newValue == value1) {
					// no change: put it back, but don't count it as dirty...
					context.putSub(key1, value1);
				} else if (newValue.isTrue()
						&& SimplifierUtility.isNumericRelational(newKey)) {
					// it goes to the rangeMap...
					extractor.extractClause((BooleanExpression) newKey);
				} else {
					// add the new sub, updating dirty...
					context.addSub(newKey, newValue, dirtyNow);
				}
			}
			dirtyOut.addAll(dirtyNow);
		}
	}

}
