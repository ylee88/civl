package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.civl.transform.analysis.common.PointsToGraphComponentFactory.PointsToConstraint;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignmentKind;
import edu.udel.cis.vsl.civl.transform.analysisIF.InsensitiveFlow;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

/**
 * <p>
 * Simple points-to analysis graph based on the naive approach introduced as a
 * background in "The Ant and the Grasshopper: Fast and Accurate Pointer
 * Analysis for Millions of Lines of Code" paper.
 * </p>
 * 
 * 
 * 
 * @author ziqing
 *
 */
public class CommonPointsToGraph implements PointsToGraph {

	/**
	 * A set of FULL entities to their associated nodes
	 */
	private Map<Entity, SymbolicExpression> entityToNode;

	/**
	 * A set of FULL nodes in graph to the {@link AssignExprIF}s, from which
	 * nodes are created.
	 */
	private Map<SymbolicExpression, AssignExprIF> nodeToAssignExpr;

	/**
	 * The inverse map of {@link #node2assignExpr}
	 */
	private Map<Integer, SymbolicExpression> assignExprToNode;

	/**
	 * The points-to function that maps a node "n" to the nodes that "n" points
	 * to:
	 */
	private Map<SymbolicExpression, Set<SymbolicExpression>> pointsTo;

	/**
	 * all subset-of relations
	 */
	private Set<SymbolicExpression> allEdges;

	/**
	 * map for looking up subset-of relations by subsets:
	 */
	private Map<SymbolicExpression, List<SymbolicExpression>> subsetToEdge;

	/**
	 * a reference to the class providing nodes, edges and constraints:
	 */
	private PointsToGraphComponentFactory componentsFactory;

	/**
	 * a reference to {@link SymbolicUniverse}
	 */
	private SymbolicUniverse universe;

	/**
	 * the program fragment associated with this graph
	 */
	private InsensitiveFlow programAbstraction;

	/**
	 * true iff a re-computation is need before answering {@link #mayPointsTo}
	 * queries
	 */
	private boolean dirty = false;

	CommonPointsToGraph(InsensitiveFlow programAbstraction,
			SymbolicUniverse universe) {
		this.universe = universe;
		this.componentsFactory = new PointsToGraphComponentFactory(universe);
		this.entityToNode = new HashMap<>();
		this.nodeToAssignExpr = new HashMap<>();
		this.assignExprToNode = new HashMap<>();
		this.pointsTo = new HashMap<>();
		this.allEdges = new HashSet<>();
		this.subsetToEdge = new HashMap<>();
		this.programAbstraction = programAbstraction;
		build(programAbstraction);
	}

	@Override
	public PointsToGraph clone() {
		CommonPointsToGraph clone = new CommonPointsToGraph(
				this.programAbstraction, universe);

		clone.entityToNode = new HashMap<>(entityToNode);
		clone.nodeToAssignExpr = new HashMap<>(nodeToAssignExpr);
		clone.assignExprToNode = new HashMap<>(assignExprToNode);
		clone.pointsTo = new HashMap<>();
		// deep copy:
		for (Entry<SymbolicExpression, Set<SymbolicExpression>> entry : pointsTo
				.entrySet()) {
			Set<SymbolicExpression> clonedPts = new TreeSet<>(
					universe.comparator());

			clonedPts.addAll(entry.getValue());
			clone.pointsTo.put(entry.getKey(), clonedPts);
		}

		clone.allEdges = new HashSet<>(allEdges);
		clone.subsetToEdge = new HashMap<>();
		// deep copy:
		for (Entry<SymbolicExpression, List<SymbolicExpression>> entry : subsetToEdge
				.entrySet())
			clone.subsetToEdge.put(entry.getKey(),
					new LinkedList<>(entry.getValue()));

		clone.dirty = this.dirty;
		return clone;
	}

	@Override
	public Iterable<AssignExprIF> mayPointsTo(Entity entity) {
		SymbolicExpression node = getNodeByEntity(entity);

		return mayPointsTo(nodeToAssignExpr.get(node), false);
	}

	@Override
	public Iterable<AssignExprIF> mayPointsTo(AssignExprIF expr) {
		return mayPointsTo(expr, false);
	}

	@Override
	public boolean addPointsTo(AssignExprIF object,
			Iterable<AssignExprIF> pointsTo) {
		SymbolicExpression node = getNodeByAssignExpr(object);
		Set<SymbolicExpression> pts = this.pointsTo.get(node);
		boolean changed = false;

		if (pts == null)
			pts = new HashSet<>();
		for (AssignExprIF ptsAbs : pointsTo) {
			SymbolicExpression ptNode = getNodeByAssignExpr(ptsAbs);

			changed |= pts.add(ptNode);
		}
		this.pointsTo.put(node, pts);
		dirty |= changed;
		return changed;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (AssignExprIF abs : nodeToAssignExpr.values()) {
			StringBuffer varSb = new StringBuffer();
			boolean hasFull = false;

			sb.append(abs + " points-to:\n");
			for (AssignExprIF pt : mayPointsTo(abs, false)) {
				varSb.append("|| " + pt.toString() + "\n");
				if (pt.isFull()) {
					hasFull = true;
					break;
				}
			}
			if (hasFull)
				sb.append("|| FULL\n");
			else
				sb.append(varSb.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Returns the points to set of
	 * <code>dereference ? deref(exprAbs) : exprAbs</code>
	 * 
	 * @param exprAbs
	 * @param dereference
	 * @return
	 */
	private Iterable<AssignExprIF> mayPointsTo(AssignExprIF exprAbs,
			boolean dereference) {
		if (dirty) {
			build(programAbstraction);
			dirty = false;
		}

		SymbolicExpression node = assignExprToNode.get(exprAbs.id());

		if (node == null)
			return new LinkedList<>();

		Set<SymbolicExpression> ptNodes = pointsTo.get(node);

		if (ptNodes == null)
			return new LinkedList<>();

		if (dereference) {
			Set<SymbolicExpression> ptNodesPts = new TreeSet<>(
					universe.comparator());

			for (SymbolicExpression ptNode : ptNodes) {
				Set<SymbolicExpression> tmp = pointsTo.get(ptNode);

				if (tmp != null)
					ptNodesPts.addAll(tmp);
			}
			ptNodes = ptNodesPts;
		}

		List<AssignExprIF> results = new LinkedList<>();

		if (ptNodes != null)
			for (SymbolicExpression ptNode : ptNodes)
				results.add(nodeToAssignExpr.get(ptNode));
		return results;
	}

	/* ****************** Dynamic Transitive Closure *********************/
	private void build(Iterable<AssignmentIF> assignments) {
		Set<SymbolicExpression> workSet = new TreeSet<>(universe.comparator());
		Map<SymbolicExpression, List<PointsToConstraint>> subIndexedConstraints = new HashMap<>(),
				superIndexedConstraints = new HashMap<>();

		// initialization with BASE and SIMPLE:
		for (AssignmentIF assign : assignments) {
			if (assign.kind() == AssignmentKind.BASE)
				initializeBASE(assign, workSet);
			else if (assign.kind() == AssignmentKind.SIMPLE)
				initializeSIMPLE(assign, workSet);
			else
				initializeCOMPLEX(assign, workSet, subIndexedConstraints,
						superIndexedConstraints);
		}

		FirstRemovableSet<SymbolicExpression> workList = new FirstRemovableSet<>(
				workSet);

		transClosureBuild(workList, subIndexedConstraints,
				superIndexedConstraints);
	}

	private void initializeBASE(AssignmentIF base,
			Set<SymbolicExpression> workSet) {
		// a = &b -> {b} sub-of a -> a points-to b
		SymbolicExpression lhsNode = getNodeByAssignExpr(base.lhs());
		SymbolicExpression rhsNode = getNodeByAssignExpr(base.rhs());
		Set<SymbolicExpression> ptSet = this.pointsTo.get(lhsNode);

		if (ptSet == null)
			ptSet = new TreeSet<>(universe.comparator());
		ptSet.add(rhsNode);
		this.pointsTo.put(lhsNode, ptSet);
		workSet.add(lhsNode);
		workSet.add(rhsNode);
	}

	private void initializeSIMPLE(AssignmentIF simple,
			Set<SymbolicExpression> workSet) {
		// a = b -> b subset-of a
		SymbolicExpression lhsNode = getNodeByAssignExpr(simple.lhs());
		SymbolicExpression rhsNode = getNodeByAssignExpr(simple.rhs());
		SymbolicExpression edge = componentsFactory.edge(rhsNode, lhsNode);

		saveEdge(edge);
		workSet.add(lhsNode);
		workSet.add(rhsNode);
	}

	private void initializeCOMPLEX(AssignmentIF complex,
			Set<SymbolicExpression> workSet,
			Map<SymbolicExpression, List<PointsToConstraint>> subIndexedConstraints,
			Map<SymbolicExpression, List<PointsToConstraint>> superIndexedConstraints) {
		SymbolicExpression lhsNode = getNodeByAssignExpr(complex.lhs());
		SymbolicExpression rhsNode = getNodeByAssignExpr(complex.rhs());

		workSet.add(lhsNode);
		workSet.add(rhsNode);

		PointsToConstraint constraint;

		if (complex.kind() == AssignmentKind.COMPLEX_LD) {
			// *a = b -> b subset-of *a
			constraint = componentsFactory.newConstraint(true, rhsNode,
					lhsNode);
			updateEdgesWithConstraints(constraint);
		} else {
			// a = *b -> *b subset-of a:
			constraint = componentsFactory.newConstraint(false, rhsNode,
					lhsNode);
			updateEdgesWithConstraints(constraint);
		}

		List<PointsToConstraint> tmp = subIndexedConstraints
				.get(constraint.subset());

		if (tmp == null)
			tmp = new LinkedList<>();
		tmp.add(constraint);
		subIndexedConstraints.put(constraint.subset(), tmp);
		tmp = superIndexedConstraints.get(constraint.superset());
		if (tmp == null)
			tmp = new LinkedList<>();
		tmp.add(constraint);
		superIndexedConstraints.put(constraint.superset(), tmp);
	}

	/**
	 * algorithm from "The Ant and the Grasshopper: Fast and Accurate Pointer
	 * Analysis for Millions of Lines of Code", Figure 1
	 * 
	 * @param workSet
	 */
	private void transClosureBuild(
			FirstRemovableSet<SymbolicExpression> workSet,
			Map<SymbolicExpression, List<PointsToConstraint>> subIndexedConstraints,
			Map<SymbolicExpression, List<PointsToConstraint>> superIndexedConstraints) {
		while (!workSet.isEmpty()) {
			SymbolicExpression node = workSet.removeFirst();
			Set<SymbolicExpression> pts = pointsTo.get(node);

			if (pts != null)
				for (SymbolicExpression pt : pts) {
					// process constraints for complex_rd:
					Iterable<PointsToConstraint> constraints = subIndexedConstraints
							.get(node);

					if (constraints != null)
						for (PointsToConstraint constraint : constraints) {
							// for every *b subset-of a
							if (constraint.isSuperDeref())
								continue;

							SymbolicExpression ptSubsetofSuper = componentsFactory
									.edge(pt, constraint.superset());

							if (!allEdges.contains(ptSubsetofSuper)) {
								workSet.add(pt);
								saveEdge(ptSubsetofSuper);
							}
						}
					// process constraints for complex_ld:
					constraints = superIndexedConstraints.get(node);
					if (constraints != null)
						for (PointsToConstraint constraint : constraints) {
							// for every b subset-of *a
							if (!constraint.isSuperDeref())
								continue;

							SymbolicExpression subsetofPt = componentsFactory
									.edge(constraint.subset(), pt);

							if (!allEdges.contains(subsetofPt)) {
								workSet.add(constraint.subset());
								this.saveEdge(subsetofPt);
							}
						}
				}

			// for every superset of "pt", add what "pt" points-to to the
			// superset:
			List<SymbolicExpression> edges = subsetToEdge.get(node);

			if (edges != null) {
				SymbolicExpression[] currEdges = new SymbolicExpression[edges
						.size()];

				// the following loop may modify "edges" hence use another array
				// for iteration:
				edges.toArray(currEdges);
				for (SymbolicExpression edge : currEdges) {
					SymbolicExpression superNode = componentsFactory
							.getSuperset(edge);
					Set<SymbolicExpression> superPts = pointsTo.get(superNode);
					Set<SymbolicExpression> nodePts = pointsTo.get(node);

					if (superPts == null)
						superPts = new TreeSet<>(universe.comparator());
					if (nodePts != null)
						if (superPts.addAll(nodePts))
							workSet.add(superNode);
					pointsTo.put(superNode, superPts);
					// update edges with constraints that involve the
					// "superNode" since its points-to function is updated:
					List<PointsToConstraint> tmp = subIndexedConstraints
							.get(superNode);

					if (tmp != null)
						for (PointsToConstraint cons : tmp)
							if (!cons.isSuperDeref())
								// only for *superNode subset-of a:
								updateEdgesWithConstraints(cons);
					tmp = superIndexedConstraints.get(superNode);
					if (tmp != null)
						for (PointsToConstraint cons : tmp)
							if (cons.isSuperDeref())
								// only for a subset-of *superNode:
								updateEdgesWithConstraints(cons);
				}
			}
		}
	}

	/**
	 * <p>
	 * As described in the paper that new edges will be added when points-to
	 * sets, which are associated with constraints, are updated: "As we update
	 * the points-to sets, we must also add new edges to represent the complex
	 * constraints. "
	 * 
	 * </p>
	 * 
	 * @param constraint
	 */
	private void updateEdgesWithConstraints(PointsToConstraint constraint) {
		if (constraint.isSuperDeref()) {
			// if b subset-of *a, add edges b -> pts(a)
			Set<SymbolicExpression> allSuperPointsTo = pointsTo
					.get(constraint.superset());

			if (allSuperPointsTo != null)
				for (SymbolicExpression superPointsTo : allSuperPointsTo) {
					SymbolicExpression edge = componentsFactory
							.edge(constraint.subset(), superPointsTo);

					saveEdge(edge);
				}
		} else {
			// if *b subset-of a, add edges pts(b) -> a
			Set<SymbolicExpression> allSubPointsTo = pointsTo
					.get(constraint.subset());

			if (allSubPointsTo != null)
				for (SymbolicExpression subPointsTo : allSubPointsTo) {
					SymbolicExpression edge = componentsFactory
							.edge(subPointsTo, constraint.superset());

					saveEdge(edge);
				}
		}
	}

	/* ************ utils *********** */
	/**
	 * 
	 * @param expr
	 *            an {@link AssignExprIF}
	 * @return the node associated with the given AssignExprIF
	 */
	private SymbolicExpression getNodeByAssignExpr(AssignExprIF expr) {
		SymbolicExpression node;

		node = assignExprToNode.get(expr.id());
		if (node != null)
			return node;
		if (expr.isFull())
			node = componentsFactory.fullNode();
		else if (expr.source() == null) {
			node = componentsFactory.newNode();
		} else
			node = getNodeByEntity(expr.source());
		this.assignExprToNode.put(expr.id(), node);
		this.nodeToAssignExpr.put(node, expr);
		return node;
	}

	/**
	 * 
	 * @param entity
	 *            an {@link Entity}
	 * @return the node associated with the entity
	 */
	private SymbolicExpression getNodeByEntity(Entity entity) {
		SymbolicExpression node = this.entityToNode.get(entity);

		if (node == null) {
			node = this.componentsFactory.newNode();
			this.entityToNode.put(entity, node);
		}
		return node;
	}

	/**
	 * Add an edge to graph
	 * 
	 * @param edge
	 */
	private void saveEdge(SymbolicExpression edge) {
		SymbolicExpression sub = componentsFactory.getSubset(edge);
		List<SymbolicExpression> edgesSharingSub = subsetToEdge.get(sub);

		allEdges.add(edge);
		if (edgesSharingSub == null)
			edgesSharingSub = new LinkedList<>();
		edgesSharingSub.add(edge);
		subsetToEdge.put(sub, edgesSharingSub);
	}

	private class FirstRemovableSet<T> {

		private Set<T> set;

		private LinkedList<T> list;
		FirstRemovableSet(Set<T> ts) {
			set = new HashSet<>(ts);
			list = new LinkedList<>(ts);
		}

		boolean isEmpty() {
			return list.isEmpty();
		}

		void add(T t) {
			if (set.add(t))
				list.addLast(t);
		}

		T removeFirst() {
			T t = list.removeFirst();
			boolean mustBeTrue = set.remove(t);

			assert mustBeTrue;
			return t;
		}

		@Override
		public String toString() {
			return list.toString();
		}
	}
}
