package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.type.IF.ArrayType;
import edu.udel.cis.vsl.abc.ast.type.IF.PointerType;
import edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind;
import edu.udel.cis.vsl.abc.ast.util.ExpressionEvaluator;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.analysisIF.SimpleReadWriteAnalyzer.RWSet;
import edu.udel.cis.vsl.civl.transform.analysisIF.SimpleReadWriteAnalyzer.RWSetElement;
import edu.udel.cis.vsl.civl.transform.analysisIF.SimpleReadWriteAnalyzer.SimpleFullSetException;

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
			assert w.arraySubscript != null;

			for (RWSetElement r : arrReads) {
				// If two entities are different or one of them is null, the w
				// and r are not referring to the same object;
				// If two entities are same, w and r are referring to the same
				// object:
				if (w.entity != r.entity)
					continue;
				// If two entities are both null, w and r are referring to
				// allocations, if their allocations are coming from the same
				// lexical allocation statement, they might referring to the
				// same object:
				if (w.entity == null)
					if (w.strOrAlloc != r.strOrAlloc)
						continue;
				if (debug) {
					System.out.println("Checking Array Refs for ");
					System.out.println(print(w) + " and ");
					System.out.println(print(r));
				}
				independent &= checkSubscriptIndependent(w, r, threadVariables,
						fullWrites);
				if (!independent)
					return false;
			}
		}
		return independent;
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
	 * if "idx" only consists of read-only objects and thread variables.</li>
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
	private boolean checkSubscriptIndependent(RWSetElement w, RWSetElement r,
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		int dims;

		if (w.entity != null) {
			ArrayType arrType = (ArrayType) ((Variable) w.entity).getType();

			dims = arrType.getDimension();
		} else {
			PointerType ptrType = (PointerType) w.strOrAlloc.nonEntitySource()
					.getType();

			dims = 0;
			do {
				dims++;
				if (ptrType.referencedType().kind() == TypeKind.POINTER)
					ptrType = (PointerType) ptrType.referencedType();
				else
					break;
			} while (true);
		}

		ExpressionNode wIdx[] = getSubscriptIndices(w.arraySubscript);
		ExpressionNode rIdx[] = getSubscriptIndices(r.arraySubscript);
		ExpressionNode unimplExpr = null;

		/*
		 * Note that there is a limitation to due the abstraction made by {@link
		 * FlowInsensePointsToAnalyzer}: given expression in program "a[i][j]"
		 * where "a" is a pointer and "a" points array "T b[N][N][N]". Due to
		 * abstraction, "a" actually may points-to "b" or "b[x]" but will be
		 * over-approximated as "b", hence we have no way to decide if the
		 * "b[i][j]" , which is the abstraction of "a[i][j]", is independent of
		 * another expression "b[x][i][j]".
		 * 
		 * Therefore, if the dimension of the base array of the subscript
		 * expression is NOT equal to the number of indices in a subscript
		 * expression, this method has to give up.
		 */
		if (wIdx.length != dims)
			unimplExpr = w.arraySubscript;
		else if (rIdx.length != dims)
			unimplExpr = r.arraySubscript;
		if (unimplExpr != null)
			throw new CIVLUnimplementedFeatureException(
					"OpenMP simplifier analyze a subscript operation "
							+ unimplExpr.prettyRepresentation()
							+ " with a pointer to a sub-array of "
							+ w.entity.getName());

		boolean independent = checkSubscriptIndependentWorker(dims, wIdx, rIdx,
				threadVars, fullWrites) == CompareResult.INDEPENDENT;

		if (debug) {
			if (!independent) {
				System.out.print("possible data race:");
				System.out.print("a thread write to " + print(w));
				System.out.println(
						" while another thread reads " + print(r) + "\n");
			} else
				System.out.println("independent\n");
		}
		return independent;
	}

	/**
	 * See
	 * {@linkplain ArrayReferenceDependencyAnalyzer#checkSubscriptIndependent(RWSetElement, RWSetElement, Set, Set)}
	 * 
	 * @param currentDim
	 *            the current checking dimension
	 * @param wIdx
	 *            indices (0th element is the index of the first dimension) of
	 *            the original subscript expression. Current checking subscript
	 *            expression is indexed by<code>
	 *            wIdx[0], wIdx[1], ..., wIdx[currentDim]
	 *            </code>
	 * @param rIdx
	 *            similar to wIdx for the other subscript expression
	 * @param threadVars
	 *            thread variable set
	 * @param fullWrites
	 *            full write set
	 * @return
	 */
	private CompareResult checkSubscriptIndependentWorker(int currentDim,
			ExpressionNode wIdx[], ExpressionNode rIdx[],
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		assert currentDim >= 1;
		if (currentDim == 1)
			return this.compare(wIdx[0], rIdx[0], threadVars, fullWrites);

		CompareResult arrayResult = checkSubscriptIndependentWorker(
				currentDim - 1, wIdx, rIdx, threadVars, fullWrites);

		if (arrayResult == CompareResult.IDENTICAL)
			return compare(wIdx[currentDim - 1], rIdx[currentDim - 1],
					threadVars, fullWrites);
		else
			return arrayResult;
	}

	/**
	 * 
	 * see
	 * {@link #checkSubscriptIndependentWorker(int, ExpressionNode[], ExpressionNode[], Set, Set)}
	 */
	private CompareResult compare(ExpressionNode idx0, ExpressionNode idx1,
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
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
			if (ExpressionEvaluator.checkEqualityWithConditions(idx0, idx1,
					new LinkedList<>()))
				return CompareResult.IDENTICAL;
		} else {
			// IF botn index expressions are math functions over thread vars,
			// check if they are independent:
			if (ExpressionEvaluator.checkFunctionDisagrement(idx0, idx1,
					mathFuncInputs))
				return CompareResult.INDEPENDENT;
		}
		return CompareResult.UNKNOWN;
	}

	/**
	 * 
	 * @param arraySubscripts
	 * @return an array of index expressions. Indices in the returned array are
	 *         ordered as in lexical array subscript from LEFT to RIGHT
	 */
	private ExpressionNode[] getSubscriptIndices(OperatorNode arraySubscripts) {
		OperatorNode opNode = arraySubscripts;
		// indices from left to right:
		LinkedList<ExpressionNode> indices = new LinkedList<>();

		while (opNode.getOperator() == Operator.SUBSCRIPT) {
			indices.addFirst(opNode.getArgument(1));
			if (opNode.getArgument(0)
					.expressionKind() == ExpressionKind.OPERATOR)
				opNode = (OperatorNode) opNode.getArgument(0);
			else
				break;
		}

		ExpressionNode[] ret = new ExpressionNode[indices.size()];

		indices.toArray(ret);
		return ret;
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
		assert exprRWSet.writes.isEmpty();

		// fullWrites contains no thread variables, so
		// expr is considered a function over thread variables if
		// the intersection of "exprRWSet.reads" and "fullWrites" is empty
		List<RWSetElement> intersect = exprRWSet.reads.parallelStream()
				.filter(referToSameObject(fullWrites))
				.collect(Collectors.toList());

		if (!intersect.isEmpty())
			return null;

		// get the input subset:
		Set<Variable> subset = new HashSet<>();

		for (RWSetElement e : exprRWSet.reads)
			if (e.arraySubscript == null && threadVars.contains(e.entity))
				subset.add((Variable) e.entity);
			else
				return null;
		return subset;
	}

	/* *********** printing ********** */

	private String print(RWSetElement e) {
		String arr;
		if (e.entity != null)
			arr = e.entity.getName();
		else
			arr = "alloc:" + e.strOrAlloc.nonEntitySource().getSource()
					.getLocation(true);

		ExpressionNode idx[] = getSubscriptIndices(e.arraySubscript);
		String result = arr;

		for (int i = 0; i < idx.length; i++)
			result += "[" + idx[i].prettyRepresentation() + "]";
		return result;
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
					if (e.sameObject(t))
						return true;
				return false;
			}
		};
	}
}
