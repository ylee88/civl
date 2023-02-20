package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSTupleComponentReference;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.object.NumberObject;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSTupleComponentReference extends CommonNTValueSetReference
		implements VSTupleComponentReference {

	private IntObject fieldIndex;

	public CommonVSTupleComponentReference(SymbolicType referenceType,
			SymbolicConstant function,
			SymbolicSequence<SymbolicExpression> parentIndexSequence,
			IntObject fieldIndex) {
		super(referenceType, function, parentIndexSequence);
		SymbolicObject index = parentIndexSequence.get(1).argument(0);

		assert index.symbolicObjectKind() == SymbolicObjectKind.NUMBER
				&& ((IntegerNumber) ((NumberObject) index).getNumber())
						.intValue() == fieldIndex.getInt();
		this.fieldIndex = fieldIndex;
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.TUPLE_COMPONENT;
	}

	@Override
	public boolean isTupleComponentReference() {
		return true;
	}

	@Override
	public IntObject getIndex() {
		return fieldIndex;
	}
}
