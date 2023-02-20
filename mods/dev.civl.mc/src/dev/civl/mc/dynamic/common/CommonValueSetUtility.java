package dev.civl.mc.dynamic.common;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.dynamic.IF.ValueSetUtility;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.NTValueSetReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSArrayElementReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSArraySectionReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSTupleComponentReference;
import dev.civl.sarl.IF.expr.valueSetReference.ValueSetReference;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicType.SymbolicTypeKind;
import dev.civl.sarl.IF.type.SymbolicUnionType;

/**
 * This class is unused for now. May be useful in the future.
 * 
 * TODO: the {@link #buildFrameCondition} has not been completed yet. It needs
 * to elaborate all tuple components that are not referred by a value set
 * template and assert that they are unchanged.
 * 
 * These methods were designed to implement mem_havoc in a way that the havoc
 * operation always refreshes a whole variable and returns boolean expression as
 * the precise frame condition. But it seems that this approach slows down CIVL.
 */
class CommonValueSetUtility implements ValueSetUtility {

	private final SymbolicUniverse universe;

	private final SymbolicUtility symbolicUtil;

	CommonValueSetUtility(SymbolicUniverse universe,
			SymbolicUtility symbolicUtil) {
		this.universe = universe;
		this.symbolicUtil = symbolicUtil;
	}

	@Override
	public List<ValueSetReference> extendToFull(ValueSetReference vsRef,
			SymbolicType varType) {
		SymbolicType type = referredType(vsRef, varType);

		if (isPrimitive(type)) {
			// the reference already refers to primitives:
			return Arrays.asList(vsRef);
		}
		return extendToFullWorker(vsRef, type);
	}

	@Override
	public Iterable<List<ValueSetReference>> toDisjointGroups(
			SymbolicExpression valueSetTemplate) {
		Map<Trie, List<ValueSetReference>> groups = new IdentityHashMap<>();
		Trie root = new Trie();
		SymbolicType varType = universe.valueType(valueSetTemplate);

		for (ValueSetReference vsRef : universe
				.valueSetReferences(valueSetTemplate)) {
			for (ValueSetReference extVsRef : extendToFull(vsRef, varType)) {
				Trie node = buildTrie(root, extVsRef);

				groups.computeIfAbsent(node, k -> new LinkedList<>())
						.add(extVsRef);
			}
		}
		return groups.values();
	}

	@Override
	public BooleanExpression buildFrameCondition(SymbolicType varType,
			SymbolicExpression oldVal, SymbolicExpression newVal,
			SymbolicExpression valueSetTemplate) {
		// we need one integral bound variable per array level in the type
		// hierarchy:
		int arrayLevels = arrayLevelsInType(varType);
		List<MultiQuantifierAssertion> assertions = new LinkedList<>();
		LinkedList<SymbolicConstant> boundVars = new LinkedList<>(
				symbolicUtil.freshBoundVariablesFor(arrayLevels,
						universe.integerType(), oldVal, newVal));

		for (List<ValueSetReference> group : toDisjointGroups(
				valueSetTemplate)) {
			for (int lv = 0; lv < arrayLevels; lv++)
				assertions.add(buildFrameConditionWorker(varType, oldVal,
						newVal, unrollLevels(group), boundVars, lv, 0));
		}

		return assertions.stream().map(p -> p.toForallAssertion()).reduce(
				universe.trueExpression(), (a, b) -> universe.and(a, b));
	}

	/**
	 * @return the {@link SymbolicType} referred by <code>vsRef</code> on a
	 *         variable of <code>type</code>
	 */
	private SymbolicType referredType(ValueSetReference vsRef,
			SymbolicType type) {
		if (vsRef.isIdentityReference())
			return type;
		type = referredType(((NTValueSetReference) vsRef).getParent(), type);
		switch (vsRef.valueSetReferenceKind()) {
			case ARRAY_ELEMENT :
			case ARRAY_SECTION : {
				SymbolicArrayType arrayType = (SymbolicArrayType) type;

				return arrayType.elementType();
			}
			case TUPLE_COMPONENT : {
				SymbolicTupleType tupleType = (SymbolicTupleType) type;
				VSTupleComponentReference vsTupleRef = (VSTupleComponentReference) vsRef;

				return tupleType.sequence()
						.getType(vsTupleRef.getIndex().getInt());
			}
			case UNION_MEMBER :
			case OFFSET :
				throw new CIVLUnimplementedFeatureException(
						"Manipulate value set reference of kind "
								+ vsRef.valueSetReferenceKind() + ".");
			case IDENTITY :
			default :
				assert false : "unreachable";
		}
		return null; // unreachable
	}

	/**
	 * Worker method of {@link #extendToFull(ValueSetReference, SymbolicType)}.
	 * Extends a value set reference to an equivalent set of ones referring to
	 * primitives.
	 */
	private List<ValueSetReference> extendToFullWorker(ValueSetReference vsRef,
			SymbolicType type) {
		if (isPrimitive(type))
			return Arrays.asList(vsRef);
		if (type.typeKind() == SymbolicTypeKind.ARRAY) {
			SymbolicArrayType arrType = (SymbolicArrayType) type;

			if (!arrType.isComplete())
				throw new CIVLUnimplementedFeatureException(
						"Extending value set references to arrays of incomplete type.");

			SymbolicCompleteArrayType completeArrayType = (SymbolicCompleteArrayType) arrType;

			vsRef = universe.vsArraySectionReference(vsRef, universe.zeroInt(),
					completeArrayType.extent());
			return extendToFullWorker(vsRef, completeArrayType.elementType());
		} else if (type.typeKind() == SymbolicTypeKind.TUPLE) {
			SymbolicTupleType tupleType = (SymbolicTupleType) type;
			int idx = 0;
			List<ValueSetReference> results = new LinkedList<>();

			for (SymbolicType componentType : tupleType.sequence()) {
				IntObject idxObj = universe.intObject(idx++);
				ValueSetReference nxtRef = universe
						.vsTupleComponentReference(vsRef, idxObj);

				results.addAll(extendToFullWorker(nxtRef, componentType));
			}
			return results;
		}
		throw new CIVLUnimplementedFeatureException(
				"Extending value set references to objects of an other type than array or tuple.");
	}

	/**
	 * Update the prefix trees <code>trie</code> representing a value set
	 * template with a <code>vsRef</code> in the value set template.
	 */
	private Trie buildTrie(Trie trie, ValueSetReference vsRef) {
		if (vsRef.isIdentityReference())
			return trie;

		Trie parentTrie = buildTrie(trie,
				((NTValueSetReference) vsRef).getParent());

		if (vsRef.isTupleComponentReference()) {
			IntObject idx = ((VSTupleComponentReference) vsRef).getIndex();

			return parentTrie.children.computeIfAbsent(idx, k -> new Trie());
		}
		return parentTrie;
	}

	/**
	 * <p>
	 * For a group of {@link ValueSetReference}s, this method builds an
	 * assertion stating that the objects that are not referred by those
	 * references at a specific array level in the type hierarchy still have
	 * their old values.
	 * </p>
	 * 
	 * <p>
	 * For example, let <code>arrLvToExclude == 1</code>, for a reference group
	 * <code> {&a[X][Y], &a[Z][W]} </code> associated to array type
	 * <code>T[n][m]</code>, this method asserts that <code>
	 * 
	 *   FORALL i. 0 <= i < n => 
	 *     (FORALL j. 0 <= j < m && j not_in Y && j not_in W => 
	 *       a[i][j] preserves old value 
	 * 
	 * </code>
	 * </p>
	 * 
	 * @param type
	 *            the type of <code>oldVal</code> and <code>newVal</code>
	 * @param oldVal
	 *            the old object value
	 * @param newVal
	 *            the new object value
	 * @param unrolledGroup
	 *            the unrolled {@link ValueSetReference}s in a group. See
	 *            {@link #unrollLevels(List)} and
	 *            {@link #toDisjointGroups(SymbolicExpression)}
	 * @param boundVars
	 *            a list of bound variables that will all be used to quantify
	 *            over array indices.
	 * @param arrLvToExclude
	 *            the level of array-element/section-reference kind, at which
	 *            the returning assertion quantifies over the complement of the
	 *            ranges referred by the group.
	 * @param arrLv
	 *            the current level of array-element/section-reference kind
	 */
	private MultiQuantifierAssertion buildFrameConditionWorker(
			SymbolicType type, SymbolicExpression oldVal,
			SymbolicExpression newVal,
			List<Iterable<ValueSetReference>> unrolledGroup,
			List<SymbolicConstant> boundVars, int arrLvToExclude, int arrLv) {
		if (isPrimitive(type))
			return new MultiQuantifierAssertion(
					universe.equals(oldVal, newVal));

		SymbolicType nxtType;
		SymbolicExpression nxtOldVal, nxtNewVal;
		Iterable<ValueSetReference> vsRefGroup = unrolledGroup.get(0);

		if (type.typeKind() == SymbolicTypeKind.ARRAY) {
			NumericSymbolicConstant bv = (NumericSymbolicConstant) boundVars
					.get(0);
			boolean excludeReferredRange = arrLvToExclude == arrLv;
			BooleanExpression restriction;

			if (excludeReferredRange) {
				/*
				 * The restriction should assert that the bound variable is in
				 * the valid range of the array type but not in the ranges
				 * referred by those references. Here we build the negation of
				 * the restriction, which later will connect to the predicate
				 * through logical OR operator.
				 */
				List<BooleanExpression> clauses = new LinkedList<>();

				for (ValueSetReference vsRef : vsRefGroup)
					clauses.add(isInArrayElementReferenceRange(bv, vsRef));
				clauses.add(universe
						.not(isInArrayRange(bv, (SymbolicArrayType) type)));
				restriction = universe.or(clauses);
			} else
				/*
				 * Here the restriction asserts that the bound variable is in
				 * the range of the array type.
				 */
				restriction = isInArrayRange(bv, (SymbolicArrayType) type);

			nxtType = ((SymbolicArrayType) type).elementType();
			nxtOldVal = universe.arrayRead(oldVal, bv);
			nxtNewVal = universe.arrayRead(newVal, bv);

			MultiQuantifierAssertion assertion = buildFrameConditionWorker(
					nxtType, nxtOldVal, nxtNewVal,
					unrolledGroup.subList(1, unrolledGroup.size()),
					boundVars.subList(1, boundVars.size()), arrLvToExclude,
					arrLv + 1);

			if (excludeReferredRange) {
				assertion.predicate = universe.or(restriction,
						assertion.predicate);
				assertion.predicate = universe.forall(bv, assertion.predicate);
			} else
				assertion.addRestriction(bv, restriction);
			return assertion;
		} else if (type.typeKind() == SymbolicTypeKind.TUPLE) {
			IntObject idx = null;

			for (ValueSetReference vsRef : vsRefGroup) {
				if (idx != null)
					idx = ((VSTupleComponentReference) vsRef).getIndex();
				else
					assert idx == ((VSTupleComponentReference) vsRef)
							.getIndex();
			}
			assert idx != null;
			nxtType = ((SymbolicTupleType) type).sequence()
					.getType(idx.getInt());
			nxtOldVal = universe.tupleRead(oldVal, idx);
			nxtNewVal = universe.tupleRead(newVal, idx);
			return buildFrameConditionWorker(nxtType, nxtOldVal, nxtNewVal,
					unrolledGroup.subList(1, unrolledGroup.size()), boundVars,
					arrLvToExclude, arrLv);
		} else
			throw new CIVLUnimplementedFeatureException(
					"Building a frame condition for an object of " + type
							+ " type.");
	}

	/**
	 * @return a boolean expression asserting that the <code>bv</code> is in the
	 *         referred range of the array element/section reference
	 *         <code>arrRef</code>.
	 */
	private BooleanExpression isInArrayElementReferenceRange(
			NumericExpression bv, ValueSetReference arrRef) {
		if (arrRef.isArrayElementReference()) {
			VSArrayElementReference eltRef = (VSArrayElementReference) arrRef;

			return universe.equals(bv, eltRef.getIndex());
		} else {
			assert arrRef.isArraySectionReference();
			VSArraySectionReference secRef = (VSArraySectionReference) arrRef;
			NumericExpression lb = secRef.lowerBound(); // inclusive
			NumericExpression ub = secRef.upperBound(); // exclusive

			return universe.and(universe.lessThanEquals(lb, bv),
					universe.lessThan(bv, ub));
		}
	}

	/**
	 * @return a boolean expression asserting that the <code>bv</code> is in the
	 *         index range of the array type <code>arrType</code>.
	 */
	private BooleanExpression isInArrayRange(NumericExpression bv,
			SymbolicArrayType arrType) {
		if (arrType.isComplete()) {
			SymbolicCompleteArrayType compType = (SymbolicCompleteArrayType) arrType;
			NumericExpression lb = universe.zeroInt(); // inclusive
			NumericExpression ub = compType.extent(); // exclusive

			return universe.and(universe.lessThanEquals(lb, bv),
					universe.lessThan(bv, ub));
		}
		return universe.trueExpression();
	}

	/**
	 * 
	 * @param type
	 *            a {@link SymbolicType}
	 * @return the number of array levels in the type hierarchy of
	 *         <code>type</code>
	 */
	private int arrayLevelsInType(SymbolicType type) {
		switch (type.typeKind()) {
			case ARRAY :
				return arrayLevelsInType(
						((SymbolicArrayType) type).elementType()) + 1;
			case TUPLE : {
				int lvs = 0;

				for (SymbolicType subType : ((SymbolicTupleType) type)
						.sequence())
					lvs += arrayLevelsInType(subType);
				return lvs;
			}
			case UNION : {
				int lvs = 0;

				for (SymbolicType subType : ((SymbolicUnionType) type)
						.sequence())
					lvs += arrayLevelsInType(subType);
				return lvs;
			}
			case BOOLEAN :
			case CHAR :
			case INTEGER :
			case REAL :
			case UNINTERPRETED :
				return 0;
			case SET :
			case MAP :
			case FUNCTION :
			default :
				throw new CIVLUnimplementedFeatureException(
						"Computing array type level hierarchy in type of kind "
								+ type.typeKind());
		}
	}

	/**
	 * <p>
	 * Unrolls the given group of references. For example, suppose the given
	 * group is <code>{f1(f2(f3)), g1(g2(g3))}</code>, this method returns
	 * <code>
	 *   {f3, g3},             // list head
	 *   {f2(f3), g2(g3)}, 
	 *   {f1(f2(f3)), g1(g2(g3))}
	 * </code>
	 * </p>
	 * 
	 * @param group
	 *            a group of {@link ValueSetReference} of same depth
	 */
	private List<Iterable<ValueSetReference>> unrollLevels(
			List<ValueSetReference> group) {
		LinkedList<Iterable<ValueSetReference>> result = new LinkedList<>();
		List<ValueSetReference> nextLevel = new LinkedList<>();

		while (true) {
			List<ValueSetReference> thisLevel = new LinkedList<>();

			for (ValueSetReference ref : group)
				if (!ref.isIdentityReference()) {
					thisLevel.add(ref);
					nextLevel.add(((NTValueSetReference) ref).getParent());
				}
			if (thisLevel.isEmpty())
				break;
			result.addFirst(thisLevel);
			group = nextLevel;
			nextLevel = new LinkedList<>();
		}
		return result;
	}

	/**
	 * @param type
	 * @return true iff <code>type</code> is a primitive type
	 */
	private boolean isPrimitive(SymbolicType type) {
		if (type.isBoolean() || type.isNumeric() || type.isChar())
			return true;
		return type.typeKind() == SymbolicTypeKind.UNINTERPRETED
				|| type.typeKind() == SymbolicTypeKind.FUNCTION;
	}

	/**
	 * The simplest prefix tree (trie) data structure
	 * 
	 * @author ziqingluo
	 *
	 */
	private class Trie {
		Map<IntObject, Trie> children = new IdentityHashMap<>();
	}

	/**
	 * <p>
	 * Representing a quantified predicate containing multiple quantifiers in
	 * such a form: <code>
	 * FORALL var_1. restriction_1 => (FORALL var_2. restriction_2 => ( ... => predicate) ...))
	 * </code>
	 * 
	 * It requires that each <code>restriction_i</code> can only involve free
	 * variables and <code>var_i</code>.
	 * </p>
	 * 
	 * @author ziqingluo
	 */
	private class MultiQuantifierAssertion {
		/**
		 * A list of pairs, each of which is a quantified variable and the
		 * restriction of the quantification.
		 */
		List<Pair<SymbolicConstant, BooleanExpression>> qRestrictions;
		/**
		 * 
		 */
		BooleanExpression predicate;

		MultiQuantifierAssertion(BooleanExpression predicate) {
			this.predicate = predicate;
			this.qRestrictions = new LinkedList<>();
		}

		/**
		 * Add a quantified variable and the restriction of the quantification.
		 */
		void addRestriction(SymbolicConstant bv,
				BooleanExpression restriction) {
			qRestrictions.add(new Pair<>(bv, restriction));
		}

		/**
		 * Convert this data structure to the equivalent
		 * {@link BooleanExpression}
		 */
		BooleanExpression toForallAssertion() {
			BooleanExpression result = predicate;

			for (Pair<SymbolicConstant, BooleanExpression> res : qRestrictions)
				result = universe.forall(res.left,
						universe.implies(res.right, result));
			return result;
		}
	}
}
