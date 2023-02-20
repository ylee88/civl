package dev.civl.abc.front.common.parse;

import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;

public interface OmpPragmaParser {
	CommonTree parse(Source source, TokenStream tokens) throws SyntaxException;
}
