package dev.civl.sarl.expr.common;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.TupleComponentReference;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.object.NumberObject;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * Implementation of a non-trivial reference to a TupleComponent
 */
public class CommonTupleComponentReference extends CommonNTReference
		implements TupleComponentReference {

	private int size = -1;

	/**
	 * The fieldIndex duplicated the information in one of the arguments, but
	 * there is no obvious way to translate from a NumberObject to an IntObject
	 * so cache it here.
	 */
	private IntObject fieldIndex;

	/**
	 * Constructor asserts that parentIndexSequnce is a valid and Concrete
	 * IntegerNumber
	 * 
	 * @param referenceType
	 * @param tupleComponentReferenceFunction
	 * @param parentIndexSequence
	 * @param fieldIndex
	 */
	public CommonTupleComponentReference(SymbolicType referenceType,
			SymbolicConstant tupleComponentReferenceFunction,
			SymbolicSequence<SymbolicExpression> parentIndexSequence,
			IntObject fieldIndex) {
		super(referenceType, tupleComponentReferenceFunction,
				parentIndexSequence);
		assert parentIndexSequence.get(1)
				.operator() == SymbolicOperator.CONCRETE
				&& parentIndexSequence.get(1)
						.argument(0) instanceof NumberObject
				&& ((IntegerNumber) ((NumberObject) parentIndexSequence.get(1)
						.argument(0)).getNumber()).intValue() == fieldIndex
								.getInt();
		this.fieldIndex = fieldIndex;
	}

	/**
	 * @return fieldIndex
	 */
	@Override
	public IntObject getIndex() {
		return fieldIndex;
	}

	/**
	 * @return True
	 */
	@Override
	public boolean isTupleComponentReference() {
		return true;
	}

	/**
	 * @return ReferenceKind.TUPLE_COMPONENT
	 */
	@Override
	public ReferenceKind referenceKind() {
		return ReferenceKind.TUPLE_COMPONENT;
	}

	@Override
	public int size() {
		if (size < 0)
			// this node (size of 1) + index (size of 1) + size of parent:
			size = 2 + getParent().size();
		return size;
	}
}
