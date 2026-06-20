package dev.civl.abc.ast.node.common;

import java.math.BigInteger;

import dev.civl.abc.ast.node.IF.expression.FloatingConstantNode;
import dev.civl.abc.ast.node.IF.expression.IntegerConstantNode;
import dev.civl.abc.ast.node.common.expression.CommonFloatingConstantNode;
import dev.civl.abc.ast.node.common.expression.CommonIntegerConstantNode;
import dev.civl.abc.ast.type.IF.FloatingType;
import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.ast.type.IF.StandardSignedIntegerType;
import dev.civl.abc.ast.type.IF.StandardSignedIntegerType.SignedIntKind;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType.UnsignedIntKind;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.IF.FloatingValue;
import dev.civl.abc.ast.value.IF.IntegerValue;
import dev.civl.abc.ast.value.IF.RealFloatingValue;
import dev.civl.abc.ast.value.IF.ValueFactory;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;

public class LiteralInterpreter {

	private TypeFactory typeFactory;

	private ValueFactory valueFactory;

	// private StandardSignedIntegerType SCHAR, SSHORT;

	private StandardSignedIntegerType SINT, SLONG, SLLONG;

	// private StandardUnsignedIntegerType UCHAR, USHORT;

	private StandardUnsignedIntegerType UINT, ULONG, ULLONG;

	private FloatingType FLOAT, DOUBLE, LDOUBLE, FLOAT_COMPLEX, DOUBLE_COMPLEX, LDOUBLE_COMPLEX;

	/** The potential type lists for each kind of suffix. */
	private IntegerType[] noneDec, noneHex, uDec, uHex, lDec, lHex, ulDec, ulHex, llDec, llHex, ullDec, ullHex;

	public LiteralInterpreter(TypeFactory typeFactory, ValueFactory valueFactory) {
		this.typeFactory = typeFactory;
		this.valueFactory = valueFactory;
		// SCHAR = typeFactory.signedIntegerType(SignedIntKind.SIGNED_CHAR);
		// SSHORT = typeFactory.signedIntegerType(SignedIntKind.SHORT);
		SINT = typeFactory.signedIntegerType(SignedIntKind.INT);
		SLONG = typeFactory.signedIntegerType(SignedIntKind.LONG);
		SLLONG = typeFactory.signedIntegerType(SignedIntKind.LONG_LONG);
		// UCHAR =
		// typeFactory.unsignedIntegerType(UnsignedIntKind.UNSIGNED_CHAR);
		// USHORT = typeFactory
		// .unsignedIntegerType(UnsignedIntKind.UNSIGNED_SHORT);
		UINT = typeFactory.unsignedIntegerType(UnsignedIntKind.UNSIGNED);
		ULONG = typeFactory.unsignedIntegerType(UnsignedIntKind.UNSIGNED_LONG);
		ULLONG = typeFactory.unsignedIntegerType(UnsignedIntKind.UNSIGNED_LONG_LONG);
		FLOAT = (FloatingType) typeFactory.basicType(BasicTypeKind.FLOAT);
		DOUBLE = (FloatingType) typeFactory.basicType(BasicTypeKind.DOUBLE);
		LDOUBLE = (FloatingType) typeFactory.basicType(BasicTypeKind.LONG_DOUBLE);
		FLOAT_COMPLEX = (FloatingType) typeFactory.basicType(BasicTypeKind.FLOAT_COMPLEX);
		DOUBLE_COMPLEX = (FloatingType) typeFactory.basicType(BasicTypeKind.DOUBLE_COMPLEX);
		LDOUBLE_COMPLEX = (FloatingType) typeFactory.basicType(BasicTypeKind.LONG_DOUBLE_COMPLEX);
		noneDec = new IntegerType[] { SINT, SLONG, SLLONG };
		noneHex = new IntegerType[] { SINT, UINT, SLONG, ULONG, SLLONG, ULLONG };
		uDec = uHex = new IntegerType[] { UINT, ULONG, ULLONG };
		lDec = new IntegerType[] { SLONG, SLLONG };
		lHex = new IntegerType[] { SLONG, ULONG, SLLONG, ULLONG };
		ulDec = ulHex = new IntegerType[] { ULONG, ULLONG };
		llDec = new IntegerType[] { SLLONG };
		llHex = new IntegerType[] { SLLONG, ULLONG };
		ullDec = ullHex = new IntegerType[] { ULLONG };
	}

	public IntegerConstantNode integerConstant(Source source, String text) throws SyntaxException {
		String stripped, suffix;
		int length = text.length();
		IntegerConstantNode node;
		IntegerType type;
		int base;
		IntegerType[] typeList;
		BigInteger bigIntValue;
		IntegerValue value;

		while (length >= 1) {
			char c = text.charAt(length - 1);

			if (c != 'U' && c != 'u' && c != 'l' && c != 'L')
				break;
			length--;
		}
		stripped = text.substring(0, length);
		suffix = text.substring(length);
		try {
			if (stripped.startsWith("0")) {
				if (stripped.startsWith("0x") || stripped.startsWith("0X")) {
					base = 16;
					stripped = stripped.substring(2);
				} else {
					base = 8;
				}
			} else {
				base = 10;
			}
		} catch (NumberFormatException e) {
			throw new SyntaxException("Unable to extract integer value from " + text + ":\n" + e, source);
		}
		bigIntValue = new BigInteger(stripped, base);
		suffix = suffix.toLowerCase();
		// see table on C11 page 64
		if (suffix.isEmpty())
			typeList = (base == 10 ? noneDec : noneHex);
		else if (suffix.equals("u"))
			typeList = (base == 10 ? uDec : uHex);
		else if (suffix.equals("l"))
			typeList = (base == 10 ? lDec : lHex);
		else if (suffix.equals("ul") || suffix.equals("lu"))
			typeList = (base == 10 ? ulDec : ulHex);
		else if (suffix.equals("ll"))
			typeList = (base == 10 ? llDec : llHex);
		else if (suffix.equals("ull") || suffix.equals("llu"))
			typeList = (base == 10 ? ullDec : ullHex);
		else
			throw new SyntaxException("Unknown suffix " + suffix, source);
		type = typeFactory.rangeChoice(bigIntValue, typeList);
		if (type == null)
			throw new SyntaxException("Unable to find integer type to represent constant ", source);
		value = valueFactory.integerValue(type, bigIntValue);
		node = new CommonIntegerConstantNode(source, text, value);
		return node;
	}

	public FloatingConstantNode floatingConstant(Source source, String text) throws SyntaxException {
		int base, length, expPos, dotPos;
		String stripped, suffix, significand, wholePart, fractionPart, exponent;
		BigInteger wholePartValue, fractionPartValue, exponentValue;

		text = text.toLowerCase();
		length = text.length();
		while (length >= 1) {
			char c = text.charAt(length - 1);

			if (c != 'l' && c != 'f' && c != 'i' && c != 'j')
				break;
			length--;
		}
		stripped = text.substring(0, length);
		suffix = text.substring(length);
		if (stripped.startsWith("0x")) {
			base = 16;
			stripped = stripped.substring(2);
		} else {
			base = 10;
		}
		expPos = stripped.indexOf(base == 10 ? 'e' : 'p');
		if (expPos >= 0) {
			significand = stripped.substring(0, expPos);
			exponent = stripped.substring(expPos + 1);
		} else {
			significand = stripped;
			exponent = "";
		}
		dotPos = significand.indexOf('.');
		if (dotPos >= 0) {
			wholePart = significand.substring(0, dotPos);
			fractionPart = significand.substring(dotPos + 1);
		} else {
			wholePart = significand;
			fractionPart = "";
		}

		FloatingType realType, complexType = null;
		boolean isComplex;

		switch (suffix) {
		case "":
			realType = DOUBLE;
			isComplex = false;
			break;
		case "f":
			realType = FLOAT;
			isComplex = false;
			break;
		case "l":
			realType = LDOUBLE;
			isComplex = false;
			break;
		case "i":
		case "j":
			realType = DOUBLE;
			complexType = DOUBLE_COMPLEX;
			isComplex = true;
			break;
		case "if":
		case "jf":
			realType = FLOAT;
			complexType = FLOAT_COMPLEX;
			isComplex = true;
			break;
		case "il":
		case "jl":
			realType = LDOUBLE;
			complexType = LDOUBLE_COMPLEX;
			isComplex = true;
			break;
		default:
			throw new SyntaxException("Unknown floating suffix: " + suffix, source);
		}
		if (wholePart.isEmpty())
			wholePartValue = BigInteger.ZERO;
		else
			wholePartValue = new BigInteger(wholePart, base);
		if (fractionPart.isEmpty())
			fractionPartValue = BigInteger.ZERO;
		else
			fractionPartValue = new BigInteger(fractionPart, base);
		if (exponent.isEmpty())
			exponentValue = BigInteger.ZERO;
		else
			exponentValue = new BigInteger(exponent, 10);

		FloatingValue value;
		if (isComplex) {
			RealFloatingValue imag = valueFactory.realFloatingValue(realType, base, wholePartValue, fractionPartValue,
					fractionPart.length(), exponentValue);
			RealFloatingValue realZero = valueFactory.realFloatingValue(realType, base, BigInteger.ZERO,
					BigInteger.ZERO, 1, BigInteger.ZERO);
			value = valueFactory.complexFloatingValue(complexType, realZero, imag);

		} else {
			value = valueFactory.realFloatingValue(realType, base, wholePartValue, fractionPartValue,
					fractionPart.length(), exponentValue);
		}
		return new CommonFloatingConstantNode(source, text, wholePart, fractionPart, exponent, value);
	}

}
