package dev.civl.abc.token.IF;

/**
 * A formation is a record of the history of events that went into the formation
 * of a token. Examples of such events include preprocessor inclusion (
 * <code>#include</code>), preprocessor macro expansion, and adjacent string
 * literal concatenation.
 * 
 * Formations may have a recursive structure.
 * 
 * @author siegel
 * 
 */
public interface Formation {

	/**
	 * Returns a human-readable textual description of this formation that is
	 * not a complete sentence, but is meant to be appended to a string that
	 * describes the token. For example, this method might returns something
	 * like "formed by concatenating ...".
	 * 
	 * @return description of formation as clause to be appended to description
	 *         of token
	 */
	String suffix();

	/**
	 * In the sequence of files that led, through inclusions and macro
	 * expansions, to the creation of the token, this returns the last file. The
	 * last file should be the file that contains the actual sequence of
	 * characters that comprise the text of the token.
	 * 
	 * I.e., if F1 includes F2, and F2 includes F3, ..., and Fn-1 includes Fn,
	 * this returns Fn. Hence it is the file that is closest to the final token.
	 * For a macro expansion, the last file is the file of the macro definition
	 * (if the token is from the macro definition replacement list) or the file
	 * of the macro invocation (if the macro is a function macro and the token
	 * originates in an argument to the macro invocation).
	 * 
	 * TODO: define last file for concatenation, stringification, and other
	 * formations.
	 * 
	 * @return last file in inclusion/expansion sequence
	 */
	SourceFile getLastFile();
}
