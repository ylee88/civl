package dev.civl.abc.token.IF;

import java.io.File;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

/**
 * Utility class providing static methods dealing with Token objects.
 * 
 * @author siegel
 * 
 */
public class TokenUtils {

	public final static Token eofToken = new CommonToken(Token.EOF);

	/**
	 * Computes a short version of the file name from a token's source file.
	 * 
	 * @param token
	 *                        a token
	 * @param abbreviated
	 *                        true iff the result is an abbreviated file name,
	 *                        i.e., shorter file name, which is calculated by
	 *                        the static hash map.
	 * @return the short file name
	 */
	public static String getShortFilename(Token token, boolean abbreviated) {
		if (token instanceof CivlcToken) {
			CivlcToken ppToken = (CivlcToken) token;
			SourceFile file = ppToken.getSourceFile();

			if (abbreviated)
				return file.getIndexName();
			else
				return file.getNickname();
		} else {
			CharStream stream = token.getInputStream();

			if (stream == null)
				return "<unknown file>";
			else {
				String filename = stream.getSourceName();
				int separatorIndex = filename
						.lastIndexOf(File.pathSeparatorChar);

				if (separatorIndex >= 0
						&& separatorIndex < filename.length() - 1)
					filename = filename.substring(separatorIndex + 1);
				return filename;
			}
		}
	}

	public static TokenSource makeTokenSourceFromList(CivlcToken first) {
		return new ListTokenSource(first);
	}

}

/**
 * A simple TokenSource formed from a linked list of PreprocessorTokens, given
 * the first element in the list. The token source appends an infinite number of
 * invalid tokens???? after the last token in the list.
 * 
 * @author siegel
 * 
 */
class ListTokenSource implements TokenSource {

	private CivlcToken current;

	ListTokenSource(CivlcToken first) {
		this.current = first;
	}

	@Override
	public Token nextToken() {
		Token result = current;

		if (result == null)
			result = TokenUtils.eofToken;

		else
			current = current.getNext();
		return result;
	}

	@Override
	public String getSourceName() {
		if (current == null)
			return "unknown";

		CharStream stream = current.getInputStream();

		if (stream == null)
			return "unknown";

		String name = stream.getSourceName();

		if (name == null)
			return "unknown";

		return name;
	}

}
