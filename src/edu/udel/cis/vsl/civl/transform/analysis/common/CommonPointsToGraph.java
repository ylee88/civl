package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.UnaryOperator;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicConstant;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression.SymbolicOperator;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;
import edu.udel.cis.vsl.sarl.object.common.SimpleSequence;

public class CommonPointsToGraph implements PointsToGraph {

	private int typeCounter = 0;

	private static final String typeName = "tau";
	private static final String Ref = "Ref";

	// algebraic type: t | Ref(t)
	private final SymbolicType genType;

	private final SymbolicConstant refType;

	private final SymbolicConstant FULL;

	private SymbolicUniverse universe;

	private Map<Variable, SymbolicExpression> var2type;

	private Map<SymbolicExpression, Variable> type2var;

	private Map<ExpressionNode, SymbolicExpression> alloc2type;

	private Map<SymbolicExpression, ExpressionNode> type2alloc;

	private Map<SymbolicExpression, Set<SymbolicExpression>> superSetRelation;

	// invariant: depth(key) < depth(value):
	private Map<SymbolicExpression, SymbolicExpression> subMap;

	CommonPointsToGraph(SymbolicUniverse universe) {
		this.universe = universe;
		this.var2type = new HashMap<>();
		this.type2var = new HashMap<>();
		this.alloc2type = new HashMap<>();
		this.type2alloc = new HashMap<>();
		this.superSetRelation = new HashMap<>();
		this.subMap = new HashMap<>();

		genType = universe.symbolicUninterpretedType(typeName);

		SymbolicType refTypeFuncType = universe
				.functionType(Arrays.asList(genType), genType);

		refType = universe.symbolicConstant(universe.stringObject(Ref),
				refTypeFuncType);
		FULL = universe.symbolicConstant(universe.stringObject("FULL"),
				genType);
	}

	@Override
	public void complete() {
		substitute();
	}

	@Override
	public Set<Variable> mayPointsTo(Variable variable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSubsetRelation(SymbolicExpression subT,
			SymbolicExpression superT) {
		Set<SymbolicExpression> subsets = superSetRelation.get(superT);

		if (subsets == null)
			subsets = new TreeSet<>(universe.comparator());
		subsets.add(subT);
		superSetRelation.put(superT, subsets);
	}

	@Override
	public SymbolicExpression addVariable(Variable var) {
		SymbolicExpression tau = var2type.get(var);

		if (tau == null) {
			tau = this.newType();
			var2type.put(var, tau);
			type2var.put(tau, var);
		}
		return tau;
	}

	@Override
	public SymbolicExpression getPointsTo(SymbolicExpression t) {
		if (depth(t) > 1)
			return getReferred(t);
		else {
			SymbolicExpression referredTau;
			SymbolicExpression equiv = this.subMap.get(t);

			if (equiv == null) {
				referredTau = newType();
				equiv = makeRefOf(referredTau);
				addEquivRelation(t, equiv);
			} else
				referredTau = getReferred(equiv);

			return referredTau;
		}
	}

	@Override
	public SymbolicExpression addAllocation(ExpressionNode source) {
		SymbolicExpression tau = alloc2type.get(source);

		if (tau == null) {
			tau = newType();
			alloc2type.put(source, tau);
			type2alloc.put(tau, source);
		}
		return tau;
	}

	@Override
	public SymbolicExpression makePointsTo(SymbolicExpression t) {
		return this.makeRefOf(t);
	}

	@Override
	public SymbolicExpression getPointsToFull() {
		return FULL;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (Variable var : var2type.keySet())
			sb.append(printPointsToInfo(var) + "\n");
		return sb.toString();
	}

	/* ************** private methods ************** */
	private void addEquivRelation(SymbolicExpression t0,
			SymbolicExpression t1) {
		assert depth(t0) < depth(t1);
		assert !subMap.containsKey(t0);
		subMap.put(t0, t1);
	}

	private int depth(SymbolicExpression t) {
		int depth = 1;

		while (t.operator() != SymbolicOperator.SYMBOLIC_CONSTANT) {
			assert t.operator() == SymbolicOperator.APPLY;
			t = getReferred(t);
			depth++;
		}
		return depth;
	}

	// given t, return Ref(t)
	private SymbolicExpression makeRefOf(SymbolicExpression t) {
		return universe.apply(this.refType, Arrays.asList(t));
	}

	// given Ref(t), return t
	@SuppressWarnings("unchecked")
	private SymbolicExpression getReferred(SymbolicExpression t) {
		return ((SimpleSequence<? extends SymbolicExpression>) t.argument(1))
				.get(0);
	}

	// fresh new t
	private SymbolicExpression newType() {
		return universe.symbolicConstant(
				universe.stringObject(typeName + typeCounter++), genType);
	}

	private void substitute() {
		UnaryOperator<SymbolicExpression> replacer = universe
				.mapSubstituter(subMap);
		boolean changed = false;

		do {
			changed = substituteWorker(replacer);
		} while (changed);
	}

	private boolean substituteWorker(
			UnaryOperator<SymbolicExpression> replacer) {
		boolean changed = false;

		for (Variable key : var2type.keySet()) {
			SymbolicExpression oldVal = var2type.get(key);
			SymbolicExpression newVal = replacer.apply(oldVal);

			if (oldVal != newVal) {
				changed = true;
				var2type.put(key, newVal);
				type2var.put(newVal, key);
			}
		}

		for (ExpressionNode key : alloc2type.keySet()) {
			SymbolicExpression oldVal = alloc2type.get(key);
			SymbolicExpression newVal = replacer.apply(oldVal);

			if (oldVal != newVal) {
				changed = true;
				alloc2type.put(key, newVal);
				type2alloc.put(newVal, key);
			}
		}

		for (SymbolicExpression key : superSetRelation.keySet()) {
			Set<SymbolicExpression> oldVal = superSetRelation.get(key);
			Set<SymbolicExpression> newVal = new TreeSet<>(
					universe.comparator());
			boolean localChanged = false;
			SymbolicExpression newKey = replacer.apply(key);

			if (newKey != key)
				changed = localChanged = true;

			for (SymbolicExpression sub : oldVal) {
				SymbolicExpression newSub = replacer.apply(sub);

				if (newSub != sub)
					localChanged = true;
				newVal.add(newSub);
			}
			if (localChanged) {
				changed = true;
				superSetRelation.put(newKey, newVal);
			}
		}
		return changed;
	}

	private String printPointsToInfo(Variable var) {
		String result = var.getName() + " : " + var2type.get(var) + "\n";
		String prefix = "|  | ";
		SymbolicExpression type = var2type.get(var);
		if (depth(type) > 1) {
			SymbolicExpression referredType = getReferred(type);
			Set<SymbolicExpression> subsets = superSetRelation
					.get(referredType);

			if (subsets != null)
				for (SymbolicExpression subset : subsets) {
					result += prefix + subset + "\n";
				}
		}
		return result;
	}
}
