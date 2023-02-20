package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSArrayElementReference;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSArrayElementReference extends CommonNTValueSetReference
		implements VSArrayElementReference {

	/**
	 * Constructor that builds a CommonVSArrayElementReference.
	 * 
	 * @param referenceType
	 * @param arrayElementReferenceFunction
	 * @param parentIndexSequence
	 * 
	 * @return CommonArrayElementReference
	 */
	public CommonVSArrayElementReference(SymbolicType referenceType,
			SymbolicConstant arrayElementReferenceFunction,
			SymbolicSequence<SymbolicExpression> parentIndexSequence) {
		super(referenceType, arrayElementReferenceFunction,
				parentIndexSequence);
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.ARRAY_ELEMENT;
	}

	@Override
	public NumericExpression getIndex() {
		return getIndexExpression();
	}

	@Override
	public boolean isArrayElementReference() {
		return true;
	}
}
