package dev.civl.sarl.expr.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Stream;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SARLException;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.expr.SymbolicRange;
import dev.civl.sarl.IF.expr.SymbolicRange.RangeKind;
import dev.civl.sarl.IF.expr.valueSetReference.NTValueSetReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSArrayElementReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSArraySectionReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSIdentityReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSOffsetReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSTupleComponentReference;
import dev.civl.sarl.IF.expr.valueSetReference.VSUnionMemberReference;
import dev.civl.sarl.IF.expr.valueSetReference.ValueSetReference;
import dev.civl.sarl.IF.expr.valueSetReference.ValueSetReference.VSReferenceKind;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.object.StringObject;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicFunctionType;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicTypeSequence;
import dev.civl.sarl.IF.type.SymbolicUnionType;
import dev.civl.sarl.IF.type.SymbolicType.SymbolicTypeKind;
import dev.civl.sarl.expr.IF.BooleanExpressionFactory;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.expr.IF.SymbolicRangeFactory;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSArrayElementReference;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSArraySectionReference;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSIdentityReference;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSOffsetReference;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSTupleComponentReference;
import dev.civl.sarl.expr.common.valueSetReference.CommonVSUnionMemberReference;
import dev.civl.sarl.ideal.IF.Constant;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.object.common.SimpleSequence;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;
import dev.civl.sarl.util.Pair;

/**
 * The factory provides interfaces for
 * <ul>
 * <li>creating instances of {@link ValueSetReference}s</li>
 * <li>operations over {@link ValueSetReference}s</li>
 * </ul>
 * 
 * @author ziqing
 *
 */
public class VSReferenceFactory {

	/*
	 * References to factories:
	 */
	private ObjectFactory objectFactory;

	private SymbolicTypeFactory typeFactory;

	private NumericExpressionFactory numericFactory;

	private SymbolicRangeFactory rangeFactory;

	/**
	 * The symbolic type of {@link ValueSetReference} expressions
	 */
	private SymbolicType valueSetReferenceType;

	/*
	 * Functions that map arguments to value set references
	 */
	private SymbolicConstant arrayElementReferenceFunction;

	private SymbolicConstant arraySectionReferenceFunction;

	private SymbolicConstant tupleComponentReferenceFunction;

	private SymbolicConstant unionMemberReferenceFunction;

	private SymbolicConstant offsetReferenceFunction;

	/*
	 * constant:
	 */
	private VSIdentityReference identityReference;

	/* Constructor */
	VSReferenceFactory(NumericExpressionFactory numericFactory) {
		this.numericFactory = numericFactory;
		this.objectFactory = numericFactory.objectFactory();
		this.typeFactory = numericFactory.typeFactory();
		this.rangeFactory = new CommonSymbolicRangeFactory(numericFactory);

		SymbolicType integerType = typeFactory.integerType();
		SymbolicTypeSequence refIdxSeq, refRangeSeq;
		SymbolicFunctionType refIdxFuncType, refRangeFuncType;

		valueSetReferenceType = objectFactory.canonic(typeFactory.tupleType(
				objectFactory.stringObject("VSRef"),
				typeFactory.sequence(new SymbolicType[] { integerType })));
		refIdxSeq = objectFactory.canonic(typeFactory.sequence(
				new SymbolicType[] { valueSetReferenceType, integerType }));
		refRangeSeq = objectFactory.canonic(
				typeFactory.sequence(new SymbolicType[] { valueSetReferenceType,
						integerType, integerType, integerType }));
		refIdxFuncType = objectFactory.canonic(
				typeFactory.functionType(refIdxSeq, valueSetReferenceType));
		refRangeFuncType = objectFactory.canonic(
				typeFactory.functionType(refRangeSeq, valueSetReferenceType));
		arrayElementReferenceFunction = symbolicConstant(
				objectFactory.stringObject("VSArrayElementRef"),
				refIdxFuncType);
		arraySectionReferenceFunction = symbolicConstant(
				objectFactory.stringObject("VSArraySectionRef"),
				refRangeFuncType);
		tupleComponentReferenceFunction = symbolicConstant(
				objectFactory.stringObject("VSTupleComponentRef"),
				refIdxFuncType);
		unionMemberReferenceFunction = symbolicConstant(
				objectFactory.stringObject("VSUnionMemberRef"), refIdxFuncType);
		offsetReferenceFunction = symbolicConstant(
				objectFactory.stringObject("VSOffsetRef"), refIdxFuncType);
		identityReference = objectFactory.canonic(new CommonVSIdentityReference(
				valueSetReferenceType, numericFactory.oneInt()));
	}

	/**
	 * 
	 * @return the symbolic type of {@link ValueSetReference}s
	 */
	SymbolicType valueSetReferenceType() {
		return valueSetReferenceType;
	}

	/**
	 * The general interface for creating {@link ValueSetReference}s
	 * 
	 * @param operator
	 *            The {@link SymbolicOperator} of the {@link ValueSetReference}
	 *            instance
	 * @param args
	 *            Arguments of the {@link ValueSetReference} instance
	 * @return a {@link ValueSetReference} expression, which has the given
	 *         operator and arguments
	 */
	ValueSetReference valueSetReference(SymbolicOperator operator,
			SymbolicObject... args) {
		if (operator == SymbolicOperator.TUPLE) {
			return vsIdentityReference();
		} else {
			assert operator == SymbolicOperator.APPLY;
			if (args[0] == arrayElementReferenceFunction) {
				@SuppressWarnings("unchecked")
				SimpleSequence<? extends SymbolicObject> seq = (SimpleSequence<? extends SymbolicObject>) args[1];
				return vsArrayElementReference((ValueSetReference) seq.get(0),
						(NumericExpression) seq.get(1));
			} else if (args[0] == arraySectionReferenceFunction) {
				@SuppressWarnings("unchecked")
				SimpleSequence<? extends SymbolicObject> seq = (SimpleSequence<? extends SymbolicObject>) args[1];
				return vsArraySectionReference((ValueSetReference) seq.get(0),
						(NumericExpression) seq.get(1),
						(NumericExpression) seq.get(2),
						(NumericExpression) seq.get(3));
			} else if (args[0] == this.tupleComponentReferenceFunction) {
				@SuppressWarnings("unchecked")
				SimpleSequence<? extends SymbolicObject> seq = (SimpleSequence<? extends SymbolicObject>) args[1];
				IntObject idx = objectFactory.intObject(
						((IntegerNumber) ((Constant) seq.get(1)).number())
								.intValue());

				return vsTupleComponentReference((ValueSetReference) seq.get(0),
						idx);
			} else if (args[0] == this.unionMemberReferenceFunction) {
				@SuppressWarnings("unchecked")
				SimpleSequence<? extends SymbolicObject> seq = (SimpleSequence<? extends SymbolicObject>) args[1];
				IntObject idx = objectFactory.intObject(
						((IntegerNumber) ((Constant) seq.get(1)).number())
								.intValue());

				return vsUnionMemberReference((ValueSetReference) seq.get(0),
						idx);
			} else {
				assert args[0] == this.offsetReferenceFunction;
				@SuppressWarnings("unchecked")
				SimpleSequence<? extends SymbolicObject> seq = (SimpleSequence<? extends SymbolicObject>) args[1];
				return vsOffsetReference((ValueSetReference) seq.get(0),
						(NumericExpression) seq.get(1));
			}
		}
	}

	/**
	 * Creating an instance of {@link VSArraySectionReference}
	 */
	VSArraySectionReference vsArraySectionReference(ValueSetReference parent,
			NumericExpression lower, NumericExpression upper,
			NumericExpression step) {
		return objectFactory.canonic(new CommonVSArraySectionReference(
				valueSetReferenceType, arraySectionReferenceFunction,
				makeSequence(parent, lower, upper, step)));
	}

	/**
	 * Creating an instance of {@link VSArrayElementReference}
	 */
	VSArrayElementReference vsArrayElementReference(ValueSetReference parent,
			NumericExpression index) {
		return objectFactory.canonic(new CommonVSArrayElementReference(
				valueSetReferenceType, arrayElementReferenceFunction,
				makeSequence(parent, index)));
	}

	/**
	 * Creating an instance of {@link VSOffsetReference}
	 */
	VSOffsetReference vsOffsetReference(ValueSetReference parent,
			NumericExpression offset) {
		return objectFactory
				.canonic(new CommonVSOffsetReference(valueSetReferenceType,
						offsetReferenceFunction, makeSequence(parent, offset)));
	}

	/**
	 * Creating an instance of {@link VSTupleComponentReference}
	 */
	VSTupleComponentReference vsTupleComponentReference(
			ValueSetReference parent, IntObject index) {
		NumericExpression idx = numericFactory
				.number(objectFactory.numberObject(numericFactory
						.numberFactory().integer(index.getInt())));
		return objectFactory.canonic(new CommonVSTupleComponentReference(
				valueSetReferenceType, tupleComponentReferenceFunction,
				makeSequence(parent, idx), index));
	}

	/**
	 * Creating an instance of {@link VSUnionMemberReference}
	 */
	VSUnionMemberReference vsUnionMemberReference(ValueSetReference parent,
			IntObject index) {
		NumericExpression idx = numericFactory
				.number(objectFactory.numberObject(numericFactory
						.numberFactory().integer(index.getInt())));
		return objectFactory.canonic(new CommonVSUnionMemberReference(
				valueSetReferenceType, unionMemberReferenceFunction,
				makeSequence(parent, idx), index));
	}

	/**
	 * Creating an instance of {@link VSIdentityReference}
	 */
	VSIdentityReference vsIdentityReference() {
		return identityReference;
	}

	/**
	 * Normalize a set of {@link ValueSetReference}s, which are associated with
	 * a {@link SymbolicType} "valueType":
	 * <ol>
	 * <li>all value set references will have the same depth as the one, which
	 * has the maximum depth, in them; reference:
	 * {@link #depth(ValueSetReference)}</li>
	 * <li>combine array element/slice references to array slice references in
	 * trivial cases</li>
	 * <li>get rid of duplicated value set references</li>
	 * <li>the same set of value set references will be in a canonicalized order
	 * from smallest to the greatest</li>
	 * </ol>
	 *
	 * @param valueType
	 *            the symbolic type of the value, subset of which are referred
	 *            by the given value set references
	 * @param vsRefs
	 *            an array of {@link ValueSetReference}s
	 * @return an array of normalized {@link ValueSetReference}s
	 */
	ValueSetReference[] normalize(SymbolicType valueType,
			ValueSetReference[] vsRefs) {
		vsRefs = toMaxDepth(deleteSubReferences(vsRefs), valueType);
		Arrays.sort(vsRefs, objectFactory.comparator());
		return vsRefs;
	}

	ValueSetReference[] simplify(Reasoner reasoner, SymbolicType valueType,
			ValueSetReference[] vsRefs) {
		vsRefs = normalize(valueType, vsRefs);

		ValueSetReference[][] groups = grouping(vsRefs);
		int finalNumRefs = 0;

		for (int i = 0; i < groups.length; i++) {
			groups[i] = simplifyGroup(reasoner, groups[i]);
			finalNumRefs += groups[i].length;
		}

		ValueSetReference[] results = new ValueSetReference[finalNumRefs];

		finalNumRefs = 0;
		for (ValueSetReference[] group : groups) {
			System.arraycopy(group, 0, results, finalNumRefs, group.length);
			finalNumRefs += group.length;
		}
		Arrays.sort(results, objectFactory.comparator());
		return results;
	}

	/* ******************** Operations ************************/
	/**
	 * <p>
	 * Tests if the given set of {@link ValueSetReference}s "superRefs" is a
	 * super set of "subRefs".
	 * </p>
	 * 
	 * @param valueType
	 *            the symbolic type that all elements in "superRefs" and
	 *            "subRefs" are referring to
	 * @param superRefs
	 *            the set of {@link ValueSetReference}s that will be tested if
	 *            it is a super set of another
	 * @param subRefs
	 *            the set of {@link ValueSetReference}s that will be tested if
	 *            it is a subset of another
	 * @return a boolean expression representing the test result
	 */
	BooleanExpression valueSetContains(SymbolicType valueType,
			ValueSetReference[] superRefs, ValueSetReference subRefs[]) {
		BooleanExpressionFactory boolFactory = numericFactory.booleanFactory();
		BooleanExpression result = boolFactory.trueExpr();
		ValueSetReference[][] refsToSameDepth = toMaxDepth(valueType, superRefs,
				subRefs);

		superRefs = refsToSameDepth[0];
		subRefs = refsToSameDepth[1];
		for (ValueSetReference subRef : subRefs) {
			ValueSetReference[] candidates = getSameConcreteStructureAs(
					superRefs, subRef, true).left;

			// no value set reference in "super" contains the "subRef"
			if (candidates.length == 0)
				return boolFactory.falseExpr();

			List<List<VSRefComp>> superDomain = getDomain(
					Arrays.asList(candidates));
			List<VSRefComp> subDomain = getDomain(subRef,
					superDomain.get(0).size());
			BooleanExpression contains = contains(superDomain, subDomain);
			result = boolFactory.and(contains, result);
		}
		return result;
	}

	/**
	 * <p>
	 * Test if two value set reference have no intersection, i.e., if they are
	 * used to refer the same object, their referred parts have no overlap.
	 * </p>
	 *
	 * <p>
	 * This method returns the condition that is true iff the two value set
	 * references have no intersection.
	 * </p>
	 *
	 * @param valueType
	 *            the type of the value that the given value set reference
	 *            associated with
	 * @param vsr0
	 *            a value set reference
	 * @param vsr1
	 *            a value set reference
	 * @return the boolean condition that is true iff the two value set
	 *         references have no intersection.
	 */
	BooleanExpression valueSetNoIntersect(SymbolicType valueType,
			ValueSetReference vsr0, ValueSetReference vsr1) {
		ValueSetReference[] vsr0arr = new ValueSetReference[] { vsr0 };
		ValueSetReference[] vsr1arr = new ValueSetReference[] { vsr1 };
		ValueSetReference[][] vsrsOfMaxDepth = toMaxDepth(valueType, vsr0arr,
				vsr1arr);
		BooleanExpression result = numericFactory.booleanFactory().trueExpr();

		assert vsrsOfMaxDepth.length == 2;
		for (ValueSetReference v0 : vsrsOfMaxDepth[0])
			for (ValueSetReference v1 : vsrsOfMaxDepth[1])
				if (sameConcreteStructure(v0, v1, false)) {
					List<List<VSRefComp>> doms = getDomain(
							Arrays.asList(v0, v1));
					List<VSRefComp> dom0 = doms.get(0);
					List<VSRefComp> dom1 = doms.get(1);

					assert dom0.size() == dom1.size();
					if (dom0.size() == 0) // same object
						return numericFactory.booleanFactory().falseExpr();

					BooleanExpression cond = disjoint(dom0, dom1);
					result = numericFactory.booleanFactory().and(result, cond);
				}
		return result;
	}
	
	ValueSetReference[] valueSetDiff(SymbolicType valueType,
			ValueSetReference[] refs0, ValueSetReference[] refs1) {
		ValueSetReference[][] vsrsOfMaxDepth = toMaxDepth(valueType, refs0, refs1);
		assert vsrsOfMaxDepth.length == 2;
		List<ValueSetReference> workList = new LinkedList<ValueSetReference>(
				Arrays.asList(vsrsOfMaxDepth[0])),
				processedList = new LinkedList<>();
		
		for (ValueSetReference v1 : vsrsOfMaxDepth[1]) {
			List<VSRefComp> dom1 = getDomain(Arrays.asList(v1)).get(0);
			for (ValueSetReference v0 : workList) {
				if (sameConcreteStructure(v0, v1, false)) {
					List<VSRefComp> dom0 = getDomain(Arrays.asList(v0)).get(0);
					int domSize = dom0.size();
					ArrayList<SymbolicRange> origRanges = new ArrayList<>(
							domSize),
							lowerRanges = new ArrayList<>(domSize),
							upperRanges = new ArrayList<>(domSize);

					Iterator<VSRefComp> dom1Iter = dom1.iterator();
					for (VSRefComp comp0 : dom0) {
						VSRefComp comp1 = dom1Iter.next();
						origRanges.add(comp0.range);
						
						SymbolicRange[] compDiff = rangeFactory
								.diff(comp0.range, comp1.range);
						lowerRanges.add(compDiff[0]);
						upperRanges.add(compDiff[1]);
					}
					for (int i = 0; i < domSize; i++) {
						SymbolicRange rangeTmp = origRanges.get(i);
						origRanges.set(i, lowerRanges.get(i));
						processedList
								.add(replaceArraySections(origRanges, v0));
						origRanges.set(i, upperRanges.get(i));
						processedList
								.add(replaceArraySections(origRanges, v0));
						origRanges.set(i, rangeTmp);
					}
				} else {
					processedList.add(v0);
				}
			}
			workList = processedList;
			processedList = new LinkedList<>();
		}
		return workList.toArray(new ValueSetReference[0]);
	}

	/**
	 * <p>
	 * Applying a default widening operator to a value set template (in the form
	 * of a valueType and a set of value set references).
	 * </p>
	 * 
	 * <p>
	 * The default widening operator is defined by: <code>
	 * INTPUT refs : a set of value set references
	 * OUTPUT out  : a set of value set references
	 *   result : empty set
	 *   for-each (group : grouping(refs)) 
	 *     if (|group| > 1) 
	 *       result.add(widening(group))
	 *   return result
	 * </code> where "widening" by default is defined by
	 * {@link #defaultWidening(SymbolicType, ValueSetReference[])}. Also see
	 * {@link #grouping(ValueSetReference[])}.
	 * </p>
	 * 
	 * @param valueType
	 *            the symbolic type of the value where the given set of
	 *            {@link ValueSetReference}s refer to
	 * @param refs
	 *            a set of {@link ValueSetReference}s
	 * @return a java-array of value set references after the widening operation
	 */
	ValueSetReference[] valueSetWidening(Reasoner reasoner,
			SymbolicType valueType, ValueSetReference[] refs) {
		refs = simplify(reasoner, valueType, refs);
		ValueSetReference[][] groups = grouping(refs);
		List<ValueSetReference> result = new LinkedList<>();

		for (ValueSetReference[] group : groups)
			if (group.length > 1)
				result.add(defaultWidening(valueType, group));
			else
				result.add(group[0]);

		ValueSetReference[] ret = new ValueSetReference[result.size()];

		result.toArray(ret);
		return ret;
	}

	ValueSetReference[] valueSetProtectiveWidening(Reasoner reasoner,
			SymbolicType valueType, ValueSetReference[] mRefs,
			ValueSetReference[] pRefs) {
		mRefs = simplify(reasoner, valueType, mRefs);
		pRefs = normalize(valueType, pRefs);

		ArrayList<ValueSetReference[]> mGroups = new ArrayList<>(mRefs.length);
		ArrayList<ValueSetReference[]> pGroups = new ArrayList<>(mRefs.length);

		for (int remaining = mRefs.length; remaining != 0; remaining = mRefs.length) {
			ValueSetReference modelRef = mRefs[0];
			Pair<ValueSetReference[], ValueSetReference[]> sames_remains = getSameConcreteStructureAs(
					mRefs, modelRef, false);
			mRefs = sames_remains.right;
			mGroups.add(sames_remains.left);

			sames_remains = getSameConcreteStructureAs(pRefs, modelRef, false);
			pRefs = sames_remains.right;
			pGroups.add(sames_remains.left);
		}
		List<ValueSetReference> result = new LinkedList<>();

		for (int i = 0; i < mGroups.size(); ++i) {
			result.addAll(protectiveWidening(reasoner, valueType,
					mGroups.get(i), pGroups.get(i)).mWidened);
		}

		ValueSetReference[] ret = new ValueSetReference[result.size()];

		result.toArray(ret);
		return ret;
	}

	ValueSetReference[] valueSetElimWidening(Reasoner reasoner,
			SymbolicType valueType, ValueSetReference[] refs,
			SymbolicExpression elimExpr, NumericExpression lower,
			NumericExpression upper) {
		refs = simplify(reasoner, valueType, refs);
		if (elimExpr.operator() == SymbolicOperator.SYMBOLIC_CONSTANT) {
			List<ValueSetReference> result = new LinkedList<>();

			for (ValueSetReference ref : refs)
				result.add(elimWidening(reasoner, valueType, ref,
						(SymbolicConstant) elimExpr, lower, upper));

			ValueSetReference[] ret = new ValueSetReference[result.size()];

			result.toArray(ret);
			return ret;
		}
		return refs;
	}

	/* ************************ private methods **************************/
	private class VSRefComp {
		private VSReferenceKind refKind;
		private SymbolicRange range;
		private SymbolicType parentType;

		private VSRefComp(SymbolicType parentType, VSReferenceKind refKind,
				SymbolicRange range) {
			this.range = range;
			this.refKind = refKind;
			this.parentType = parentType;
		}

		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (this.getClass() != other.getClass())
				return false;
			VSRefComp castOther = (VSRefComp) other;
			return refKind.equals(castOther.refKind)
					&& range.equals(castOther.range);
		}

		public VSReferenceKind refKind() {
			return refKind;
		}

		public SymbolicRange range() {
			return range;
		}

		public SymbolicType parentType() {
			return parentType;
		}
		
		public String toString() {
			return refKind.toString()+"("+parentType+", "+range+")";
		}
	}

	private boolean domainKind(VSReferenceKind k) {
		switch (k) {
		case ARRAY_ELEMENT:
		case ARRAY_SECTION:
		case OFFSET:
			return true;
		default:
			return false;
		}
	}

	private VSRefComp vsRefComp(SymbolicType type, VSReferenceKind kind,
			SymbolicRange range) {
		assert kind == VSReferenceKind.ARRAY_SECTION || range
				.getRangeKind() == RangeKind.SINGLETON : "Only array sections"
						+ " are allowed non-singleton ranges.";
		return new VSRefComp(type, kind, range);
	}

	private VSRefComp vsRefComp(VSReferenceKind kind, SymbolicRange range) {
		return vsRefComp(null, kind, range);
	}

	private VSRefComp vsRefComp(SymbolicType type, ValueSetReference vsr) {
		VSReferenceKind kind = vsr.valueSetReferenceKind();

		switch (kind) {
		case ARRAY_ELEMENT:
			return vsRefComp(type, kind, rangeFactory
					.symbolicRange(((VSArrayElementReference) vsr).getIndex()));
		case ARRAY_SECTION:
			VSArraySectionReference sectionRef = (VSArraySectionReference) vsr;

			return vsRefComp(type, kind,
					rangeFactory.symbolicRange(sectionRef.lowerBound(),
							sectionRef.upperBound(), sectionRef.step()));
		case OFFSET:
			return vsRefComp(type, kind, rangeFactory
					.symbolicRange(((VSOffsetReference) vsr).getOffset()));
		case TUPLE_COMPONENT:
			return vsRefComp(type, kind,
					rangeFactory.symbolicRange(numericFactory
							.number(((VSTupleComponentReference) vsr).getIndex()
									.getInt())));
		case UNION_MEMBER:
			return vsRefComp(type, kind,
					rangeFactory.symbolicRange(
							numericFactory.number(((VSUnionMemberReference) vsr)
									.getIndex().getInt())));
		case IDENTITY:
			break;
		}
		return null;
	}

	private VSRefComp vsRefComp(ValueSetReference vsr) {
		return vsRefComp(null, vsr);
	}

	private ValueSetReference valueSetReference(ValueSetReference parent,
			VSRefComp child) {
		SymbolicRange range = child.range();
		switch (child.refKind()) {
		case ARRAY_ELEMENT:
		case ARRAY_SECTION:
			if (range.getRangeKind() == RangeKind.SINGLETON)
				return vsArrayElementReference(parent, range.getLower());
			else
				return vsArraySectionReference(parent, range.getLower(),
						range.getUpper(), range.getStep());
		case OFFSET:
			return vsOffsetReference(parent, range.getLower());
		case TUPLE_COMPONENT:
			return vsTupleComponentReference(parent,
					objectFactory.intObject(((IntegerNumber) numericFactory
							.extractNumber(range.getLower())).intValue()));
		case UNION_MEMBER:
			return vsUnionMemberReference(parent,
					objectFactory.intObject(((IntegerNumber) numericFactory
							.extractNumber(range.getLower())).intValue()));
		case IDENTITY:
			break;
		}
		return vsIdentityReference();
	}

	private SymbolicConstant symbolicConstant(StringObject name,
			SymbolicType type) {
		if (type.isNumeric())
			return numericFactory.symbolicConstant(name, type);
		return objectFactory.canonic(new CommonSymbolicConstant(name, type));
	}

	private SymbolicSequence<SymbolicExpression> makeSequence(
			ValueSetReference parent, SymbolicExpression... indices) {
		SymbolicExpression seqEles[] = new SymbolicExpression[indices.length
				+ 1];

		System.arraycopy(indices, 0, seqEles, 1, indices.length);
		seqEles[0] = parent;
		return objectFactory.sequence(seqEles);
	}

	/**
	 * <p>
	 * <b>pre-condition:</b> 1) the given "group" is returned by
	 * {@link #grouping(ValueSetReference[])}; 2) group.length > 1
	 * </p>
	 * 
	 * <p>
	 * Definition: <code>
	 * while (|group| > 1) {
	 *   for any pair (r0, r1 : group) {
	 *       r = apply(r0, r1)
	 *       group.remove(r0)
	 *       group.remove(r1)
	 *       group.add(r)
	 *   }
	 *   return group;
	 * }
	 * </code> where "apply" is defined by <code>
	 *  apply(r0, r1) = 
	 *    if (r0 is array element/section reference && r0 != r1)
	 *      return arraySectionRef(apply(parent(r0), parent(r1)), [0 .. extent : 1])
	 *    else if (r0 is non-trivial)
	 *      return r0[apply(parent(r0), parent(r1)) / parent(r0)] // replace parent
	 *    else 
	 *      return r0;
	 * </code>
	 * </p>
	 */
	private ValueSetReference defaultWidening(SymbolicType valueType,
			ValueSetReference[] group) {
		int length = group.length;

		while (length > 1) {
			ValueSetReference r0 = group[0];
			ValueSetReference r1 = group[length - 1];

			group[0] = defaultWideningWorker(valueType, r0, r1);
			length--;
		}
		return group[0];
	}

	/**
	 * Recursive helper method for
	 * {@link #defaultWidening(SymbolicType, ValueSetReference[])}
	 */
	private ValueSetReference defaultWideningWorker(SymbolicType valueType,
			ValueSetReference r0, ValueSetReference r1) {
		NumericExpression[] args0 = null;
		NumericExpression[] args1;
		boolean isArrayRef = false;

		if (r0.isIdentityReference()) {
			assert r1.isIdentityReference();
			return r0;
		}
		if (r0.isArrayElementReference()) {
			VSArrayElementReference r = (VSArrayElementReference) r0;

			args0 = new NumericExpression[] { r.getIndex() };
			isArrayRef = true;
		} else if (r0.isArraySectionReference()) {
			VSArraySectionReference r = (VSArraySectionReference) r0;

			args0 = new NumericExpression[] { r.lowerBound(), r.upperBound(),
					r.step() };
			isArrayRef = true;
		}
		if (isArrayRef) {
			if (r1.isArrayElementReference()) {
				VSArrayElementReference r = (VSArrayElementReference) r1;

				args1 = new NumericExpression[] { r.getIndex() };
			} else {
				VSArraySectionReference r = (VSArraySectionReference) r1;

				args1 = new NumericExpression[] { r.lowerBound(),
						r.upperBound(), r.step() };
			}
			if (!Arrays.equals(args0, args1)) {
				// widening:
				SymbolicArrayType arrTy = (SymbolicArrayType) referredType(
						valueType, ((NTValueSetReference) r0).getParent());
				ValueSetReference parent = defaultWideningWorker(valueType,
						((NTValueSetReference) r0).getParent(),
						((NTValueSetReference) r1).getParent());
				if (arrTy.isComplete())
					return vsArraySectionReference(parent,
							numericFactory.zeroInt(),
							((SymbolicCompleteArrayType) arrTy).extent(),
							numericFactory.oneInt());
				else
					return parent;
			}
		}

		@SuppressWarnings("unchecked")
		SimpleSequence<SymbolicExpression> parentIndices = (SimpleSequence<SymbolicExpression>) r0
				.argument(1);
		ValueSetReference parent = defaultWideningWorker(valueType,
				((NTValueSetReference) r0).getParent(),
				((NTValueSetReference) r1).getParent());

		parentIndices = (SimpleSequence<SymbolicExpression>) parentIndices
				.set(0, parent);
		return valueSetReference(SymbolicOperator.APPLY, r0.argument(0),
				parentIndices);
	}

	private class ProtectiveWideningResult {
		List<ValueSetReference> mWidened;
		List<ValueSetReference> pProtected;
		List<ValueSetReference> pDamaged;
	}

	private ProtectiveWideningResult protectiveWidening(Reasoner reasoner,
			SymbolicType valueType, ValueSetReference[] mGroup,
			ValueSetReference[] pGroup) {
		assert mGroup.length > 0;
		List<List<VSRefComp>> mDomains = getDomain(valueType,
				Arrays.asList(mGroup));
		List<List<VSRefComp>> pDomains = getDomain(Arrays.asList(pGroup));

		ProtectiveWideningResult result = new ProtectiveWideningResult();
		result.mWidened = new ArrayList<>(mGroup.length);

		List<Integer> damagedIndices = new ArrayList<>(pGroup.length);
		List<Integer> undamagedIndices = new ArrayList<>(pGroup.length);

		// Initially all elements of pGroup are considered undamaged.
		for (int i = 0; i < pGroup.length; ++i) {
			undamagedIndices.add(i);
		}

		/*
		 * We will call elements of the mGroup array "mRefs." Similarly, "pRefs"
		 * refer to elements of the pGroup array.
		 * 
		 * We say that a pRef p is "guarded" from an mRef m if we are able to
		 * prove that mRef does not cover pRef. A pRef p is said to be "damaged"
		 * if it was not guarded by some encountered mRef m.
		 */
		for (int mInd = 0; mInd < mGroup.length; ++mInd) {
			// The pRefs "at risk" of being damaged by m.
			List<Integer> riskSet = undamagedIndices;
			// The list of elements found to be guarded from m
			List<Integer> guardedIndices = new ArrayList<>(
					undamagedIndices.size());
			List<VSRefComp> mDomain = mDomains.get(mInd);
			List<SymbolicRange> widenedRanges = new ArrayList<>(mDomain.size());
			int level = 0;
			for (VSRefComp mComp : mDomain) {
				SymbolicRange mRange = mComp.range();
				// upper bounds of pRef ranges found to fall strictly below
				// mRange
				List<NumericExpression> lowerBounds = new ArrayList<>(
						pGroup.length);
				// lower bounds of pRef ranges found to fall strictly above
				// mRange
				List<NumericExpression> strictUpperBounds = new ArrayList<>(
						pGroup.length);
				// pRefs with ranges not provably disjoint from mRange
				List<Integer> newRiskSet = new ArrayList<>(riskSet.size());

				for (int pInd : riskSet) {
					SymbolicRange pRange = pDomains.get(pInd).get(level)
							.range();
					boolean guarded = false;

					if (mComp.refKind() == VSReferenceKind.OFFSET && reasoner
							.isValid(rangeFactory.neq(mRange, pRange))) {
						guarded = true;
					} else {
						if (reasoner.isValid(
								rangeFactory.strictlyBelow(pRange, mRange))) {
							guarded = true;

							// p is guarded from m since pRange is below mRange
							lowerBounds.add(pRange.getUpper());
						} else if (reasoner.isValid(
								rangeFactory.strictlyBelow(mRange, pRange))) {
							guarded = true;

							// p is guarded from m since pRange is above mRange
							strictUpperBounds.add(pRange.getLower());
						}
					}

					if (guarded)
						guardedIndices.add(pInd);
					else
						newRiskSet.add(pInd);
				}

				if (mComp.refKind() == VSReferenceKind.OFFSET) {
					widenedRanges.add(null);
				} else {
					NumericExpression lower = lowerBounds.isEmpty()
							? numericFactory.zeroInt()
							: numericFactory.max(lowerBounds);

					assert mComp.parentType()
							.typeKind() == SymbolicTypeKind.ARRAY;
					SymbolicArrayType parentType = (SymbolicArrayType) mComp
							.parentType();
					assert parentType.isComplete();

					NumericExpression upper = strictUpperBounds.isEmpty()
							? ((SymbolicCompleteArrayType) parentType).extent()
							: numericFactory.min(strictUpperBounds);

					widenedRanges.add(rangeFactory.symbolicRange(lower, upper));
				}
				/*
				 * At this point, newRiskSet holds the indices from riskSet that
				 * could not be proven to be disjoint from m. Hence, we can
				 * narrow our search at the next structural level to only
				 * include these indices
				 */
				riskSet = newRiskSet;
				++level;
			}
			// At this point, anything left in the riskSet potentially
			// overlapped with m at every level and hence it is damaged. All
			// other indices are contained in guardedIndices
			damagedIndices.addAll(riskSet);
			// Update undamgedIndices to only include the elements that were
			// guarded by m
			undamagedIndices = guardedIndices;

			result.mWidened
					.add(replaceArraySections(widenedRanges, mGroup[mInd]));
		}
		result.pProtected = new ArrayList<>(undamagedIndices.size());

		for (int i : undamagedIndices) {
			result.pProtected.add(pGroup[i]);
		}

		result.pDamaged = new ArrayList<>(damagedIndices.size());

		for (int i : damagedIndices) {
			result.pDamaged.add(pGroup[i]);
		}

		return result;
	}

	private ValueSetReference elimWidening(Reasoner reasoner,
			SymbolicType valueType, ValueSetReference ref,
			SymbolicConstant symConst, NumericExpression lower,
			NumericExpression upper) {
		if (ref.isIdentityReference()) {
			return ref;
		}
		NumericExpression inclusiveUpper = numericFactory.subtract(upper,
				numericFactory.oneInt());

		if (ref.isArrayElementReference()) {
			VSArrayElementReference r = (VSArrayElementReference) ref;
			NumericExpression index = r.getIndex();
			Pair<NumericExpression, NumericExpression> range = widenToRange(
					reasoner, index, symConst, lower, inclusiveUpper);
			if (range != null) {
				ValueSetReference parent = elimWidening(reasoner, valueType,
						r.getParent(), symConst, lower, upper);
				return vsArraySectionReference(parent, range.left,
						numericFactory.add(range.right,
								numericFactory.oneInt()),
						numericFactory.oneInt());
			}
		}
		if (ref.isArraySectionReference()) {
			VSArraySectionReference r = (VSArraySectionReference) ref;
			NumericExpression rLowerBound = r.lowerBound();
			Pair<NumericExpression, NumericExpression> lowerRange = widenToRange(
					reasoner, rLowerBound, symConst, lower, inclusiveUpper);
			NumericExpression newLowerBound = lowerRange == null ? rLowerBound
					: lowerRange.left;

			NumericExpression rUpperBound = r.upperBound();
			Pair<NumericExpression, NumericExpression> upperRange = widenToRange(
					reasoner, rUpperBound, symConst, lower, inclusiveUpper);
			NumericExpression newUpperBound = upperRange == null ? rUpperBound
					: upperRange.right;

			ValueSetReference parent = elimWidening(reasoner, valueType,
					r.getParent(), symConst, lower, upper);
			return vsArraySectionReference(parent, newLowerBound, newUpperBound,
					numericFactory.oneInt());
		}
		@SuppressWarnings("unchecked")
		SimpleSequence<SymbolicExpression> parentIndices = (SimpleSequence<SymbolicExpression>) ref
				.argument(1);
		ValueSetReference parent = elimWidening(reasoner, valueType,
				((NTValueSetReference) ref).getParent(), symConst, lower,
				upper);

		parentIndices = (SimpleSequence<SymbolicExpression>) parentIndices
				.set(0, parent);
		return valueSetReference(SymbolicOperator.APPLY, ref.argument(0),
				parentIndices);
	}

	private class SignedNumericExpression {
		public NumericExpression expr;
		public int sign;

		public SignedNumericExpression(NumericExpression expr, int sign) {
			this.expr = expr;
			this.sign = sign;
		}
	}

	private Pair<NumericExpression, NumericExpression> widenToRange(
			Reasoner reasoner, NumericExpression expr, SymbolicConstant var,
			NumericExpression lower, NumericExpression upper) {
		switch (expr.operator()) {
		case SYMBOLIC_CONSTANT: {
			if (((SymbolicConstant) expr).name() == var.name()) {
				return new Pair<>(lower, upper);
			}
			return null;
		}
		case NEGATIVE: {
			Pair<NumericExpression, NumericExpression> argResult = widenToRange(
					reasoner, (NumericExpression) expr.argument(0), var, lower,
					upper);
			return argResult == null ? null
					: new Pair<>(numericFactory.minus(argResult.right),
							numericFactory.minus(argResult.left));
		}
		case ADD: {
			NumericExpression unwidened = numericFactory.zeroInt(),
					newLower = numericFactory.zeroInt(),
					newUpper = numericFactory.zeroInt();
			boolean widened = false;

			for (SymbolicObject obj : expr.getArguments()) {
				NumericExpression arg = (NumericExpression) obj;
				Pair<NumericExpression, NumericExpression> result = widenToRange(
						reasoner, arg, var, lower, upper);
				if (result != null) {
					widened = true;
					newLower = numericFactory.add(newLower, result.left);
					newUpper = numericFactory.add(newUpper, result.right);
				} else {
					unwidened = numericFactory.add(unwidened, arg);
				}
			}
			return widened
					? new Pair<>(numericFactory.add(newLower, unwidened),
							numericFactory.add(newUpper, unwidened))
					: null;
		}
		case SUBTRACT: {
			Pair<NumericExpression, NumericExpression> leftResult = widenToRange(
					reasoner, (NumericExpression) expr.argument(0), var, lower,
					upper),
					rightResult = widenToRange(reasoner,
							numericFactory.minus(
									(NumericExpression) expr.argument(1)),
							var, lower, upper);
			if (leftResult == null && rightResult == null) {
				return null;
			}
			NumericExpression newLower = leftResult == null
					? numericFactory.zeroInt()
					: leftResult.left,
					newUpper = leftResult == null ? numericFactory.zeroInt()
							: leftResult.right;
			newLower = rightResult == null ? newLower
					: numericFactory.add(newLower, rightResult.left);
			newUpper = rightResult == null ? newUpper
					: numericFactory.add(newUpper, rightResult.right);
			return new Pair<>(newLower, newUpper);
		}
		case MULTIPLY: {
			assert expr.numArguments() > 0;
			List<Pair<NumericExpression, NumericExpression>> widenedRanges = new ArrayList<Pair<NumericExpression, NumericExpression>>(
					expr.numArguments());
			List<NumericExpression> unwidenedRanges = new ArrayList<NumericExpression>(
					expr.numArguments());
			for (SymbolicObject obj : expr.getArguments()) {
				NumericExpression arg = (NumericExpression) obj;
				Pair<NumericExpression, NumericExpression> result = widenToRange(
						reasoner, arg, var, lower, upper);
				if (result == null) {
					unwidenedRanges.add(arg);
				} else {
					widenedRanges.add(result);
				}
			}
			if (widenedRanges.isEmpty()) {
				return null;
			}

			SignedNumericExpression unwidenedProduct = new SignedNumericExpression(
					numericFactory.oneInt(), 1);
			if (!unwidenedRanges.isEmpty()) {
				unwidenedProduct.expr = unwidenedRanges.get(0);
				for (int i = 1; i < unwidenedRanges.size(); i++) {
					unwidenedProduct.expr = numericFactory.multiply(
							unwidenedProduct.expr, unwidenedRanges.get(i));
				}
				unwidenedProduct.sign = reasoner.valid(
						numericFactory.lessThanEquals(numericFactory.zeroInt(),
								unwidenedProduct.expr)) == Prove.RESULT_YES
										? 1
										: (reasoner.valid(
												numericFactory.lessThanEquals(
														unwidenedProduct.expr,
														numericFactory
																.zeroInt())) == Prove.RESULT_YES
																		? -1
																		: 0);
			}

			Pair<SignedNumericExpression, SignedNumericExpression> newRange = new Pair<>(
					unwidenedProduct, unwidenedProduct);
			for (Pair<NumericExpression, NumericExpression> interval : widenedRanges) {
				int leftSign = 0;
				int rightSign = 0;
				if (reasoner.valid(
						numericFactory.lessThanEquals(numericFactory.zeroInt(),
								interval.left)) == Prove.RESULT_YES) {
					leftSign = 1;
					rightSign = 1;
				} else if (reasoner.valid(numericFactory.lessThanEquals(
						interval.right,
						numericFactory.zeroInt())) == Prove.RESULT_YES) {
					leftSign = -1;
					rightSign = -1;
				} else {
					if (reasoner.valid(numericFactory.lessThanEquals(
							interval.left,
							numericFactory.zeroInt())) == Prove.RESULT_YES)
						leftSign = -1;
					if (reasoner.valid(numericFactory.lessThanEquals(
							numericFactory.zeroInt(),
							interval.right)) == Prove.RESULT_YES)
						rightSign = 1;
				}
				newRange = multiplyInterval(newRange,
						new Pair<>(
								new SignedNumericExpression(interval.left,
										leftSign),
								new SignedNumericExpression(interval.right,
										rightSign)));
			}
			return new Pair<NumericExpression, NumericExpression>(
					newRange.left.expr, newRange.right.expr);
		}
		default:
			return null;
		}
	}

	Pair<SignedNumericExpression, SignedNumericExpression> multiplyInterval(
			Pair<SignedNumericExpression, SignedNumericExpression> i1,
			Pair<SignedNumericExpression, SignedNumericExpression> i2) {
		if (i1.left.sign > 0 && i2.left.sign > 0) {
			SignedNumericExpression lower = new SignedNumericExpression(
					numericFactory.multiply(i1.left.expr, i2.left.expr), 1);
			SignedNumericExpression upper = new SignedNumericExpression(
					numericFactory.multiply(i1.right.expr, i2.right.expr), 1);
			return new Pair<SignedNumericExpression, SignedNumericExpression>(
					lower, upper);
		}
		return null;
	}

	/**
	 * <p>
	 * <b>pre-condition:</b> all group members have the same
	 * {@link #depth(ValueSetReference)}
	 * </p>
	 * <p>
	 * Normalize a group of value set references. For "group", see
	 * {@link #grouping(ValueSetReference[])}. For "normalize", see
	 * {@link #normalize(SymbolicType, ValueSetReference[])}
	 * </p>
	 * 
	 * @param group
	 *            a group of value set references
	 * @return normalized group fo value set references
	 */
	private ValueSetReference[] simplifyGroup(Reasoner reasoner,
			ValueSetReference[] group) {
		boolean hasCombined = false;
		ValueSetReference[] result = Arrays.copyOf(group, group.length);
		int length = result.length;

		Arrays.sort(result, objectFactory.comparator());
		do {
			int i, j;
			ValueSetReference combined = null;

			hasCombined = false;
			for (i = 0; i < length; i++) {
				for (j = 0; j < length; j++)
					if (i == j)
						continue;
					else {
						combined = combine(reasoner, result[i], result[j]);
						if (combined != null) {
							hasCombined = true;
							break;
						}
					}
				if (hasCombined) {
					// replace the "result[i]" and "result[j]" with "combined":
					int min = i < j ? i : j;
					int max = min == i ? j : i;

					if (max < length - 1)
						System.arraycopy(result, max + 1, result, max,
								length - max - 1);
					length--;
					result[min] = combined;
					break;
				}
			}
		} while (hasCombined);
		return Arrays.copyOf(result, length);
	}

	/**
	 * Attempt to combine two {@link ValueSetReference}s r0 and r1 into a single
	 * value set reference which is equivalent to the union of r0 and r1. If
	 * such a combination cannot be done soundly then null is returned.
	 * 
	 * If at all possible, the parameters r0 and r1 should be ordered so that r1
	 * is more likely to contain r0 than the other way around.
	 * 
	 * @param reasoner
	 *            A {@link Reasoner} which may be used to determine the
	 *            relationship between the domains of a and b. May be null in
	 *            which case simple syntactic reasoning is used.
	 * @param r0
	 *            a value set reference
	 * @param r1
	 *            a value set reference
	 * @return the combined value set reference, or null if a and b are not
	 *         combinable.
	 */
	private ValueSetReference combine(Reasoner reasoner, ValueSetReference r0,
			ValueSetReference r1) {
		List<List<VSRefComp>> domains = getDomain(Arrays.asList(r0, r1));

		/**
		 * It is sound to combine r0 and r1 iff one of the following conditions
		 * occurs:
		 * 
		 * 1. One of r0 or r1 is subset of the other in which case the result
		 * will be the superset.
		 * 
		 * 2. r0 and r1 have equal domains except at one level, but the union of
		 * these differing VSRefComp can be represented by a single VSRefComp c.
		 * The result in this case is r0 with its differing component replaced
		 * by the combined component c.
		 * 
		 * subInd and superInd are meant for resolving case 1 while diffPos is
		 * meant for resolving case 2.
		 */
		Integer subInd = null, superInd = null;
		Integer diffPos = null;

		ListIterator<VSRefComp> iter0 = domains.get(0).listIterator(),
				iter1 = domains.get(1).listIterator();

		while (iter0.hasNext()) {
			VSRefComp comp0 = iter0.next(), comp1 = iter1.next();
			// We use an array so that we may index it with subInd and superInd
			SymbolicRange[] ranges = new SymbolicRange[] { comp0.range(),
					comp1.range() };
			VSReferenceKind kind = comp0.refKind();
			if (kind != comp1.refKind()) {
				assert kind == VSReferenceKind.ARRAY_ELEMENT
						|| kind == VSReferenceKind.ARRAY_SECTION;
				assert comp1.refKind() == VSReferenceKind.ARRAY_ELEMENT
						|| comp1.refKind() == VSReferenceKind.ARRAY_SECTION;
			}

			// Currently don't support combining ranges with nontrivial steps
			if (!ranges[0].getStep().isOne() || !ranges[1].getStep().isOne())
				return null;
			BooleanExpression rangeEquality = rangeFactory.equals(ranges[0],
					ranges[1]);

			if (!rangeEquality.isTrue()) {
				boolean combineRange = false;

				if (reasoner == null) {
					combineRange = true;
				} else if (superInd != null) {
					assert subInd != null;
					if (!reasoner.isValid(rangeFactory.subset(ranges[subInd],
							ranges[superInd])))
						return null;
				} else if (!reasoner.isValid(rangeEquality)) {
					// Refs with differing offset components can't be combined
					if (kind == VSReferenceKind.OFFSET)
						return null;

					combineRange = true;
					if (reasoner.isValid(
							rangeFactory.subset(ranges[0], ranges[1]))) {
						subInd = 0;
						superInd = 1;
					} else if (reasoner.isValid(
							rangeFactory.subset(ranges[1], ranges[0]))) {
						subInd = 1;
						superInd = 0;
					}
				}

				if (combineRange) {
					// Refs without a subset relationship and multiple diffs
					// cannot be combined.
					if (diffPos != null)
						return null;
					diffPos = iter0.previousIndex();
				}
			}
		}

		if (superInd != null)
			return superInd == 1 ? r1 : r0;
		if (diffPos != null) {
			SymbolicRange combinedRange = rangeFactory.tryUnion(reasoner,
					domains.get(0).get(diffPos).range(),
					domains.get(1).get(diffPos).range());

			if (combinedRange == null)
				return null;
			return replaceWithArraySection(diffPos, combinedRange, r0);
		}
		return r0;
	}

	/**
	 * <p>
	 * Given a depth counter "n" and a value set reference "origin", an ancestor
	 * <code>a</code> of "origin" is referred by: <code>
	 * a = origin;
	 * while(n > 1) {
	 *   if (a is array element/section reference) 
	 *     n--;
	 *    else if (a is offset reference) 
	 *     n--;
	 *   a = parent(a);
	 * }
	 * </code>. And, the result <code>a</code> must not be an
	 * {@link VSOffsetReference}.
	 * </p>
	 * 
	 * <p>
	 * Given a range "newRange", let
	 * <code>a' = arraySectionRef(parent(a), newRange)</code>, this method
	 * returns <code>origin[a' / a]</code>.
	 * </p>
	 * 
	 * <p>
	 * For example, <code>
	 *   replaceArraySection(2, [x .. y : z],  
	 *                       vsOffsetRef(TupleComponentRef(vsArraySectionRef(Identity(), a .. b : c) idx) offset) 
	 *                      )
	 *   = vsOffsetRef(TupleComponentRef(vsArraySectionRef(Identity(), x .. y : z) idx) offset) 
	 * 
	 * </code>
	 * </p>
	 * 
	 * @param index
	 *            the number of array element/section references or offset
	 *            reference that is counted from origin to the ancestor (the
	 *            ancestor itself is counted as well) that will be replaced.
	 * @param newRange
	 *            a range that refers to an array section that will be replaced
	 * @param origin
	 *            the value set reference before the replacement
	 * @return the replaced value set reference
	 */
	private ValueSetReference replaceWithArraySection(int index,
			SymbolicRange newRange, ValueSetReference origin) {
		return replaceArraySectionsWorker(Stream.iterate(0, i -> i + 1)
				.map(i -> i == index ? newRange : null), origin);
	}

	private ValueSetReference replaceArraySections(
			List<SymbolicRange> newRanges, ValueSetReference origin) {
		return replaceArraySectionsWorker(newRanges.stream(), origin);
	}

	private ValueSetReference replaceArraySectionsWorker(
			Stream<SymbolicRange> compStream, ValueSetReference vsr) {
		LinkedList<VSRefComp> stack = new LinkedList<>();
		Iterator<SymbolicRange> compIter = compStream.iterator();

		while (!vsr.isIdentityReference() && compIter.hasNext()) {
			VSReferenceKind compKind = vsr.valueSetReferenceKind();
			SymbolicRange newRange = domainKind(compKind) ? compIter.next()
					: null;

			stack.push(newRange != null
					? vsRefComp(newRange.getRangeKind() == RangeKind.SINGLETON
							? compKind
							: VSReferenceKind.ARRAY_SECTION, newRange)
					: vsRefComp(vsr));

			vsr = ((NTValueSetReference) vsr).getParent();
		}

		while (!stack.isEmpty()) {
			vsr = valueSetReference(vsr, stack.pop());
		}
		return vsr;
	}

	/**
	 * Returns the number of recursive levels of the value set reference
	 * 
	 * @param ref
	 *            a value set reference expression
	 * @return the number of recursive levels of the value set reference
	 */
	private int depth(ValueSetReference ref) {
		if (ref.isIdentityReference())
			return 1;
		else
			return depth(((NTValueSetReference) ref).getParent()) + 1;
	}

	/**
	 * <p>
	 * Given a set of "superDomain": <code>
	 * {superDomain[0][0] X superDomain[1][0] X ... X superDomain[n-1][0]},
	 * {superDomain[0][1] X superDomain[1][1] X ... X superDomain[n-1][1]},
	 * ...
	 * {superDomain[0][m-1] X superDomain[1][m-1] X ... X superDomain[n-1][m-1]}
	 * </code> , where <code>superDomain[i][j]</code> is a range of integers,
	 * and a "subDomain" : <code> 
	 * {subDomain[0] X ... X subDomain[1] ... X subDomain[n-1]}
	 * </code>, returns a boolean expression: <code>
	 * FORALL int e. e in subDomain s.t. (EXISTS int e'. e' in {superDomain[0][0], superDomain[1][0], ... } && e == e')  OR
	 *                                   (EXISTS int e'. e' in {superDomain[0][1], superDomain[1][1], ... } && e == e')  OR
	 *                                   ...
	 *                                   (EXISTS int e'. e' in {superDomain[0][m-1], superDomain[1][m-1], ... } && e == e')  OR
	 * </code>
	 * </p>
	 * 
	 * @param superDomains
	 * @param subDomain
	 * @return
	 */
	private BooleanExpression contains(List<List<VSRefComp>> superDomains,
			List<VSRefComp> subDomain) {
		BooleanExpressionFactory boolFactory = numericFactory.booleanFactory();
		int numDims = subDomain.size();

		if (numDims == 0)
			return boolFactory.trueExpr();

		int numRanges = superDomains.size();
		BooleanExpression forallRestriction = boolFactory.trueExpr();
		// TODO: Figure out a way to ensure we generate a variable not free in
		// the ranges.
		String superEleName = "i", subEleName = "j";
		ArrayList<NumericSymbolicConstant> superEles = new ArrayList<>(numDims);
		ArrayList<NumericSymbolicConstant> subEles = new ArrayList<>(numDims);

		Iterator<VSRefComp> subIter = subDomain.iterator();
		for (int i = 0; i < numDims; ++i) {
			SymbolicRange subRange = subIter.next().range();

			superEles.add((NumericSymbolicConstant) symbolicConstant(
					objectFactory.stringObject(superEleName + i),
					typeFactory.integerType()));
			NumericSymbolicConstant subEle = (NumericSymbolicConstant) symbolicConstant(
					objectFactory.stringObject(subEleName + i),
					typeFactory.integerType());
			subEles.add(subEle);
			assert !subRange.getLower().getFreeVars().contains(subEle)
					&& !subRange.getUpper().getFreeVars().contains(subEle)
					&& !subRange.getStep().getFreeVars().contains(
							subEle) : "bound variable 'i' has been used in expression";
			forallRestriction = boolFactory.and(forallRestriction,
					rangeFactory.inRange(subEle, subRange));
		}

		BooleanExpression existsPred[] = new BooleanExpression[numRanges];
		BooleanExpression forallPred;

		Iterator<List<VSRefComp>> superDomainsIter = superDomains.iterator();
		for (int i = 0; i < numRanges; ++i) {
			existsPred[i] = boolFactory.trueExpr();
			List<VSRefComp> superDomain = superDomainsIter.next();
			assert (superDomain.size() == numDims);
			Iterator<VSRefComp> superDomainIter = superDomain.iterator();

			for (int j = 0; j < numDims; ++j) {
				VSRefComp superRange = superDomainIter.next();
				existsPred[i] = boolFactory.and(existsPred[i], rangeFactory
						.inRange(superEles.get(j), superRange.range()));
				existsPred[i] = boolFactory.and(existsPred[i], numericFactory
						.equals(superEles.get(j), subEles.get(j)));
			}
			for (NumericSymbolicConstant superEle : superEles) {
				existsPred[i] = boolFactory.exists(superEle, existsPred[i]);
			}
		}
		forallPred = boolFactory.or(Arrays.asList(existsPred));
		forallPred = boolFactory.or(boolFactory.not(forallRestriction),
				forallPred);
		for (int i = 0; i < numDims; i++)
			forallPred = boolFactory.forall(subEles.get(i), forallPred);
		return forallPred;
	}

	/**
	 * <p>
	 * Given two "domains": <code>
	 * {range[0] X range[1] X ... X range[n-1]}
	 * </code> and <code>
	 * {range2[0] X range2[1] X ... X range2[n-1]}
	 * </code>, returns the condition that is true iff the two domains have no
	 * intersection, i.e., <code>
	 *   rangeNoIntersect(range[0], range2[0])  OR
	 *   rangeNoIntersects(range[1], range2[1]) OR
	 *   ...                                    OR
	 *   rangeNoIntersects(range[n-1], range2[n-1])
	 * </code>
	 * </p>
	 *
	 * <p>
	 * pre-condition: two domains have the same positive number "n" of ranges
	 * </p>
	 *
	 * @param dom1
	 *            a domain which is represented by a sequence of ranges, each
	 *            range is an array of three NumericExpression
	 * @param dom2
	 *            a domain which is represented by a sequence of ranges, each
	 *            range is an array of three NumericExpression
	 * @return the condition that is true iff the two given domain have no
	 *         intersection
	 */
	private BooleanExpression disjoint(List<VSRefComp> dom1,
			List<VSRefComp> dom2) {
		assert dom1.size() == dom2.size() && dom1.size() > 0;

		Iterator<VSRefComp> iter1 = dom1.iterator(), iter2 = dom2.iterator();
		BooleanExpression result = rangeFactory.disjoint(iter1.next().range(),
				iter2.next().range());

		while (iter1.hasNext()) {
			result = numericFactory.booleanFactory().or(result, rangeFactory
					.disjoint(iter1.next().range(), iter2.next().range()));
		}
		return result;
	}

	/**
	 * <p>
	 * <b>pre-condition</b> the input value set references must 1) have same
	 * depth, 2) have same kind of structure, i.e. their ancestors at the same
	 * level have the same reference kind. 3) for their ancestors at the same
	 * level that have tuple component or union member reference kind, their
	 * field/member indices must be the same.
	 * </p>
	 *
	 * <p>
	 * This method helps obtaining the DOMAINs of some value set references.
	 * Returns a three dimensional array of
	 * <code>NumericExoression[H][num_ranges][3]</code>. H is the number of
	 * recursive levels, at each of which all candidates are array element/slice
	 * references or offset references. A DOMAIN at a recursive level
	 * <code>i</code> is a set of ranges, each of which is taken from the
	 * ancestor of a candidate at recursive level <code>i</code>. A range
	 * consists of an inclusive lower bound, an exclusive higher bound and a
	 * step. Note that for an array element reference or an offset reference who
	 * has a sole argument "index", this method gets range: [index, index+1)
	 * with default step 1.
	 * </p>
	 *
	 * <p>
	 * Note that "NON-array element/slice or offset reference" ancestors at some
	 * recursive levels are not represented in the returned domain. They are not
	 * needed actually.
	 * </p>
	 *
	 * @param refs
	 *            output of
	 *            {@link #getSameConcreteStructureAs(ValueSetReference[], ValueSetReference, boolean)}
	 * @return a 3 dimensional array: first dimension is the number of recursive
	 *         levels, (NOTE that indices in first dimensions are ORDERED from
	 *         the recursive level of higher depth to lower); the second
	 *         dimension is the number of ranges in a recursive level; the third
	 *         dimension is the range: low, high and step.
	 */
	private List<List<VSRefComp>> getDomain(List<ValueSetReference> refs) {
		List<List<VSRefComp>> results = new ArrayList<>(refs.size());

		Integer domainSize = null;

		for (ValueSetReference r : refs) {
			List<VSRefComp> rDom = getDomain(r, domainSize);
			domainSize = rDom.size();
			results.add(rDom);
		}

		return results;
	}

	private List<VSRefComp> getDomain(ValueSetReference vsr,
			Integer domainSize) {
		ArrayList<VSRefComp> domain = domainSize != null
				? new ArrayList<>(domainSize)
				: new ArrayList<>();

		while (!vsr.isIdentityReference()) {
			if (domainKind(vsr.valueSetReferenceKind())) {
				domain.add(vsRefComp(vsr));
			}

			vsr = ((NTValueSetReference) vsr).getParent();
		}

		return domain;
	}

	private List<VSRefComp> getDomain(SymbolicType valueType,
			ValueSetReference vsr) {
		if (valueType == null)
			return getDomain(vsr, null);

		LinkedList<ValueSetReference> refStack = new LinkedList<>();
		int domainSize = 0;

		while (!vsr.isIdentityReference()) {
			refStack.push(vsr);
			if (domainKind(vsr.valueSetReferenceKind())) {
				++domainSize;
			}

			vsr = ((NTValueSetReference) vsr).getParent();
		}

		VSRefComp[] domain = new VSRefComp[domainSize];
		int i = domainSize;

		while (!refStack.isEmpty()) {
			--i;
			vsr = refStack.pop();

			if (domainKind(vsr.valueSetReferenceKind())) {
				domain[i] = vsRefComp(valueType, vsr);
			}
			valueType = referredTypeFromParent(valueType, vsr);
		}

		return new ArrayList<VSRefComp>(Arrays.asList(domain));
	}

	private List<List<VSRefComp>> getDomain(SymbolicType valueType,
			List<ValueSetReference> vsrs) {
		List<List<VSRefComp>> domains = new ArrayList<>(vsrs.size());

		for (ValueSetReference vsr : vsrs) {
			domains.add(getDomain(valueType, vsr));
		}

		return domains;
	}

	/**
	 * <p>
	 * Dividing a set of value set references into several groups. For value set
	 * references <code>R</code> that in a same group, they satisfy such a
	 * condition: <code>
	 *   forall r : R.  getSameConcreteStructureAs(R, r) == R
	 * </code> (see
	 * {@link #getSameConcreteStructureAs(ValueSetReference[], ValueSetReference, boolean)}
	 * ).
	 * </p>
	 * 
	 * <p>
	 * In other words, all elements in one group have the same concrete
	 * structures.
	 * </p>
	 * 
	 * @param refs
	 *            a set of value set references
	 * @return a set of groups of value set references
	 */
	private ValueSetReference[][] grouping(ValueSetReference refs[]) {
		int remaining = refs.length;
		List<ValueSetReference[]> groups = new LinkedList<>();

		while (remaining != 0) {
			ValueSetReference[] oldRefs = refs;
			Pair<ValueSetReference[], ValueSetReference[]> sames_remains;

			sames_remains = getSameConcreteStructureAs(oldRefs, oldRefs[0],
					false);
			refs = sames_remains.right;
			remaining = refs.length;
			groups.add(sames_remains.left);
		}

		ValueSetReference[][] ret = new ValueSetReference[groups.size()][];

		groups.toArray(ret);
		return ret;
	}

	/**
	 * <p>
	 * <b>pre-condition:</b> all input value set references are returned by
	 * {@link #toMaxDepth(ValueSetReference[], SymbolicType)}
	 * </p>
	 * 
	 * <p>
	 * Returns a subset of "refs" that have the same concrete structure as the
	 * given "model"
	 * </p>
	 * 
	 * @param refs
	 *            a set of value set references where ones that have
	 *            {@link #sameConcreteStructure(ValueSetReference, ValueSetReference, boolean)}
	 *            as the given "model" will be returned.
	 * @param model
	 *            a value set reference that the returned value set references
	 *            must have
	 *            {@link #sameConcreteStructure(ValueSetReference, ValueSetReference, boolean)}
	 *            as this one
	 * @param ignoreOffSet
	 *            set to true to ignore {@link VSOffsetReference}s, i.e. the
	 *            offsets of two value set references do not have to be the
	 *            same.
	 * @return a PAIR, LEFT: a subset of "refs" that have the same concrete
	 *         structure as the given "model"; RIGHT: a subset of "refs" that
	 *         have different concrete structure as the given "model".
	 */
	private Pair<ValueSetReference[], ValueSetReference[]> getSameConcreteStructureAs(
			ValueSetReference refs[], ValueSetReference model,
			boolean ignoreOffSet) {
		ValueSetReference sames[] = new ValueSetReference[refs.length];
		ValueSetReference diffs[] = new ValueSetReference[refs.length];
		int ctSame = 0, ctDiff = 0;

		for (int i = 0; i < refs.length; i++)
			if (sameConcreteStructure(refs[i], model, ignoreOffSet))
				sames[ctSame++] = refs[i];
			else
				diffs[ctDiff++] = refs[i];
		return new Pair<>(Arrays.copyOf(sames, ctSame),
				Arrays.copyOf(diffs, ctDiff));
	}

	/**
	 * Helper method for
	 * {@link #getSameConcreteStructureAs(ValueSetReference[], ValueSetReference, boolean)}.
	 * For the rules of determining if two value set references "a" and "b" have
	 * the same concrete structure, i.e. if ancestors of "a" and "b" at the same
	 * recursive level are exactly the same if they have tuple component kind,
	 * union member kind or (optional) offset kind.
	 *
	 * @param vs0
	 *            a value set reference
	 * @param vs1
	 *            a value set reference
	 * @param ignoreOffset
	 *            set to true to ignore {@link VSOffsetReference}s, i.e. the
	 *            offsets of two value set references do not have to be the same
	 *            in order to have same concrete structure.
	 * @return true iff the "vs0" and "vs1" have the same concrete structure
	 */
	private boolean sameConcreteStructure(ValueSetReference vs0,
			ValueSetReference vs1, boolean ignoreOffset) {
		if (diffConcreteStructureKind(vs0, vs1, ignoreOffset))
			return false;
		while (!vs0.isIdentityReference()) {
			switch (vs0.valueSetReferenceKind()) {
			case ARRAY_ELEMENT:
			case ARRAY_SECTION:
				break;
			case OFFSET:
				if (!ignoreOffset)
					if (((VSOffsetReference) vs0)
							.getOffset() != ((VSOffsetReference) vs1)
									.getOffset())
						return false;
				break;
			case TUPLE_COMPONENT:
				if (((VSTupleComponentReference) vs0)
						.getIndex() != ((VSTupleComponentReference) vs1)
								.getIndex())
					return false;
				break;
			case UNION_MEMBER:
				if (((VSUnionMemberReference) vs0)
						.getIndex() != ((VSUnionMemberReference) vs1)
								.getIndex())
					return false;
				break;
			case IDENTITY:
			default:
				throw new SARLException("unreachable");
			}
			vs0 = ((NTValueSetReference) vs0).getParent();
			vs1 = ((NTValueSetReference) vs1).getParent();
			if (diffConcreteStructureKind(vs0, vs1, ignoreOffset))
				return false;
		}
		return true;
	}

	/**
	 * @return true iff the given two value set reference have different kinds
	 *         and at least one of them does not have either ARRAY_ELEMENT,
	 *         ARRAY_SECTION or OFFSET (depends on "ignoreOffset") kind.
	 */
	private boolean diffConcreteStructureKind(ValueSetReference vs0,
			ValueSetReference vs1, boolean ignoreOffset) {
		if (vs0.valueSetReferenceKind() != vs1.valueSetReferenceKind()) {
			VSReferenceKind kind0 = vs0.valueSetReferenceKind(),
					kind1 = vs1.valueSetReferenceKind();
			boolean isStructureKind0 = kind0 != VSReferenceKind.ARRAY_ELEMENT;
			boolean isStructureKind1 = kind1 != VSReferenceKind.ARRAY_ELEMENT;

			isStructureKind0 &= kind0 != VSReferenceKind.ARRAY_SECTION;
			isStructureKind1 &= kind1 != VSReferenceKind.ARRAY_SECTION;

			if (ignoreOffset) {
				isStructureKind0 &= kind0 != VSReferenceKind.OFFSET;
				isStructureKind1 &= kind1 != VSReferenceKind.OFFSET;
			}
			if (isStructureKind0 || isStructureKind1)
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Let <code>m</code> be the one, who has the maximum depth, in the given
	 * {@link ValueSetReference} set. Each {@link ValueSetReference} in the
	 * given set will be extended to have either
	 * <ul>
	 * <li>1) as great depth as it can have if its greatest depth is less than
	 * or equal to the depth of <code>m</code>,</li>
	 * <li>2) the same depth as <code>m<code> otherwise</li>
	 * </ul>
	 * </p>
	 * 
	 * <p> For example, given two value set references 
	 * <code>&a.x, &a.y[0][0]</code> over such a variable: <code>
	 * struct T {
	 *   int x[10];
	 *   int y[10][10];
	 * } a;
	 * </code>, <code>&a.y[0][0]</code> has the maximum depth among two of them.
	 * reference <code>&a.x</code> will be extended to have as great depth as it
	 * can <code>&a.x[0 .. 9]</code> and reference <code>&a.y[0][0]</code> will
	 * be extended to have the same depth as the maximum one which is itself.
	 * </p>
	 * 
	 * @param refs
	 *            a java-array of {@link ValueSetReference}s
	 * @param valueType
	 *            the symbolic type of the value where all value set references
	 *            refer to
	 * @return a set of extended value set references. It may contains more
	 *         elements than the input java-array references
	 */
	private ValueSetReference[] toMaxDepth(ValueSetReference[] refs,
			SymbolicType valueType) {
		return toMaxDepth(valueType, refs)[0];
	}

	/**
	 * <p>
	 * This method is a variant of
	 * {@link #toMaxDepth(ValueSetReference[], SymbolicType)}. This variant
	 * takes multiple (array of) sets of value set references, then processes
	 * them as a big set, finally returns them as multiple (array of) sets. The
	 * returned array of sets corresponds to the input array of sets.
	 * </p>
	 * 
	 * @param valueType
	 *            the symbolic type of the value where all value set references
	 *            refer to
	 * @param refSets
	 *            an array of {@link ValueSetReference} set
	 * @return an array of extended {@link ValueSetReference} set, each of which
	 *         may contains more elements than the input java-array references.
	 *         <p>
	 *         Elements in the returned array correspond to the ones in the
	 *         input (parameter "refSets") array.
	 *         </p>
	 * 
	 */
	private ValueSetReference[][] toMaxDepth(SymbolicType valueType,
			ValueSetReference[]... refSets) {
		int depths[][] = new int[refSets.length][];
		int maxDepth = -1;

		for (int i = 0; i < depths.length; i++) {
			depths[i] = new int[refSets[i].length];
			for (int j = 0; j < refSets[i].length; j++) {
				depths[i][j] = depth(refSets[i][j]);
				if (depths[i][j] > maxDepth)
					maxDepth = depths[i][j];
			}
		}

		List<ValueSetReference[]> results = new LinkedList<>();

		for (int i = 0; i < refSets.length; i++) {
			List<ValueSetReference> result = new LinkedList<>();

			for (int j = 0; j < refSets[i].length; j++)
				result.addAll(Arrays.asList(extend(refSets[i][j],
						referredType(valueType, refSets[i][j]),
						maxDepth - depths[i][j])));

			ValueSetReference[] resultArray = new ValueSetReference[result
					.size()];

			result.toArray(resultArray);
			results.add(resultArray);
		}

		ValueSetReference[][] ret = new ValueSetReference[results.size()][];

		results.toArray(ret);
		return ret;
	}

	/**
	 * Extends the given value set reference, as many as possible, to at most
	 * "toMax" more recursive levels, results in a set of value set references.
	 * 
	 * @param ref
	 *            the value set reference where extends from
	 * @param referredType
	 *            the type of an element in the value subset referred by the
	 *            given value set reference
	 * @param toMax
	 *            the extra (at most) more recursive levels to extend to
	 * @return a set of extended value set references
	 */
	private ValueSetReference[] extend(ValueSetReference ref,
			SymbolicType referredType, int toMax) {
		return extendWorker(new ValueSetReference[] { ref }, referredType,
				toMax);
	}
	
	/**
	 * the recursive helper method for
	 * {@link #extend(ValueSetReference, SymbolicType, int)}
	 */
	private ValueSetReference[] extendWorker(ValueSetReference refs[],
			SymbolicType referredType, int toMax) {
		if (toMax == 0)
			return refs;
		switch (referredType.typeKind()) {
			case ARRAY : {
				SymbolicArrayType arrTy = (SymbolicArrayType) referredType;

				if (!arrTy.isComplete())
					// do not further extend references to incomplete arrays
					return refs;
				NumericExpression extent = ((SymbolicCompleteArrayType) arrTy)
						.extent();
				ValueSetReference[] results = new ValueSetReference[refs.length];

				for (int i = 0; i < results.length; i++)
					results[i] = vsArraySectionReference(refs[i],
							numericFactory.zeroInt(), extent,
							numericFactory.oneInt());
				return extendWorker(results, arrTy.elementType(), toMax - 1);
			}
		case TUPLE: {
			SymbolicTupleType tupleType = (SymbolicTupleType) referredType;
			int numTypes = tupleType.sequence().numTypes();
			List<ValueSetReference> results = new LinkedList<>();

			for (int i = 0; i < numTypes; i++) {
				ValueSetReference[] expandedRefs = new ValueSetReference[refs.length];

				for (int j = 0; j < refs.length; j++)
					expandedRefs[j] = vsTupleComponentReference(refs[j],
							objectFactory.intObject(i));

				results.addAll(Arrays.asList(extendWorker(expandedRefs,
						tupleType.sequence().getType(i), toMax - 1)));
			}

			ValueSetReference[] ret = new ValueSetReference[results.size()];

			results.toArray(ret);
			return ret;
		}
		case UNION: {
			SymbolicUnionType unionType = (SymbolicUnionType) referredType;
			int numTypes = unionType.sequence().numTypes();
			List<ValueSetReference> results = new LinkedList<>();

			for (int i = 0; i < numTypes; i++) {
				ValueSetReference[] expandedRefs = new ValueSetReference[refs.length];

				for (int j = 0; j < refs.length; j++)
					expandedRefs[j] = vsUnionMemberReference(refs[j],
							objectFactory.intObject(i));

				results.addAll(Arrays.asList(extendWorker(expandedRefs,
						unionType.sequence().getType(i), toMax - 1)));
			}

			ValueSetReference[] ret = new ValueSetReference[results.size()];

			results.toArray(ret);
			return ret;
		}
		// unimplemented:
		case MAP:
		case SET:
			throw new SARLException(
					"Unimplemented symbolic types: " + referredType);
		// basics:
		case BOOLEAN:
		case CHAR:
		case FUNCTION:
		case INTEGER:
		case REAL:
		case UNINTERPRETED:
			assert toMax >= 0;
			return refs;
		default:
			throw new SARLException("Unreachable");
		}
	}

	/**
	 * <p>
	 * Deletes a set of value set references <code>D</code> from a given set
	 * <code>R</code>, resulting in R' = R - D, such that each
	 * <code>d in D</code> has exactly one <code>ancestor(d) in R'</code>, and
	 * no <code>r in R'</code> has a strict ancestor in <code>R'</code>.
	 * <code>R'</code> is returned.
	 * </p>
	 * 
	 * @param refs
	 *            the given set of value set references <code>R</code>
	 * @return a subset of the given value set </ode>R'</code> references as
	 *         described above
	 */
	private ValueSetReference[] deleteSubReferences(ValueSetReference refs[]) {
		ValueSetReference[] copies = Arrays.copyOf(refs, refs.length);
		Set<Integer> deletingIndices = new HashSet<>();

		Arrays.sort(copies, new Comparator<ValueSetReference>() {
			@Override
			public int compare(ValueSetReference o1, ValueSetReference o2) {
				return depth(o1) - depth(o2);
			}
		});
		for (int i = 1; i < copies.length; i++) {
			// if any ref with shorter depth than me (copies[i]) is one of my
			// ancestor then add i to deletingIndices.
			for (int j = 0; j < i; j++) {
				ValueSetReference ancestor = copies[i];
				boolean isSubRef = false;

				// Note that if a deleting ref "r" is an ancestor of me, there
				// must be another non-deleting ref which is an ancestor for
				// both "r" and me so that it is ok that we do not check the
				// deletingIndices set.
				while (true) {
					if (ancestor == copies[j]) {
						deletingIndices.add(i);
						isSubRef = true;
						break;
					}
					if (ancestor.isIdentityReference())
						break;
					ancestor = ((NTValueSetReference) ancestor).getParent();
				}
				if (isSubRef)
					break;
			}
		}
		if (deletingIndices.size() > 0) {
			ValueSetReference[] results = new ValueSetReference[copies.length
					- deletingIndices.size()];
			int ct = 0;

			for (int i = 0; i < copies.length; i++)
				if (!deletingIndices.contains(i))
					results[ct++] = copies[i];
			assert ct == results.length;
			return results;
		}
		return copies;
	}

	/**
	 * Returns the type of an element in the value subset referred by the given
	 * value set reference.
	 * 
	 * @param valueType
	 *            the type of the value where the given value set reference
	 *            refers to
	 * @param ref
	 *            a value set reference
	 * @return the type of an element in the value subset referred by the given
	 *         value set reference
	 */
	private SymbolicType referredType(SymbolicType valueType,
			ValueSetReference ref) {
		return ref.valueSetReferenceKind() == VSReferenceKind.IDENTITY
				? valueType
				: referredTypeFromParent(referredType(valueType,
						((NTValueSetReference) ref).getParent()), ref);
	}

	private SymbolicType referredTypeFromParent(SymbolicType parentType,
			ValueSetReference ref) {
		VSReferenceKind kind = ref.valueSetReferenceKind();
		assert kind != VSReferenceKind.IDENTITY;
		switch (ref.valueSetReferenceKind()) {
		case ARRAY_ELEMENT:
		case ARRAY_SECTION:
			return ((SymbolicArrayType) parentType).elementType();
		case TUPLE_COMPONENT: {
			VSTupleComponentReference tupleRef = (VSTupleComponentReference) ref;

			return ((SymbolicTupleType) parentType).sequence()
					.getType(tupleRef.getIndex().getInt());
		}
		case UNION_MEMBER: {
			VSUnionMemberReference tupleRef = (VSUnionMemberReference) ref;

			return ((SymbolicUnionType) parentType).sequence()
					.getType(tupleRef.getIndex().getInt());
		}
		case OFFSET:
			throw new SARLException("Computing referred type of"
					+ " OFFSET ValueSetReference is unsupported");
		case IDENTITY:
		}
		// Unreachable
		return null;
	}
}
