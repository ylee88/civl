package dev.civl.mc.dynamic.IF;

import java.util.List;

import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.ValueSetReference;
import dev.civl.sarl.IF.type.SymbolicType;

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

	/**
	 * <p>
	 * This method over-approximates <code>vsRef</code> if it references into
	 * sequence elements. In this case, the returned value is an ancestor that
	 * references to the outer-most sequence object that encloses what
	 * <code>vsRef</code> references to.
	 * </p>
	 * 
	 * <p>
	 * The parameter <code>variableOrMallocElementType</code> is the CIVLType of
	 * the variable where <code>vsRef</code> references, if <code>vsRef</code>
	 * does NOT reference to a heap object. Otherwise,
	 * <code>variableOrMallocElementType</code> is the element type statically
	 * determined by the corresponding malloc statement. In this case, the root
	 * ancestor of <code>vsRef</code> references to the heap object instead of
	 * the heap variable.
	 * </p>
	 * 
	 * <p>
	 * The reason of why this method analyzes <code>vsRef</code> with respect to
	 * CIVLType instead of dynamic types is that sequence type is invisible to
	 * dynamic types. The reason of why we treat variables and heap objects
	 * differently is that there is no CIVLType exisiting in the model for a
	 * heap object. There are only CIVLType of static element types extracted
	 * from malloc statement.
	 * </p>
	 */
	ValueSetReference getVSReferenceToSequenceOrNoop(
			CIVLType variableOrMallocElementType, boolean isMallocElementType,
			ValueSetReference vsRef);
}
