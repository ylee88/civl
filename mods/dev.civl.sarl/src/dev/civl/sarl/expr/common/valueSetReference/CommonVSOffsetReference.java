package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSOffsetReference;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSOffsetReference extends CommonNTValueSetReference
		implements VSOffsetReference {

	public CommonVSOffsetReference(SymbolicType referenceType,
			SymbolicConstant function,
			SymbolicSequence<SymbolicExpression> parentIndexSequence) {
		super(referenceType, function, parentIndexSequence);
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.OFFSET;
	}

	@Override
	public NumericExpression getOffset() {
		return this.getIndexExpression();
	}

	@Override
	public boolean isOffsetReference() {
		return true;
	}
}
