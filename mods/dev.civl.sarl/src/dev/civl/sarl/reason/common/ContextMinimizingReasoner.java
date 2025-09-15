package dev.civl.sarl.reason.common;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.civl.sarl.IF.ModelResult;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.UnaryOperator;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Interval;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.ideal.IF.IdealFactory;
import dev.civl.sarl.ideal.IF.RationalExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.prove.IF.TheoremProverFactory;
import dev.civl.sarl.simplify.IF.ContextPartition;
import dev.civl.sarl.simplify.IF.Range;
import dev.civl.sarl.simplify.IF.Simplify;
import dev.civl.sarl.simplify.simplification.ProverHeuristic;
import dev.civl.sarl.simplify.simplification.Strategy;
import dev.civl.sarl.simplify.simplification.TotalProverHeuristic;
import dev.civl.sarl.simplify.simplifier.Context;

/**
 * <p>
 * A {@link Reasoner} based on <strong>context minimization</strong>. Given a
 * context (the boolean expression which serves as the underlying assumption)
 * and a predicate (the boolean expression to check for validity or to be
 * simplified), the context minimization algorithm produces a new context which
 * is possibly weaker than the original one, but which is guaranteed to produce
 * an equivalent result when used as the context for validity or simplification.
 * </p>
 * 
 * <p>
 * In addition, this reasoner uses simplification, caching, and calls to
 * underlying {@link TheoremProver}s as needed.
 * </p>
 * 
 * @see {@link ContextPartition}
 * 
 * @author Stephen F. Siegel
 */
public class ContextMinimizingReasoner implements Reasoner {

	// Static fields...

	/**
	 * Print debugging information?
	 */
	private final static boolean debug = false;

	/**
	 * Where to print the debugging information.
	 */
	private final static PrintStream debugOut = System.out;

	// Instance fields...

	protected PreUniverse universe;

	protected ContextMinimizingReasonerFactory reasonerFactory;

	protected List<Context> contextStack = new LinkedList<>();

	protected UnaryOperator<SymbolicExpression> boundCleaner;

	final protected boolean backwardsSub;

	/**
	 * The context (i.e., path condition) associated to this reasoner. All
	 * simplifications and queries are executed using this context as the
	 * underlying assumption.
	 */
	private List<BooleanExpression> origAssumptionStack;

	/**
	 * The partition of the set of conjunctive clauses of the context into
	 * equivalence classes. Two clauses are equivalent if they share a common
	 * variable; complete (take the transitive closure) to an equivalence
	 * relation.
	 */
	private ContextPartition partition;

	/**
	 * A set of pure functions with definitions:
	 */
	private ProverFunctionInterpretation logicFunctions[];

	// Constructors...

	/**
	 * Constructs new context-minimizing reasoner.
	 * 
	 * @param factory
	 *            the factory used for producing this and other instances of
	 *            {@link ContextMinimizingReasoner}
	 * @param context
	 *            the context (i.e., path condition), the fixed, underlying
	 *            assumption used when processing all simplification and theorem
	 *            prover queries with this reasoner
	 */
	public ContextMinimizingReasoner(PreUniverse universe,
			IdealFactory idealFactory, TheoremProverFactory proverFactory,
			ContextMinimizingReasonerFactory reasonerFactory,
			List<BooleanExpression> origAssumptionStack,
			boolean useBackwardSubstitution,
			ProverFunctionInterpretation logicFunctions[]) {
		int stackSize = origAssumptionStack.size();

		assert stackSize > 0;
		assert logicFunctions != null;

		this.universe = universe;
		this.reasonerFactory = reasonerFactory;
		this.logicFunctions = logicFunctions;
		this.origAssumptionStack = origAssumptionStack;
		this.partition = Simplify.newContextPartition(universe,
				origAssumptionStack);
		this.backwardsSub = useBackwardSubstitution;
		this.boundCleaner = universe.newMinimalBoundCleaner();

		Context lastContext = Context.newContext(universe, idealFactory,
				proverFactory,
				(BooleanExpression) boundCleaner
						.apply(origAssumptionStack.get(0)),
				useBackwardSubstitution, logicFunctions);

		contextStack.add(lastContext);
		for (int i = 1; i < stackSize; i++) {
			lastContext = lastContext
					.createSubContext((BooleanExpression) boundCleaner
							.apply(origAssumptionStack.get(i)));
			contextStack.add(lastContext);
		}
	}

	private Context topContext() {
		return contextStack.get(contextStack.size() - 1);
	}

	/**
	 * Use context minimization to compute a reduced context for the given
	 * expression.
	 * 
	 * @param expression
	 *            a symbolic expression that is to be simplified or validated
	 * @return the {@link Reasoner} for the reduced context
	 */
	private ContextMinimizingReasoner getMinimizedReasonerFor(
			SymbolicExpression expression) {
		List<BooleanExpression> minimizedAssumptionStack = partition
				.minimizeFor(expression, universe);
		ContextMinimizingReasoner minimizedReasoner;

		if (minimizedAssumptionStack == origAssumptionStack) {
			minimizedReasoner = this;
		} else {
			minimizedReasoner = getReasoner(minimizedAssumptionStack,
					backwardsSub);
		}
		return minimizedReasoner;
	}

	/**
	 * Attempts to determine validity (or unsatisfiability) of
	 * <code>predicate</code> without printing anything. Uses context-reduction,
	 * caching, simplification, and theorem-provers as needed.
	 * 
	 * @param predicate
	 *            non-<code>null</code> boolean expression whose validity under
	 *            this context is to be determined
	 * @param getModel
	 *            if <code>true</code>, try to find a model (concrete
	 *            counterexample) if the result is not valid, i.e., return an
	 *            instance of {@link ModelResult}.
	 * @param checkUnsat
	 *            if <code>true</code>, try to determine if the conjunction of
	 *            the given predicate and context is unsatisfiable; otherwise,
	 *            try to determine if the context entails the predicate.
	 * @return a non-<code>null</code> validity result
	 */
	private ValidityResult checkValidOrUnsat(BooleanExpression predicate,
			boolean getModel, boolean checkUnsat) {
		if (predicate.isTrue())
			return checkUnsat ? Prove.RESULT_NO : Prove.RESULT_YES;
		if (predicate.isFalse())
			return checkUnsat ? Prove.RESULT_YES : Prove.RESULT_NO;

		ValidityResult result = topContext().checkProverCache(predicate,
				getModel, checkUnsat);

		if (result != null)
			return result;
		ContextMinimizingReasoner reducedReasoner = getMinimizedReasonerFor(
				predicate);
		BooleanExpression transformedPredicate = predicate;

		if (debug) {
			debugOut.println("Reduced context       : "
					+ reducedReasoner.origAssumptionStack);
			debugOut.println("Transformed predicate : " + transformedPredicate);
		}

		if (reducedReasoner != this) {
			result = reducedReasoner.validOrUnsatCacheNoReduce(
					transformedPredicate, getModel, checkUnsat);
			topContext().updateCache(transformedPredicate, result, checkUnsat);
		} else {
			result = this.validOrUnsatNoCacheNoReduce(transformedPredicate,
					getModel, checkUnsat);
		}
		return result;
	}

	/**
	 * <p>
	 * Attempts to determine the validity of <code>predicate</code>, without
	 * printing anything and without using context-reduction. May check the
	 * cache(s) for previous results on <code>predicate</code>; may use
	 * simplification; may use the theorem prover.
	 * </p>
	 * 
	 * <p>
	 * Precondition: the <code>context</code> is already reduced for
	 * <code>predicate</code>.
	 * </p>
	 * 
	 * @param predicate
	 *            non-<code>null</code> boolean expression whose validity under
	 *            this context is to be determined
	 * @param getModel
	 *            if <code>true</code>, try to find a model (concrete
	 *            counterexample) if the result is not valid, i.e., return an
	 *            instance of {@link ModelResult}.
	 * @param checkUnsat
	 *            if <code>true</code>, check for unsatisfiability; otherwise
	 *            check validity
	 * @return a non-<code>null</code> validity result
	 */
	private ValidityResult validOrUnsatCacheNoReduce(
			BooleanExpression predicate, boolean getModel, boolean checkUnsat) {
		ValidityResult result = topContext().checkProverCache(predicate,
				getModel, checkUnsat);

		if (result != null)
			return result;
		result = validOrUnsatNoCacheNoReduce(predicate, getModel, checkUnsat);
		return result;
	}

	private SymbolicExpression simplifyWork(SymbolicExpression expr,
			Strategy strategy) {
		// rename bound variables with counts starting from where the
		// original assumption renaming left off. This ensures that
		// all bound variables in the assumption and x are unique, but
		// two different x's can have same bound variables (thus
		// improving canonicalization)...
		expr = universe.cloneBoundCleaner(boundCleaner).apply(expr);
		return (SymbolicExpression) topContext().simplify(expr, strategy);
	}

	public static int dbgcnt1 = 0;

	/**
	 * <p>
	 * Attempts to determine the validity of <code>predicate</code>, without
	 * printing anything, without using context-reduction, and without checking
	 * the cache(s) for previous results on <code>predicate</code>. May use
	 * simplification and the theorem prover.
	 * </p>
	 * 
	 * <p>
	 * Precondition: the <code>context</code> is already reduced for
	 * <code>predicate</code>.
	 * </p>
	 * 
	 * @param predicate
	 *            non-<code>null</code> boolean expression whose validity under
	 *            this context is to be determined
	 * @param getModel
	 *            if <code>true</code>, try to find a model (concrete
	 *            counterexample) if the result is not valid, i.e., return an
	 *            instance of {@link ModelResult}.
	 * @param checkUnsat
	 *            if <code>true</code>, try to determine if the conjunction of
	 *            the given predicate and context is unsatisfiable; otherwise,
	 *            try to determine if the context entails the predicate.
	 * @return a non-<code>null</code> validity result
	 */
	private ValidityResult validOrUnsatNoCacheNoReduce(
			BooleanExpression predicate, boolean getModel, boolean checkUnsat) {
		if (debug) {
			dbgcnt1++;
			debugOut.println("dbgcnt1 = " + dbgcnt1);
		}
		List<BooleanExpression> newAssumptionStack = getReducedContextStack();
		BooleanExpression newPredicate = (BooleanExpression) simplifyWork(
				predicate, Strategy.standardStrategy());
		ValidityResult result = null;
		ContextMinimizingReasoner newReasoner; // may be same as old

		if (newAssumptionStack.equals(origAssumptionStack)) {
			newReasoner = this;
		} else {
			newReasoner = getReasoner(newAssumptionStack, backwardsSub);
		}

		if (newPredicate != predicate
				|| !newAssumptionStack.equals(origAssumptionStack)) {
			// the predicate or context got simpler, so start over again
			// with checks of trivial cases, cache, etc...
			if (debug) {
				debugOut.println(
						"Context              : " + origAssumptionStack);
				debugOut.println(
						"Simplified context   : " + newAssumptionStack);
				debugOut.println("Predicate            : " + predicate);
				debugOut.println("Simplified predicate : " + newPredicate);
				debugOut.flush();
			}
			result = newReasoner.checkValidOrUnsat(newPredicate, getModel,
					checkUnsat);
			// TODO: Cache in this reasoner too?
		} else {
			ProverHeuristic totalPH = new TotalProverHeuristic();
			result = getModel
					? topContext().validOrModel(newPredicate, totalPH)
					: checkUnsat
							? topContext().unsat(newPredicate, totalPH)
							: topContext().valid(newPredicate, totalPH);
		}
		return result;
	}

	/**
	 * Attempts to reduce the given <code>expression</code> to a concrete
	 * {@link Number}, without using context-reduction.
	 * 
	 * Precondition: this <code>context</code> is already the reduced context
	 * for <code>expression</code>.
	 * 
	 * @param expression
	 *            a non-<code>null</code> numeric expression
	 * @return <code>null</code> or concrete {@link Number}
	 */
	private Number extractNumberNoReduce(NumericExpression expression) {
		NumericExpression simple = (NumericExpression) simplify(expression);

		return universe.extractNumber(simple);
	}

	// Public methods...

	@Override
	public Map<SymbolicConstant, SymbolicExpression> constantSubstitutionMap() {
		return topContext().getAllSolvedVariables();
	}

	@Override
	public BooleanExpression getReducedCollapsedContext() {
		return universe.and(getReducedContextStack());
	}

	@Override
	public BooleanExpression getFullCollapsedContext() {
		return universe.and(getFullContextStack());
	}

	@Override
	public BooleanExpression getReducedContext(int index) {
		return contextStack.get(index).getReducedAssumption();
	}

	@Override
	public BooleanExpression getFullContext(int index) {
		return contextStack.get(index).getFullAssumption();
	}

	@Override
	public List<BooleanExpression> getReducedContextStack() {
		List<BooleanExpression> reducedContextStack = new ArrayList<>(
				contextStack.size());
		for (int i = 0; i < contextStack.size(); i++) {
			reducedContextStack.add(getReducedContext(i));
		}
		return reducedContextStack;
	}

	@Override
	public List<BooleanExpression> getFullContextStack() {
		List<BooleanExpression> fullContextStack = new ArrayList<>(
				contextStack.size());
		for (int i = 0; i < contextStack.size(); i++) {
			fullContextStack.add(getFullContext(i));
		}
		return fullContextStack;
	}

	@Override
	public void aggressivelySimplifyTopContext(
			Set<SymbolicConstant> aggressiveSet) {
		topContext().simplifyAssumption(
				aggressiveSet == null ? new HashSet<>() : aggressiveSet);
	}

	@Override
	public Interval assumptionAsInterval(SymbolicConstant symbolicConstant) {
		return topContext().assumptionAsInterval(symbolicConstant);
	}

	@Override
	public <T extends SymbolicExpression> T simplify(T expression,
			Set<SymbolicConstant> aggressiveSet) {

		if (debug) {
			debugOut.println("Simplifying            :" + expression + " ("
					+ expression.type() + ")");
			debugOut.println("Simplification context : " + origAssumptionStack);
		}

		ContextMinimizingReasoner reducedReasoner = getMinimizedReasonerFor(
				expression);
		Strategy strategy = aggressiveSet == null
				? Strategy.standardStrategy()
				: Strategy.standardFreeVarStrategy(aggressiveSet);
		@SuppressWarnings("unchecked")
		T result = (T) reducedReasoner.simplifyWork(expression, strategy);

		if (debug) {
			debugOut.println("Simplification result  : " + result + " ("
					+ result.type() + ")");
		}
		return result;
	}

	@Override
	public <T extends SymbolicExpression> T simplify(T expression) {
		return simplify(expression, null);
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate) {
		ValidityResult result = checkValidOrUnsat(predicate, false, true);
		return result;
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		ValidityResult result = checkValidOrUnsat(predicate, false, false);
		return result;
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		boolean showQuery = universe.getShowQueries();

		if (showQuery) {
			PrintStream out = universe.getOutputStream();
			int id = universe.numValidCalls();

			out.println(
					"ModelQuery " + id + " context   : " + origAssumptionStack);
			out.println("ModelQuery " + id + " assertion : " + predicate);
			out.flush();
		}

		ValidityResult result = checkValidOrUnsat(predicate, true, false);

		if (showQuery) {
			PrintStream out = universe.getOutputStream();
			int id = universe.numValidCalls();

			out.println("ModelQuery " + id + " result    : " + result);
			out.flush();
		}
		return result;
	}

	@Override
	public boolean isValid(BooleanExpression predicate) {
		return valid(predicate).getResultType() == ResultType.YES;
	}

	@Override
	public Number extractNumber(NumericExpression expression) {
		return getMinimizedReasonerFor(expression)
				.extractNumberNoReduce(expression);
	}

	@Override
	public Interval intervalApproximation(NumericExpression expr) {
		Range range = topContext().computeRange((RationalExpression) expr);
		Interval result = range.intervalOverApproximation();

		return result;
	}

	@Override
	public boolean checkBigOClaim(BooleanExpression indexConstraint,
			NumericExpression lhs, NumericSymbolicConstant[] limitVars,
			int[] orders) {
		// strategy: create new context and add index constraint to the
		// assumption. Perform Taylor expansions where appropriate.
		// TODO: rename the indexConstraint and the limitVars if they conflict
		// with any free variables.
		List<BooleanExpression> oldAssumptionStack = getFullContextStack();
		List<BooleanExpression> newAssumptionStack = new ArrayList<>(
				oldAssumptionStack.size() + 1);
		newAssumptionStack.addAll(oldAssumptionStack);
		newAssumptionStack.add(indexConstraint);
		Reasoner newReasoner = getReasoner(newAssumptionStack, true);
		TaylorSubstituter taylorSubstituter = new TaylorSubstituter(universe,
				universe.objectFactory(), universe.typeFactory(), newReasoner,
				limitVars, orders);
		NumericExpression newLhs = (NumericExpression) taylorSubstituter
				.apply(lhs);

		newLhs = taylorSubstituter.reduceModLimits(newLhs);
		return newReasoner
				.isValid(universe.equals(newLhs, universe.zeroReal()));
	}

	/**
	 * Get a {@link ContextMinimizingReasoner} from the reasoner factory. Hide
	 * the information of {@link ProverFunctionInterpretation}s from callers
	 * since this reasoner does not support ProverPredicate.
	 */
	protected ContextMinimizingReasoner getReasoner(
			List<BooleanExpression> assumptionStack,
			boolean useBackwardsSubstitution) {
		return reasonerFactory.getReasoner(assumptionStack,
				useBackwardsSubstitution, logicFunctions);
	}
}
