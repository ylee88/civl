package dev.civl.abc.token.common;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.SourceFile;

public class BuiltinMacroExpansion implements Formation {

	private CivlcToken macroToken;

	public BuiltinMacroExpansion(CivlcToken macroToken) {
		assert macroToken != null;
		this.macroToken = macroToken;
	}

	@Override
	public String suffix() {
		return "expanded from " + macroToken;
	}

	@Override
	public SourceFile getLastFile() {
		return macroToken.getSourceFile();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BuiltinMacroExpansion))
			return false;
		BuiltinMacroExpansion that = (BuiltinMacroExpansion) obj;
		if (macroToken == null) {
			if (that.macroToken != null)
				return false;
		} else if (!macroToken.equals(that.macroToken)) {
			return false;
		}
		return true;
	}

}
