package dev.civl.abc.front.IF;

import org.antlr.runtime.Token;

import dev.civl.abc.err.IF.ABCRuntimeException;

public class RuntimeParseException extends ABCRuntimeException {

	/**
	 * Eclipse made me do it.
	 */
	private static final long serialVersionUID = -2754044970326749601L;

	private Token token;

	public RuntimeParseException(String msg, Token token) {
		super(token == null ? msg : msg + "\nAt " + token);
		this.token = token;
	}

	public RuntimeParseException(String msg) {
		super(msg);
	}

	public Token getToken() {
		return token;
	}
}
