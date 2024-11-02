package dev.civl.abc.token.common;

import java.util.ArrayList;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.FunctionMacro;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.abc.token.IF.Stringification;

public class CommonStringification implements Stringification {

	private FunctionMacro macro;

	private int index;

	private ArrayList<CivlcToken> argument;

	public CommonStringification(FunctionMacro macro, int index,
			ArrayList<CivlcToken> argument) {
		this.macro = macro;
		this.index = index;
		this.argument = argument;
	}

	@Override
	public String suffix() {
		return "from " + argument;
	}

	@Override
	public SourceFile getLastFile() {
		return macro.getFile();
	}

	@Override
	public FunctionMacro getMacro() {
		return macro;
	}

	@Override
	public int getReplacementTokenIndex() {
		return index;
	}

	@Override
	public int getNumArgumentTokens() {
		return argument.size();
	}

	@Override
	public CivlcToken getArgumentToken(int index) {
		return argument.get(index);
	}

	@Override
	public String toString() {
		return "Stringification[" + macro + ", " + index + ", " + argument
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CommonStringification))
			return false;
		CommonStringification that = (CommonStringification) obj;
		if (macro == null) {
			if (that.macro != null)
				return false;
		} else if (!macro.equals(that.macro))
			return false;
		if (index != that.index)
			return false;
		if (argument == null) {
			if (that.argument != null)
				return false;
		} else {
			if (!argument.equals(that.argument))
				return false;
		}
		return true;
	}

}
