package dev.civl.abc.front.c.preproc;

import dev.civl.abc.token.IF.CivlcToken;

public class ChainedTokenIterator implements TokenIterator {

	private TokenIterator iter1;

	private TokenIterator iter2;

	public ChainedTokenIterator(TokenIterator iter1, TokenIterator iter2) {
		this.iter1 = iter1;
		this.iter2 = iter2;
	}

	@Override
	public boolean hasNext() {
		return iter1.hasNext() || iter2.hasNext();
	}

	@Override
	public CivlcToken next() {
		if (iter1.hasNext())
			return iter1.next();
		return iter2.next();
	}

	@Override
	public int peekTypeSkipWhitespace() {
		int result = iter1.peekTypeSkipWhitespace();
		return result == -1 ? iter2.peekTypeSkipWhitespace() : result;
	}

}
