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

	private boolean debug = true;

	private SimpleReadWriteAnalyzer analyzer;

	/**
	 * global variable that refers to the {@link Function} where the current
	 * analyzing reads/writes happen:
	 */
	private Function currentFunction;

	/**
	 * global list that refers to the bounding conditions of the thread
	 * variables in the current analyzing reads/writes:
	 */
	private List<ExpressionNode> boundingConditions;

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
	 * Since threads are sharing the lexical program, we check the in-dependency
	 * by: checking if for every write to array element "a[i][...][j]" in a
	 * generic thread and for every read to array element "a'[i'][...][j']" in
	 * the generic thread such that
	 * <ol>
	 * <li>if a == a', then i == i' and ... and j == j'</li>
	 * <li>and i,i', ... ,j,j' are all INJECTIVE functions over thread
	 * variables, where thread variables are the variables that are guaranteed
	 * to be different on different threads.</li>
	 * </ol>
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
		this.boundingConditions = boundingConditions;

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
				independent &= isIndependentWorker(w, r, threadVariables,
						fullWrites);
				if (!independent)
					return false;
			}
		}
		return independent;
	}

	/**
	 * returns true iff the write to array element "a[i][...][j]" in a thread is
	 * independent with the read to array element "a'[i'][...][j']" in another
	 * thread.
	 */
	private boolean isIndependentWorker(RWSetElement w, RWSetElement r,
			Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		boolean independent;

		if (w.entity != null) {
			ArrayType arrType = (ArrayType) ((Variable) w.entity).getType();

			independent = checkSubscriptIndependent(w, r,
					arrType.getDimension(), threadVars, fullWrites);
		} else {
			PointerType ptrType = (PointerType) w.strOrAlloc.nonEntitySource()
					.getType();
			int dims = 0;

			do {
				dims++;
				if (ptrType.referencedType().kind() == TypeKind.POINTER)
					ptrType = (PointerType) ptrType.referencedType();
				else
					break;
			} while (true);
			independent = checkSubscriptIndependent(w, r, dims, threadVars,
					fullWrites);
		}
		if (debug && !independent) {
			System.out.print("possible data race:");
			System.out.print("a thread write to " + print(w));
			System.out.println(" while another thread reads " + print(r));
		}
		return independent;
	}

	/**
	 * <p>
	 * check if a data race is possible when a thread writes to an array
	 * subscript expression "w" while another thread reads an array subscript
	 * expression "r".
	 * </p>
	 * 
	 * <p>
	 * Note that there is a limitation to due the abstraction made by
	 * {@link FlowInsensePointsToAnalyzer}: given expression in program
	 * "a[i][j]" where "a" is a pointer and "a" points array "T b[N][N][N]". Due
	 * to abstraction, "a" actually may points-to "b" or "b[x]" but will be
	 * over-approximated as "b", hence we have no way to decide if the "b[i][j]"
	 * , which is the abstraction of "a[i][j]", is independent of another
	 * expression "b[x][i][j]".
	 * 
	 * Therefore, if the dimension of the base array of the subscript expression
	 * is NOT equal to the number of indices in a subscript expression, this
	 * method has to give up.
	 * </p>
	 * 
	 * @param w
	 *            array subscript in write set
	 * @param r
	 *            array subscript in read set
	 * @param dims
	 *            base array dimension
	 * @param threadVars
	 *            the set of thread variables
	 * @param fullWrites
	 *            the full write set
	 * @return true iff a thread write "w" is independent with another read "r"
	 */
	private boolean checkSubscriptIndependent(RWSetElement w, RWSetElement r,
			int dims, Set<Variable> threadVars, Set<RWSetElement> fullWrites) {
		ExpressionNode wIdx[] = getSubscriptIndices(w.arraySubscript);
		ExpressionNode rIdx[] = getSubscriptIndices(r.arraySubscript);
		boolean independent = true;
		ExpressionNode unimplExpr = null;

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
		// check equality of indices:
		for (int i = 0; i < dims; i++) {
			independent &= ExpressionEvaluator.checkEqualityWithConditions(
					wIdx[i], rIdx[i], boundingConditions);
			if (!independent)
				return false;
		}
		// check is a math-function over threadVars:
		Set<Variable> threadVarSubset = new HashSet<>();

		for (int i = 0; i < dims; i++) {
			Set<Variable> tmp = isFunctionOverThreadVar(wIdx[i], fullWrites,
					threadVars);

			independent &= (tmp != null);
			if (!independent)
				return false;
			threadVarSubset.addAll(tmp);
		}
		// check injective:
		for (int i = 0; i < dims; i++) {
			independent &= ExpressionEvaluator.checkInjective(wIdx[i],
					threadVarSubset);
			if (!independent)
				return false;
		}
		return independent;
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
	 * <p>
	 * this method will check that:
	 * <code>readSet(expr) intersect full-writeSet = empty-set</code>
	 * </p>
	 * 
	 * @return the subset of the thread variables that the given expression
	 *         depends on if the expression is a math-function over the set of
	 *         thread variables. Otherwise, null;
	 */
	private Set<Variable> isFunctionOverThreadVar(ExpressionNode expr,
			Set<RWSetElement> fullWrites, Set<Variable> threadVars) {
		RWSet exprRWSet;
		try {
			exprRWSet = analyzer.collectRWFromStmtDeclExpr(currentFunction,
					expr, new HashSet<>());
		} catch (SimpleFullSetException e) {
			return fullWrites.isEmpty() ? new HashSet<>() : null;
		}
		assert exprRWSet.writes.isEmpty();
		// the intersection of "exprRWSet.reads" and "fullWrites" must be a
		// subset of "threadVar". If so, update "threadVar" to the subset:
		List<RWSetElement> intersect = exprRWSet.reads.parallelStream()
				.filter(referToSameObject(fullWrites))
				.collect(Collectors.toList());
		Set<Variable> subset = new HashSet<>();

		for (RWSetElement e : intersect)
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
