lexer grammar PreprocessorLexer;

/*
 * Author: Stephen F. Siegel, University of Delaware
 * Last changed: June 2012
 *
 * This is a grammar for lexical analysis for a preprocessor
 * file.  It follows the C11 Standard.  This grammar assumes
 * that the stream of characters being scanned has already
 * gone through translation phases 1 and 2.  In particular
 * backslash followed by newline sequences have been removed.
 *
 * This lexer grammar will not contain C keywords and ones for 
 * CIVL-C, ACSL, GNU and CUDA extensions of C.
 * Those keywords are defined in 
 *   dev.civl.abc.front.c.parse.PP2CivlcTokenCConverter
 * A private function named as `initCKeywordMap` shall identify 
 * and set tokens in preprocessed streams as its corresponding 
 * token-types.
 */

@header
{
package dev.civl.abc.front.c.preproc;
}

@members
{

/* Are we currently parsing ACSL annotations?  If yes, the comments that
   begin with '@' will be parsed as a sequence of ordinary preprocessor
   tokens.  If no, they will be parsed as ordinary comments, i.e., as a
   single token consisting of one big string.   This option is controled
   by the presence of the #pragma CIVL ACSL in the source file.  */
public boolean parseAnnotations = false;

/* States in a DFS looking for "#pragma CIVL ACSL" which informs the
   lexer to start scanning annotations as preprocessor tokens rather
   than as one big comment.  Start state: 1.

  0: waiting for NEWLINE: anything other than NEWLINE self loops.
     on NEWLINE goto 1.
  1: waiting for #: whitespace self-loops, 
     on # goto 2.
     anything else: goto 0.
  2: waiting for pragma: non-NEWLINE white space self-loops.
     on NEWLINE: goto 1.
     on pragma: goto 3.
     on anything else: goto 0 
  3: waiting for CIVL: non-NEWLINE white space self-loops.
     on NEWLINE: goto 1.
     on CIVL: goto 4
     on anything else: goto 0
  4: waiting for ACSL: non-NEWLINE white space self-loops.
     on NEWLINE: goto 1
     on ACSL: BINGO. set parseAnnotations to true.  Goto 0.
     on anything else: goto 0.
 */
private int annoteState = 1;

@Override
public void emitErrorMessage(String msg) { // don't try to recover!
    throw new RuntimeException(msg);
}

@Override
public void emit(Token token) {
  if (parseAnnotations && token.getType() == COMMENT) {
    String text = token.getText();
    if ("/*@".equals(text))
      token.setType(ANNOTATION_START);
    else if ("//@".equals(text))
      token.setType(INLINE_ANNOTATION_START);
  }
  super.emit(token);
  //System.out.println("Token: "+token); // DEBUGGING....
}

/* Looks for the sequence #pragma CIVL ACSL.  As soon as that is detected
   sets parseAnnotations to true.  This causes annotations to be parsed
   as preprocessor tokens rather than as text (as a normal comment would be).
 */
@Override
public Token nextToken() {
	Token token = super.nextToken();
	if (parseAnnotations)
	  return token;
	int type = token.getType();
	switch (annoteState) {
	case 0:
	  if (type == NEWLINE) annoteState = 1;
	  break;
	case 1: // at beginning of line.  this is the start state. 
	  if (type == HASH) annoteState = 2;
	  else if (type != NEWLINE && type != WS) annoteState = 0;
	  break;
	case 2:
	  if (type == NEWLINE) annoteState = 1;
	  else if (type == PRAGMA) annoteState = 3;
	  else if (type != WS) annoteState = 0;
	  break;
	case 3:
	  if (type == NEWLINE) annoteState = 1;
	  else if (type == IDENTIFIER && 
	           "CIVL".equals(token.getText().toUpperCase()))
	    annoteState = 4;
	  else if (type != WS) annoteState = 0;
	  break;
	case 4:
	  if (type == NEWLINE) annoteState = 1;
	  else if (type == IDENTIFIER && 
	           "ACSL".equals(token.getText().toUpperCase())) {
	    parseAnnotations = true;
	    //System.out.println("PARSING ANNOTATIONS NOW.");
	  }
	  else if (type != WS) annoteState = 0;
	  break;
	default:
	  assert false; // unreachable
	}
	return token;
}

}

/****** White space ******/
NEWLINE		:	'\r'? '\n'	;
WS		:	' ' | '\t'	;

/* Words that are used in both C and the preprocessor */
IF		:	'if'		;
ELSE		:	'else'		;

/* Words used in preprocessor but not in C */
DEFINE		:	'define'	;
DEFINED		:	'defined'	;
ELIF		:	'elif'		;
ENDIF		:	'endif'		;
ERROR		:	'error'		;
IFDEF		:	'ifdef'		;
IFNDEF		:	'ifndef'	;
INCLUDE		:	'include'	;
LINE		:	'line'		;
PRAGMA		:	'pragma'	;
UNDEF		:	'undef'		;

/****** Punctuators: C11 Sec. 6.4.6 ******/
ELLIPSIS	: 	'...'	 	;
DOTDOT		: 	'..' 		;
DOT		:	'.' 		;
AMPERSAND	:	'&'		;
AND		:	'&&'		;
ARROW		:	'->'		;
ASSIGN		:	'='		;
BITANDEQ	:	'&='		;
BITOR		:	'|'		;
BITOREQ		:	'|='		;
BITXOR		:	'^'		;
BITXOREQ	:	'^='		;
COLON		:	':'		;
COMMA		:	','		;
DIV		:	'/'		;
DIVEQ		:	'/='		;
EQUALS		:	'=='		;
GT		:	'>'		;
GTE		:	'>='		;
HASH		:	'#' | '%:'	;
HASHHASH	:	'##' | '%:%:'	;
LCURLY		:	'{' | '<%'	;
LPAREN		:	'('		;
LSQUARE		:	'[' | '<:'	;
LT		:	'<'		;
LTE		:	'<='		;
MINUSMINUS	:	'--'		;
MOD		:	'%'		;
MODEQ		:	'%='		;
NEQ		:	'!='		;
NOT		:	'!'		;
OR		:	'||'		;
PLUS		:	'+'		;
PLUSEQ		:	'+='		;
PLUSPLUS	:	'++'		;
QMARK		:	'?'		;
RCURLY		:	'}' | '%>'	;
RPAREN		:	')'		;
RSQUARE		:	']' | ':>'	;
SEMI		:	';'		;
SHIFTLEFT	:	'<<'		;
SHIFTLEFTEQ	:	'<<='		;
SHIFTRIGHT	:	'>>'		;
SHIFTRIGHTEQ	:	'>>='		;
STAR		:	'*'		;
STAREQ		:	'*='		;
SUB		:	'-'		;
SUBEQ		:	'-='		;
TILDE		:	'~'		;

/* CIVL-C and ACSL Punctuators */


AT			:	'@'	;
EQUIV_ACSL		:	'<==>'	;
IMPLIES			:	'=>'	;
IMPLIES_ACSL		:	'==>'	;
// LSLIST and RSLIST enclose a scope list
LSLIST			:	'<|'	;
RSLIST			:	'|>'	;
XOR_ACSL		:	'^^'	;

/* CUDA Punctuators */
LEXCON			:	'<<<'	;
REXCON			:	'>>>'	;


/****** Identifiers: C11 Sec. 6.4.2 ******/
IDENTIFIER	:	IdentifierNonDigit
			(IdentifierNonDigit | Digit)*
		;
		
fragment
IdentifierNonDigit
		:	NonDigit | UniversalCharacterName ;

fragment
Zero		:	'0' ;

fragment
Digit		:	Zero | NonZeroDigit ;

fragment
NonZeroDigit	:	'1' .. '9' ;

fragment
NonDigit	:	'A'..'Z' | 'a'..'z' | '_' | '$';

fragment
UniversalCharacterName
		:	'\\' 'u' HexQuad 
		|	'\\' 'U' HexQuad HexQuad
		;

fragment
HexQuad		:	HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit ;

fragment
HexadecimalDigit
		:	'0'..'9' | 'a'..'f' | 'A'..'F' ;

/****** Sec. 6.4.4.1: Integer constants ******/
INTEGER_CONSTANT
		:	DecimalConstant IntegerSuffix?
		|	OctalConstant IntegerSuffix?
		|	HexadecimalConstant IntegerSuffix?
		;

fragment
DecimalConstant	:	NonZeroDigit Digit* ;


fragment
IntegerSuffix	:	UnsignedSuffix LongSuffix?
		|	UnsignedSuffix LongLongSuffix
		|	LongSuffix UnsignedSuffix?
		|	LongLongSuffix UnsignedSuffix?
		;

fragment
UnsignedSuffix	:	'u' | 'U'	;

fragment
LongSuffix	:	'l' | 'L'	;

fragment
LongLongSuffix	:	'll' | 'LL'	;

fragment	
OctalConstant	:	Zero OctalDigit* IntegerSuffix? ;

fragment
HexadecimalConstant
		:	HexPrefix HexadecimalDigit+ IntegerSuffix? ;

fragment
HexPrefix	:	Zero ('x' | 'X') ;

/****** Sec. 6.4.4.2: Floating Constants ******/

FLOATING_CONSTANT
		:	DecimalFloatingConstant
		|	HexadecimalFloatingConstant
		;

fragment
DecimalFloatingConstant
		:	FractionalConstant ExponentPart? FloatingSuffix?
		|	Digit+ ExponentPart FloatingSuffix?
		;

fragment
FractionalConstant
		:	Digit* DOT Digit+
		|	Digit+ DOT
		;

fragment
ExponentPart	:	('e' | 'E') ('+' | '-')? Digit+ ;

fragment
FloatingSuffix	:	'f' | 'l' | 'F' | 'L' ;

fragment
HexadecimalFloatingConstant
		:	HexPrefix HexFractionalConstant BinaryExponentPart
			FloatingSuffix?
		|	HexPrefix HexadecimalDigit+ BinaryExponentPart
			FloatingSuffix?
		;

fragment
HexFractionalConstant
		:	HexadecimalDigit* DOT HexadecimalDigit+
		|	HexadecimalDigit+ DOT 
		;

fragment
BinaryExponentPart
		:	('p' | 'P') ('+' | '-')? Digit+ ;


/****** Preprocessing Numbers: C11 Sec 6.4.8 ******/

/* PP_NUMBER should be anything that doesn't match the previous
 * rules but does match this one.
 */
PP_NUMBER	:	'.'? Digit
			( '.'
			| IdentifierNonDigit
			| Digit
			| ('e' | 'E' | 'p' | 'P') ('+' | '-')
			)*
		;


/****** Sec. 6.4.4.4: Character Constants ******/

CHARACTER_CONSTANT
		:	('L' | 'U' | 'u')? '\'' CChar+ '\'' ;

fragment
CChar		:	~('\'' | '\\' | '\n') | EscapeSequence ;

fragment
EscapeSequence	:	'\\' ( '\'' | '"' | '\?' | '\\' |
			       'a' | 'b' | 'f' | 'n' |'r' | 't' | 'v'
			     )
		|	OctalEscape
		|	HexEscape
		;
fragment
OctalEscape	:	'\\' OctalDigit (OctalDigit OctalDigit?)? ;

fragment
OctalDigit	:	'0' .. '7';

fragment
HexEscape	:	'\\' 'x' HexadecimalDigit+ ;


/****** 6.4.5: String Literals *****/


STRING_LITERAL  :	('u8' | 'u' | 'U' | 'L')? '"' SChar* '"'
		;

fragment
SChar		:	~('"' | '\\' | '\n') | EscapeSequence ;



/* ***** Comments: C11 Sec 6.4.9 ******/

fragment
INLINE_COMMENT : '//' INLINE_COMMENT_TAIL ;

fragment
INLINE_COMMENT_TAIL
  : NEWLINE
  | EOF
  | ~('@' | '\n' | '\r') ( options {greedy=true;} : ~('\n'|'\r') )*
  | {!parseAnnotations}?=> '@' ( options {greedy=true;} : ~('\n'|'\r') )*
  | {parseAnnotations}?=> '@'
  ;
       
// the following rule is never activated but no problem, we capture the token
// in INLINE_COMMENT and then change the token type in emit()...        
INLINE_ANNOTATION_START :	'//@'	;

// the following is not quite perfect because in the case of the \n or \r
// immediately following the // it counts that white space as part of the
// comment, otherwise it doesn't.  Would like to make the \n or \r NOT
// part of the comment always, but how --- need to look ahead one character?

fragment
BLOCK_COMMENT : '/*' BLOCK_COMMENT_TAIL ;

fragment BLOCK_COMMENT_TAIL
  : '*/'
  | ~('@') ( options {greedy=false;} : . )* '*/'
  | {!parseAnnotations}?=> '@' ( options {greedy=false;} : . )* '*/'
  | {parseAnnotations}?=> '@'
  ;
              
COMMENT : INLINE_COMMENT | BLOCK_COMMENT ;

// For some reason, ANNNOTATION_START is never invoked.  No problem,
// we will catch it on emit as a COMMENT and change its type.
ANNOTATION_START	: {parseAnnotations}?=> '/*' '@' ;
ANNOTATION_END		: {parseAnnotations}?=> '*/'  ;


/* Special keywords starting with backslash reserved for extensions
 * such as ACSL */
EXTENDED_IDENTIFIER
	:
	'\\' IdentifierNonDigit (IdentifierNonDigit | Digit)* 
	;

/****** Other characters: C11 Sec. 6.4 ******/
OTHER		: . ;
