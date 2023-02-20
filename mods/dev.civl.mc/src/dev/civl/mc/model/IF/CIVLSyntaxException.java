package dev.civl.mc.model.IF;

import dev.civl.abc.token.IF.Source;
import dev.civl.mc.model.common.ABC_CIVLSource;

/**
 * An exception thrown when there is syntax error in the program being verified,
 * e.g., calling $choose_int with more than one arguments, etc.
 * 
 * @author Manchun Zheng (zmanchun)
 * 
 */
public class CIVLSyntaxException extends CIVLException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = -7304144606936703023L;

	public CIVLSyntaxException(String message, CIVLSource source) {
		super("Syntax error: " + message, source);
	}

	public CIVLSyntaxException(String message, Sourceable sourceable) {
		this(message, sourceable.getSource());
	}

	public CIVLSyntaxException(String message) {
		this(message, (CIVLSource) null);
	}

	public CIVLSyntaxException(String message, Source source) {
		this(message, new ABC_CIVLSource(source));
	}
}
