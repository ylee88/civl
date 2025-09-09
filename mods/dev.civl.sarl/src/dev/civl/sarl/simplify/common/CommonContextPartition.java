package dev.civl.sarl.simplify.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.simplify.IF.ContextPartition;

/**
 * Implementation of {@link ContextPartition}.
 * 
 * @author Stephen F. Siegel
 *
 */
public class CommonContextPartition implements ContextPartition {

	public final static boolean debug = false;

	/**
	 * The equivalence classes. Each class represents the conjunction of a set
	 * of clauses. Any two distinct classes represent mutually disjoint sets of
	 * clauses. It is possible for there to be 0 classes: this happens iff the
	 * context is "true".
	 */
	private List<List<BooleanExpression>> stackClasses;

	private int stackSize;

	/**
	 * Maps each symbolic constant X to the index of the equivalence class to
	 * which it belongs (i.e., the index in the array <code>classes</code>), or
	 * <code>null</code> if X does not occur in the context (path condition). It
	 * is possible for this map to be empty, this happens iff no symbolic
	 * constants occur in the context.
	 */
	private Map<SymbolicConstant, Integer> partitionMap = new HashMap<>();

	/**
	 * Cached results of {@link #minimizeFor(SymbolicExpression, PreUniverse)}.
	 */
	private Map<SymbolicExpression, List<BooleanExpression>> minimalContextMap = new HashMap<>();

	/**
	 * A class to use for temporary storage of data while the partition of the
	 * set of clauses is being computed. An instance represents a changing set
	 * of clauses that will eventually form an equivalence class.
	 * 
	 * @author siegel
	 *
	 */
	class Partition {
		/**
		 * The set of variables which belong to this partition, i.e., the
		 * variables v such v occurs in at least one of the clauses associated
		 * to this.
		 */
		Set<SymbolicConstant> vars = new HashSet<>();

		/**
		 * The indexes of the clauses that comprise this partition. The clauses
		 * will be numbered from 0.
		 */
		List<BitSet> clausesStack;

		/**
		 * When the algorithm completes, the final set of partitions will form
		 * the equivalence classes, and they will be numbered from 0.
		 */
		int id = -1;

		/**
		 * Forms a new empty partition which is optimized to deal with the given
		 * number of clauses.
		 * 
		 * @param numClauses
		 *            the number of clauses this partition will deal with
		 */
		public Partition(List<List<BooleanExpression>> clausesStack) {
			this.clausesStack = new ArrayList<>(clausesStack.size());
			for (List<BooleanExpression> subClauses : clausesStack) {
				this.clausesStack.add(new BitSet(subClauses.size()));
			}
		}
	}

	/**
	 * Constructs a new context partition by analyzing the given
	 * <code>context</code>, partitioning its set of conjunctive clauses into
	 * mutually disjoint equivalence classes, and storing the resulting
	 * information for later use in variables <code>classes</code> and
	 * <code>partitionMap</code>.
	 * 
	 * @param contextStack
	 *            a non-<code>null</code> boolean expression (typically the path
	 *            condition)
	 */
	public CommonContextPartition(List<BooleanExpression> contextStack,
			PreUniverse universe) {
		stackSize = contextStack.size();
		assert stackSize > 0;

		Map<SymbolicConstant, Partition> pMap = new HashMap<>();
		int numClasses = 0;
		List<List<BooleanExpression>> clausesStack = new ArrayList<>(stackSize);
		for (BooleanExpression subContext : contextStack) {
			clausesStack.add(Arrays.asList(subContext.getClauses()));
		}

		for (int i = 0; i < stackSize; i++) {
			List<BooleanExpression> subClauses = clausesStack.get(i);
			int numClauses = subClauses.size();

			for (int j = 0; j < numClauses; j++) {
				BooleanExpression clause = subClauses.get(j);
				// the partition containing this clause:
				Partition partition = null;
				Collection<SymbolicConstant> vars = universe
						.getFreeSymbolicConstants(clause);

				/*
				 * Loop invariant: partition == null or parition.clauses
				 * contains i.
				 * 
				 * For all symbolic constants v, Partitions p: v is contained in
				 * p.vars iff pMap.get(v)==p.
				 * 
				 * partition starts out null, but is set to something not null
				 * in the first iteration, i.e., in processing the first
				 * symbolic constant to occur in the clause
				 */
				for (SymbolicConstant var : vars) {
					Partition oldPartition = pMap.get(var);

					if (oldPartition == null) {
						// first time we've encountered var
						// put var in the current partition
						if (partition == null) {
							// current clause not in any partition yet
							partition = new Partition(clausesStack);
							numClasses++;
							partition.clausesStack.get(i).set(j);
						}
						partition.vars.add(var);
						pMap.put(var, partition);
					} else {
						assert oldPartition.vars.contains(var);
						if (partition == null) {
							// current clause not in any partition yet
							partition = oldPartition;
							partition.clausesStack.get(i).set(j);
						} else if (partition != oldPartition) {
							// merge partition and oldPartition:
							for (SymbolicConstant oldVar : oldPartition.vars)
								pMap.put(oldVar, partition);
							partition.vars.addAll(oldPartition.vars);
							for (int k = 0; k < stackSize; k++)
								partition.clausesStack.get(k)
										.or(oldPartition.clausesStack.get(k));
							numClasses--;
							// oldPartition can now get swept up by garb. col.
						}
					}
				}
			}
		}

		this.stackClasses = new ArrayList<>(numClasses);
		int classId = 0;

		for (Entry<SymbolicConstant, Partition> entry : pMap.entrySet()) {
			SymbolicConstant var = entry.getKey();
			Partition partition = entry.getValue();

			if (partition.id < 0) {
				List<BooleanExpression> newStackClass = new ArrayList<>(
						stackSize);

				for (int i = 0; i < stackSize; i++) {
					List<BooleanExpression> originalSubClauses = clausesStack
							.get(i);
					BooleanExpression newSubContext = universe.trueExpression();
					BitSet bitSet = partition.clausesStack.get(i);

					for (int j = bitSet.nextSetBit(0); j >= 0; j = bitSet
							.nextSetBit(j + 1)) {
						newSubContext = universe.and(newSubContext,
								originalSubClauses.get(j));
					}
					newStackClass.add(newSubContext);
				}

				stackClasses.add(newStackClass);
				partition.id = classId;
				classId++;
			}
			this.partitionMap.put(var, partition.id);
		}
		if (debug) {
			System.out.println(this);
			System.out.println();
		}
	}

	/**
	 * Given a boolean expression <code>expr</code> returns the boolean
	 * expression <code>subpc(pc, expr)</code> which is a weakening of the
	 * <code>pc</code> used to from this context partitioner and is sufficient
	 * for determining the validity of <code>expr</code>
	 * 
	 * @param expr
	 *            any non-<code>null</code> boolean expression
	 * @param universe
	 *            universe use to perform "and" operations on boolean
	 *            expressions
	 * @return <code>subpc(pc, expr)</code>: the conjunction of the clauses in
	 *         the classes corresponding to the symbolic constants occurring in
	 *         <code>expr</code>
	 */
	@Override
	public List<BooleanExpression> minimizeFor(SymbolicExpression expr,
			PreUniverse universe) {
		List<BooleanExpression> result = minimalContextMap.get(expr);

		if (result == null) {
			Set<SymbolicConstant> vars = universe
					.getFreeSymbolicConstants(expr);

			Set<Integer> resultClasses = new HashSet<>();

			for (SymbolicConstant var : vars) {
				Integer classId = partitionMap.get(var);

				if (classId != null)
					resultClasses.add(classId);
			}

			result = new ArrayList<>(stackSize);
			for (int i = 0; i < stackSize; i++) {
				result.add(universe.trueExpression());
			}

			for (int classId : resultClasses) {
				List<BooleanExpression> stackClass = stackClasses.get(classId);
				for (int i = 0; i < stackSize; i++) {
					result.set(i,
							universe.and(result.get(i), stackClass.get(i)));
				}
			}
			if (debug) {
				System.out.println("Context minimization: ");
				System.out.print(this);
				System.out.println("Expression: " + expr);
				System.out.println("Minimized context: " + result);
				System.out.println();
			}
			minimalContextMap.put(expr, result);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (int i = stackSize - 1; i >= 0; i++) {
			buf.append("Partition Stack Entry " + i + ":\n");
			for (int j = 0; j < stackClasses.get(j).size(); j++) {
				BooleanExpression classSubContext = stackClasses.get(j).get(i);
				buf.append("Class " + j + ": ");
				buf.append(classSubContext);
				buf.append("\n");
			}
		}
		return buf.toString();
	}

}
