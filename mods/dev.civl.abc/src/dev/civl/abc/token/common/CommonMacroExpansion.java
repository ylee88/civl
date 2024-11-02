package dev.civl.abc.token.common;

import org.antlr.runtime.Token;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.FunctionMacro;
import dev.civl.abc.token.IF.FunctionMacro.FunctionReplacementUnit;
import dev.civl.abc.token.IF.Macro;
import dev.civl.abc.token.IF.MacroExpansion;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.abc.token.IF.SourceFormatter;

public class CommonMacroExpansion implements MacroExpansion {

	/**
	 * The start token, a token from the macro invocation. See comments in the
	 * interface MacroExpansion explaining this concept.
	 */
	private CivlcToken startToken;

	/**
	 * The macro doing the expanding. Its name should be the same as the text of
	 * the startToken.
	 */
	private Macro macro;

	/**
	 * The index of the token resulting from the expansion in the list of
	 * replacement tokens for the macro. In other words, the end token is the
	 * index-th replacement token from macro's replacement list.
	 */
	private int index;

	CommonMacroExpansion(CivlcToken startToken, Macro macro, int index) {
		this.startToken = startToken;
		this.macro = macro;
		this.index = index;
	}

	@Override
	public String suffix() {
		if (macro instanceof FunctionMacro) {
			FunctionMacro fm = (FunctionMacro) macro;
			FunctionReplacementUnit unit = fm.getReplacementUnit(index);
			if (unit.formalIndex >= 0) { // a formal parameter
				Token token = unit.token;
				String macroDefFile = macro.getFile().getName();
				int line = token.getLine();
				String text = token.getText();
				int startCol = token.getCharPositionInLine();
				int stopCol = startCol + text.length() - 1;
				return " substituted for "
						+ SourceFormatter.locator(macroDefFile, line, startCol,
								stopCol)
						+ " " + SourceFormatter.quoteSource(text);
			}
		}
		return " expanded from " + startToken.toString();
	}

	@Override
	public String toString() {
		return "MacroExpansion[" + startToken.getText() + ", " + macro.getName()
				+ ", " + index + "]";
	}

	@Override
	public CivlcToken getStartToken() {
		return startToken;
	}

	@Override
	public Macro getMacro() {
		return macro;
	}

	@Override
	public int getReplacementTokenIndex() {
		return index;
	}

	@Override
	public SourceFile getLastFile() {
		if (macro instanceof FunctionMacro) {
			FunctionReplacementUnit unit = ((FunctionMacro) macro)
					.getReplacementUnit(index);
			if (unit.formalIndex >= 0) // a formal parameter
				return startToken.getSourceFile();
		}
		return macro.getFile();
	}

	public boolean similar(Formation form) {
		if (this == form)
			return true;
		if (!(form instanceof CommonMacroExpansion))
			return false;
		CommonMacroExpansion that = (CommonMacroExpansion) form;
		if (macro == null) {
			if (that.macro != null)
				return false;
		} else if (!macro.equals(that.macro)) {
			return false;
		}
		if (macro instanceof FunctionMacro) {
			FunctionMacro fm = (FunctionMacro) macro;
			FunctionReplacementUnit unit = fm.getReplacementUnit(index);
			if (unit.formalIndex >= 0) { // a formal parameter, substitution
				// ignore startToken
				return index == that.index;
			}
		}
		// an expansion token, ignore index
		if (startToken == null) {
			if (that.startToken != null)
				return false;
		}
		return startToken.equals(that.startToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CommonMacroExpansion))
			return false;
		CommonMacroExpansion that = (CommonMacroExpansion) obj;
		return similar(that);
		/*
		 * if (startToken == null) { if (that.startToken != null) return false;
		 * } else if (!startToken.equals(that.startToken)) { return false; } if
		 * (macro == null) { if (that.macro != null) return false; } else if
		 * (!macro.equals(that.macro)) { return false; } if (index !=
		 * that.index) return false; return true;
		 */
	}

}
