package dev.civl.abc.token.common;

import java.io.PrintStream;
import java.util.Iterator;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SourceFormatter;

public class CommonSource implements Source {

	private CivlcToken firstToken;

	private CivlcToken lastToken;

	public CommonSource(CivlcToken firstToken, CivlcToken lastToken) {
		if (firstToken == null)
			throw new IllegalArgumentException("Null firstToken");
		if (lastToken == null)
			throw new IllegalArgumentException("Null lastToken");

		int first = firstToken.getIndex();
		int last = lastToken.getIndex();

		if (last < first)
			throw new IllegalArgumentException("Last token precedes first:\n"
					+ firstToken + "\n" + lastToken);
		this.firstToken = firstToken;
		this.lastToken = lastToken;
	}

	@Override
	public CivlcToken getFirstToken() {
		return firstToken;
	}

	@Override
	public CivlcToken getLastToken() {
		return lastToken;
	}

	@Override
	public Iterator<CivlcToken> tokens() {
		return new SourceTokenIterator(firstToken, lastToken);
	}

	@Override
	public int getNumTokens() {
		return lastToken.getIndex() - firstToken.getIndex() + 1;
	}

	@Override
	public String toString() {
		return getSummary(false, false);
	}

	@Override
	public void print(PrintStream out) {
		out.print(getSummary(false, false));
	}

	@Override
	public String getLocation(boolean abbreviated) {
		if (firstToken.getIndex() < 0)
			return "";
		SourceFormatter formatter = new SourceFormatter(firstToken, lastToken);
		return formatter.getLocator(abbreviated);
		// return TokenUtils.rangeToString(firstToken, lastToken);
		/*
		 * return TokenUtils.summarizeRangeLocation(firstToken, lastToken,
		 * abbreviated);
		 */
	}

	@Override
	public String getSummary(boolean abbreviated, boolean multiline) {
		/*
		 * return TokenUtils.summarizeRange(firstToken, lastToken, abbreviated,
		 * isException);
		 */
		SourceFormatter formatter = new SourceFormatter(firstToken, lastToken);
		if (!multiline)
			return formatter.getShortString(abbreviated);
		else {
			StringBuffer buf = new StringBuffer();
			formatter.getDetailedReport(buf);
			return buf.toString();
		}
	}

	@Override
	public String getContent(boolean abbreviated) {
		SourceFormatter formatter = new SourceFormatter(firstToken, lastToken);
		return formatter.getContent();
		// return TokenUtils.rangeToString(firstToken, lastToken);
		// return TokenUtils.contentOfRange(firstToken, lastToken, abbreviated);
	}
}

class SourceTokenIterator implements Iterator<CivlcToken> {
	CivlcToken nextToken;
	CivlcToken last;

	SourceTokenIterator(CivlcToken firstToken, CivlcToken lastToken) {
		nextToken = firstToken;
		last = lastToken;
	}

	public CivlcToken next() {
		CivlcToken result = nextToken;

		if (nextToken == last)
			nextToken = null;
		else
			nextToken = nextToken.getNext();
		return result;
	}

	public boolean hasNext() {
		return nextToken != null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
