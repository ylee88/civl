package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSUnionMemberReference;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.object.IntObject;
import dev.civl.sarl.IF.object.NumberObject;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.object.SymbolicSequence;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSUnionMemberReference extends CommonNTValueSetReference
		implements VSUnionMemberReference {

	private IntObject memberIndex;

	public CommonVSUnionMemberReference(SymbolicType referenceType,
			SymbolicConstant function,
			SymbolicSequence<SymbolicExpression> parentIndexSequence,
			IntObject memberIndex) {
		super(referenceType, function, parentIndexSequence);
		SymbolicObject index = parentIndexSequence.get(1).argument(0);

		assert index.symbolicObjectKind() == SymbolicObjectKind.NUMBER
				&& ((IntegerNumber) ((NumberObject) index).getNumber())
						.intValue() == memberIndex.getInt();
		this.memberIndex = memberIndex;
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.UNION_MEMBER;
	}

	@Override
	public IntObject getIndex() {
		return memberIndex;
	}

	@Override
	public boolean isUnionMemberReference() {
		return true;
	}
}
