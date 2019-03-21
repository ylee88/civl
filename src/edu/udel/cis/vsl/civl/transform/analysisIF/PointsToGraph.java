package edu.udel.cis.vsl.civl.transform.analysisIF;

import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

;

public interface PointsToGraph {

	/**
	 * add a subset-relation to the graph
	 * 
	 * @param node
	 */
	void addSubsetRelation(SymbolicExpression subT, SymbolicExpression superT);

	// depth(r0) < depth(r1)
	// void addEquivRelation(SymbolicExpression t0, SymbolicExpression t1);

	// return t corresponds to var
	SymbolicExpression addVariable(Variable var);

	// return t corresponds to an allocaton call
	SymbolicExpression addAllocation(ExpressionNode source);

	// Suppose t = Ref(t'), return t', add to fact that t == Ref(t')
	SymbolicExpression getPointsTo(SymbolicExpression t);

	// given t, returns Ref(t)
	SymbolicExpression makePointsTo(SymbolicExpression t);

	SymbolicExpression getPointsToFull();

	/**
	 * ask what memory locations a pointer may point to :
	 */
	Set<Variable> mayPointsTo(Variable variable);

	void complete();
}
