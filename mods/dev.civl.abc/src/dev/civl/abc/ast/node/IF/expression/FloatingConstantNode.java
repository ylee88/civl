package dev.civl.abc.ast.node.IF.expression;

import dev.civl.abc.ast.value.IF.FloatingValue;

/**
 * A floating constant node represents an occurrence of a literal floating point
 * number in a program. The C11 Standard specifies the formats for floating
 * point constants in Sec. 6.4.4.2:
 * 
 * <blockquote> A floating constant has a significand part that may be followed
 * by an exponent part and a suffix that specifies its type. The components of
 * the significand part may include a digit sequence representing the
 * whole-number part, followed by a period (.), followed by a digit sequence
 * representing the fraction part. The components of the exponent part are an e,
 * E, p, or P followed by an exponent consisting of an optionally signed digit
 * sequence. Either the whole-number part or the fraction part has to be
 * present; for decimal floating constants, either the period or the exponent
 * part has to be present. </blockquote>
 * 
 * 
 * @author siegel
 * 
 */
public interface FloatingConstantNode extends ConstantNode {

	/**
	 * Is this a complex value (not real)?
	 * 
	 * @return {@code true} iff the value is a complex number, not a real number
	 */
	boolean isComplex();

	/**
	 * Returns the whole-number part of the significand. This is the digit sequence
	 * preceding the "<code>.</code>" or just the entire significand if the
	 * "<code>.</code>" is not present.
	 * 
	 * @return the whole-number part of the significand
	 */
	String wholePart();

	/**
	 * Returns the fraction part of the significand. This is the digit sequence
	 * following the "<code>.</code>". May return <code>null</code> if not fraction
	 * part is present.
	 * 
	 * @return the fraction part of the significand
	 */
	String fractionPart();

	/**
	 * Returns the exponent part of the constant, including the letter. May return
	 * <code>null</code> if the exponent part is not present.
	 * 
	 * @return the exponent part of the constant
	 */
	String exponent();

	/**
	 * Returns the floating (real or complex) value obtained by interpreting the
	 * representation of the constant.
	 * 
	 * @return the floating value
	 */
	@Override
	FloatingValue getConstantValue();

	@Override
	FloatingConstantNode copy();

}
