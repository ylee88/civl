package dev.civl.abc.front.IF;

import org.antlr.runtime.Token;

import dev.civl.abc.err.IF.ABCRuntimeException;

public class PreprocessorRuntimeException extends ABCRuntimeException {
	/**
	 * Eclipse made me do it.
	 */
	private static final long serialVersionUID = -5474369169753584154L;

	/**
	 * Token where error occurred. May be null. If non-null, a few words
	 * describing it are appended to the message.
	 */
	private Token token;

	public PreprocessorRuntimeException(String msg, Token token) {
		super(msg);
		this.token = token;
	}

	public PreprocessorRuntimeException(PreprocessorException e) {
		this(e.getMessage(), e.getToken());
	}

	public PreprocessorRuntimeException(String msg) {
		super(msg);
	}

	public Token getToken() {
		return token;
	}

	@Override
	public String toString() {
		String result = "Preprocessor error: " + super.getMessage();

		if (token != null)
			result += "\nat " + token;
		return result;
	}

}
