package dev.civl.abc.ast.value.common;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import dev.civl.abc.ast.entity.IF.Entity;
import dev.civl.abc.ast.entity.IF.Entity.EntityKind;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.entity.IF.Variable;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.expression.AlignOfNode;
import dev.civl.abc.ast.node.IF.expression.ArrowNode;
import dev.civl.abc.ast.node.IF.expression.CastNode;
import dev.civl.abc.ast.node.IF.expression.CharacterConstantNode;
import dev.civl.abc.ast.node.IF.expression.CompoundLiteralNode;
import dev.civl.abc.ast.node.IF.expression.ConstantNode;
import dev.civl.abc.ast.node.IF.expression.DotNode;
import dev.civl.abc.ast.node.IF.expression.EnumerationConstantNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.expression.SizeofNode;
import dev.civl.abc.ast.node.IF.expression.StringLiteralNode;
import dev.civl.abc.ast.type.IF.ArrayType;
import dev.civl.abc.ast.type.IF.Enumerator;
import dev.civl.abc.ast.type.IF.Field;
import dev.civl.abc.ast.type.IF.FloatingType;
import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.ast.type.IF.PointerType;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.ast.type.IF.StandardSignedIntegerType;
import dev.civl.abc.ast.type.IF.StandardSignedIntegerType.SignedIntKind;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType.UnsignedIntKind;
import dev.civl.abc.ast.type.IF.StructureOrUnionType;
import dev.civl.abc.ast.type.IF.Type;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.IF.AddressValue;
import dev.civl.abc.ast.value.IF.ArrayElementReference;
import dev.civl.abc.ast.value.IF.ArrayValue;
import dev.civl.abc.ast.value.IF.CastValue;
import dev.civl.abc.ast.value.IF.CharacterValue;
import dev.civl.abc.ast.value.IF.ComplexFloatingValue;
import dev.civl.abc.ast.value.IF.FunctionReference;
import dev.civl.abc.ast.value.IF.IntegerValue;
import dev.civl.abc.ast.value.IF.MemberReference;
import dev.civl.abc.ast.value.IF.OperatorValue;
import dev.civl.abc.ast.value.IF.RealFloatingValue;
import dev.civl.abc.ast.value.IF.StringValue;
import dev.civl.abc.ast.value.IF.StructureValue;
import dev.civl.abc.ast.value.IF.TypeValue;
import dev.civl.abc.ast.value.IF.TypeValue.TypeValueKind;
import dev.civl.abc.ast.value.IF.UnionValue;
import dev.civl.abc.ast.value.IF.Value;
import dev.civl.abc.ast.value.IF.ValueFactory;
import dev.civl.abc.ast.value.IF.VariableReference;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.token.IF.ExecutionCharacter;
import dev.civl.abc.token.IF.StringLiteral;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.UnsourcedException;

/**
 * Flyweight pattern is used for those values which are immutable. Compound
 * values are mutable.
 * 
 * @author siegel
 * 
 */
public class CommonValueFactory implements ValueFactory {

	// Fields...

	private TypeFactory typeFactory;

	private Map<Value, Value> valueMap = new HashMap<Value, Value>();

	private StandardSignedIntegerType SINT;

	private IntegerType CHAR;

	private BigInteger MAX_JINT = new BigInteger(Integer.toString(Integer.MAX_VALUE));

	private Value SINT_ONE, SINT_ZERO;

	// Constructors...

	public CommonValueFactory(Configuration configuration, TypeFactory typeFactory) {
		this.typeFactory = typeFactory;
		SINT = typeFactory.signedIntegerType(SignedIntKind.INT);
		CHAR = (IntegerType) typeFactory.basicType(BasicTypeKind.CHAR);
		SINT_ONE = integerValue(SINT, 1);
		SINT_ZERO = integerValue(SINT, 0);
	}

	// Exported methods...

	@Override
	public Value evaluate(ExpressionNode expr) throws SyntaxException {
		try {
			return evaluateHelper(expr);
		} catch (UnsourcedException err) {
			throw error(err, expr);
		}
	}

	/**
	 * Evaluates a constant expression.
	 * 
	 * Should apply conversions to result.
	 * 
	 * @param expr
	 * @return
	 * @throws SyntaxException
	 * @throws UnsourcedException if expr is not a constant expression
	 */
	private Value evaluateHelper(ExpressionNode expr) throws SyntaxException, UnsourcedException {
		if (expr instanceof AlignOfNode) {
			return alignofValue(((AlignOfNode) expr).getArgument().getType());
		} else if (expr instanceof ArrowNode) {
			ArrowNode arrowNode = (ArrowNode) expr;
			ExpressionNode structOrUnionPointer = arrowNode.getStructurePointer();
			Field[] navseq = arrowNode.getNavigationSequence();
			Value value = evaluateDereference(structOrUnionPointer);

			for (Field field : navseq)
				value = evaluateMemberAccess(value, field);
			return value;
		} else if (expr instanceof CastNode) {
			CastNode castNode = (CastNode) expr;

			return evaluateCast(castNode.getCastType().getType(), evaluate(castNode.getArgument()));
		} else if (expr instanceof CharacterConstantNode) {
			return ((CharacterConstantNode) expr).getConstantValue();
		} else if (expr instanceof CompoundLiteralNode) {
			return evaluateCompoundLiteral((CompoundLiteralNode) expr);
		} else if (expr instanceof ConstantNode) {
			return ((ConstantNode) expr).getConstantValue();
		} else if (expr instanceof DotNode) {
			DotNode dotNode = (DotNode) expr;
			ExpressionNode structOrUnion = dotNode.getStructure();
			IdentifierNode fieldIdentifier = dotNode.getFieldName();
			Field field = (Field) fieldIdentifier.getEntity();
			Value structOrUnionValue = evaluate(structOrUnion);

			return evaluateMemberAccess(structOrUnionValue, field);
		} else if (expr instanceof OperatorNode) {
			OperatorNode opNode = (OperatorNode) expr;
			Operator operator = opNode.getOperator();

			if (operator == Operator.ADDRESSOF)
				return addressValue(opNode.getArgument(0));
			else { // evaluate arguments and call apply
				int numArgs = opNode.getNumberOfArguments();
				Value[] argValues = new Value[numArgs];
				Type type = expr.getInitialType();

				for (int i = 0; i < numArgs; i++) {
					ExpressionNode arg = opNode.getArgument(i);
					Value argValue = evaluate(arg);

					argValues[i] = applyConversions(arg, argValue);
				}
				return apply(type, operator, argValues);
			}
		} else if (expr instanceof SizeofNode) {
			return sizeofValue((((SizeofNode) expr).getArgument()).getType());
		} else if (expr instanceof StringLiteralNode) {
			return ((StringLiteralNode) expr).getConstantValue();
		} else if (expr instanceof EnumerationConstantNode) {
			IdentifierNode identNode = ((EnumerationConstantNode) expr).getName();
			Entity entity = identNode.getEntity();

			if (entity.getEntityKind() == EntityKind.ENUMERATOR) {
				return ((Enumerator) entity).getValue();
			}
			return null;
		} else if (expr instanceof IdentifierExpressionNode) {
			throw new UnsourcedException("Variable in constant expression");
		}
		return null;
	}

	@Override
	public IntegerValue plusOne(IntegerValue value) {
		return integerValue(value.getType(), value.getIntegerValue().add(BigInteger.ONE));
	}

	@Override
	public AddressValue addressValue(ExpressionNode lhs) throws SyntaxException {
		if (lhs instanceof IdentifierExpressionNode) {
			Entity entity = ((IdentifierExpressionNode) lhs).getIdentifier().getEntity();
			EntityKind kind = entity.getEntityKind();

			if (kind == EntityKind.VARIABLE) {
				return variableReference((Variable) entity);
			} else if (kind == EntityKind.FUNCTION) {
				return functionReference((Function) entity);
			} else {
				throw error("Operand of & not variable or function identifier", lhs);
			}
		} else if (lhs instanceof DotNode) {
			AddressValue structureOrUnionReference = addressValue(((DotNode) lhs).getStructure());
			Field field = (Field) ((DotNode) lhs).getFieldName().getEntity();

			return memberReference(structureOrUnionReference, field);
		} else if (lhs instanceof OperatorNode) {
			OperatorNode opNode = (OperatorNode) lhs;
			Operator operator = ((OperatorNode) lhs).getOperator();

			if (operator == Operator.SUBSCRIPT) {
				AddressValue arrayReference = addressValue(opNode.getArgument(0));
				Value index = evaluate(opNode.getArgument(1));

				return arrayElementReference(arrayReference, index);
			} else if (operator == Operator.DEREFERENCE) {
				return (AddressValue) evaluate(opNode.getArgument(0));
			}
		}
		throw error("Cannot take address of expression", lhs);
	}

	@Override
	public IntegerValue integerValue(IntegerType type, BigInteger integerValue) {
		return (IntegerValue) canonic(new CommonIntegerValue(type, integerValue));
	}

	@Override
	public IntegerValue integerValue(IntegerType type, int intValue) {
		return integerValue(type, BigInteger.valueOf(intValue));
	}

	@Override
	public RealFloatingValue realFloatingValue(FloatingType type, int radix, BigInteger wholePartValue,
			BigInteger fractionPartValue, int fractionLength, BigInteger exponentValue) {
		return (RealFloatingValue) canonic(new CommonRealFloatingValue(type, radix, wholePartValue, fractionPartValue,
				fractionLength, exponentValue));
	}

	@Override
	public ComplexFloatingValue complexFloatingValue(FloatingType type, RealFloatingValue realPart, RealFloatingValue imaginaryPart) {
		return (ComplexFloatingValue) canonic(new CommonComplexFloatingValue(type, realPart, imaginaryPart));
	}

	@Override
	public Value sizeofValue(Type type) {
		return (TypeValue) canonic(new CommonTypeValue(typeFactory.size_t(), TypeValueKind.SIZEOF, type));
	}

	@Override
	public TypeValue alignofValue(Type type) {
		return (TypeValue) canonic(new CommonTypeValue(typeFactory.size_t(), TypeValueKind.ALIGNOF, type));
	}

	@Override
	public CastValue castValue(Type castType, Value argument) {
		return (CastValue) canonic(new CommonCastValue(castType, argument));
	}

	@Override
	public OperatorValue operatorValue(Type type, Operator operator, Value[] arguments) {
		return (OperatorValue) canonic(new CommonOperatorValue(type, operator, arguments));
	}

	@Override
	public StructureValue newStructureValue(StructureOrUnionType type) {
		return new CommonStructureValue(type);
	}

	@Override
	public UnionValue newUnionValue(StructureOrUnionType unionType, Field field, Value memberValue) {
		return (UnionValue) canonic(new CommonUnionValue(unionType, field, memberValue));
	}

	@Override
	public ArrayValue newArrayValue(ArrayType type) {
		return new CommonArrayValue(type);
	}

	@Override
	public Answer isZero(Value value) {
		return value.isZero();
	}

	@Override
	public CharacterValue characterValue(ExecutionCharacter character) {
		IntegerType type;

		switch (character.getCharacterKind()) {
		case CHAR:
			type = CHAR;
			break;
		case WCHAR:
			type = typeFactory.wchar_t();
			break;
		case CHAR16:
			type = typeFactory.char16_t();
			break;
		case CHAR32:
			type = typeFactory.char32_t();
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		return (CharacterValue) canonic(new CommonCharacterValue(type, character));
	}

	@Override
	/**
	 * Precondition: string literal should already have the \0 appended. In
	 * particular, it has at least one character.
	 * 
	 */
	public StringValue stringValue(StringLiteral literal) {
		int length = literal.getNumCharacters();
		IntegerValue size = integerValue(SINT, length);
		IntegerType characterType;
		ArrayType type;

		switch (literal.getStringKind()) {
		case CHAR:
		case UTF_8:
			characterType = CHAR;
			break;
		case WCHAR:
			characterType = typeFactory.wchar_t();
			break;
		case CHAR16:
			characterType = typeFactory.char16_t();
			break;
		case CHAR32:
			characterType = typeFactory.char32_t();
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		type = typeFactory.arrayType(characterType, size);
		return (StringValue) canonic(new CommonStringValue(type, literal));
	}

	public VariableReference variableReference(Variable variable) {
		PointerType pointerType = typeFactory.pointerType(variable.getType());

		return (VariableReference) canonic(new CommonVariableReference(pointerType, variable));
	}

	public FunctionReference functionReference(Function function) {
		PointerType pointerType = typeFactory.pointerType(function.getType());

		return (FunctionReference) canonic(new CommonFunctionReference(pointerType, function));
	}

	public ArrayElementReference arrayElementReference(AddressValue arrayReference, Value index) {
		PointerType arrayReferenceType = arrayReference.getType();
		ArrayType arrayType = (ArrayType) arrayReferenceType.referencedType();
		// might need to strip qualifiers?
		ObjectType elementType = arrayType.getElementType();
		PointerType elementReferenceType = typeFactory.pointerType(elementType);

		return (ArrayElementReference) canonic(
				new CommonArrayElementReference(elementReferenceType, arrayReference, index));
	}

	public MemberReference memberReference(AddressValue structureOrUnionReference, Field field) {
		ObjectType memberType = field.getType();
		PointerType memberReferenceType = typeFactory.pointerType(memberType);

		return (MemberReference) canonic(
				new CommonMemberReference(memberReferenceType, structureOrUnionReference, field));
	}

	// Helper methods.......................................................

	private Value canonic(Value value) {
		Value result = valueMap.get(value);

		if (result == null) {
			valueMap.put(value, value);
			return value;
		}
		return result;
	}

	/**
	 * Given expression e of pointer type, evaluate *e. If e had type [qualified]
	 * pointer to t, resulting value has type t.
	 * 
	 * @param pointerExpression
	 * @return
	 */
	private Value evaluateDereference(ExpressionNode pointerExpression) throws SyntaxException {
		return null; // how can this happen in a constant expression?
	}

	private Value evaluateCast(Type castType, Value value) throws UnsourcedException {
		// TODO: cast concrete numeric types if you can, pointer types, ...
		if (value == null)
			return null;
		if (castType.compatibleWith(value.getType()))
			return value;
		return canonic(new CommonCastValue(castType, value));
	}

	private Value evaluateCompoundLiteral(CompoundLiteralNode node) {
		// TODO
		return null;
	}

	private Value evalMinus(Type type, Value arg0) throws UnsourcedException {
		if (arg0 instanceof IntegerValue) {
			BigInteger big = ((IntegerValue) arg0).getIntegerValue();
			BigInteger neg = big.negate();
			Value result = integerValue((IntegerType) type, neg);

			return result;
		}
		throw new UnsourcedException("Unsupported feature: non-integer negative");
	}

	private Value evalBinaryNumericOp(Type type, Operator operator, Value arg0, Value arg1) throws UnsourcedException {
		Object val0, val1;

		if (arg0 instanceof IntegerValue) {
			val0 = ((IntegerValue) arg0).getIntegerValue();
		} else if (arg0 instanceof RealFloatingValue) {
			val0 = ((RealFloatingValue) arg0).getDoubleValue();
		} else
			throw new UnsourcedException("Expected integer or real constant, not " + arg0);
		if (arg1 instanceof IntegerValue) {
			val1 = ((IntegerValue) arg1).getIntegerValue();
		} else if (arg1 instanceof RealFloatingValue) {
			val1 = ((RealFloatingValue) arg1).getDoubleValue();
		} else
			throw new UnsourcedException("Expected integer or real constant, not " + arg1);
		if (val0 instanceof BigInteger && val1 instanceof BigInteger) {
			BigInteger big0 = ((BigInteger) val0);
			BigInteger big1 = ((BigInteger) val1);
			BigInteger bigVal;

			switch (operator) {
			case TIMES:
				bigVal = big0.multiply(big1);
				break;
			case PLUS:
				bigVal = big0.add(big1);
				break;
			case MINUS:
				bigVal = big0.subtract(big1);
				break;
			case DIV:
				bigVal = big0.divide(big1);
				break;
			case MOD:
				bigVal = big0.mod(big1);
				break;
			default:
				throw new UnsourcedException("Unexpected operator: " + operator);
			}
			return integerValue((IntegerType) type, bigVal);
		} else {
			throw new UnsourcedException("multiplication of floating constants");
		}

	}

	/**
	 * Applies an operator to some arguments to yield a constant expression.
	 * 
	 * @param type
	 * @param operator
	 * @param args
	 * @return
	 * @throws UnsourcedException if result is not a constant expression
	 */
	private Value apply(Type type, Operator operator, Value[] args) throws UnsourcedException {
		int numArgs = args.length;

		switch (operator) {
		case BITAND: // & bit-wise and
		case BITCOMPLEMENT: // ~ bit-wise complement
		case BITOR: // | bit-wise inclusive or
		case BITXOR: // ^ bit-wise exclusive or
		case CONDITIONAL: // ?: the conditional operator
		case DEREFERENCE: // * pointer dereference
		case SUBSCRIPT: // [] array subscript
			break;

		case SHIFTLEFT: // << shift left
		case SHIFTRIGHT: // >> shift right
		case EQUALS: // == equality
		case GT: // > greater than
		case GTE: // >= greater than or equals
		case LT: // < less than
		case LTE: // <= less than or equals
		case NEQ: // != not equals
			if (numArgs == 2)
				return evalBinaryIntegerOp(operator, args[0], args[1]);
			else
				throw new UnsourcedException("Expected two arguments for operator " + operator);

		case LAND: // && logical and
			if (numArgs == 2) {
				Value lval = evalBinaryIntegerOp(Operator.EQUALS, args[0], SINT_ONE);
				if (!lval.equals(SINT_ONE))
					return SINT_ZERO;
				Value rval = evalBinaryIntegerOp(Operator.EQUALS, args[0], SINT_ONE);
				if (rval.equals(SINT_ONE))
					return SINT_ONE;
				else
					return SINT_ZERO;

			} else
				throw new UnsourcedException("Expected two arguments for operator " + operator);

		case LOR: // || logical or
			if (numArgs == 2) {
				Value lval = evalBinaryIntegerOp(Operator.EQUALS, args[0], SINT_ONE);
				if (lval.equals(SINT_ONE))
					return SINT_ONE;
				Value rval = evalBinaryIntegerOp(Operator.EQUALS, args[0], SINT_ONE);
				if (rval.equals(SINT_ONE))
					return SINT_ONE;
				else
					return SINT_ZERO;

			} else
				throw new UnsourcedException("Expected two arguments for operator " + operator);

		case NOT: // ! logical not
			if (numArgs == 1)
				return evalBinaryIntegerOp(Operator.EQUALS, args[0], SINT_ZERO);
			else
				throw new UnsourcedException("Expected one arguments for operator " + operator);

		case PLUS: // + binary addition, numeric or pointer
		case DIV: // / numerical division
		case TIMES: // numeric multiplication
		case MOD: // % integer modulus
		case MINUS: // - binary subtraction (numbers and pointers)
			if (numArgs == 2)
				return evalBinaryNumericOp(type, operator, args[0], args[1]);
			else
				throw new UnsourcedException("Expected two arguments for operator " + operator);
		case UNARYMINUS: // - numeric negative
			return evalMinus(type, args[0]);
		case UNARYPLUS: // + numeric no-op</li>
			return args[0];
		default:
			throw new UnsourcedException("Illegal operator in constant expression: " + operator);
		}
		// TODO: handle specials cases for all of above
		return canonic(new CommonOperatorValue(type, operator, args));
	}

	private Value applyConversions(ExpressionNode expr, Value value) {
		// TODO
		return value;
	}

	/**
	 * 
	 * C11 Sec. 6.3.1.2: "When any scalar value is converted to _Bool, the result is
	 * 0 if the value compares equal to 0; otherwise, the result is 1."
	 * 
	 * C11 Sec. 6.3.1.3:
	 * 
	 * <blockquote> When a value with integer type is converted to another integer
	 * type other than _Bool, if the value can be represented by the new type, it is
	 * unchanged.
	 * 
	 * Otherwise, if the new type is unsigned, the value is converted by repeatedly
	 * adding or subtracting one more than the maximum value that can be represented
	 * in the new type until the value is in the range of the new type.
	 * 
	 * Otherwise, the new type is signed and the value cannot be represented in it;
	 * either the result is implementation-defined or an implementation-defined
	 * signal is raised. </blockquote>
	 * 
	 * 
	 * @param value
	 * @param newType
	 * @return
	 */
	@SuppressWarnings("unused")
	private Value evaluateIntegerConversion(Value value, IntegerType newType) {
		IntegerType oldType = (IntegerType) value.getType();

		if (oldType.equals(newType))
			return value;
		if (value instanceof IntegerValue) {
			BigInteger intVal = ((IntegerValue) value).getIntegerValue();

			// first: if intVal representable in new type, that's it:
			if (newType instanceof StandardSignedIntegerType) {
				StandardSignedIntegerType newSigned = (StandardSignedIntegerType) newType;

				if (newSigned.inMinimumRange(intVal)) {
					return integerValue(newType, intVal);
				} else if (oldType instanceof StandardSignedIntegerType) {
					SignedIntKind newKind = newSigned.getIntKind();
					SignedIntKind oldKind = ((StandardSignedIntegerType) oldType).getIntKind();

					if (newKind.compareTo(oldKind) > 0)
						return integerValue(newType, intVal);
				}
			} else if (newType instanceof StandardUnsignedIntegerType) {
				StandardUnsignedIntegerType newSigned = (StandardUnsignedIntegerType) newType;

				if (newSigned.inMinimumRange(intVal)) {
					return integerValue(newType, intVal);
				} else if (oldType instanceof StandardUnsignedIntegerType) {
					UnsignedIntKind newKind = newSigned.getIntKind();
					UnsignedIntKind oldKind = ((StandardUnsignedIntegerType) oldType).getIntKind();

					if (newKind.compareTo(oldKind) > 0)
						return integerValue(newType, intVal);
				}
			}
		}
		return castValue(newType, value);
	}

	// private Value evaluateAndorOr(Operator operator, ExpressionNode expr1,
	// ExpressionNode expr2) throws SyntaxException {
	// boolean isAnd = operator == Operator.LAND;
	// Value v1 = evaluate(expr1);
	//
	// return null;
	// }

	/**
	 * Evaluates plus in case where both types are integers. NOTE types of these
	 * values must be converted types from expression.
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	private Value evalBinaryIntegerOp(Operator operator, Value a1, Value a2) {
		IntegerType type1 = (IntegerType) a1.getType();
		IntegerType type;

		switch (operator) {
		case PLUS:
		case TIMES:
		case MINUS:
		case DIV:
		case SHIFTLEFT:
		case SHIFTRIGHT:
			type = type1;
			break;
		case EQUALS:
		case LTE:
		case GTE:
		case NEQ:
		case LT:
		case GT:
			type = SINT;
			break;
		default:
			throw new RuntimeException("This method should not be called with operator " + operator);
		}
		if (a1.equals(a2)) {
			switch (operator) {
			case EQUALS:
			case LTE:
			case GTE:
				return SINT_ONE;
			case NEQ:
			case LT:
			case GT:
				return SINT_ZERO;
			default:
			}
		}
		if (a1 instanceof IntegerValue && a2 instanceof IntegerValue) {
			BigInteger v1 = ((IntegerValue) a1).getIntegerValue();
			BigInteger v2 = ((IntegerValue) a2).getIntegerValue();
			BigInteger v3 = null;

			switch (operator) {
			case PLUS:
				v3 = v1.add(v2);
				break;
			case TIMES:
				v3 = v1.multiply(v2);
				break;
			case MINUS:
				v3 = v1.subtract(v2);
				break;
			case DIV:
				v3 = v1.divide(v2);
				break;
			case EQUALS:
				v3 = BigInteger.ZERO;
				break;
			case NEQ:
				v3 = BigInteger.ONE;
				break;
			case LT:
				v3 = v1.compareTo(v2) < 0 ? BigInteger.ONE : BigInteger.ZERO;
				break;
			case GT:
				v3 = v1.compareTo(v2) > 0 ? BigInteger.ONE : BigInteger.ZERO;
				break;
			case LTE:
				v3 = v1.compareTo(v2) <= 0 ? BigInteger.ONE : BigInteger.ZERO;
				break;
			case GTE:
				v3 = v1.compareTo(v2) >= 0 ? BigInteger.ONE : BigInteger.ZERO;
				break;
			case SHIFTLEFT:
			case SHIFTRIGHT:
				if (v1.signum() >= 0 && v2.signum() >= 0 && MAX_JINT.compareTo(v2) >= 0) {
					int v2small = v2.intValue();

					if (operator == Operator.SHIFTLEFT)
						v3 = v1.shiftLeft(v2small);
					else
						v3 = v1.shiftRight(v2small);
				}
				break;
			default:
			}
			if (v3 != null && type instanceof StandardSignedIntegerType
					&& ((StandardSignedIntegerType) type).inMinimumRange(v3)
					|| type instanceof StandardUnsignedIntegerType
							&& ((StandardUnsignedIntegerType) type).inMinimumRange(v3)) {
				return integerValue(type, v3);
			}
		}
		return operatorValue(type, operator, new Value[] { a1, a2 });
	}

	private Value evaluateMemberAccess(Value structureOrUnionValue, Field field) throws UnsourcedException {
		if (structureOrUnionValue instanceof StructureValue) {
			return ((StructureValue) structureOrUnionValue).getMember(field);
		}
		if (structureOrUnionValue instanceof UnionValue) {
			UnionValue unionValue = (UnionValue) structureOrUnionValue;
			Field unionField = unionValue.getField();

			if (!unionField.equals(field))
				throw new UnsourcedException(
						"Union value field differs from requested field:\n" + unionField + "\n" + field);
			return unionValue.getMemberValue();
		}
		// TODO: need to create a new dot value.
		throw new UnsourcedException("Cannot evaluate structure or union value: " + structureOrUnionValue);
	}

	private SyntaxException error(String message, ASTNode node) {
		return new SyntaxException(message, node.getSource());
	}

	private SyntaxException error(UnsourcedException e, ASTNode node) {
		return new SyntaxException(e, node.getSource());
	}

}
