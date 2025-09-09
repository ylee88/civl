package dev.civl.sarl.IF;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.Arrays;

import dev.civl.sarl.IF.expr.BooleanExpression;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dev.civl.sarl.SARL;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.ValueSetReference;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.type.SymbolicType;

public class ValueSetTest {

	private static PrintStream out = System.out;

	private SymbolicUniverse universe;
	private NumericExpression zero, one, two, three, four, five, six;
	private IntObject zeroInt, oneInt;
	private SymbolicType integerType;

	@Before
	public void setUp() throws Exception {
		universe = SARL.newStandardUniverse();
		zero = universe.integer(0);
		one = universe.integer(1);
		two = universe.integer(2);
		three = universe.integer(3);
		four = universe.integer(4);
		five = universe.integer(5);
		six = universe.integer(6);
		integerType = universe.integerType();
		zeroInt = universe.intObject(0);
		oneInt = universe.intObject(1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void valueSetCreation() {
		ValueSetReference ref = universe.vsIdentityReference();
		SymbolicType type = integerType;

		ref = universe.vsTupleComponentReference(ref, oneInt);
		ref = universe.vsArrayElementReference(ref, one);
		ref = universe.vsUnionMemberReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);
		// ref = ARRAY_SEC(UNION_MEMBER(ARRAY_ELEMENT(TUPLE_COMPONENT(<1>, 1) 1)
		// 0) [0 .. 6 : 1]);
		type = universe.arrayType(integerType, six);
		type = universe.unionType(universe.stringObject("ut"),
				Arrays.asList(type, integerType));
		type = universe.arrayType(type, six);
		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(integerType, type));

		ValueSetReference ref1 = universe.vsIdentityReference();

		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		ref1 = universe.vsArrayElementReference(ref1, zero);
		ref1 = universe.vsUnionMemberReference(ref1, zeroInt);
		ref1 = universe.vsArraySectionReference(ref1, zero, six);
		// ref1 = ARRAY_SEC(UNION_MEMBER(ARRAY_ELEMENT(TUPLE_COMPONENT(<1>, 1)
		// 0)
		// 0) [0 .. 6 : 1]);

		ValueSetReference ref2 = universe.vsIdentityReference();

		ref2 = universe.vsTupleComponentReference(ref2, oneInt);
		ref2 = universe.vsArrayElementReference(ref2, zero);
		ref2 = universe.vsUnionMemberReference(ref2, zeroInt);
		// ref2 is same as ref1 but has shorter depth

		SymbolicExpression vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		SymbolicExpression vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref2 });
		ValueSetReference ref3 = universe.vsIdentityReference();

		ref3 = universe.vsTupleComponentReference(ref3, oneInt);
		ref3 = universe.vsArraySectionReference(ref3, zero, two);
		ref3 = universe.vsUnionMemberReference(ref3, zeroInt);
		ref3 = universe.vsArraySectionReference(ref3, zero, six);

		SymbolicExpression vst2 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref3 });

		// {ref, ref1} normalize to ref3
		// {ref, ref2} normalize to ref3
		assertTrue(vst == vst2 && vst == vst1);

	}

	// similar to valueSetCreation() but the two references in a value set
	// template have different depth:
	@Test
	public void referencesWithDifferentDepth() {
		ValueSetReference ref = universe.vsIdentityReference();
		ValueSetReference ref1 = universe.vsIdentityReference();
		SymbolicType type = integerType;

		type = universe.arrayType(integerType, six);
		type = universe.unionType(universe.stringObject("ut"),
				Arrays.asList(type));
		type = universe.arrayType(type, six);
		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(type));

		ref = universe.vsTupleComponentReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);
		ref = universe.vsUnionMemberReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);

		SymbolicExpression vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		SymbolicExpression vst1;

		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		assertTrue(vst == vst1);

		ref1 = universe.vsTupleComponentReference(ref1, zeroInt);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref1 });
		assertTrue(vst == vst1);

		ref1 = universe.vsArraySectionReference(ref1, zero, six);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref1 });
		assertTrue(vst == vst1);

		ref1 = universe.vsUnionMemberReference(ref1, zeroInt);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref1 });
		assertTrue(vst == vst1);
	}

	@Test
	public void referencesWithDifferentDepth2() {
		ValueSetReference ref = universe.vsIdentityReference();
		ValueSetReference ref1 = universe.vsIdentityReference();
		SymbolicType type = integerType;

		type = universe.arrayType(integerType, six);
		type = universe.unionType(universe.stringObject("ut"),
				Arrays.asList(type, type));
		type = universe.arrayType(type, six);
		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(type, type));

		ref = universe.vsTupleComponentReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);
		ref = universe.vsUnionMemberReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);

		SymbolicExpression vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		SymbolicExpression vst1;

		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref1 });
		assertTrue(vst != vst1);
	}

	@Test
	public void referencesWithDifferentDepth3() {
		ValueSetReference ref = universe.vsIdentityReference();
		ValueSetReference ref1 = universe.vsIdentityReference();
		SymbolicType type = integerType;

		type = universe.arrayType(integerType, six);
		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(type, type));
		type = universe.arrayType(type, six);
		type = universe.unionType(universe.stringObject("ut"),
				Arrays.asList(type, type));

		ref = universe.vsUnionMemberReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);
		ref = universe.vsTupleComponentReference(ref, zeroInt);
		ref = universe.vsArraySectionReference(ref, zero, six);

		SymbolicExpression vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		SymbolicExpression vst1;

		ref1 = universe.vsUnionMemberReference(ref1, oneInt);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref, ref1 });
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref1 });
		assertTrue(vst != vst1);
	}

	@Test
	public void rangeCombinations() {
		ValueSetReference ref0, ref1, ref2, ref3, ref4, ref5;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst0;

		type = universe.arrayType(type, six);
		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(type, type));
		vst0 = universe
				.valueSetTemplate(type,
						new ValueSetReference[] {
								universe.vsArraySectionReference(
										universe.vsTupleComponentReference(
												universe.vsIdentityReference(),
												universe.intObject(0)),
										zero, six),
								universe.vsArraySectionReference(
										universe.vsTupleComponentReference(
												universe.vsIdentityReference(),
												universe.intObject(1)),
										zero, six) });

		ref0 = ref1 = ref2 = universe.vsTupleComponentReference(
				universe.vsIdentityReference(), universe.intObject(0));
		ref3 = ref4 = ref5 = universe.vsTupleComponentReference(
				universe.vsIdentityReference(), universe.intObject(1));
		ref0 = universe.vsArraySectionReference(ref0, two, four);
		ref1 = universe.vsArraySectionReference(ref1, zero, two);
		ref2 = universe.vsArraySectionReference(ref2, four, six);

		ref3 = universe.vsArrayElementReference(ref3, two);
		ref4 = universe.vsArraySectionReference(ref4, zero, two);
		ref5 = universe.vsArraySectionReference(ref5, three, six);

		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1, ref2, ref3, ref4, ref5 });
		assertTrue(vst == vst0);
	}

	@Test
	public void rangeCombinations2() {
		ValueSetReference ref0, ref1, ref2, ref3, ref4, ref5;
		SymbolicType type = integerType;
		SymbolicExpression vst;

		type = universe.arrayType(universe.arrayType(type, six), six);
		ref0 = ref1 = ref2 = ref3 = ref4 = ref5 = universe
				.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(
				universe.vsArrayElementReference(ref0, zero), one);
		ref1 = universe.vsArrayElementReference(
				universe.vsArraySectionReference(ref1, zero, three), one);
		ref2 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref2, zero, three), one, two);
		ref3 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref3, zero, three), two, four);
		ref4 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref4, three, four), two, four);
		ref5 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref5, five, six), three, six);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1, ref2, ref3, ref4, ref5 });
		// one combined:
		assertTrue(universe.tupleRead(vst, universe.intObject(1))
				.numArguments() == 4);
	}

	@Test
	public void testContains() {
		Reasoner reasoner = universe.reasoner(universe.trueExpression());
		ValueSetReference ref0, ref1, ref2, ref3, ref4, ref5;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1;

		type = universe.arrayType(universe.arrayType(type, six), six);
		ref0 = ref1 = ref2 = ref3 = ref4 = ref5 = universe
				.vsIdentityReference();

		ref0 = universe.vsArrayElementReference(
				universe.vsArrayElementReference(ref0, zero), zero);
		ref1 = universe.vsArrayElementReference(
				universe.vsArraySectionReference(ref1, zero, two), zero);
		ref2 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref2, zero, three), zero, two);
		ref3 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref3, zero, four), zero, four);
		ref4 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref4, four, six), zero, four);
		ref5 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref5, two, six), zero, four);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1 });
		assertTrue(reasoner.isValid(universe.valueSetContains(vst1, vst)));
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref2 });
		assertTrue(reasoner.isValid(universe.valueSetContains(vst1, vst)));
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1, ref2 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref3 });
		assertTrue(reasoner.isValid(universe.valueSetContains(vst1, vst)));
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref5 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref3, ref4 });
		assertTrue(reasoner.isValid(universe.valueSetContains(vst1, vst)));
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref5 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref3 });
		assertTrue(reasoner.valid(universe.valueSetContains(vst1, vst))
				.getResultType() == ResultType.NO);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref5 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref4 });
		assertTrue(reasoner.valid(universe.valueSetContains(vst1, vst))
				.getResultType() == ResultType.NO);
	}

	@Test
	public void testContains2() {
		Reasoner reasoner = universe.reasoner(universe.trueExpression());
		ValueSetReference ref0, ref1, ref2, ref3;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1;

		type = universe.arrayType(universe.arrayType(type, six), six);
		ref0 = ref1 = ref2 = ref3 = universe.vsIdentityReference();

		ref0 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref0, zero, six), zero, six,
				two);
		ref1 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref1, two, five), zero, six,
				three);
		ref2 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref2, two, five), two, five);
		ref3 = universe.vsArrayElementReference(
				universe.vsArraySectionReference(ref3, two, five), one);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref2 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });
		assertTrue(reasoner.isValid(universe.valueSetContains(vst1, vst)));
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref3 });
		assertTrue(reasoner.valid(universe.valueSetContains(vst1, vst))
				.getResultType() == ResultType.NO);
	}

	@Test
	public void testWidening() {
		ValueSetReference ref0, ref1, ref2, ref3, ref4;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1;

		type = universe.arrayType(universe.arrayType(type, six), six);
		ref0 = ref1 = ref2 = ref3 = ref4 = universe.vsIdentityReference();

		ref0 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref0, two, six), two, four);
		ref1 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref1, two, six), zero, three);
		ref2 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref2, two, five), two, five);
		ref3 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref3, zero, six), zero, six);
		ref4 = universe.vsArraySectionReference(
				universe.vsArraySectionReference(ref4, two, six), zero, four);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref4 });
		vst = universe.valueSetWidening(universe.trueExpression(), vst);
		vst.equals(vst1);
		assertTrue(vst == vst1);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1, ref2 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref3 });
		assertTrue(vst != vst1);
		assertTrue(universe.valueSetWidening(universe.trueExpression(), vst) == vst1);
	}

	@Test
	public void testWidening2() {
		ValueSetReference ref0, ref1, ref2;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		ref0 = ref1 = ref2 = universe.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(ref0, zero);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, two, four);
		ref1 = universe.vsArrayElementReference(ref1, one);
		ref1 = universe.vsTupleComponentReference(ref1, zeroInt);
		ref1 = universe.vsArraySectionReference(ref1, one, three);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });

		ref2 = universe.vsArraySectionReference(ref2, zero, six);
		ref2 = universe.vsTupleComponentReference(ref2, zeroInt);
		ref2 = universe.vsArraySectionReference(ref2, zero, six);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref2 });
		assertTrue(vst != vst1);
		vst = universe.valueSetWidening(universe.trueExpression(), vst);
		assertTrue(vst == vst1);
	}

	@Test
	public void testWidening3() {
		ValueSetReference ref0, ref1, ref2;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		ref0 = ref1 = ref2 = universe.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(ref0, zero);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, two, four);
		ref1 = universe.vsArrayElementReference(ref1, one);
		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		ref1 = universe.vsArraySectionReference(ref1, one, three);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });

		ref2 = universe.vsArraySectionReference(ref2, zero, six);
		ref2 = universe.vsTupleComponentReference(ref2, zeroInt);
		ref2 = universe.vsArraySectionReference(ref2, zero, six);
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref2 });
		assertTrue(vst != vst1);
		vst1 = universe.valueSetWidening(universe.trueExpression(), vst);
		assertTrue(vst == vst1);
	}

	@Test
	public void testUnion() {
		ValueSetReference ref0, ref1;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1, vst2, vst3;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		ref0 = ref1 = universe.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(ref0, zero);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, two, four);
		ref1 = universe.vsArrayElementReference(ref1, one);
		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		ref1 = universe.vsArraySectionReference(ref1, one, three);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1 });
		vst2 = universe.valueSetUnion(vst, vst1);
		vst3 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });
		assertTrue(vst2 == vst3);
	}

	/**
	 * <p>
	 * Assuming steps of ranges can only be one...
	 *
	 * <code>v[a .. b].f[c .. d]</code> and <code>v[e .. f].f[g .. h]</code>
	 * have no intersection iff <code>
	 *     [c .. d] and [g .. h] have no intersection, i.e.,
	 *     d <= g || h <= c, OR
	 *
	 *     [a .. b] and [e .. f] have no intersection, i.e.,
	 *     b <= e || f <= a
	 * </code>
	 * </p>
	 */
	@Test
	public void testNoIntersect() {
		ValueSetReference ref0, ref1;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1; // vst2, vst3;

		type = universe.tupleType(universe.stringObject("tt"),
				Arrays.asList(universe.arrayType(type, six)));
		// tt[6], where tt is tuple containing an array field:
		type = universe.arrayType(type, six);

		NumericExpression a, b, c, d, e, f, g, h;

		a = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("a"), universe.integerType());
		b = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("b"), universe.integerType());
		c = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("c"), universe.integerType());
		d = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("d"), universe.integerType());
		e = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("e"), universe.integerType());
		f = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("f"), universe.integerType());
		g = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("g"), universe.integerType());
		h = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("h"), universe.integerType());

		ref0 = ref1 = universe.vsIdentityReference();
		ref0 = universe.vsArraySectionReference(ref0, a, b);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, c, d);
		ref1 = universe.vsArraySectionReference(ref1, e, f);
		ref1 = universe.vsTupleComponentReference(ref1, zeroInt);
		ref1 = universe.vsArraySectionReference(ref1, g, h);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1 });

		BooleanExpression result = universe.valueSetNoIntersect(vst, vst1);
		BooleanExpression oracle = universe.or(universe.lessThanEquals(d, g),
				universe.lessThanEquals(h, c));

		oracle = universe.or(oracle, universe.or(universe.lessThanEquals(b, e),
				universe.lessThanEquals(f, a)));

		assertTrue(universe.equals(result, oracle).isTrue());
	}

	/**
	 * <code>v[a .. b].f[c .. d]</code> and <code>v[a .. b].g[c .. d]</code>
	 * have no intersection.
	 */
	@Test
	public void testNoIntersect2() {
		ValueSetReference ref0, ref1;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1; // , vst2, vst3;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		NumericExpression a, b, c, d, e, f, g, h;

		a = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("a"), universe.integerType());
		b = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("b"), universe.integerType());
		c = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("c"), universe.integerType());
		d = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("d"), universe.integerType());
		e = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("e"), universe.integerType());
		f = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("f"), universe.integerType());
		g = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("g"), universe.integerType());
		h = (NumericExpression) universe.symbolicConstant(
				universe.stringObject("h"), universe.integerType());

		ref0 = ref1 = universe.vsIdentityReference();
		ref0 = universe.vsArraySectionReference(ref0, a, b);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, c, d);
		ref1 = universe.vsArraySectionReference(ref1, e, f);
		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		ref1 = universe.vsArraySectionReference(ref1, g, h);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1 });
		assertTrue(universe.valueSetNoIntersect(vst, vst1).isTrue());
	}

	@Test
	public void testNoIntersectBad() {
		ValueSetReference ref0, ref1;
		SymbolicType type = integerType;
		SymbolicExpression vst, vst1; // , vst2, vst3;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);

		ref0 = ref1 = universe.vsIdentityReference();
		ref0 = universe.vsArraySectionReference(ref0, one, four);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, one, four);
		ref1 = universe.vsArraySectionReference(ref1, two, six);
		ref1 = universe.vsTupleComponentReference(ref1, zeroInt);
		ref1 = universe.vsArraySectionReference(ref1, two, six);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		vst1 = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref1 });
		assertTrue(universe.valueSetNoIntersect(vst, vst1).isFalse());
	}

	@Test
	public void testAssign() {
		ValueSetReference ref0, ref1;
		SymbolicType type = integerType;
		SymbolicExpression vst;

		type = universe.tupleType(universe.stringObject("tt"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		ref0 = ref1 = universe.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(ref0, zero);
		ref0 = universe.vsTupleComponentReference(ref0, zeroInt);
		ref0 = universe.vsArraySectionReference(ref0, two, four);
		ref1 = universe.vsArrayElementReference(ref1, one);
		ref1 = universe.vsTupleComponentReference(ref1, oneInt);
		ref1 = universe.vsArraySectionReference(ref1, one, three);
		vst = universe.valueSetTemplate(type,
				new ValueSetReference[] { ref0, ref1 });

		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), type);
		SymbolicExpression ret = universe.valueSetAssigns(X, vst, Y);

		out.println(ret);

		SymbolicExpression val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, zero), zeroInt),
				two);

		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(Y, zero), zeroInt), two));
		val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, zero), zeroInt),
				three);
		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(Y, zero), zeroInt),
				three));
		val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, one), oneInt), one);
		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(Y, one), oneInt), one));
		val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, one), oneInt), two);
		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(Y, one), oneInt), two));
		val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, one), zeroInt), two);
		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(X, one), zeroInt), two));
		val = universe.arrayRead(
				universe.tupleRead(universe.arrayRead(ret, zero), oneInt), two);
		assertTrue(val == universe.arrayRead(
				universe.tupleRead(universe.arrayRead(X, zero), oneInt), two));

		ref0 = universe.vsIdentityReference();
		ref0 = universe.vsArrayElementReference(ref0, one);
		ref0 = universe.vsTupleComponentReference(ref0, oneInt);
		ref0 = universe.vsArraySectionReference(ref0, zero, six);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref0 });
		ret = universe.valueSetAssigns(X, vst, Y);

		val = universe.tupleRead(universe.arrayRead(ret, one), oneInt);
		assertTrue(val == universe.tupleRead(universe.arrayRead(ret, one),
				oneInt));
	}

	@Test
	public void testAssign2() {
		ValueSetReference ref;
		SymbolicType type = integerType;
		SymbolicExpression vst;

		type = universe.unionType(universe.stringObject("ut"), Arrays.asList(
				universe.arrayType(type, six), universe.arrayType(type, six)));
		type = universe.arrayType(type, six);
		ref = universe.vsIdentityReference();
		ref = universe.vsArrayElementReference(ref, one);
		ref = universe.vsUnionMemberReference(ref, oneInt);
		ref = universe.vsArraySectionReference(ref, one, three);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref });

		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), type);
		SymbolicExpression ret = universe.valueSetAssigns(X, vst, Y);

		out.println(ret);

		SymbolicExpression val = universe.arrayRead(
				universe.unionExtract(zeroInt, universe.arrayRead(ret, zero)),
				two);

		assertTrue(val == universe.arrayRead(
				universe.unionExtract(zeroInt, universe.arrayRead(X, zero)),
				two));
		val = universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(ret, one)),
				two);
		assertTrue(val == universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(Y, one)),
				two));

		ref = universe.vsIdentityReference();
		ref = universe.vsArrayElementReference(ref, one);
		ref = universe.vsUnionMemberReference(ref, oneInt);
		ref = universe.vsArraySectionReference(ref, zero, six);
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref });
		ret = universe.valueSetAssigns(X, vst, Y);

		val = universe.unionExtract(oneInt, universe.arrayRead(ret, one));
		assertTrue(val == universe.unionExtract(oneInt,
				universe.arrayRead(ret, one)));
	}

	@Test
	public void testAssign3() {
		ValueSetReference ref;
		SymbolicType type = integerType;
		SymbolicExpression vst;

		NumericExpression N = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("N"), integerType);
		NumericExpression I = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("I"), integerType);
		NumericExpression J = (NumericExpression) universe
				.symbolicConstant(universe.stringObject("J"), integerType);

		type = universe.unionType(universe.stringObject("ut"), Arrays.asList(
				universe.arrayType(type, N), universe.arrayType(type, N)));
		type = universe.arrayType(type, N);
		ref = universe.vsIdentityReference();
		ref = universe.vsArrayElementReference(ref, I);
		ref = universe.vsUnionMemberReference(ref, oneInt);
		ref = universe.vsArraySectionReference(ref, J, universe.add(J, six));
		vst = universe.valueSetTemplate(type, new ValueSetReference[] { ref });

		SymbolicConstant X = universe
				.symbolicConstant(universe.stringObject("X"), type);
		SymbolicConstant Y = universe
				.symbolicConstant(universe.stringObject("Y"), type);
		SymbolicExpression ret = universe.valueSetAssigns(X, vst, Y);

		out.println(ret);

		SymbolicExpression val = universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(ret, I)), J);

		assertTrue(val == universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(Y, I)), J));
		val = universe.arrayRead(universe.unionExtract(oneInt,
				universe.arrayRead(ret, universe.add(I, one))), J);
		assertTrue(
				val == universe.arrayRead(
						universe.unionExtract(oneInt,
								universe.arrayRead(X, universe.add(I, one))),
						J));
		val = universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(ret, I)),
				universe.add(J, one));
		assertTrue(val == universe.arrayRead(
				universe.unionExtract(oneInt, universe.arrayRead(Y, I)),
				universe.add(J, one)));
	}
}
