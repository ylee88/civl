package dev.civl.abc.front.IF;

import org.antlr.runtime.Token;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.token.IF.TokenUtils;

public class PreprocessorException extends ABCException {

	/**
	 * Eclipse made me do it.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Token where error occurred. May be null. If non-null, a few words
	 * describing it are appended to the message.
	 */
	private Token token;

	public PreprocessorException(String msg, Token token) {
		super(msg);
		this.token = token;
	}

	public PreprocessorException(String msg) {
		super(msg);
	}

	public Token getToken() {
		return token;
	}

	@Override
	public String toString() {
		String result = "Preprocessor error: " + super.getMessage();

		if (token != null)
			result += "\nat " + TokenUtils.location(token, false) + ": "
					+ TokenUtils.quotedText(token);
		return result;
	}
}
