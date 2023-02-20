package dev.civl.sarl.expr.common.valueSetReference;

import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.expr.valueSetReference.VSIdentityReference;
import dev.civl.sarl.IF.type.SymbolicType;

public class CommonVSIdentityReference extends CommonValueSetReference
		implements VSIdentityReference {

	public CommonVSIdentityReference(SymbolicType type,
			SymbolicExpression... args) {
		super(type, args);
	}

	@Override
	public VSReferenceKind valueSetReferenceKind() {
		return VSReferenceKind.IDENTITY;
	}

	@Override
	public boolean isIdentityReference() {
		return true;
	}
}
