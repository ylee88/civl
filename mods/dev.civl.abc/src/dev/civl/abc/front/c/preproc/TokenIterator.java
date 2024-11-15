package dev.civl.abc.front.c.preproc;

import java.util.Iterator;

import dev.civl.abc.token.IF.CivlcToken;

public interface TokenIterator extends Iterator<CivlcToken> {

	/**
	 * Peeks into the iterator and returns the type of the first non-whitespace
	 * token.
	 * 
	 * @return type of first non-whitespace token or -1 if all remaining tokens
	 *         are whitespace
	 */
	public int peekTypeSkipWhitespace();

}
