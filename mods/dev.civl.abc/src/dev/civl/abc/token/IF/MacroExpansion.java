package dev.civl.abc.token.IF;

/**
 * A MacroExpansion represents an instance of the expansion of a preprocessor
 * (object or function) macro.
 * 
 * A new token resulting from a macro expansion has two values associated to it:
 * a start token (a token from the macro invocation), and a replacement token
 * index. The following examples illustrate this mapping from final token to
 * (macro invocation token in f2, macro definition replacement token index in
 * f1)
 * 
 * <pre>
f1: #define X R1 R2
f2: #include "f1"
    X
Result: R1 R2

Token R1: (X,0)
Token R2: (X,1)
 * </pre>
 * 
 * <pre>
f1: #define MAX(X,Y) X > Y ? X : Y
f2: #include "f1"
    MAX(a, b*c)
Result: a > b*c ? a : b*c 

Token a: (a, 0)
Token >: (MAX, 1)
Token b: (b, 2)
Token *: (*, 2)
Token c: (c, 2)
Token ?: (MAX, 3)
Token a: (a, 4)
Token :: (MAX, 5)
Token b: (b, 6)
Token *: (*, 6)
Token c: (c, 6)
 * </pre>
 * 
 * @author siegel
 */
public interface MacroExpansion extends Formation {

	/**
	 * Gets the token from the macro invocation which led to the construction of
	 * the new token. The start token is either the macro name occurring in the
	 * invocation (in the case of an object macro or the case of a function
	 * macro in which the new token is a replacement token that is not an
	 * argument), or a token occurring in an argument of a function macro
	 * invocation.
	 * 
	 * @return the original token which is replaced
	 */
	CivlcToken getStartToken();

	/**
	 * Returns the (function or object) macro that is being applied.
	 * 
	 * @return the macro being applied
	 */
	Macro getMacro();

	/**
	 * Returns the index of the replacement token in the sequence of replacement
	 * tokens specified in the macro definition.
	 * 
	 * @return the replacement token index
	 */
	int getReplacementTokenIndex();
}
