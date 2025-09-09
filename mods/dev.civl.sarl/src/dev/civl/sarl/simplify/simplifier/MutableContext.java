package dev.civl.sarl.simplify.simplifier;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import dev.civl.sarl.IF.ModelResult;
import dev.civl.sarl.IF.SARLConstants;
import dev.civl.sarl.IF.SARLInternalException;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.IF.number.RationalNumber;
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
import dev.civl.sarl.ideal.IF.Constant;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.Monic;
import dev.civl.sarl.ideal.IF.Monomial;
import dev.civl.sarl.ideal.IF.Polynomial;
import dev.civl.sarl.ideal.IF.Primitive;
import dev.civl.sarl.ideal.IF.PrimitivePower;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.simplify.IF.Range;
import dev.civl.sarl.simplify.IF.RangeFactory;
import dev.civl.sarl.simplify.common.SARLProverAdaptor;
import dev.civl.sarl.simplify.norm.GaussianNormalizer;
import dev.civl.sarl.simplify.norm.MultiOrNormalizer;
import dev.civl.sarl.simplify.norm.Normalizer;
import dev.civl.sarl.simplify.norm.NormalizerChain;
import dev.civl.sarl.simplify.norm.RangeNormalizer;
import dev.civl.sarl.simplify.norm.SubstitutionNormalizer;
import dev.civl.sarl.simplify.norm.TupleNormalizer;
import dev.civl.sarl.simplify.simplification.GenericSimplification;
import dev.civl.sarl.simplify.simplification.ProverHeuristic;
import dev.civl.sarl.simplify.simplification.Simplification;
import dev.civl.sarl.simplify.simplification.Strategy;
import dev.civl.sarl.util.Pair;
import dev.civl.sarl.util.WorkMap;

/**
 * A structured representation of a boolean formula (assumption), used for
 * simplifying symbolic expressions. The two main data structures in this
 * representation are the substitution map and the range map. The substitution
 * map maps symbolic expressions to symbolic expressions and is used to replace
 * subexpressions with their simplified forms. The range map maps {@link Monic}s
 * to {@link Range}s and specifies a concrete range for each {@link Monic}.
 * 
 * @author Stephen F. Siegel (siegel)
 */
public class MutableContext implements Context {

	// Static fields...

	public final static boolean debug = false;

	// Instance Fields ...

	protected MutableContext superContext;
	
	/*
	 * An ancestor context which is designated to perform type simplification.
	 * 
	 * Type simplification attempts to simplify array extents which can cause
	 * problems if a given array is simplified to have different extents in
	 * multiple occurrences of the same expression. The typeContext is the
	 * original context in our chain of ancestors which initiated a
	 * simplification and thus provides the most specific context that can
	 * safely simplify types while maintaining consistency across a given
	 * expression.
	 */
	protected MutableContext typeContext;

	/**
	 * A "weak" cache. See {@link clearWeakCaches}.
	 * 
	 * A cache of all simplifications computed under this
	 * {@link MutableContext}. For any entry (x,y), the following formula must
	 * be valid: context -> x=y.
	 */
	private Map<SymbolicObject, SymbolicObject> simplificationCache = null;
	/**
	 * A mapping from assumptions to previously created subcontexts with a
	 * logically equivalent (under the assumption of this context) assumption.
	 * 
	 * A "weak" cache. See {@link clearWeakCaches}.
	 */
	private Map<BooleanExpression, MutableContext> subContextCache = new HashMap<>();

	protected Map<BooleanExpression, ValidityResult> validityCache = new HashMap<>();
	protected Map<BooleanExpression, ValidityResult> unsatCache = new HashMap<>();

	/**
	 * Should backwards substitution be used to solve for variables in terms of
	 * other variables?
	 */
	final boolean backwardsSub;

	/**
	 * An object that gathers together references to various tools that are
	 * needed for this class to do its work.
	 */
	SimplifierUtility util;

	private SARLProverAdaptor proverAdaptor;

	/**
	 * The prover factory that we use to lazily construct our prover for checks
	 * of valid or unsat.
	 */
	protected TheoremProverFactory proverFactory;

	/**
	 * The prover that we will use to check validity or unsatisfiability of a
	 * statement given the current context.
	 */
	private TheoremProver prover = null;

	/**
	 * <p>
	 * The essential substitution map. When simplifying an expression, any
	 * occurrence of the key of an entry in this map will be replaced by the
	 * value of the entry.
	 * </p>
	 *
	 * Invariants:
	 * 
	 * <ul>
	 * 
	 * <li>If the value of an entry is non-null, it will have a type compatible
	 * with that of the key.</li>
	 * 
	 * <li>No key occurs as a subexpression of any value or of any other key.
	 * </li>
	 * 
	 * <li>For each entry, the key is a {@link SymbolicConstant} or the value is
	 * concrete. [Note: this may change in the future.]</li>
	 * 
	 * <li>If the type of an entry is real, the key is a {@link Monic}. If that
	 * {@link Monic} is a {@link Polynomial}, its constant term is 0.</li>
	 * 
	 * <li>If the type of an entry is integer, the key and value are
	 * {@link Monomial}s, the coefficients of those two {@link Monomials} are
	 * relatively prime, and the key's coefficient is positive. WHY? Suppose
	 * 2X=3Y is an entry. Then X=(3*Y)/2. But the latter form loses information
	 * because the "/" is integer division. It loses the information that Y is
	 * even. However, if the key is a symbolic constant, it is a monic, and if
	 * the value is concrete, then you can always divide both side by the
	 * coefficient, so under the current assumption, the key will always be
	 * monic, and if a polynomial the constant term is 0, exactly as for reals.
	 * 
	 * TODO: this is very confusing. Doesn't it contradict the statement above
	 * that a key is a symbolic constant or the value is concrete????</li>
	 * 
	 * </ul>
	 * 
	 * Example: subMap: wx->1, y->x Applied to wy -> wx -> 1. Hence substitution
	 * is not necessarily idempotent, even with these assumptions
	 * 
	 * TODO: think about replacing all/some of these memory-inefficient maps
	 * with sorted arrays, at least after this Context has been initialized.
	 */
	Map<SymbolicExpression, SymbolicExpression> subMap;

	/**
	 * <p>
	 * Map giving precise range of {@link Monic}s. Associates to each
	 * {@link Monic} a {@link Range} such that the set of all possible values
	 * the monic can assume are contained in that range. Monics that have a
	 * single concrete value are removed from this map and placed in the
	 * {@link #subMap}. No solved variables can occur in this map.
	 * </p>
	 * 
	 * <p>
	 * This map is used to form the assumption (full and reduced).
	 * </p>
	 */
	WorkMap<Monic, Range> rangeMap;

	/**
	 * The symbolic constants that are involved in substitution map or range map
	 * entries that have been modified since the last normalization. An entry
	 * that does not contain any variables in this set should not need to be
	 * normalized.
	 */
	Set<SymbolicConstant> theDirtySet = new HashSet<>();

	private ProverFunctionInterpretation logicFunctions[];

	// Constructors ...

	/**
	 * Constructs new {@link MutableContext} with all empty maps. This
	 * represents the assumption "true". No initialization is done.
	 * 
	 * @param util
	 *            info structure with references to commonly-used factories and
	 *            other objects
	 * @param useBackwardSubstitution
	 *            should this {@link MutableContext} use backwards substitution
	 *            after Gaussian elimination to remove additional symbolic
	 *            constants from the context?
	 */
	protected MutableContext(MutableContext superContext, boolean isTypeContext,
			SimplifierUtility util, TheoremProverFactory proverFactory,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		this.superContext = superContext;
		this.typeContext = isTypeContext ? this : superContext.typeContext;
		this.util = util;
		proverAdaptor = new SARLProverAdaptor(util.getUniverse());
		this.proverFactory = proverFactory;
		this.subMap = new TreeMap<>(util.universe.comparator());
		this.rangeMap = new WorkMap<>(util.idealFactory.monicComparator());
		this.backwardsSub = useBackwardSubstitution;
		this.simplificationCache = new HashMap<>();
		this.logicFunctions = logicFunctions;
	}

	/**
	 * Constructs new {@link MutableContext} with given fields. Initialization
	 * is carried out.
	 * 
	 * @param util
	 *            info structure with references to commonly-used factories and
	 *            other objects
	 * @param subMap
	 *            substitution map; see {@link #subMap}
	 * @param rangeMap
	 *            range map; see {@link #rangeMap}
	 * @param useBackwardSubstitution
	 *            should this {@link MutableContext} use backwards substitution
	 *            after Gaussian elimination to remove additional symbolic
	 *            constants from the context?
	 */
	/*
	 * Context(SimplifierUtility util, TheoremProverFactory proverFactory,
	 * Map<SymbolicExpression, SymbolicExpression> subMap, WorkMap<Monic, Range>
	 * rangeMap, boolean useBackwardSubstitution) { this.util = util;
	 * proverAdaptor = new SARLProverAdaptor(util.getUniverse());
	 * this.proverFactory = proverFactory; this.subMap = subMap; this.rangeMap =
	 * rangeMap; this.backwardsSub = useBackwardSubstitution;
	 * initialize(util.trueExpr); }
	 */

	protected MutableContext(MutableContext superContext, boolean isTypeContext,
			SimplifierUtility util, TheoremProverFactory proverFactory,
			BooleanExpression assumption, boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		this(superContext, isTypeContext, util, proverFactory, useBackwardSubstitution,
				logicFunctions);
		initialize(assumption);
	}

	/**
	 * Create context from the given assumption. The assumption is parsed and
	 * processed to populate the fields of this context.
	 * 
	 * @param superContext
	 *            The super context of this context. All assumptions of the
	 *            super context are implicitly assumptions in this context
	 * @param util
	 *            info structure with references to commonly-used factories and
	 *            other objects
	 * @param assumption
	 *            the assumption this context will represent
	 * @param useBackwardSubstitution
	 *            should this {@link MutableContext} use backwards substitution
	 *            after Gaussian elimination to remove additional symbolic
	 *            constants from the context?
	 */
	public MutableContext(PreUniverse universe, IdealFactory idealFactory,
			TheoremProverFactory proverFactory, BooleanExpression assumption,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		this(null, true, new SimplifierUtility(universe, idealFactory), proverFactory,
				assumption, useBackwardSubstitution, logicFunctions);
	}

	public void checkSubMapInvariant() {
		Set<SymbolicExpression> keySetCopy = new HashSet<SymbolicExpression>();
		keySetCopy.addAll(subMap.keySet());
		for (SymbolicExpression key : keySetCopy) {
			SymbolicExpression keyValue = subMap.remove(key);
			assert keyValue != null;
			assert !keyValue.containsSubobject(key);
			assertKeyIsAbsent(key);
			subMap.put(key, keyValue);
		}

		Set<SymbolicExpression> collapsedKeySet = new HashSet<SymbolicExpression>();
		MutableContext ancestor = superContext;

		while (ancestor != null) {
			collapsedKeySet.addAll(ancestor.subMap.keySet());
			ancestor = ancestor.superContext;
		}

		for (SymbolicExpression key : collapsedKeySet) {
			assertKeyIsAbsent(key);
		}
	}

	private void assertKeyIsAbsent(SymbolicExpression key) {
		for (Entry<SymbolicExpression, SymbolicExpression> entry : subMap
				.entrySet()) {
			assert !entry.getKey().containsSubobjectIgnoringType(key);
			assert !entry.getValue().containsSubobjectIgnoringType(key);
		}

		for (SymbolicExpression rangeKey : rangeMap.keySet()) {
			assert !rangeKey.containsSubobjectIgnoringType(key);
		}
	}

	// Private methods ....

	/**
	 * <p>
	 * Transforms a pair of {@link Monomial} by dividing both elements by an
	 * appropriate constant so that (1) if the type is real, the coefficient for
	 * the left component is 1; (2) if the type is integer, the coefficient for
	 * the left component is positive and the GCD of the absolute values of the
	 * left and right coefficients is 1.
	 * </p>
	 * 
	 * <p>
	 * Example: x is an int, (3x,2). This should be an inconsistency. But
	 * (3x,2y) could be OK. It implies x=(2y) INTDIV 3, but is stronger than
	 * that formula. It is equivalent to (real)x = 2((real)y)/3, but it is
	 * debatable whether you should make this substitution.
	 * </p>
	 * 
	 * @param pair
	 *            a pair of non-<code>null</code> {@link Monomial}s of the same
	 *            type
	 * @throws InconsistentContextException
	 *             if an inconsistency is detected in the course of simplifying
	 *             the equality
	 */
	private void monicizeMonomialPair(Pair<Monomial, Monomial> pair)
			throws InconsistentContextException {
		Monomial x = pair.left;

		if (x instanceof Monic)
			return;

		Monomial y = pair.right;
		IdealFactory idf = util.idealFactory;
		NumberFactory nf = util.numberFactory;
		PreUniverse universe = util.universe;
		Constant constant = x.monomialConstant(idf);
		Monic xMonic = x.monic(idf);

		if (x.type().isReal()) {
			pair.left = xMonic;
			pair.right = (Monomial) universe.divide(y, constant);
		} else {
			IntegerNumber yCoefficient = (IntegerNumber) y.monomialConstant(idf)
					.number();
			IntegerNumber xCoefficient = (IntegerNumber) constant.number();
			boolean negate = xCoefficient.signum() < 0;
			IntegerNumber xCoefficientAbs = negate
					? nf.negate(xCoefficient)
					: xCoefficient;
			IntegerNumber gcd = nf.gcd(xCoefficientAbs,
					(IntegerNumber) nf.abs(yCoefficient));
			Monic yMonic = y.monic(idf);

			if (gcd.isOne()) {
				if (negate) {
					pair.left = (Monomial) universe.minus(x);
					pair.right = (Monomial) universe.minus(y);
				}
			} else {
				if (yMonic.isOne() && gcd != xCoefficientAbs) {
					// something like 3x=2 can't hold, but 2x=4 is fine...
					throw new InconsistentContextException();
				}
				pair.left = idf.monomial(
						idf.constant(nf.divide(xCoefficientAbs, gcd)), xMonic);
				pair.right = idf
						.monomial(
								idf.constant(negate
										? nf.negate(
												nf.divide(yCoefficient, gcd))
										: nf.divide(yCoefficient, gcd)),
								yMonic);
			}
		}
	}

	/**
	 * <p>
	 * Given a substitution x->y in which both x and y are {@link Monomial}s,
	 * transforms that substitution into a standard form. In the standard form,
	 * x and y are the equivalent symbolic expressions, x is a {@link Monic},
	 * and if x is a {@link Polynomial} then its constant term is 0. Moreover,
	 * (1) if the type is real, the coefficient for the left component is 1; (2)
	 * if the type is integer, the coefficient for the left component is
	 * positive and the GCD of the absolute values of the left and right
	 * coefficients is 1.
	 * </p>
	 * <p>
	 * If in the process of transformation it is determined that x and y are
	 * equivalent, both fields of the pair will be set to <code>null</code>.
	 * </p>
	 * 
	 * @param pair
	 *            a substitution pair specifying a value x and the value y that
	 *            is to be substituted for x
	 * @throws InconsistentContextException
	 *             if the type is integer and an inconsistency is detected such
	 *             as 2x->3
	 */
	private void standardizeMonomialPair(Pair<Monomial, Monomial> pair)
			throws InconsistentContextException {
		IdealFactory idf = util.idealFactory;
		PreUniverse universe = util.universe;

		while (true) {
			monicizeMonomialPair(pair);
			if (pair.left instanceof Polynomial) {
				Polynomial poly = (Polynomial) pair.left;
				Constant c = poly.constantTerm(idf);

				if (c.isZero())
					break;
				else {
					pair.left = (Monomial) universe.subtract(poly, c);
					pair.right = (Monomial) universe.subtract(pair.right, c);
				}
			} else { // pair.left is a Monic and not a Polynomial
				break;
			}
		}
		if (pair.left.equals(pair.right)) {
			pair.left = null;
			pair.right = null;
		} else if (pair.left.isOne() && pair.right instanceof Constant) {
			throw new InconsistentContextException();
		}
	}

	/**
	 * Given a normalized {@link Monic} and a {@link Range} range, computes the
	 * intersection of {@code range} with the current known range of the monic
	 * based on the {@link #subMap} and {@link #rangeMap} of this
	 * {@link MutableContext}.
	 * 
	 * @param monic
	 *            a normalized {@link Monic}
	 * @param range
	 *            a {@link Range} of the same type as the monic
	 * @return the intersection of {@code range} with the current known range of
	 *         {@link Monic} based on the entries of the {@link #subMap} and
	 *         {@link #rangeMap}
	 */
	private Range intersectWithRangeOf(Monic monic, Range range) {
		SymbolicExpression value = getSub(monic);

		if (value instanceof Constant) {
			Number number = ((Constant) value).number();

			if (range.containsNumber(number)) {
				range = util.rangeFactory.singletonSet(number);
			} else {
				range = util.rangeFactory.emptySet(range.isIntegral());
			}
		} else {
			Range oldRange = getRange(monic);

			if (oldRange != null)
				range = util.rangeFactory.intersect(range, oldRange);
		}
		return range;
	}

	/**
	 * Computes an (over-)estimate of the possible values of a
	 * {@link Polynomial} based on the current assumptions of this
	 * {@link MutableContext}.
	 * 
	 * @param poly
	 *            a non-{@code null} {@link Polynomial}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code poly} at any point satisfying the assumptions
	 *         of this context will lie in that range
	 */
	private Range computeRange(Polynomial poly) {
		IdealFactory idf = util.idealFactory;
		RangeFactory rf = util.rangeFactory;
		Constant constantTerm = poly.constantTerm(idf);
		Number constant = constantTerm.number();
		Range result;

		if (constant.isZero()) {
			result = rf.singletonSet(constant);
			for (Monomial term : poly.termMap(idf)) {
				result = rf.add(result, computeRange(term));
				if (result.isUniversal())
					break;
			}
			result = intersectWithRangeOf(poly, result);
		} else {
			result = rf.add(
					computeRange((Monomial) idf.subtract(poly, constantTerm)),
					constant);
		}
		return result;
	}

	/**
	 * Computes an (over-)estimate of the possible values of a power expression
	 * based on the current assumptions of this {@link MutableContext}.
	 * 
	 * Currently, this is a very rough over-estimate that only tries to get
	 * right the signedness (greater than 0, greater than or equal to 0, etc.).
	 * 
	 * @param base
	 *            the base in the power expression (can have real or integer
	 *            type)
	 * @param exponent
	 *            the exponent in the power expression (must have same type as
	 *            {@code base})
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating base raised to the exponent power at any point
	 *         satisfying the assumptions of this context will lie in that range
	 */
	private Range computeRangeOfPower(RationalExpression base,
			RationalExpression exponent) {
		IdealFactory idf = util.idealFactory;
		RangeFactory rf = util.rangeFactory;
		NumberFactory nf = util.numberFactory;
		boolean isIntegral = base.type().isInteger();
		Number zero = isIntegral
				? util.numberFactory.zeroInteger()
				: util.numberFactory.zeroRational();

		// if base>0, then base^exponent>0:
		if (((BooleanExpression) simplify(idf.isPositive(base),
				Strategy.standardStrategy())).isTrue())
			return rf.interval(isIntegral, zero, true,
					nf.infiniteNumber(isIntegral, true), true);

		// if base>=0, then base^exponent>=0:

		Range ge0 = rf.interval(isIntegral, zero, false,
				nf.infiniteNumber(isIntegral, true), true);

		if (((BooleanExpression) simplify(idf.isNonnegative(base),
				Strategy.standardStrategy())).isTrue())
			return ge0;

		// if exponent is not integral or is even, base^exponent>=0:

		Number exponentNumber = idf.extractNumber(exponent);

		if (exponentNumber != null) {
			if (exponentNumber instanceof IntegerNumber) {
				IntegerNumber exponentInteger = (IntegerNumber) exponentNumber;

				if (nf.mod(exponentInteger, nf.integer(2)).isZero()) {
					return ge0;
				}
			} else {
				if (!nf.isIntegral((RationalNumber) exponentNumber))
					return ge0;
				else {
					IntegerNumber exponentInteger = nf
							.integerValue((RationalNumber) exponentNumber);

					if (nf.mod(exponentInteger, nf.integer(2)).isZero())
						return ge0;
				}
			}
		}
		return rf.universalSet(isIntegral);
	}

	/**
	 * Computes an (over-)estimate of the possible values of a {@link Primitive}
	 * based on the current assumptions of this {@link MutableContext}.
	 * 
	 * @param primitive
	 *            a non-{@code null} {@link Primitive} expression
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code primitive} at any point satisfying the
	 *         assumptions of this context will lie in that range
	 */
	private Range computeRange(Primitive primitive) {
		if (primitive instanceof Polynomial)
			return computeRange((Polynomial) primitive);
		if (primitive.operator() == SymbolicOperator.POWER)
			return computeRangeOfPower(
					(RationalExpression) primitive.argument(0),
					(RationalExpression) primitive.argument(1));

		SymbolicExpression value = getSub(primitive);

		if (value instanceof Constant)
			return util.rangeFactory.singletonSet(((Constant) value).number());

		Range oldRange = getRange(primitive);

		if (oldRange != null)
			return oldRange;
		if (primitive.operator() == SymbolicOperator.MODULO)
			return computeDefaultModRange((Monomial) primitive.argument(0),
					(Monomial) primitive.argument(1));
		return util.rangeFactory.universalSet(primitive.type().isInteger());
	}

	/**
	 * Computes an (over-)estimate of the possible values of a
	 * {@link PrimitivePower} based on the current assumptions of this
	 * {@link MutableContext}.
	 * 
	 * @param pp
	 *            a non-{@code null} {@link PrimitivePower}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code pp} at any point satisfying the assumptions of
	 *         this context will lie in that range
	 */
	private Range computeRange(PrimitivePower pp) {
		if (pp instanceof Primitive)
			return computeRange((Primitive) pp);

		IntegerNumber exponent = pp.monomialDegree(util.numberFactory);
		Range result = util.rangeFactory
				.power(computeRange(pp.primitive(util.idealFactory)), exponent);

		result = intersectWithRangeOf(pp, result);
		return result;
	}

	/**
	 * Computes an (over-)estimate of the possible values of a {@link Monic}
	 * based on the current assumptions of this {@link MutableContext}.
	 * 
	 * @param monic
	 *            a non-{@code null} {@link Monic}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code monic} at any point satisfying the assumptions
	 *         of this context will lie in that range
	 */
	private Range computeRange(Monic monic) {
		if (monic instanceof PrimitivePower)
			return computeRange((PrimitivePower) monic);

		RangeFactory rf = util.rangeFactory;
		NumberFactory nf = util.numberFactory;
		Range result = rf.singletonSet(
				monic.type().isInteger() ? nf.oneInteger() : nf.oneRational());

		for (PrimitivePower pp : monic.monicFactors(util.idealFactory)) {
			result = rf.multiply(result, computeRange(pp));
			if (result.isUniversal())
				break;
		}
		result = intersectWithRangeOf(monic, result);
		return result;
	}

	/**
	 * Computes an (over-)estimate of the possible values of a {@link Monomial}
	 * based on the current assumptions of this {@link MutableContext}.
	 * 
	 * @param monomial
	 *            a non-{@code null} {@link Monomial}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code monomial} at any point satisfying the
	 *         assumptions of this context will lie in that range
	 */
	private Range computeRange(Monomial monomial) {
		if (monomial instanceof Monic)
			return computeRange((Monic) monomial);
		if (monomial instanceof Constant)
			return util.rangeFactory
					.singletonSet(((Constant) monomial).number());
		return util.rangeFactory.multiply(
				computeRange(monomial.monic(util.idealFactory)),
				monomial.monomialConstant(util.idealFactory).number());
	}

	/**
	 * Computes a default range for a%b. Recall a = (a div b)*b + a%b. The sign
	 * of a%b is the sign of a. (a div b) is rounded towards 0.
	 * 
	 * Case 1: a>=0 and b>0. Then a%b is in [0,min(a,b-1)].
	 * 
	 * Case 2: a>=0 and b<0. Then a%b is in [0,min(a,-b-1)].
	 * 
	 * Case 3: a<=0 and b>0. Then a%b is in [max(a,1-b),0].
	 * 
	 * Case 4: a<=0 and b<0. Then a%b is in [max(a,1+b),0].
	 * 
	 * If ab>=0, a%b is in [0,b-1]. If ab<=0, a%b is in [1-b,0]. In any case,
	 * a%b is in [1-b,b-1].
	 * 
	 * The behavior is undefined if b could be 0.
	 * 
	 * @param a
	 *            the dividend, an integer expression
	 * @param b
	 *            the divisor, an integer expression
	 * @return a conservative concrete range on a%b under the assumptions of
	 *         this {@link MutableContext}
	 */
	private Range computeDefaultModRange(Monomial a, Monomial b) {
		RangeFactory rf = util.rangeFactory;
		NumberFactory nf = util.numberFactory;
		Interval b_interval = computeRange(b).intervalOverApproximation();
		Interval a_interval = computeRange(a).intervalOverApproximation();
		Range result = null;

		if (a_interval.isEmpty() || b_interval.isEmpty())
			return rf.emptySet(true);
		if (a_interval.lower().signum() >= 0) {
			Number right;

			if (b_interval.lower().signum() >= 0) // [0,min(a,b-1)]
				right = nf.decrement(b_interval.upper());
			else if (b_interval.upper().signum() <= 0) // [0,min(a,-b-1)]
				right = nf.negate(nf.increment(b_interval.lower()));
			else
				right = util.max(nf.decrement(b_interval.upper()),
						nf.negate(nf.increment(b_interval.lower())));
			right = util.min(a_interval.upper(), right);
			result = rf.interval(true, nf.zeroInteger(), false, right,
					right.isInfinite());
		} else if (a_interval.upper().signum() <= 0) {
			Number left;

			if (b_interval.lower().signum() >= 0) // [max(a,1-b),0]
				left = nf.increment(nf.negate(b_interval.upper()));
			else if (b_interval.upper().signum() <= 0) // [max(a,1+b),0]
				left = nf.increment(b_interval.lower());
			else
				left = util.min(nf.increment(nf.negate(b_interval.upper())),
						nf.increment(b_interval.lower()));
			left = util.max(a_interval.lower(), left);
			result = rf.interval(true, left, left.isInfinite(),
					nf.zeroInteger(), false);
		}
		return result == null ? rf.universalSet(true) : result;
	}

	/**
	 * <p>
	 * Inserts an entry into the {@link #subMap} and also checks for an
	 * inconsistency between the old and new values, if an old value existed.
	 * </p>
	 * 
	 * <p>
	 * Preconditions: the {@code key} and {@code value} must satisfy the
	 * invariants described for {@link #subMap}.
	 * </p>
	 * 
	 * @param key
	 *            the expression on the left side of the substitution
	 * @param value
	 *            the expression on the right side which will replace the
	 *            {@code key}
	 * @param dirtySet
	 *            the free symbolic constants involved in the key and value will
	 *            be added to this set
	 * @return the old value associated to {@code key}, or {@code null} if there
	 *         was no entry for {@code key}
	 * @throws InconsistentContextException
	 *             if the old value was a concrete value and not equal to the
	 *             new value
	 */
	private SymbolicExpression updateSub(SymbolicExpression key,
			SymbolicExpression value, Set<SymbolicConstant> dirtySet)
			throws InconsistentContextException {
		SymbolicExpression old = subMap.put(key, value);

		if (old != value) {
			clearSimplificationCache();
			dirtySet.addAll(key.getFreeVars());
			dirtySet.addAll(value.getFreeVars());
			if (old != null) {
				switch (value.type().typeKind()) {
					case BOOLEAN :
					case CHAR :
					case INTEGER :
					case REAL :
					case UNINTERPRETED :
						if (value.operator() == SymbolicOperator.CONCRETE && old
								.operator() == SymbolicOperator.CONCRETE) {
							throw new InconsistentContextException();
						}
					default :
						// We overwrote "key -> old" with "key -> value" so it must hold that "value = old"
						subMap.put(util.universe.equals(value, old), util.trueExpr);
						dirtySet.addAll(old.getFreeVars());
				}
			}
		}
		return old;
	}

	/**
	 * Computes the boolean formula represented by this context.
	 * 
	 * @param full
	 *            should the formula include the equalities giving the values of
	 *            the solved variables?
	 * @return the boolean formula as specified
	 */
	private BooleanExpression getAssumption(boolean full) {
		BooleanExpression result = util.trueExpr;

		for (Entry<SymbolicExpression, SymbolicExpression> subEntry : subMap
				.entrySet()) {
			SymbolicExpression key = subEntry.getKey();

			if (full || !(key instanceof SymbolicConstant))
				result = util.universe.and(result,
						util.universe.equals(key, subEntry.getValue()));
		}
		for (Entry<Monic, Range> rangeEntry : rangeMap.entrySet()) {
			Monic monic = rangeEntry.getKey();
			Range range = rangeEntry.getValue();

			/*
			 * Monics are expanded in order to keep expressions involving them
			 * in a simple form if possible. This means that if a super context
			 * has 1<=x<=9, and this context has x<=5 then it will result in
			 * x<=5 rather than 1<=x<=5. Conversely, if this context has 1<=x<=5
			 * then it will also result in x<=5.
			 */
			if (superContext != null) {
				Range contextRange = superContext.computeRange(monic);

				if (!contextRange.isUniversal()) {
					range = util.rangeFactory.expand(range, contextRange);
				}

			}
			result = util.universe.and(result,
					range.symbolicRepresentation(monic, util.universe));
		}
		/*
		if (full && superContext != null) {
			superContext.subContextCache.put(result, this);
		}
		*/

		return result;
	}

	// Package-private methods ...

	/**
	 * Attempts to find, in the context, a clause which states the
	 * differentiability of the given <code>function</code>. This is a clause
	 * with operator {@link SymbolicOperator#DIFFERENTIABLE} and with the
	 * function argument (argument 0) equal to <code>function</code>.
	 * 
	 * @param function
	 *            the function for which a differentiability claim is sought
	 * @return a clause in the context dealing with the differentiability of
	 *         <code>function</code>, or <code>null</code> if no such clause is
	 *         found.
	 */
	BooleanExpression findDifferentiableClaim(SymbolicExpression function) {
		for (Entry<SymbolicExpression, SymbolicExpression> entry : getSubEntries()) {
			if (!entry.getValue().isTrue())
				continue;

			BooleanExpression clause = (BooleanExpression) entry.getKey();

			if (clause.operator() != SymbolicOperator.DIFFERENTIABLE)
				continue;
			if (clause.argument(0).equals(function))
				return clause;
		}
		return null;
	}

	/**
	 * Initializes this context by consuming and analyzing the given assumption
	 * and updating all data structures to represent the assumption in a
	 * structured way. After initialization, this context is basically immutable
	 * (an exception being the {@link #simplificationCache}).
	 * 
	 * @param assumption
	 *            the boolean expression which is to be represented by this
	 *            context
	 */
	void initialize(BooleanExpression assumption) {
		if (debug)
			System.out.println("Creating context : " + assumption);
		assume(assumption);
		normalize();
	}

	public Map<SymbolicConstant, SymbolicExpression> getAllSolvedVariables() {
		Map<SymbolicConstant, SymbolicExpression> solvedVariables = new TreeMap<>(
				util.variableComparator);

		MutableContext currContext = this;
		while (currContext != null) {
			for (Entry<SymbolicExpression, SymbolicExpression> entry : currContext
					.getSubEntries()) {
				SymbolicExpression key = entry.getKey();

				if (key instanceof SymbolicConstant)
					solvedVariables.put((SymbolicConstant) key,
							entry.getValue());
			}
			currContext = currContext.superContext;
		}
		return solvedVariables;
	}

	/**
	 * If this assumption is exactly equivalent to the claim that the given
	 * symbolic constant lies in some interval, returns that interval.
	 * Otherwise, returns {@code null}.
	 * 
	 * @param symbolicConstant
	 *            the symbolic constant
	 * @return the interval or {@code null}
	 */
	@Override
	public Interval assumptionAsInterval(SymbolicConstant symbolicConstant) {
		if (!subMap.isEmpty()) {
			if (!rangeMap.isEmpty() || subMap.size() != 1)
				return null;

			Entry<SymbolicExpression, SymbolicExpression> entry = subMap
					.entrySet().iterator().next();

			if (!entry.getKey().equals(symbolicConstant))
				return null;

			SymbolicExpression value = entry.getValue();

			if (!(value instanceof Constant))
				return null;
			return util.numberFactory
					.singletonInterval(((Constant) value).number());
		}
		if (rangeMap.size() == 1) {
			Entry<Monic, Range> entry = rangeMap.entrySet().iterator().next();

			if (!entry.getKey().equals(symbolicConstant))
				return null;

			Range range = entry.getValue();

			return range.asInterval();
		}
		return null;
	}

	/**
	 * Adds all entries in the {@link #subMap} to the specified map.
	 * 
	 * @param map
	 *            a map to which the entries of the {@link #subMap} will be
	 *            added
	 */
	private void addSubsToMap(Map<SymbolicExpression, SymbolicExpression> map) {
		if (superContext != null)
			superContext.addSubsToMap(map);
		map.putAll(subMap);
	}

	/**
	 * Returns a map consisting of all entries in the substitution map of this
	 * {@link MutableContext} and all of its ancestors. An entry from a child
	 * overrides an entry with the same key from the super context.
	 * 
	 * @return a map consisting of all subMap entries from this context and its
	 *         ancestors
	 */
	Map<SymbolicExpression, SymbolicExpression> getFullSubMap() {
		Map<SymbolicExpression, SymbolicExpression> map = new HashMap<>();

		addSubsToMap(map);
		return map;
	}

	/**
	 * Looks up the given {@link Monic} in this context's range map. If not
	 * found there, look in the super context.
	 * 
	 * @param key
	 *            a {@link Monic}
	 * @return the value associated to that key in the range map of this context
	 *         or one of its ancestors, or {@code null} if that monic does not
	 *         occur as a key in this context or any of its ancestors
	 */
	Range getRange(Monic key) {
		Range result = rangeMap.get(key);

		if (result != null || superContext == null)
			return result;
		return superContext.getRange(key);
	}

	/**
	 * Puts this {@link MutableContext} into the "inconsistent" state. This
	 * occurs when the assumption is determined to be inconsistent, i.e.,
	 * equivalent to <i>false</i>. All maps are cleared except for the single
	 * entry <code>false -> true</code> in the {@link #subMap}.
	 */
	void makeInconsistent() {
		rangeMap.clear();
		subMap.clear();
		putSub(util.falseExpr, util.trueExpr);
	}

	// Protected methods ...

	protected BooleanExpression getCollapsedAssumption(boolean full) {
		BooleanExpression collapsedAssumption = getAssumption(full);

		if (superContext != null)
			collapsedAssumption = util.universe.and(
					superContext.getCollapsedAssumption(full),
					collapsedAssumption);

		return collapsedAssumption;
	}

	// Public methods ...

	public Context getSuperContext() {
		return superContext;
	}

	@Override
	public MutableContext createSubContext(BooleanExpression assumption) {
		return createSubContext(assumption, true);
	}
	
	public MutableContext createSubContext(BooleanExpression assumption, boolean isTypeContext) {
		MutableContext subContext = subContextCache.get(assumption);
		if (subContext == null) {
			subContext = new MutableContext(this, isTypeContext, this.util, this.proverFactory,
					assumption, this.backwardsSub, this.logicFunctions);
			//subContextCache.put(assumption, subContext);
		}
		//subContextCache.put(subContext.getFullAssumption(), subContext);
		return subContext;
	}

	/**
	 * Gets the range map of this context. Does not include anything from any
	 * ancestor.
	 * 
	 * @return this context's range map, {@link #rangeMap}
	 */
	public WorkMap<Monic, Range> getRangeMap() {
		return rangeMap;
	}

	/**
	 * Constructs an instance of {@link LinearSolver} that can be used to
	 * simplify the {@link #subMap} of this {@link MutableContext}.
	 * 
	 * @return a linear solver for simplifying the {@link #subMap}, or
	 *         {@code null} if no simplifications are possible
	 */
	public LinearSolver getLinearSolver() {
		if (subMap.isEmpty())
			return null;

		return superContext == null
				? LinearSolver.reduce(util, subMap, util.monicComparator,
						backwardsSub)
				: LinearSolver.reduceRelative(util,
						superContext.getFullSubMap(), subMap,
						util.monicComparator, backwardsSub);
	}

	/**
	 * Clears the "weak" caches
	 */
	private void clearSimplificationCache() {
		// subContextCache.clear();
		if (simplificationCache != null)
			simplificationCache.clear();
	}

	@Override
	public void simplifyAssumption(Set<SymbolicConstant> aggressiveSet) {
		normalize();
	}

	/**
	 * <p>
	 * Places a substitution pair into a standard form. If the original pair is
	 * (x,y) and the new pair is (x',y') then the following formula will be
	 * valid: x=y -> x'=y'.
	 * </p>
	 * 
	 * <p>
	 * If in the new pair, x and y are the same symbolic expression, both
	 * components of the pair will be set to <code>null</code>.
	 * </p>
	 * 
	 * @param pair
	 *            a substitution pair
	 * @throws InconsistentContextException
	 *             if the type is integer and an inconsistency is detected such
	 *             as 2x->3
	 */
	public void standardizePair(
			Pair<SymbolicExpression, SymbolicExpression> pair)
			throws InconsistentContextException {
		if (pair.left == pair.right) {
			pair.left = null;
			pair.right = null;
			return;
		}
		if (pair.left.type().isIdeal()) {
			if (pair.left.operator() == SymbolicOperator.CAST) {
				// if x has type hint, (int)x->y should be changed to x->(hint)y
				SymbolicType type = pair.left.type();
				SymbolicExpression original = (SymbolicExpression) pair.left
						.argument(0);
				SymbolicType originalType = original.type();

				if (originalType.isHerbrand()
						&& originalType.isInteger() == type.isInteger()) {
					// problem: original expression might not be Monomial. Could
					// be RationalExpression like x/y. Questionable whether such
					// a thing should occur on right side of substitution. If it
					// does, should form the equality expression and process
					// from the beginning.
					pair.left = original;
					pair.right = util.universe.cast(originalType, pair.right);
					return;
				}
			}
			if (!(pair.left instanceof Monomial
					&& pair.right instanceof Monomial)) {
				BooleanExpression equation = util.universe.equals(pair.left,
						pair.right);

				pair.left = (Monomial) equation.argument(0);
				pair.right = (Monomial) equation.argument(1);
			}
			assert pair.left instanceof Monomial;
			assert pair.right instanceof Monomial;

			@SuppressWarnings("unchecked")
			Pair<Monomial, Monomial> monomialPair = (Pair<Monomial, Monomial>) (Pair<?, ?>) pair;

			standardizeMonomialPair(monomialPair);
		}
	}

	/**
	 * Looks up an entry in the substitution map of this context and its super
	 * contexts
	 * 
	 * @param key
	 *            the key to look up
	 * @return the value associated to that key in the substitution map. This is
	 *         the value that will always be substituted for {@code key} when a
	 *         symbolic expression is simplified by this {@link MutableContext}.
	 */
	public SymbolicExpression getSub(SymbolicExpression key) {
		SymbolicExpression result = subMap.get(key);

		if (result == null && superContext != null)
			result = superContext.getSub(key);
		return result;
	}

	/**
	 * Get subMap entries just from this context proper, not from super-context
	 * (if any).
	 * 
	 * @return the entries in {@link #subMap}
	 */
	public Set<Entry<SymbolicExpression, SymbolicExpression>> getSubEntries() {
		return subMap.entrySet();
	}

	/**
	 * Returns the simplifier utility used by this context. That object provides
	 * references to many different commonly used fields, and basic utility
	 * methods.
	 * 
	 * @return the simplifier utility
	 */
	public SimplifierUtility getInfo() {
		return util;
	}

	/**
	 * Enter the given data into this context's simplification cache. Future
	 * simplifications of the key will take place quickly by looking it up in
	 * the cache.
	 * 
	 * @param a
	 *            symbolic object
	 * @param value
	 *            the simplified version of that symbolic object
	 */
	public void cacheSimplification(SymbolicObject key, SymbolicObject value) {
		simplificationCache.put(key, value);
	}

	/**
	 * Retrieves the simplified version of an object from this context's
	 * simplification cache.
	 * 
	 * @param key
	 *            a symbolic object
	 * @return the cached result of simplification or {@code null} if there is
	 *         no such cached result
	 */
	public SymbolicObject getSimplification(SymbolicObject key) {
		return simplificationCache.get(key);
	}

	public boolean applySubMapToSelf(Set<SymbolicConstant> dirtyOut)
			throws InconsistentContextException {
		Map<SymbolicExpression, SymbolicExpression> ancestorSubMap = new HashMap<SymbolicExpression, SymbolicExpression>();
		MutableContext ancestor = superContext;

		while (ancestor != null) {
			ancestorSubMap.putAll(ancestor.subMap);
			ancestor = ancestor.superContext;
		}

		UnaryOperator<SymbolicExpression> subOperator = e -> {
			SymbolicExpression val = subMap.get(e);
			return val == null ? ancestorSubMap.get(e) : val;
		};
		boolean changed = false;
		Set<SymbolicExpression> keySetCopy = new HashSet<SymbolicExpression>();

		keySetCopy.addAll(subMap.keySet());
		for (SymbolicExpression key : keySetCopy) {
			SymbolicExpression keyValue = removeSubkey(key);

			UnaryOperator<SymbolicExpression> substituter = new ContextSubstituter(
					util.getUniverse(), subOperator);
			SymbolicExpression newKey = substituter.apply(key);
			SymbolicExpression newKeyValue = substituter.apply(keyValue);

			if ((newKey.isFalse() && newKeyValue.isTrue())
					|| newKey.isTrue() && newKeyValue.isFalse()) {
				throw new InconsistentContextException();
			}

			if (key != newKey || keyValue != newKeyValue) {
				changed = true;
				if (newKey != newKeyValue) {
					addSub(newKey, newKeyValue, dirtyOut);
				}
			} else {
				putSub(key, keyValue);
			}
		}
		UnaryOperator<SymbolicExpression> substituter = new ContextSubstituter(
				util.getUniverse(), subOperator);

		rangeMap.makeAllDirty(); // put everything on the work list
		for (Entry<Monic, Range> oldEntry = rangeMap
				.hold(); oldEntry != null; oldEntry = rangeMap.hold()) {
			Monic oldKey = oldEntry.getKey();
			NumericExpression newKey = (NumericExpression) substituter
					.apply(oldKey);
			if (oldKey != newKey) {
				changed = true;
				ContextExtractor extractor = new ContextExtractor(this,
						dirtyOut);
				extractor.extractCNF(oldEntry.getValue()
						.symbolicRepresentation(newKey, util.getUniverse()));
			} else {
				rangeMap.release();
			}
		}

		return changed;
	}

	/**
	 * Computes an (over-)estimate of the possible values of a
	 * {@link RationalExpression} based on the current assumptions of this
	 * {@link MutableContext}. Points at which this rational expression are
	 * undefined (because, e.g., the denominator is 0) are ignored.
	 * 
	 * @param expression
	 *            a non-{@code null} {@link RationalExpression}
	 * @return a {@link Range} of concrete values such that the result of
	 *         evaluating {@code rat} at any point satisfying the assumptions of
	 *         this context will lie in that range
	 */
	@Override
	public Range computeRange(NumericExpression expression) {
		if (expression instanceof Monomial)
			return computeRange((Monomial) expression);

		IdealFactory idf = util.idealFactory;
		RationalExpression rat = (RationalExpression) expression;

		return util.rangeFactory.divide(computeRange(rat.numerator(idf)),
				computeRange(rat.denominator(idf)));
	}

	/**
	 * Enters a substitution into this context's substitution map, and clears
	 * the simplification cache.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value that will be substituted for {@code key} in future
	 *            simplifications
	 */
	public void putSub(SymbolicExpression key, SymbolicExpression value) {
		subMap.put(key, value);
		clearSimplificationCache();
	}

	/**
	 * Normalizes an entry for the {@link #subMap} and adds it if it results in
	 * a change to the map. Adds an entry to the {@link #subWorklist}.
	 * 
	 * @param x
	 *            the key for the new substitution
	 * @param y
	 *            the value for the new substitution
	 * @param dirtySet
	 *            symbolic constants in any entries created or modified in this
	 *            operation will be added to this set
	 * @throws InconsistentContextException
	 *             if the addition of this fact results in a contradiction in
	 *             this context
	 */
	public void addSub(SymbolicExpression x, SymbolicExpression y,
			Set<SymbolicConstant> dirtySet)
			throws InconsistentContextException {
		Pair<SymbolicExpression, SymbolicExpression> pair = new Pair<>(x, y);

		standardizePair(pair);

		SymbolicExpression newKey = pair.left;

		if (newKey == null)
			return; // a trivial substitution

		SymbolicExpression newValue = pair.right;
		SymbolicExpression oldValue = getSub(newKey);

		if (oldValue != null && oldValue.equals(newValue))
			return; // this sub is already in the subMap
		updateSub(newKey, newValue, dirtySet);
	}

	public SymbolicExpression removeSubkey(SymbolicExpression key) {
		clearSimplificationCache();
		return subMap.remove(key);
	}

	/**
	 * Updates the state of this {@link MutableContext} by restricting the range
	 * of a normal {@link Monic}. This may result in changes to the
	 * {@link #rangeMap} , {@link #subMap}, or both.
	 * 
	 * @param key
	 *            a normal {@link Monic}
	 * @param range
	 *            a {@link Range} for {@code key}, with the same type
	 * @throws InconsistentContextException
	 *             if the restriction results in the {@code key} having an empty
	 *             range
	 */
	public void restrictRange(Monic key, Range range,
			Set<SymbolicConstant> dirtySet)
			throws InconsistentContextException {
		Range original = getRange(key);

		if (original == null) {
			SymbolicExpression value = getSub(key);

			if (value instanceof Constant) {
				Number number = ((Constant) value).number();

				if (range.containsNumber(number)) {
					return;
				} else {
					throw new InconsistentContextException();
				}
			}
			if (key.operator() == SymbolicOperator.MODULO) {
				Range modRange = computeDefaultModRange(
						(Monomial) key.argument(0), (Monomial) key.argument(1));

				range = util.rangeFactory.intersect(range, modRange);
			}
		} else {
			range = util.rangeFactory.intersect(original, range);
			if (range.equals(original))
				return;
		}
		if (range.isEmpty())
			throw new InconsistentContextException();

		Number value = range.getSingletonValue();

		if (value == null) {
			rangeMap.put(key, range);
			dirtySet.addAll(key.getFreeVars());
			clearSimplificationCache();
		} else {
			addSub(key, util.universe.number(value), dirtySet);
			if (original != null) {
				rangeMap.remove(key);
				clearSimplificationCache();
			}
		}
	}

	private static int simpCount = 0;

	private static PrintStream out = System.out;

	/**
	 * Simplifies a symbolic expression by looking in the substitution map of
	 * {@link #theContext} and applying an appropriate sequence of
	 * {@link Simplification}s. These actions are repeated until stabilization.
	 * 
	 * @param expr
	 *            the symbolic expression to be simplified
	 * @return the simplified version of the expression
	 */
	private SymbolicExpression simplifyExpressionWork(SymbolicExpression expr,
			Strategy strategy) {
		int id = simpCount++;
		int outercount = 0;

		if (debug) {
			out.println("Simplification " + id + " start : " + expr);
		}

		SymbolicExpression result = expr, x = expr;
		// the last simplification that made a change:
		Class<? extends Simplification> lastChange = null;

		outer : while (true) {
			SymbolicExpression tmp = getSub(x);

			if (tmp != null) {
				// There are some options here. After a context is completed,
				// the sub map should be idempotent, so we can just break.
				// But during simplification, it might not be, and breaking will
				// cause another simplification round, which will go through the
				// cache. That may or may not save time over iterating here.
				result = tmp;
				break;
			}

			List<Simplification> simplifications = strategy.select(x);
			int innercount = 0;

			for (Simplification s : simplifications) {
				if (s.getClass().equals(lastChange)) {
					// simplifications are idempotent
					// so no need to do one twice in a row
					continue;
				}
				if (debug) {
					out.println("Simplification " + id + "." + outercount + "."
							+ innercount + ": " + s.getClass().getSimpleName());
				}
				result = s.apply(this, strategy, x);
				if (debug) {
					out.print("Simplification " + id + "." + outercount + "."
							+ innercount + " result : ");
					if (x == result)
						out.println("no change");
					else
						out.println(result);
				}
				if (x != result) {
					x = result;
					outercount++;
					lastChange = s.getClass();
					continue outer;
				}
				innercount++;
			}
			break;
		}
		if (debug) {
			out.println("Simplification " + id + " final result : " + result);
		}
		return result;
	}

	/**
	 * Performs the work necessary for simplifying a sequence of symbolic
	 * expressions. The result is obtained by simplifying each component
	 * individually.
	 * 
	 * @param sequence
	 *            any canonic symbolic expression sequence
	 * @return the simplified sequence
	 */
	private SymbolicSequence<?> simplifySequenceWork(
			SymbolicSequence<?> sequence, Strategy strategy) {
		int size = sequence.size();
		SymbolicSequence<?> result = sequence;

		for (int i = 0; i < size; i++) {
			SymbolicExpression oldElement = sequence.get(i);
			SymbolicExpression newElement = (SymbolicExpression) simplify(
					oldElement, strategy);

			if (newElement != oldElement) {
				SymbolicExpression[] newElements = new SymbolicExpression[size];

				for (int j = 0; j < i; j++)
					newElements[j] = sequence.get(j);
				newElements[i] = newElement;
				for (int j = i + 1; j < size; j++)
					newElements[j] = (SymbolicExpression) simplify(
							sequence.get(j), strategy);
				result = util.universe.objectFactory().sequence(newElements);
				break;
			}
		}
		return result;
	}

	/**
	 * Performs the work required to simplify a non-simple symbolic type. A
	 * primitive type is returned unchanged. For compound types, simplification
	 * is recursive on the structure of the type. Ultimately a non-trivial
	 * simplification can occur because array types may involve an expression
	 * for the length of the array.
	 *
	 * <p>
	 * A subtle point is that the types must be simplified based on the *global*
	 * context, i.e., the last ancestor context of the current context, which is
	 * not an instance of {@link SubContext}. Otherwise, the simplified
	 * expression could end up with two different versions of a variable with
	 * two different types. The following example illustrates this:
	 * </p>
	 * 
	 * <pre>
	 * N:int
	 * A:int[N]
	 * A[0]>7         // this A has type int[N]
	 * N=1 -> A[0]=6  // this A has type int[1]
	 * </pre>
	 * 
	 * @param type
	 *            any non-null non-simple symbolic type
	 * @return simplified version of that type
	 */
	private SymbolicType simplifyTypeWork(SymbolicType type,
			Strategy strategy) {
		if (typeContext != this)
			return (SymbolicType) typeContext.simplify(type, strategy);
		
		PreUniverse universe = util.universe;
		SymbolicTypeKind kind = type.typeKind();

		switch (kind) {
			case ARRAY : {
				SymbolicArrayType arrayType = (SymbolicArrayType) type;
				SymbolicType elementType = arrayType.elementType();
				SymbolicType simplifiedElementType = (SymbolicType) simplify(
						elementType, strategy);

				if (arrayType.isComplete()) {
					NumericExpression extent = ((SymbolicCompleteArrayType) arrayType)
							.extent();
					NumericExpression simplifiedExtent = (NumericExpression) simplify(
							extent, strategy);

					if (elementType != simplifiedElementType
							|| extent != simplifiedExtent)
						return universe.arrayType(simplifiedElementType,
								simplifiedExtent);
					return arrayType;
				} else {
					if (elementType != simplifiedElementType)
						return universe.arrayType(simplifiedElementType);
					return arrayType;
				}
			}
			case FUNCTION : {
				SymbolicFunctionType functionType = (SymbolicFunctionType) type;
				SymbolicTypeSequence inputs = functionType.inputTypes();
				SymbolicTypeSequence simplifiedInputs = (SymbolicTypeSequence) simplify(
						inputs, strategy);
				SymbolicType output = functionType.outputType();
				SymbolicType simplifiedOutput = (SymbolicType) simplify(output,
						strategy);

				if (inputs != simplifiedInputs || output != simplifiedOutput)
					return universe.functionType(simplifiedInputs,
							simplifiedOutput);
				return type;
			}
			case TUPLE : {
				SymbolicTypeSequence sequence = ((SymbolicTupleType) type)
						.sequence();
				SymbolicTypeSequence simplifiedSequence = (SymbolicTypeSequence) simplify(
						sequence, strategy);

				if (simplifiedSequence != sequence)
					return universe.tupleType(((SymbolicTupleType) type).name(),
							simplifiedSequence);
				return type;
			}
			case UNION : {
				SymbolicTypeSequence sequence = ((SymbolicUnionType) type)
						.sequence();
				SymbolicTypeSequence simplifiedSequence = (SymbolicTypeSequence) simplify(
						sequence, strategy);

				if (simplifiedSequence != sequence)
					return universe.unionType(((SymbolicUnionType) type).name(),
							simplifiedSequence);
				return type;
			}
			default :
				throw new SARLInternalException("unreachable");
		}
	}

	/**
	 * Performs the work necessary to simplify a type sequence. The
	 * simplification of a type sequence is the sequence resulting from
	 * simplifying each component type individually.
	 * 
	 * @param sequence
	 *            any non-{@code null} type sequence
	 * @return the simplified sequence
	 */
	private SymbolicTypeSequence simplifyTypeSequenceWork(
			SymbolicTypeSequence sequence, Strategy strategy) {
		if (typeContext != this)
			return (SymbolicTypeSequence) typeContext.simplify(sequence, strategy);
		
		int size = sequence.numTypes();

		for (int i = 0; i < size; i++) {
			SymbolicType type = sequence.getType(i);
			SymbolicType simplifiedType = (SymbolicType) simplify(type,
					strategy);

			if (type != simplifiedType) {
				SymbolicType[] newTypes = new SymbolicType[size];

				for (int j = 0; j < i; j++)
					newTypes[j] = sequence.getType(j);
				newTypes[i] = simplifiedType;
				for (int j = i + 1; j < size; j++)
					newTypes[j] = (SymbolicType) simplify(sequence.getType(j),
							strategy);

				return util.universe.typeSequence(Arrays.asList(newTypes));
			}
		}
		return sequence;
	}

	/**
	 * Performs the work necessary to simplify a non-simple symbolic object.
	 * This just redirects to the appropriate specific method, such as
	 * {@link #simplifySequenceWork(SymbolicSequence)},
	 * {@link #simplifyTypeWork(SymbolicType)}, etc.
	 * 
	 * @param object
	 *            a non-null non-simple symbolic object
	 * @return the simplified version of that object
	 */
	private SymbolicObject simplifyObjectWork(SymbolicObject object,
			Strategy strategy) {
		switch (object.symbolicObjectKind()) {
			case EXPRESSION :
				return simplifyExpressionWork((SymbolicExpression) object,
						strategy);
			case SEQUENCE :
				return simplifySequenceWork((SymbolicSequence<?>) object,
						strategy);
			case TYPE :
				return simplifyTypeWork((SymbolicType) object, strategy);
			case TYPE_SEQUENCE :
				return simplifyTypeSequenceWork((SymbolicTypeSequence) object,
						strategy);
			default :
				throw new SARLInternalException("unreachable");
		}
	}

	@Override
	public SymbolicObject simplify(SymbolicObject object, Strategy strategy) {
		if (SimplifierUtility.isSimpleObject(object))
			return object;

		SymbolicObject result = getSimplification(object);

		if (result == null) {
			result = simplifyObjectWork(object, strategy);
			cacheSimplification(object, result);
		}
		return result;
	}

	@Override
	public SymbolicExpression genericSimplify(Strategy strategy,
			SymbolicExpression x) {
		return new GenericSimplification().apply(this, strategy, x);
	}

	// Public methods implementing methods specified in ContextIF...

	public void print(PrintStream out) {
		out.println("subMap:");
		SimplifierUtility.printMap(out, subMap);
		out.println("rangeMap:");
		SimplifierUtility.printMap(out, rangeMap);
		out.flush();
	}

	public boolean isInconsistent() {
		SymbolicExpression result = subMap.get(util.falseExpr);

		return result != null && result.isTrue();
	}

	public void assume(BooleanExpression expr) {
		ContextExtractor extractor = new ContextExtractor(this, theDirtySet);

		try {
			while (true) {
				extractor.extractCNF(expr);
				if (applySubMapToSelf(theDirtySet)) {
					expr = getFullAssumption();
					rangeMap.clear();
					subMap.clear();
				} else {
					break;
				}
			}
		} catch (InconsistentContextException e) {
			makeInconsistent();
		}
	}

	@Override
	public boolean isUnsat(BooleanExpression predicate,
			ProverHeuristic heuristic) {
		return unsat(predicate, heuristic) == Prove.RESULT_YES;
	}

	@Override
	public boolean isValid(BooleanExpression predicate,
			ProverHeuristic heuristic) {
		return valid(predicate, heuristic) == Prove.RESULT_YES;
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate,
			ProverHeuristic heuristic) {
		if (predicate.isTrue()) {
			return Prove.RESULT_YES;
		} else if (predicate.isFalse()) {
			return Prove.RESULT_NO;
		}

		return heuristic.attemptValid(predicate)
				? checkValidOrUnsat(predicate, false, false)
				: Prove.RESULT_MAYBE;
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate,
			ProverHeuristic heuristic) {
		if (predicate.isFalse()) {
			return Prove.RESULT_YES;
		} else if (predicate.isTrue()) {
			return Prove.RESULT_NO;
		}

		return heuristic.attemptUnsat(predicate)
				? checkValidOrUnsat(predicate, false, true)
				: Prove.RESULT_MAYBE;
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate,
			ProverHeuristic heuristic) {
		return heuristic.attemptValid(predicate)
				? checkValidOrUnsat(predicate, true, false)
				: Prove.RESULT_MAYBE;
	}

	private ValidityResult checkValidOrUnsat(BooleanExpression predicate,
			boolean getModel, boolean checkUnsat) {
		util.universe.incrementValidCount();

		PrintStream outStream = util.universe.getOutputStream();
		String queryTypeStr = getModel
				? "Model"
				: checkUnsat ? "Unsat" : "Valid";
		int queryId = util.universe.numValidCalls();
		String queryTitle = queryTypeStr + "-Query " + queryId;
		boolean showQuery = util.universe.getShowQueries();

		if (showQuery) {
			outStream.println(queryTitle + " context        : "
					+ getCollapsedAssumption(true));
			outStream.println(queryTitle + " assertion      : " + predicate);
			outStream.flush();
		}

		ValidityResult result = checkProverCache(predicate, getModel,
				checkUnsat);

		if (showQuery && result != null) {
			outStream.println(queryTitle + " cached result  :" + result);
		} else if (result == null) {
			BooleanExpression reducedPredicate = (BooleanExpression) util.universe
					.constantSubstituter(getAllSolvedVariables())
					.apply(predicate);
			BooleanExpression adaptedPredicate = proverAdaptor
					.apply(reducedPredicate);

			if (getModel) {
				assert !checkUnsat
						: "currently unsat-checking cannot give model";
				result = getProver().validOrModel(adaptedPredicate);
			} else {
				result = checkUnsat
						? getProver().unsat(adaptedPredicate)
						: getProver().valid(adaptedPredicate);
			}

			updateCache(predicate, result, checkUnsat);

			if (showQuery) {
				outStream.println(queryTitle + " result         : " + result);
				outStream.flush();
			}
		}

		return result;
	}

	private TheoremProver getProver() {
		if (prover == null) {
			BooleanExpression newContext = util.getUniverse().and(
					proverAdaptor.apply(getCollapsedAssumption(false)),
					proverAdaptor.getAxioms());

			if (logicFunctions != null) {
				prover = proverFactory.newProver(newContext, logicFunctions);
			} else {
				prover = proverFactory.newProver(newContext);
			}
		}
		return prover;
	}

	@Override
	public void updateCache(BooleanExpression predicate, ValidityResult result,
			boolean updateUnsatCache) {
		if (contextStackIsTrivial()) {
			if (updateUnsatCache)
				predicate.setUnsatisfiability(result.getResultType());
			else
				predicate.setValidity(result.getResultType());
			if (result instanceof ModelResult) {
				assert !updateUnsatCache
						: "currently unsat-checking cannot give a model";
				validityCache.putIfAbsent(predicate, result);
			}
		} else {
			if (updateUnsatCache)
				unsatCache.putIfAbsent(predicate, result);
			else
				validityCache.putIfAbsent(predicate, result);
		}
	}

	@Override
	public ValidityResult checkProverCache(BooleanExpression predicate,
			boolean getModel, boolean checkUnsat) {
		ResultType contextFreeResultType = contextStackIsTrivial()
				? checkUnsat
						? predicate.getUnsatisfiability()
						: predicate.getValidity()
				: null;
		if (contextFreeResultType != null) {
			return getModel && contextFreeResultType == ResultType.NO
					? validityCache.get(predicate)
					: Prove.validityResult(contextFreeResultType);
		} else {
			ValidityResult result = checkUnsat
					? unsatCache.get(predicate)
					: validityCache.get(predicate);
			if (result == null && superContext != null
					&& superContext.checkProverCache(predicate, getModel,
							checkUnsat) == Prove.RESULT_YES) {
				updateCache(predicate, Prove.RESULT_YES, checkUnsat);
				return Prove.RESULT_YES;
			}
			return result;
		}
	}

	@Override
	public BooleanExpression getReducedAssumption() {
		return getAssumption(false);
	}

	@Override
	public BooleanExpression getFullAssumption() {
		return getAssumption(true);
	}

	@Override
	public boolean contextIsTrivial() {
		return rangeMap.isEmpty() && subMap.isEmpty();
	}
	@Override
	public boolean contextStackIsTrivial() {
		boolean result = contextIsTrivial();
		if (superContext != null) {
			result &= superContext.contextStackIsTrivial();
		}
		return result;
	}

	public void normalize() {
		try {
			Normalizer rangeNormalizer = new RangeNormalizer(this);
			Normalizer substitutionNormalizer = new SubstitutionNormalizer(
					this);
			Normalizer tupleNormalizer = new TupleNormalizer(this);
			Normalizer gaussianNormalizer = new GaussianNormalizer(this);
			Normalizer multiOrNormalizer = new MultiOrNormalizer(this);
			Normalizer subMapNormalizer;

			if (SARLConstants.useMultiOrReduction)
				subMapNormalizer = new NormalizerChain(this,
						substitutionNormalizer, tupleNormalizer,
						gaussianNormalizer, multiOrNormalizer);
			else
				subMapNormalizer = new NormalizerChain(this,
						substitutionNormalizer, tupleNormalizer,
						gaussianNormalizer);

			Normalizer normalizer = new NormalizerChain(this, rangeNormalizer,
					subMapNormalizer);
			Set<SymbolicConstant> dirtOut = SimplifierUtility.newDirtySet();

			normalizer.normalize(theDirtySet, dirtOut);
		} catch (InconsistentContextException e) {
			makeInconsistent();
		}
	}

	// Public methods overriding implemented methods ...

	/*
	 * @Override public Context clone() { return new Context(util,
	 * proverFactory, SimplifierUtility.cloneTreeMap(subMap), rangeMap.clone(),
	 * backwardsSub); }
	 */

	@Override
	public String toString() {
		return "Context[" + subMap + ", " + rangeMap + "]";
	}

}
