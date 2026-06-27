package dev.civl.mc.transform.common;

import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.DIV;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.DIVEQ;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.EQUALS;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.MINUS;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.MINUSEQ;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.NEQ;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.PLUS;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.PLUSEQ;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.TIMES;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.TIMESEQ;
import static dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator.UNARYMINUS;
import static dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind.BOOL;
import static dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind.DOUBLE_COMPLEX;
import static dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind.FLOAT_COMPLEX;
import static dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind.LONG_DOUBLE_COMPLEX;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.conversion.IF.Conversion;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.PairNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.compound.CompoundInitializerNode;
import dev.civl.abc.ast.node.IF.compound.DesignationNode;
import dev.civl.abc.ast.node.IF.declaration.InitializerNode;
import dev.civl.abc.ast.node.IF.expression.CastNode;
import dev.civl.abc.ast.node.IF.expression.CompoundLiteralNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.expression.FloatingConstantNode;
import dev.civl.abc.ast.node.IF.expression.FunctionCallNode;
import dev.civl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.IfNode;
import dev.civl.abc.ast.node.IF.statement.LoopNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.ast.node.IF.type.TypedefNameNode;
import dev.civl.abc.ast.type.IF.ArithmeticType;
import dev.civl.abc.ast.type.IF.AtomicType;
import dev.civl.abc.ast.type.IF.FloatingType.FloatKind;
import dev.civl.abc.ast.type.IF.QualifiedObjectType;
import dev.civl.abc.ast.type.IF.StandardBasicType;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.ast.type.IF.Type;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.IF.ComplexFloatingValue;
import dev.civl.abc.ast.value.IF.RealFloatingValue;
import dev.civl.abc.ast.value.IF.ValueFactory.Answer;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.mc.config.IF.CIVLConstants;
import dev.civl.mc.model.IF.CIVLInternalException;

public class ComplexWorker extends BaseWorker {

	private static String COMPLEX_H = "complex.h";

	private static String COMPLEX_CVL = "complex.cvl";

	private static String MATH_H = "math.h";

//	private static String MATH_H_MACRO = "_MATH_";
//
//	private static String COMPLEX_H_MACRO = "_COMPLEX_";

	private TypeFactory typeFactory;

	/**
	 * The last index of a child node of root belong to math.h, or -1 if no math.h
	 * is present. This is needed so that complex.cvl can be inserted after math.h.
	 */
	private int mathHIndex = -1;

	public ComplexWorker(String transformerName, ASTFactory astFactory) {
		super(transformerName, astFactory);
		typeFactory = astFactory.getTypeFactory();
	}

	/**
	 * Is the given type one of the 3 native C complex types: double _Complex, float
	 * _Complex, or long double _Complex?
	 * 
	 * @param type the type, which may be null
	 * @return {@code} true iff {@code type} is one of the 3 native C complex types
	 */
	private boolean isComplex(Type type) {
		if (type == null)
			return false;
		switch (type.kind()) {
		case BASIC: {
			BasicTypeKind btk = ((StandardBasicType) type).getBasicTypeKind();
			return btk == DOUBLE_COMPLEX || btk == FLOAT_COMPLEX || btk == LONG_DOUBLE_COMPLEX;
		}
		case QUALIFIED:
			return isComplex(((QualifiedObjectType) type).getBaseType());
		case ATOMIC:
			return isComplex(((AtomicType) type).getBaseType());
		default:
			return false;
		}
	}

	private boolean isBool(Type type) {
		if (type == null)
			return false;
		switch (type.kind()) {
		case BASIC: {
			BasicTypeKind btk = ((StandardBasicType) type).getBasicTypeKind();
			return btk == BOOL;
		}
		case QUALIFIED:
			return isBool(((QualifiedObjectType) type).getBaseType());
		case ATOMIC:
			return isBool(((AtomicType) type).getBaseType());
		default:
			return false;
		}
	}

	private boolean isReal(Type type) {
		if (type == null)
			return false;
		if (type instanceof ArithmeticType)
			return ((ArithmeticType) type).inRealDomain();
		if (type instanceof QualifiedObjectType)
			return isReal(((QualifiedObjectType) type).getBaseType());
		if (type instanceof AtomicType)
			return isReal(((AtomicType) type).getBaseType());
		return false;
	}

	/**
	 * Returns the basic type kind of a complex type.
	 * 
	 * @param complexType one of the complex types
	 * @return the basic type kind of the given type
	 */
	private BasicTypeKind kind(Type complexType) {
		switch (complexType.kind()) {
		case BASIC:
			return ((StandardBasicType) complexType).getBasicTypeKind();
		case QUALIFIED:
			return kind(((QualifiedObjectType) complexType).getBaseType());
		case ATOMIC:
			return kind(((AtomicType) complexType).getBaseType());
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Given a type node for one of the complex types, returns a new type node for
	 * the corresponding CIVL complex type: one of the $*_complex types. Type
	 * qualifiers are preserved.
	 * 
	 * Note: a type node for a complex type must be one of the following: a
	 * TypedefNameNode, BasicTypeNode, or AtomicTypeNode.
	 * 
	 * @param kind   a basic type kind, one of *_COMPLEX
	 * @param source source for the type node for the new node
	 * @return new typedef name node
	 */
	private TypeNode replacementTypeNode(TypeNode complexTypeNode) {
		Source source = complexTypeNode.getSource();
		IdentifierNode idn;
		switch (kind(complexTypeNode.getType())) {
		case DOUBLE_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$double_complex");
			break;
		case FLOAT_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$float_complex");
			break;
		case LONG_DOUBLE_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$ldouble_complex");
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		TypedefNameNode newtn = nodeFactory.newTypedefNameNode(idn, null);
		newtn.setAtomicQualified(complexTypeNode.isAtomicQualified());
		newtn.setConstQualified(complexTypeNode.isConstQualified());
		newtn.setRestrictQualified(complexTypeNode.isRestrictQualified());
		newtn.setVolatileQualified(complexTypeNode.isVolatileQualified());
		newtn.setInputQualified(complexTypeNode.isInputQualified());
		newtn.setOutputQualified(complexTypeNode.isOutputQualified());
		return newtn;
	}

	private TypeNode replacementTypeNode(Type complexType, Source source) {
		IdentifierNode idn;
		switch (kind(complexType)) {
		case DOUBLE_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$double_complex");
			break;
		case FLOAT_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$float_complex");
			break;
		case LONG_DOUBLE_COMPLEX:
			idn = nodeFactory.newIdentifierNode(source, "$ldouble_complex");
			break;
		default:
			throw new RuntimeException("unreachable");
		}

		TypedefNameNode newtn = nodeFactory.newTypedefNameNode(idn, null);

		// Note: _Atomic(type) is a type specifier, represented by an AtomicType and an
		// AtomicTypeNode.
		// _Atomic ... is a type qualifier, represented by an AtomicType and an
		// arbitrary TypeNode with the atomic-qualified bit set.
		switch (complexType.kind()) {
		case BASIC:
			return newtn;
		case QUALIFIED: {
			QualifiedObjectType qot = (QualifiedObjectType) complexType;
			newtn.setAtomicQualified(false);
			newtn.setConstQualified(qot.isConstQualified());
			newtn.setRestrictQualified(qot.isRestrictQualified());
			newtn.setVolatileQualified(qot.isVolatileQualified());
			newtn.setInputQualified(qot.isInputQualified());
			newtn.setOutputQualified(qot.isOutputQualified());
			return newtn;
		}
		case ATOMIC:
			// choice: AtomicTypeNode, or just qualify the typedef name node.
			return nodeFactory.newAtomicTypeNode(source, newtn);
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Is the operator one of the assignment operators that combines an arithmetic
	 * operation with assignment, possibly on complex numbers: +=, -=, *=, or /=.
	 * 
	 * @param op any Operator
	 * @return {@code true} iff {@code op} is one of the 4 operators above
	 */
	private boolean isAssignOp(Operator op) {
		return op == PLUSEQ || op == MINUSEQ || op == TIMESEQ || op == DIVEQ;
	}

	/**
	 * Is the operator one that performs an arithmetic operation that could possibly
	 * consume a complex type. This includes the assignment operators +=, -=, etc.,
	 * as well as the pure operators +,-, etc. It includes == and !=, and the unary
	 * minus operator as well.
	 * 
	 * @param op any Operator
	 * @return {@code true} iff {@code op} is an operator
	 */
	private boolean isArithmeticOp(Operator op) {
		return isAssignOp(op) || op == PLUS || op == MINUS || op == TIMES || op == DIV || op == EQUALS || op == NEQ
				|| op == UNARYMINUS;
	}

	private ExpressionNode realToComplex(ExpressionNode realExpr, Type complexType) {
		// Result will look like: ($*_complex){ realExpr, 0 }
		// The int 0 will be converted to the appropriate real type.
		// Note: we already checked all the static type properties before getting to
		// this Transformer, so we can assume they are all good.
		Source source = realExpr.getSource();
		ExpressionNode zeroNode = nodeFactory.newIntConstantNode(source, 0);
		realExpr.remove();
		PairNode<DesignationNode, InitializerNode> realPair = nodeFactory.newPairNode(source, null, realExpr),
				imagPair = nodeFactory.newPairNode(source, null, zeroNode);
		TypeNode typeNode = replacementTypeNode(complexType, source);
		CompoundInitializerNode cin = nodeFactory.newCompoundInitializerNode(source, Arrays.asList(realPair, imagPair));
		CompoundLiteralNode cln = nodeFactory.newCompoundLiteralNode(source, typeNode, cin);
		cln.setInitialType(complexType);
		return cln;
	}

	private ExpressionNode complexToBool(ExpressionNode node, Type complexType) {
		Source source = node.getSource();
		String name;

		switch (kind(complexType)) {
		case FLOAT_COMPLEX:
			name = "$cfloat2bool";
			break;
		case DOUBLE_COMPLEX:
			name = "$cdouble2bool";
			break;
		case LONG_DOUBLE_COMPLEX:
			name = "$cldouble2bool";
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		node.remove();
		FunctionCallNode fcn = nodeFactory.newFunctionCallNode(source,
				nodeFactory.newIdentifierExpressionNode(source, nodeFactory.newIdentifierNode(source, name)),
				Arrays.asList(node), null);
		fcn.setInitialType(typeFactory.basicType(BOOL));
		return fcn;
	}

	private ExpressionNode complexToComplex(ExpressionNode node, Type oldComplexType, Type newComplexType) {
		Source source = node.getSource();
		BasicTypeKind kind1 = kind(oldComplexType), kind2 = kind(newComplexType);

		if (kind1 == kind2)
			return node;

		String name;

		switch (kind(oldComplexType)) {
		case FLOAT_COMPLEX:
			name = kind2 == DOUBLE_COMPLEX ? "$cfloat2double" : "$cfloat2ldouble";
			break;
		case DOUBLE_COMPLEX:
			name = kind2 == FLOAT_COMPLEX ? "$cdouble2float" : "$cdouble2ldouble";
			break;
		case LONG_DOUBLE_COMPLEX:
			name = kind2 == FLOAT_COMPLEX ? "$cldouble2float" : "$cldouble2double";
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		node.remove();
		FunctionCallNode fcn = nodeFactory.newFunctionCallNode(source,
				nodeFactory.newIdentifierExpressionNode(source, nodeFactory.newIdentifierNode(source, name)),
				Arrays.asList(node), null);
		fcn.setInitialType(newComplexType);
		return fcn;
	}

	private ExpressionNode complexToReal(ExpressionNode node, Type realType) {
		Source source = node.getSource();
		node.remove();
		ExpressionNode result = nodeFactory.newDotNode(source, node, nodeFactory.newIdentifierNode(source, "real"));
		result.setInitialType(realType);
		return result;
	}

	private ExpressionNode convert(ExpressionNode node, Type oldType, Type newType) {
		if (isComplex(oldType)) {
			if (isBool(newType))
				return complexToBool(node, oldType);
			else if (isReal(newType))
				return complexToReal(node, newType);
			else if (isComplex(newType))
				return complexToComplex(node, oldType, newType);
			else
				throw new CIVLInternalException("No conversion from " + oldType + " to " + newType, node.getSource());
		} else if (isComplex(newType)) { // non-complex -> complex
			return realToComplex(node, newType);
		}
		// conversion does not involve complex type: ignore
		return node;
	}

	private ExpressionNode convertLiteral(FloatingConstantNode fcn) {
		assert fcn.isComplex();
		Source source = fcn.getSource();
		ComplexFloatingValue value = (ComplexFloatingValue) fcn.getConstantValue();
		RealFloatingValue realPart = value.getRealPart(), imagPart = value.getImaginaryPart();
		assert realPart.isZero() == Answer.YES;
		FloatingConstantNode imagNode = nodeFactory.newFloatingConstantNode(source, fcn.getStringRepresentation(),
				fcn.wholePart(), fcn.fractionPart(), fcn.exponent(), imagPart);
		String zeroString, typeString;
		FloatKind fkind = realPart.getType().getFloatKind();
		if (fkind == FloatKind.DOUBLE) {
			zeroString = "0.0";
			typeString = "$double_complex";
		} else if (fkind == FloatKind.FLOAT) {
			zeroString = "0.0f";
			typeString = "$float_complex";
		} else if (fkind == FloatKind.LONG_DOUBLE) {
			zeroString = "0.0l";
			typeString = "$ldouble_complex";
		} else {
			throw new RuntimeException("unreachable");
		}
		FloatingConstantNode zeroNode;
		try {
			zeroNode = nodeFactory.newFloatingConstantNode(source, zeroString);
		} catch (SyntaxException e) {
			throw new CIVLInternalException("Syntax error parsing zero constant: " + zeroString, source);
		}
		PairNode<DesignationNode, InitializerNode> realPair = nodeFactory.newPairNode(source, null, zeroNode),
				imagPair = nodeFactory.newPairNode(source, null, imagNode);
		IdentifierNode idNode = nodeFactory.newIdentifierNode(source, typeString);
		TypedefNameNode tdnn = nodeFactory.newTypedefNameNode(idNode, null);
		CompoundInitializerNode cin = nodeFactory.newCompoundInitializerNode(source, Arrays.asList(realPair, imagPair));
		CompoundLiteralNode cln = nodeFactory.newCompoundLiteralNode(source, tdnn, cin);
		cln.setInitialType(fcn.getInitialType());
		return cln;
	}

	/**
	 * Given an operator node for one of the operators satisfying method
	 * {@link #isArithmeticOp(Operator)}, produces the replacement node using the
	 * appropriate function call. For pure operators, the replacement node will be a
	 * function call node. For operators that combine an assignment with the
	 * operation (e.g., "+="), the replacement node will be an assignment node in
	 * which the second argument is the function call node.
	 * 
	 * @param opNode an operator node for one of the operators satisfying
	 *               {@link #isArithmeticOp(Operator)}
	 * @return the replacement node
	 */
	private ExpressionNode arithmeticReplacement(OperatorNode opNode) {
		Operator op = opNode.getOperator();
		ExpressionNode arg0 = opNode.getArgument(0);
		Source source = opNode.getSource();
		String funName;
		switch (op) {
		case PLUS:
			funName = "$cadd";
			break;
		case PLUSEQ:
			funName = "$caddeq";
			break;
		case MINUS:
			funName = "$csub";
			break;
		case MINUSEQ:
			funName = "$csubeq";
			break;
		case TIMES:
			funName = "$cmul";
			break;
		case TIMESEQ:
			funName = "$cmuleq";
			break;
		case DIV:
			funName = "$cdiv";
			break;
		case DIVEQ:
			funName = "$cdiveq";
			break;
		case EQUALS:
			funName = "$ceq";
			break;
		case NEQ:
			funName = "$cneq";
			break;
		case UNARYMINUS:
			funName = "$cneg";
			break;
		default:
			throw new RuntimeException("unreachable");
		}

		BasicTypeKind kind = kind(arg0.getConvertedType());
		if (kind == FLOAT_COMPLEX)
			funName += "f";
		else if (kind == LONG_DOUBLE_COMPLEX)
			funName += "l";
		IdentifierNode funNameNode = nodeFactory.newIdentifierNode(source, funName);
		IdentifierExpressionNode funExprNode = nodeFactory.newIdentifierExpressionNode(source, funNameNode);

		// Pattern: a+b ==> fun(a,b). a+=b ==> fun(&a,b).
		// Note: have to use a pointer &a. Alternatives would require creating two
		// copies of a, which would be wrong if evaluation of a has side-effects, e.g.,
		// if a is array[++i].

		List<ExpressionNode> argList = new LinkedList<>();
		int numArgs = opNode.getNumberOfArguments();

		for (int i = 0; i < numArgs; i++) {
			ExpressionNode arg = opNode.getArgument(i);
			arg.remove();
			argList.add(arg);
		}
		if (isAssignOp(op)) {
			assert numArgs == 2;
			arg0 = argList.get(0);
			argList.set(0, nodeFactory.newOperatorNode(arg0.getSource(), Operator.ADDRESSOF, arg0));
		}
		ExpressionNode result = nodeFactory.newFunctionCallNode(source, funExprNode, argList, null);
		result.setInitialType(opNode.getInitialType());
		return result;
	}

	/**
	 * Replaces C complex primitives with CIVL-C structure primitives in complex.cvh
	 * and complex.cvl.
	 * 
	 * typeNode: just replace type node
	 * 
	 * constantNode "3if" followed by conversion to double complex: first
	 * convertLiteral then apply conversions.
	 * 
	 * constantNode "1" converted to a complex type: apply conversions
	 * 
	 * operator node "a+b" followed by conversions: first arithmetic replacement,
	 * then apply conversions
	 * 
	 * cast node to or from complex: convert cast, then apply additional implicit
	 * conversions
	 * 
	 * Expression node: first translate to new node, then apply conversions.
	 * 
	 * @param node the root of the tree in which replacement will occur
	 * @return {@code true} iff any change was made to the tree
	 */
	private boolean process(ASTNode node) {
		boolean change = false;
		int numChildren = node.numChildren();

		for (int i = 0; i < numChildren; i++) {
			ASTNode child = node.child(i);
			if (child != null && process(child))
				change = true;
		}

		ASTNode parent = node.parent();
		int idx = node.childIndex();

		if (node instanceof TypeNode) {
			// for reasons I don't understand, a TypedefNameNode may
			// contain qualifiers but those are not present in its Type.
			Type type = ((TypeNode) node).getType();
			if (type != null && isComplex(type)) {
				node = replacementTypeNode((TypeNode) node);
				assert node != null;
				parent.setChild(idx, node);
				change = true;
			}
		} else if (node instanceof ExpressionNode) {
			// first, save the conversions:
			int numConversions = ((ExpressionNode) node).getNumConversions();
			Conversion[] conversions = new Conversion[numConversions];
			for (int i = 0; i < numConversions; i++)
				conversions[i] = ((ExpressionNode) node).getConversion(i);

			if (node instanceof OperatorNode) {
				OperatorNode opNode = (OperatorNode) node;
				if (isArithmeticOp(opNode.getOperator()) && isComplex(opNode.getArgument(0).getConvertedType())) {
					node = arithmeticReplacement(opNode);
					assert node != null;
					parent.setChild(idx, node);
					change = true;
				}
			} else if (node instanceof FloatingConstantNode) {
				FloatingConstantNode fcn = (FloatingConstantNode) node;
				if (fcn.isComplex()) {
					node = convertLiteral(fcn);
					assert node != null;
					parent.setChild(idx, node);
					change = true;
				}
			} else if (node instanceof CastNode) {
				ExpressionNode arg = ((CastNode) node).getArgument();
				Type oldType = arg.getConvertedType();
				Type newType = ((CastNode) node).getInitialType();
				ExpressionNode tmp = convert(arg, oldType, newType);
				if (tmp != arg) {
					node = tmp;
					parent.setChild(idx, node);
					change = true;
				}
			}

			// now, apply the conversions:
			for (int i = 0; i < numConversions; i++) {
				Conversion cv = conversions[i];
				ExpressionNode tmp = convert((ExpressionNode) node, cv.getOldType(), cv.getNewType());
				if (tmp != node) {
					node = tmp;
					parent.setChild(idx, node);
					change = true;
				}
			}
		} else if (node instanceof IfNode || node instanceof LoopNode) {
			ExpressionNode cond = node instanceof IfNode ? ((IfNode) node).getCondition()
					: ((LoopNode) node).getCondition();
			int condIdx = cond.childIndex();
			Type type = cond.getType();
			if (isComplex(type)) {
				cond = complexToBool(cond, type);
				node.setChild(condIdx, cond);
				change = true;
			}
		}
		return change;
	}

	@Override
	protected AST transformCore(AST ast) throws SyntaxException {
		SequenceNode<BlockItemNode> root = ast.getRootNode();
		boolean needsTransform = false;

		for (ASTNode node = root; !needsTransform && node != null; node = node.nextDFS()) {
			if (node instanceof ExpressionNode) {
				ExpressionNode expr = (ExpressionNode) node;
				if (isComplex(expr.getInitialType())) {
					needsTransform = true;
					break;
				}
				if (!needsTransform) {
					int numConversions = expr.getNumConversions();
					for (int i = 0; !needsTransform && i < numConversions; i++) {
						if (isComplex(expr.getConversion(i).getNewType())) {
							needsTransform = true;
							break;
						}
					}
				}
			} else if (node instanceof TypeNode) {
				if (isComplex(((TypeNode) node).getType())) {
					needsTransform = true;
					break;
				}
			}
		}

		if (!needsTransform)
			return ast;

		boolean isWhole = ast.isWholeProgram();
		Collection<SourceFile> sourceFiles = ast.getSourceFiles();
		boolean hasComplexCvl = false;

		// remove all items from complex.h...
		ast.release();
		int nchildren = root.numChildren();
		for (int i = 0; i < nchildren; i++) {
			BlockItemNode node = root.getSequenceChild(i);
			Source source = node.getSource();
			String sourceName = source.getFirstToken().getSourceFile().getName();
			if (COMPLEX_H.equals(sourceName)) {
				root.removeChild(i);
			} else if (COMPLEX_CVL.equals(sourceName)) {
				hasComplexCvl = true;
			} else if (MATH_H.equals(sourceName)) {
				mathHIndex = i;
			}
		}
		// TODO: this only sets the child to null. get rid of the null gaps?

		process(root);
		if (!hasComplexCvl) {
			// remove old math.h if present:
			if (mathHIndex >= 0) {
				nchildren = root.numChildren();
				for (int i = 0; i < nchildren; i++) {
					ASTNode node = root.child(i);
					if (node != null && MATH_H.equals(node.getSource().getFirstToken().getSourceFile().getName()))
						root.removeChild(i);
				}
			}
			// insert complex.cvl (which includes math.h and complex.cvh) at beginning:
			File file = new File(CIVLConstants.CIVL_LIB_SRC_PATH, COMPLEX_CVL);
			AST lib = this.parseSystemLibrary(file, EMPTY_MACRO_MAP);
			SequenceNode<BlockItemNode> libRoot = lib.getRootNode();
			lib.release();
			List<BlockItemNode> libNodes = new LinkedList<BlockItemNode>();
			for (BlockItemNode node : libRoot) {
				node.remove();
				libNodes.add(node);
			}
			root.insertChildren(0, libNodes);
		}
		ast = astFactory.newAST(root, sourceFiles, isWhole);
		return ast;
	}
}
