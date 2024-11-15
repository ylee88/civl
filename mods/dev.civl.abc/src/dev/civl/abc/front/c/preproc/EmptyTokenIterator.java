package dev.civl.abc.front.c.preproc;

import java.util.NoSuchElementException;

import dev.civl.abc.token.IF.CivlcToken;

/**
 * Empty iterator.
 * 
 * @author Stephen F. Siegel, University of Delaware
 * 
 */
public class EmptyTokenIterator implements TokenIterator {

	public EmptyTokenIterator() {
	}

	public CivlcToken peek() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public CivlcToken next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Should not happen");
	}

	@Override
	public int peekTypeSkipWhitespace() {
		return -1;
	}
}
