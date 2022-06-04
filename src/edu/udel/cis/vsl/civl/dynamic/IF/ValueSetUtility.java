package edu.udel.cis.vsl.civl.dynamic.IF;

import java.util.List;

import edu.udel.cis.vsl.sarl.IF.expr.BooleanExpression;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;
import edu.udel.cis.vsl.sarl.IF.expr.valueSetReference.ValueSetReference;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;

public interface ValueSetUtility {

	/**
	 * Extends the {@link ValueSetReference} <code>vsRef</code> to an equivalent
	 * set of references to primitives
	 * 
	 * @param vsRef
	 *            a {@link ValueSetReference}
	 * @param varType
	 *            the type of variables where <code>vsRef</code> applies
	 * @return an set of references to primitives equivalent to the given
	 *         <code>vsRef</code>
	 */
	List<ValueSetReference> extendToFull(ValueSetReference vsRef,
			SymbolicType varType);

	/**
	 * 
	 * Divides {@link ValueSetReference}s in a value set template to a number of
	 * groups. Every pair of groups are referring to disjoint objects in a
	 * variable.
	 */
	Iterable<List<ValueSetReference>> toDisjointGroups(
			SymbolicExpression valueSetTemplate);

	/**
	 * Builds an assertion stating that all the objects in a variable of
	 * <code>varType</code> NOT referred by <code>valueSetTemplate</code>
	 * preserve their old values. (i.e., their <code>newVal</code> equals to
	 * their <code>oldVal</code>).
	 * 
	 * @param varType
	 *            the type of the variable associated to
	 *            <code>valueSetTemplate</code>
	 * @param oldVal
	 *            old value of the variable
	 * @param newVal
	 *            new value of the variable
	 * @param valueSetTemplate
	 *            a value set template associated to <code>varType</code>
	 * @return an assertion as described above.
	 */
	BooleanExpression buildFrameCondition(SymbolicType varType,
			SymbolicExpression oldVal, SymbolicExpression newVal,
			SymbolicExpression valueSetTemplate);
}
