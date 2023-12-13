package dev.civl.mc.model.common;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.type.CIVLType;

public class CommonNondetFunction extends CommonFunction
		implements
			CIVLFunction {

	public CommonNondetFunction(CIVLSource source, Identifier name,
			CIVLType returnType, Scope containingScope, int fid) {
		super(source, false, name, containingScope, null, returnType,
				containingScope, fid, null);
	}

	@Override
	public boolean isNondet() {
		return true;
	}

	@Override
	public boolean isNormalFunction() {
		return false;
	}
}
