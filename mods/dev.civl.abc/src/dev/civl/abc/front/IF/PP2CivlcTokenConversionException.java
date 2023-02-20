package dev.civl.abc.front.IF;

import org.antlr.runtime.Token;

import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.token.IF.TokenUtils;

public class PP2CivlcTokenConversionException extends ABCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Token token;

	public PP2CivlcTokenConversionException(String message, Token t) {
		super(message);
		token = t;
	}

	@Override
	public String toString() {
		String result = "PP2CivlcTokenConverter error: " + super.getMessage();

		if (token != null)
			result += "\nat " + TokenUtils.location(token, false) + ": "
					+ TokenUtils.quotedText(token);
		return result;
	}
}
