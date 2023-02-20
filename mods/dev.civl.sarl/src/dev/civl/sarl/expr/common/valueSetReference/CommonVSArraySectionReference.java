package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSArraySectionReference;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSArraySectionReference extends CommonNTValueSetReference
		implements VSArraySectionReference {

	/**
	 * Constructor builds an instance of {@link VSArraySectionReference}
	 * 
	 * @param referenceType
	 * @param function
	 * @param parentLoHiStepSequence
	 */
	public CommonVSArraySectionReference(SymbolicType referenceType,
			SymbolicConstant function,
			SymbolicSequence<SymbolicExpression> parentLoHiStepSequence) {
		super(referenceType, function, parentLoHiStepSequence);
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.ARRAY_SECTION;
	}

	@Override
	public NumericExpression lowerBound() {
		return this.getIndexExpression();
	}

	@Override
	public NumericExpression upperBound() {
		return (NumericExpression) ((SymbolicSequence<?>) this.argument(1))
				.get(2);
	}

	@Override
	public NumericExpression step() {
		return (NumericExpression) ((SymbolicSequence<?>) this.argument(1))
				.get(3);
	}

	@Override
	public boolean isArraySectionReference() {
		return true;
	}
}
