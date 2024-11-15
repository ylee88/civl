package dev.civl.abc.front.c.preproc;

import java.util.NoSuchElementException;

import dev.civl.abc.token.IF.CivlcToken;

/**
 * A simple iterator over CTokens that works by following the "next" fields in
 * the tokens.
 * 
 * @author Stephen F. Siegel, University of Delaware
 * 
 */
public class ListTokenIterator implements TokenIterator {

	private CivlcToken theNextToken;

	public ListTokenIterator(CivlcToken firstToken) {
		theNextToken = firstToken;
	}

	public CivlcToken peek() {
		return theNextToken;
	}

	@Override
	public boolean hasNext() {
		return theNextToken != null;
	}

	@Override
	public CivlcToken next() {
		if (theNextToken == null)
			throw new NoSuchElementException();
		CivlcToken result = theNextToken;
		theNextToken = theNextToken.getNext();
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Should not happen");
	}

	@Override
	public int peekTypeSkipWhitespace() {
		for (CivlcToken t = theNextToken; t != null; t = t.getNext()) {
			if (!PreprocessorUtils.isWhiteSpace(t))
				return t.getType();
		}
		return -1;
	}
}
