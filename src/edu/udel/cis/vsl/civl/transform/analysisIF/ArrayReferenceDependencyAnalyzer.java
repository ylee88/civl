package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.udel.cis.vsl.abc.analysis.pointsTo.IF.AssignExprIF;
import edu.udel.cis.vsl.abc.analysis.pointsTo.IF.AssignExprIF.AssignExprKind;
import edu.udel.cis.vsl.abc.analysis.pointsTo.IF.AssignOffsetIF;
import edu.udel.cis.vsl.abc.analysis.pointsTo.IF.AssignStoreExprIF;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.util.ExpressionEvaluator;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSet;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetBaseElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetElement.RWSetElementKind;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetFieldElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetOffsetElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.ReadWriteDataStructures.RWSetSubscriptElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.SimpleReadWriteAnalyzer.SimpleFullSetException;

//TODO: need to be cleaned up
public class ArrayReferenceDependencyAnalyzer {

	private static enum CompareResult {
		IDENTICAL, INDEPENDENT, UNKNOWN,
	}

	private boolean debug = true;

	private SimpleReadWriteAnalyzer analyzer;

	/**
	 * global variable that refers to the {@link Function} where the current
	 * analyzing reads/writes happen:
	 */
	private Function currentFunction;

	public ArrayReferenceDependencyAnalyzer(SimpleReadWriteAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * <p>
	 * Check if the write to array elements of a thread is dependent on the read
	 * to array elements of another thread.
	 * </p>
	 * 
	 * <p>
	 * To check each pair of array element access "w" and "r", if their
	 * accessing base arrays are independent, "w" and "r" are independent; else
	 * their base arrays are identical, "w" and "r" have the forms:
	 * "a[i][...][j]" and "a[i'][...][j']". The check of "a[i][...][j]" and
	 * "a[i'][...][j']" is done by
	 * {@link #checkSubscriptIndependent(RWSetElement, RWSetElement, int, Set, Set)}
	 * </p>
	 * 
	 * @param function
	 *            the {@link Function} where all the writes and reads happens
	 * @param boundingConditions
	 *            a boolean assumption
	 * @param arrWrites
	 *            a set of write to array elements
	 * @param arrReads
	 *            a set of read to array elements
	 * @param fulWrites
	 *            the set containing all the write accesses that may be done in
	 *            a specific statement
	 * @param threadVariables
	 *            a set of thread variables, thread variables are the variables
	 *            that are guaranteed to be different on different threads
	 * @return true if and only if that threads read/write are independent
	 */
	public boolean threadsArrayAccessIndependent(Function function,
			List<ExpressionNode> boundingConditions,
			Iterable<RWSetElement> arrWrites, Iterable<RWSetElement> arrReads,
			Set<RWSetElement> fullWrites, Set<Variable> threadVariables) {
		boolean independent = true;
		this.currentFunction = function;

		for (RWSetElement w : arrWrites) {
			for (RWSetElement r : arrReads) {
				if (!w.root().mayEquals(r.root()))
					continue;

				if (debug) {
					System.out.println("Checking Array Refs for ");
					System.out.println(w + " and ");
					System.out.println(r);
				}
				independent &= checkIndependent(w, r, threadVariables,
						fullWrites);
				// independent &= checkSubscriptIndependent(w, r, wIdxs, rIdxs,
				// threadVariables, fullWrites);
				if (!independent) {
					System.out.println("may dependent!");
					return false;
				}
			}
		}
		return independent;
	}

	private boolean checkIndependent(RWSetElement w, RWSetElement r,
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		/*
		 * pre-process w and r :
		 * 
		 * w will never has (or recursively has) OFFSET kind since 1) the design
		 * of the structure disallow recursive OFFSET kind; 2) an OFFSET kind
		 * expression can never be written.
		 * 
		 * If r has OFFSET kind, denoted as "a := ptr + oft", it may dependent
		 * on w iff "w := ptr2" and "ptr" and "ptr2" are dependent.
		 * 
		 * Hence let r' = r if r has NO OFFSET kind, otherwise, r' = base(r).
		 * 
		 * w and r are pre-determined to be independent if w and r' has
		 * different depth.
		 */
		if (r.kind() == RWSetElementKind.OFFSET)
			r = ((RWSetOffsetElement) r).base();
		if (w.depth() != r.depth())
			return true;
		return checkIndependentWorker(w, r, threadVars,
				fullWrites) == CompareResult.INDEPENDENT;
	}

	/**
	 * pre-condition: w and r shall have exact same (recursive) structure
	 * 
	 * 
	 * @return
	 */
	private CompareResult checkIndependentWorker(RWSetElement w, RWSetElement r,
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		if (w.kind() != r.kind())
			return CompareResult.INDEPENDENT;

		if (w.kind() == RWSetElementKind.BASE) {
			RWSetBaseElement wb = (RWSetBaseElement) w,
					rb = (RWSetBaseElement) r;

			if (wb.base().mayEquals(rb.base()))
				return CompareResult.IDENTICAL;
			else
				return CompareResult.UNKNOWN;
		}

		CompareResult result;

		switch (w.kind()) {
			case FIELD : {
				RWSetFieldElement wf = (RWSetFieldElement) w,
						rf = (RWSetFieldElement) r;

				if (wf.field() != rf.field())
					return CompareResult.INDEPENDENT;
				return checkIndependentWorker(wf.struct(), rf.struct(),
						threadVars, fullWrites);
			}
			case SUBSCRIPT : {
				RWSetSubscriptElement ws = (RWSetSubscriptElement) w,
						rs = (RWSetSubscriptElement) r;

				result = checkIndependentWorker(ws.array(), rs.array(),
						threadVars, fullWrites);
				if (result != CompareResult.IDENTICAL)
					return result;
				return compareIndices(ws.offset(), ws.indices(), rs.offset(),
						rs.indices(), threadVars, fullWrites);
			}
			default :
				throw new CIVLInternalException(
						"unexpected error: independent analysis for "
								+ w.source().prettyRepresentation() + " and "
								+ r.source().prettyRepresentation(),
						w.source().getSource());
		}
	}

	/**
	 * <p>
	 * Recursively checks if two subscript expression: "a[i][...][j]" and
	 * "a[i'][...][j']" are independent if they are accessed by different
	 * threads.
	 * </p>
	 * 
	 * <p>
	 * Let "compare(idx, idx')" be a method compares two indices "idx" and
	 * "idx'". The result of comparison can be one of the three cases:
	 * <ol>
	 * <li>IDENTICAL: if both "idx" and "idx'" are read-only and they are
	 * lexically identical. IDENTICAL infers that they always have same values
	 * at runtime.</li>
	 * 
	 * <li>INDEPENDENT: if both "idx" and "idx'" are math-functions "f" and "f'"
	 * over thread variables and it can be proved
	 * <code>f(X) != f(X') iff X != X'</code>, where "X" ("X'") represents the
	 * inputs. Note that we say "idx" is a math-function over thread variables
	 * if "idx" only consists of read-only objects and thread variables. Note
	 * that "read-only" is a special case of "math-function".</li>
	 * 
	 * <li>UNKNOWN: nothing can be concluded</li>
	 * </ol>
	 * </p>
	 * 
	 * <p>
	 * Let "check" denote this method, now given "a[i][...][j]" and
	 * "a[i'][...][j']", pseudo code of this method is: <code>	
	 *   checkResult = check(a[i][...], a[i'][...]);
	 *   if (arrayResult == IDENTICAL) 
	 *     return compare(j, j');
	 *   if (arrayResult == INDEPENDENT)
	 *     return INDEPENDENT;
	 *   else 
	 *     return UNKNOWN;
	 * </code>
	 * </p>
	 * 
	 */
	private CompareResult compareIndices(AssignOffsetIF aOft,
			ExpressionNode[] aIndices, AssignOffsetIF bOft,
			ExpressionNode[] bIndices, Set<Variable> threadVars,
			Set<RWSetElement> fullWrites) {
		if (!aOft.hasConstantValue() || !bOft.hasConstantValue())
			return CompareResult.UNKNOWN;
		assert aIndices.length == bIndices.length;

		Integer aOftInt = aOft.constantValue();
		Integer bOftInt = bOft.constantValue();

		CompareResult result = compare2(aOftInt, aIndices[0], bOftInt,
				bIndices[0], threadVars, fullWrites);
		int i = 1;

		while (result == CompareResult.IDENTICAL && i++ < aIndices.length) {
			result = compare2(0, aIndices[i], 0, bIndices[i], threadVars,
					fullWrites);
		}
		return result;
	}

	private CompareResult compare2(Integer oft0, ExpressionNode idx0,
			Integer oft1, ExpressionNode idx1, Set<Variable> threadVars,
			Set<RWSetElement> fullWrites) {
		Set<Variable> mathFuncInputs = new HashSet<>();
		Set<Variable> tmp = getMathFuncInputs(idx0, fullWrites, threadVars);

		if (tmp == null)
			return CompareResult.UNKNOWN;
		mathFuncInputs.addAll(tmp);
		tmp = getMathFuncInputs(idx1, fullWrites, threadVars);
		if (tmp == null)
			return CompareResult.UNKNOWN;
		mathFuncInputs.addAll(tmp);
		if (mathFuncInputs.isEmpty()) {
			// IF both index expressions are pure read-only, check if they are
			// lexically identical:
			if (ExpressionEvaluator.checkEqualityWithConditions2(oft0, idx0,
					oft1, idx1, new LinkedList<>()))
				return CompareResult.IDENTICAL;
		}
		// IF both index expressions are math functions (including read-only)
		// over thread vars, check if they are independent:
		if (ExpressionEvaluator.checkFunctionDisagrement(oft0, idx0, oft1, idx1,
				mathFuncInputs))
			return CompareResult.INDEPENDENT;
		return CompareResult.UNKNOWN;
	}

	/**
	 * <p>
	 * return true iff the given expression "expr" is a math function over the
	 * given set of thread-variables.
	 * </p>
	 *
	 * 
	 * @return the subset of the thread variables that the given expression
	 *         depends on if the expression is a math-function over the set of
	 *         thread variables. Otherwise, null;
	 */
	private Set<Variable> getMathFuncInputs(ExpressionNode expr,
			Set<RWSetElement> fullWrites, Set<Variable> threadVars) {
		RWSet exprRWSet;
		try {
			exprRWSet = analyzer.collectRWFromStmtDeclExpr(currentFunction,
					expr, new HashSet<>());
		} catch (SimpleFullSetException e) {
			return fullWrites.isEmpty() ? new HashSet<>() : null;
		}
		assert exprRWSet.writes().isEmpty();

		// fullWrites contains no thread variables, so
		// expr is considered a function over thread variables if
		// the intersection of "exprRWSet.reads" and "fullWrites" is empty
		List<RWSetElement> intersect = exprRWSet.reads().parallelStream()
				.filter(referToSameObject(fullWrites))
				.collect(Collectors.toList());

		// If there exist objects that are in the write set, this is not a
		// math-function:
		if (!intersect.isEmpty())
			return null;

		// get the input subset:
		Set<Variable> subset = new HashSet<>();

		for (RWSetElement e : exprRWSet.reads()) {
			AssignExprIF varAbs = e.root();

			assert varAbs != null && varAbs.kind() == AssignExprKind.STORE;
			AssignStoreExprIF store = (AssignStoreExprIF) varAbs;

			if (store.isAllocation())
				continue;
			if (threadVars.contains(store.variable()))
				subset.add(store.variable());
		}
		return subset;
	}

	/* ************* java predicates ************/
	/**
	 * a predicate over a set of RWSetElement that each element t refers to the
	 * same variable or heap object as at least one element t' in the given
	 * "otherSet"
	 */
	private Predicate<RWSetElement> referToSameObject(
			Set<RWSetElement> otherSet) {
		return new Predicate<RWSetElement>() {
			Set<RWSetElement> compareTo = otherSet;

			@Override
			public boolean test(RWSetElement t) {
				for (RWSetElement e : compareTo)
					if (e.root().mayEquals(t.root()))
						return true;
				return false;
			}
		};
	}
}
