package dev.civl.mc.model.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.statement.MallocStatement;
import dev.civl.mc.model.IF.type.CIVLArrayType;
import dev.civl.mc.model.IF.type.CIVLBundleType;
import dev.civl.mc.model.IF.type.CIVLCompleteArrayType;
import dev.civl.mc.model.IF.type.CIVLCompleteDomainType;
import dev.civl.mc.model.IF.type.CIVLDomainType;
import dev.civl.mc.model.IF.type.CIVLEnumType;
import dev.civl.mc.model.IF.type.CIVLFunctionType;
import dev.civl.mc.model.IF.type.CIVLHeapType;
import dev.civl.mc.model.IF.type.CIVLMemType;
import dev.civl.mc.model.IF.type.CIVLPointerType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType.PrimitiveTypeKind;
import dev.civl.mc.model.IF.type.CIVLRegularRangeType;
import dev.civl.mc.model.IF.type.CIVLScopeType;
import dev.civl.mc.model.IF.type.CIVLSetType;
import dev.civl.mc.model.IF.type.CIVLStateType;
import dev.civl.mc.model.IF.type.CIVLStructOrUnionType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.type.CIVLType.TypeKind;
import dev.civl.mc.model.IF.type.StructOrUnionField;
import dev.civl.mc.model.common.type.CommonArrayType;
import dev.civl.mc.model.common.type.CommonBundleType;
import dev.civl.mc.model.common.type.CommonCIVLStateType;
import dev.civl.mc.model.common.type.CommonCompleteArrayType;
import dev.civl.mc.model.common.type.CommonCompleteDomainType;
import dev.civl.mc.model.common.type.CommonDomainType;
import dev.civl.mc.model.common.type.CommonEnumType;
import dev.civl.mc.model.common.type.CommonFunctionType;
import dev.civl.mc.model.common.type.CommonHeapType;
import dev.civl.mc.model.common.type.CommonMemType;
import dev.civl.mc.model.common.type.CommonPointerType;
import dev.civl.mc.model.common.type.CommonPrimitiveType;
import dev.civl.mc.model.common.type.CommonRegularRangeType;
import dev.civl.mc.model.common.type.CommonScopeType;
import dev.civl.mc.model.common.type.CommonSetType;
import dev.civl.mc.model.common.type.CommonStructOrUnionField;
import dev.civl.mc.model.common.type.CommonStructOrUnionType;
import dev.civl.mc.util.IF.Singleton;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.NumericSymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.object.StringObject;
import dev.civl.sarl.IF.type.SymbolicArrayType;
import dev.civl.sarl.IF.type.SymbolicCompleteArrayType;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicType.SymbolicTypeKind;
import dev.civl.sarl.IF.type.SymbolicUnionType;

public class CommonCIVLTypeFactory implements CIVLTypeFactory {

	/**
	 * The name of the dynamic scope type.
	 */
	private static final String UNINTERPRETED_SCOPE_TYPE_NAME = "scope";

	/**
	 * The name of an uninterpreted type that represents a unique ID of a state:
	 */
	private static final String UNINTERPRETED_STATE_TYPE_NAME = "$state_key";

	/* *********************** Package-private Fields ********************** */

	/**
	 * The unique boolean type used in the system.
	 */
	CIVLPrimitiveType booleanType;

	/**
	 * The CIVL bundle type, which is unique for a given CIVL model. A bundle
	 * type is a union type of all types referenced by a given CIVL model. A
	 * bundle type needs to be completed at the end of the construction of the
	 * model.
	 */
	CIVLBundleType bundleType;

	/**
	 * The unique char type used in the system.
	 */
	CIVLPrimitiveType charType;

	/**
	 * The CIVL domain type.
	 */
	CIVLDomainType domainType = null;

	/**
	 * The unique dynamic symbolic type used in the system.
	 */
	SymbolicTupleType dynamicSymbolicType;

	/**
	 * The unique dynamic type used in the system.
	 */
	CIVLPrimitiveType dynamicType;

	/**
	 * The unique symbolic function pointer type used in the system. Function
	 * pointer type need to be different from pointer type, because there is
	 * analysis particularly for pointers, like heap object reachability,
	 * reachable memory units, etc.
	 */
	SymbolicTupleType functionPointerSymbolicType;

	Map<SymbolicType, CIVLPrimitiveType> primitiveTypeMap = new HashMap<>();

	/**
	 * The map of handled object types and their field ID in the heap type. Each
	 * handled object type referenced in the model should have the corresponding
	 * heap field, because it will be allocated in the heap.
	 */
	Map<CIVLType, Integer> heapFieldTypeMap = new HashMap<>();

	/**
	 * A map from identifier expressions of symbolic types to their
	 * corresponding {@link CIVLType}s. An identifier expression of a symbolic
	 * type is obtained through
	 * {@link #expressionOfType(CIVLType, SymbolicType)}.
	 */
	Map<SymbolicExpression, CIVLType> dynamicTypeID2staticType = new HashMap<>();

	/**
	 * The CIVL heap type, which is unique for a given CIVL model. A heap type
	 * is a struct type of all types appearing in a malloc statement, plus all
	 * handled object types used by the model. A heap type needs to be completed
	 * at the end of the construction of the model.
	 */
	CIVLHeapType heapType;

	/**
	 * The unique integer type used in the system.
	 */
	CIVLPrimitiveType integerType;

	/**
	 * The unique symbolic pointer type used in the system.
	 */
	SymbolicTupleType pointerSymbolicType;

	/**
	 * The unique symbolic process type used in the system.
	 */
	SymbolicTupleType processSymbolicType;

	/**
	 * The unique process type used in the system.
	 */
	CIVLPrimitiveType processType;

	/**
	 * The dynamic type of {@link #stateType}
	 */
	final SymbolicTupleType stateSymbolicType;

	/**
	 * The $state type in CIVL-C language
	 */
	CIVLStateType stateType;

	/**
	 * The regular range type, which is (int, int, int), corresponding to (low,
	 * high, step).
	 */
	CIVLRegularRangeType rangeType = null;

	/**
	 * The unique real type used in the system.
	 */
	CIVLPrimitiveType realType;

	/**
	 * The unique scope type used in the system.
	 */
	CIVLScopeType scopeType;

	/**
	 * The unique symbolic scope type used in the system.
	 */
	SymbolicType scopeSymbolicType;

	/**
	 * The map of types of system libraries, e.g., $gcomm/$comm for comm, $file
	 * for stdio, $gbarrier/$barrier for concurrency, etc. A map is used so that
	 * in the future if new types are introduced by system libraries, the
	 * interface won't have to be changed.
	 */
	Map<String, CIVLType> systemTypes = new HashMap<>();

	/**
	 * The unique SARL symbolic universe used in the system.
	 */
	SymbolicUniverse universe;

	/**
	 * The unique void type used in the system.
	 */
	CIVLPrimitiveType voidType;

	SymbolicType voidSymbolicType;

	/**
	 * The dynamic type of {@link CIVLMemType}, whose definition is given by
	 * {@link #dynamicMemType()}
	 */
	SymbolicTupleType dynamicMemType;

	/**
	 * The uninterpreted function sizeof.
	 */
	private SymbolicExpression sizeofFunction;

	CIVLConfiguration config;

	/* *************************** Private Fields ************************** */

	/**
	 * The "sudo" source for objects created during translation.
	 */
	private CIVLSource systemSource = new SystemCIVLSource();

	/* **************************** Constructor **************************** */

	/**
	 * Constructs a new instance of CIVL type factory.
	 * 
	 * @param universe
	 *            The symbolic universe to be used.
	 */
	public CommonCIVLTypeFactory(SymbolicUniverse universe,
			CIVLConfiguration config) {
		Iterable<SymbolicType> intTypeSingleton = new Singleton<SymbolicType>(
				universe.integerType());
		LinkedList<SymbolicType> pointerComponents = new LinkedList<>();
		LinkedList<SymbolicType> fpointerComponents = new LinkedList<>();

		this.config = config;
		this.universe = universe;
		scopeType = (CIVLScopeType) primitiveType(PrimitiveTypeKind.SCOPE,
				universe.symbolicUninterpretedType(
						UNINTERPRETED_SCOPE_TYPE_NAME));
		scopeSymbolicType = scopeType.getDynamicType(universe);
		processSymbolicType = universe
				.tupleType(universe.stringObject("process"), intTypeSingleton);
		processType = primitiveType(PrimitiveTypeKind.PROCESS,
				processSymbolicType);
		/*
		 * A CIVL-C $state type is a tuple of a unique uninterpreted type which
		 * refers to a state s and an integer array which maps scope values from
		 * the state s to the current state.
		 */
		stateSymbolicType = universe.tupleType(universe.stringObject("$state"),
				Arrays.asList(
						universe.symbolicUninterpretedType(
								UNINTERPRETED_STATE_TYPE_NAME),
						universe.arrayType(scopeSymbolicType)));
		stateType = (CIVLStateType) primitiveType(PrimitiveTypeKind.STATE,
				stateSymbolicType);
		dynamicSymbolicType = universe.tupleType(
				universe.stringObject("dynamicType"), intTypeSingleton);
		dynamicType = primitiveType(PrimitiveTypeKind.DYNAMIC,
				dynamicSymbolicType);
		sizeofFunction = universe.symbolicConstant(
				ModelConfiguration
						.getSizeofNonPrimitiveTypeFunctionName(universe),
				universe.functionType(
						new Singleton<SymbolicType>(dynamicSymbolicType),
						universe.integerType()));
		pointerComponents.add(scopeType.getDynamicType(universe));
		pointerComponents.add(universe.integerType());
		pointerComponents.add(universe.referenceType());
		pointerSymbolicType = universe
				.tupleType(universe.stringObject("pointer"), pointerComponents);
		fpointerComponents.add(scopeType.getDynamicType(universe));
		fpointerComponents.add(universe.integerType());
		functionPointerSymbolicType = universe.tupleType(
				universe.stringObject("fpointer"), fpointerComponents);
		this.voidSymbolicType = universe
				.tupleType(universe.stringObject("void"), new ArrayList<>());
		this.voidType = primitiveType(PrimitiveTypeKind.VOID, voidSymbolicType);
		this.integerType = primitiveType(PrimitiveTypeKind.INT,
				universe.integerType());
		this.booleanType = primitiveType(PrimitiveTypeKind.BOOL,
				universe.booleanType());
		this.realType = primitiveType(PrimitiveTypeKind.REAL,
				universe.realType());
		this.charType = primitiveType(PrimitiveTypeKind.CHAR,
				universe.characterType());
		this.rangeType = new CommonRegularRangeType(
				new CommonIdentifier(this.systemSource,
						universe.stringObject("$regular_range")),
				universe, integerType);
		this.systemTypes.put(ModelConfiguration.RANGE_TYPE, rangeType);

	}

	/* ******************* Methods from CIVLTypeFactory ******************** */

	/*
	 * *********************************************************************
	 * CIVL Types
	 * *********************************************************************
	 */

	@Override
	public void addHeapFieldObjectType(CIVLType type, int id) {
		this.heapFieldTypeMap.put(type, id);
	}

	@Override
	public int getHeapFieldId(CIVLType type) {
		if (this.heapFieldTypeMap.containsKey(type))
			return heapFieldTypeMap.get(type);
		return -1;
	}

	@Override
	public CIVLPrimitiveType booleanType() {
		return booleanType;
	}

	@Override
	public void completeHeapType(CIVLHeapType heapType,
			Collection<MallocStatement> mallocs) {
		SymbolicTupleType dynamicType = computeDynamicHeapType(mallocs);
		SymbolicExpression initialValue = computeInitialHeapValue(dynamicType);
		SymbolicExpression undefinedValue = universe.symbolicConstant(
				universe.stringObject("UNDEFINED"), dynamicType);

		heapType.complete(mallocs, dynamicType, initialValue, undefinedValue);
		this.heapType = heapType;
	}

	@Override
	public CIVLPrimitiveType charType() {
		return charType;
	}

	@Override
	public CIVLCompleteArrayType completeArrayType(CIVLType elementType,
			Expression extent) {
		return new CommonCompleteArrayType(elementType, extent);
	}

	@Override
	public CIVLPrimitiveType dynamicType() {
		return dynamicType;
	}

	@Override
	public CIVLEnumType enumType(String name,
			Map<String, BigInteger> valueMap) {
		return new CommonEnumType(name, valueMap, universe.integerType());
	}

	@Override
	public CIVLFunctionType functionType(CIVLType returnType,
			CIVLType[] paraTypes) {
		return new CommonFunctionType(returnType, paraTypes);
	}

	@Override
	public CIVLHeapType heapType(String name) {
		return new CommonHeapType(name);
	}

	@Override
	public CIVLArrayType incompleteArrayType(CIVLType baseType) {
		return new CommonArrayType(baseType);
	}

	@Override
	public CIVLPrimitiveType integerType() {
		return integerType;
	}

	@Override
	public CIVLBundleType initBundleType() {
		return new CommonBundleType();
	}

	@Override
	public CIVLPointerType pointerType(CIVLType baseType) {
		if (baseType.isFunction())
			return new CommonPointerType(baseType,
					this.functionPointerSymbolicType);
		return new CommonPointerType(baseType, pointerSymbolicType);
	}

	@Override
	public CIVLPrimitiveType processType() {
		return processType;
	}

	@Override
	public CIVLPrimitiveType realType() {
		return realType;
	}

	@Override
	public CIVLScopeType scopeType() {
		return scopeType;
	}

	@Override
	public StructOrUnionField structField(Identifier name, CIVLType type,
			int index, boolean isAnonymous,
			CIVLStructOrUnionType enclosingType) {
		return new CommonStructOrUnionField(name, type, index, isAnonymous,
				enclosingType);
	}

	@Override
	public CIVLStructOrUnionType structOrUnionType(Identifier name,
			boolean isStruct) {
		return new CommonStructOrUnionType(name, isStruct);
	}

	@Override
	public CIVLPrimitiveType voidType() {
		return voidType;
	}

	@Override
	public CIVLHeapType heapType() {
		return this.heapType;
	}

	@Override
	public CIVLBundleType bundleType() {
		return this.bundleType;
	}

	@Override
	public void addSystemType(String name, CIVLType type) {
		this.systemTypes.put(name, type);
	}

	@Override
	public CIVLType systemType(String name) {
		return systemTypes.get(name);
	}

	@Override
	public CIVLCompleteDomainType completeDomainType(CIVLType rangeType,
			int dim) {
		return new CommonCompleteDomainType(rangeType, dim);
	}

	/*
	 * *********************************************************************
	 * SARL symbolic types
	 * *********************************************************************
	 */

	@Override
	public SymbolicUnionType bundleSymbolicType() {
		return bundleType.getDynamicType(universe);
	}

	@Override
	public SymbolicTupleType dynamicSymbolicType() {
		return dynamicSymbolicType;
	}

	@Override
	public SymbolicTupleType functionPointerSymbolicType() {
		return functionPointerSymbolicType;
	}

	@Override
	public SymbolicType heapSymbolicType() {
		return heapType.getDynamicType(universe);
	}

	@Override
	public CIVLType rangeType() {
		return this.rangeType;
	}

	@Override
	public CIVLDomainType domainType(CIVLType rangeType) {
		if (this.domainType == null) {
			this.domainType = new CommonDomainType(rangeType);
		}
		return this.domainType;
	}

	@Override
	public SymbolicTupleType pointerSymbolicType() {
		return pointerSymbolicType;
	}

	@Override
	public SymbolicTupleType processSymbolicType() {
		return processSymbolicType;
	}

	@Override
	public SymbolicType scopeSymbolicType() {
		return scopeSymbolicType;
	}

	/*
	 * *********************************************************************
	 * Special handling
	 * *********************************************************************
	 */
	@Override
	public void completeBundleType(CIVLBundleType bundleType,
			List<CIVLType> eleTypes, Collection<SymbolicType> elementTypes) {
		LinkedList<SymbolicType> includedTypes = new LinkedList<SymbolicType>();
		SymbolicUnionType dynamicType;
		Set<SymbolicType> seenTypes = new HashSet<>();

		for (SymbolicType type : elementTypes) {
			if (!seenTypes.contains(type)) {
				includedTypes.add(type);
				seenTypes.add(type);
			}

			SymbolicType arrayType = universe.arrayType(type);

			if (!seenTypes.contains(arrayType)) {
				includedTypes.add(arrayType);
				seenTypes.add(arrayType);
			}
		}
		dynamicType = universe.unionType(universe.stringObject("$bundle"),
				includedTypes);
		bundleType.complete(eleTypes, includedTypes, dynamicType);
		this.bundleType = bundleType;
	}

	/* ************************** Private Methods ************************** */
	/**
	 * Computes the dynamic heap type, based on the list of malloc statements
	 * encountered in the model.
	 * 
	 * @param mallocStatements
	 *            The list of malloc statements in the model.
	 * @return The symbolic heap type.
	 */
	private SymbolicTupleType computeDynamicHeapType(
			Iterable<MallocStatement> mallocStatements) {
		LinkedList<SymbolicType> fieldTypes = new LinkedList<SymbolicType>();
		SymbolicTupleType result;

		for (MallocStatement statement : mallocStatements) {
			SymbolicType fieldType = universe
					.arrayType(statement.getDynamicObjectType());

			fieldTypes.add(fieldType);
		}
		result = universe.tupleType(universe.stringObject("$heap"), fieldTypes);
		return result;
	}

	/**
	 * Computes the symbolic initial value of a specified heap type.
	 * 
	 * @param heapDynamicType
	 *            The heap type to use.
	 * @return The initial value of the given help type.
	 */
	private SymbolicExpression computeInitialHeapValue(
			SymbolicTupleType heapDynamicType) {
		LinkedList<SymbolicExpression> fields = new LinkedList<SymbolicExpression>();
		SymbolicExpression result;

		for (SymbolicType fieldType : heapDynamicType.sequence()) {
			SymbolicArrayType arrayType = (SymbolicArrayType) fieldType;
			SymbolicType objectType = arrayType.elementType();
			SymbolicExpression emptyArray = universe.emptyArray(objectType);

			fields.add(emptyArray);
		}
		result = universe.tuple(heapDynamicType, fields);
		return result;
	}

	/**
	 * Creates an instance of a CIVL primitive type, including void, integer,
	 * boolean, real, char, scope, process, and dynamic types.
	 * 
	 * @param kind
	 *            The kind of the primitive type. See also
	 *            {@link PrimitiveTypeKind}.
	 * @param dynamicType
	 *            The corresponding SARL symbolic type of the CIVL primitive
	 *            type.
	 * @return The CIVL primitive type of the given kind.
	 */
	private CIVLPrimitiveType primitiveType(PrimitiveTypeKind kind,
			SymbolicType dynamicType) {
		CIVLPrimitiveType result;
		NumericExpression size = null;
		BooleanExpression fact = null;

		if (kind != PrimitiveTypeKind.VOID)
			size = sizeofPrimitiveType(kind);
		if (size == null)
			fact = universe.trueExpression();
		else
			fact = universe.lessThan(universe.zeroInt(), size);
		if (kind == PrimitiveTypeKind.SCOPE)
			result = new CommonScopeType(dynamicType, size, fact);
		else if (kind == PrimitiveTypeKind.STATE)
			result = new CommonCIVLStateType(dynamicType, size, fact);
		else
			result = new CommonPrimitiveType(kind, dynamicType, size, fact);

		primitiveTypeMap.put(dynamicType, result);
		return result;
	}

	/**
	 * Create a new numeric expression for a sizeof expression of a certain
	 * primitive type.
	 * 
	 * @param kind
	 *            The kind of the primitive type of the sizeof expression.
	 * @return The numeric expression of the sizeof expression.
	 */
	private NumericExpression sizeofPrimitiveType(PrimitiveTypeKind kind) {

		if (kind == PrimitiveTypeKind.CHAR)
			return universe.oneInt();

		if (kind == PrimitiveTypeKind.INT && config.svcomp()) {
			return universe.integer(this.config.getIntBit());
		}

		StringObject name = ModelConfiguration
				.getSizeofPrimitiveTypeName(universe, kind);
		NumericSymbolicConstant result = (NumericSymbolicConstant) universe
				.symbolicConstant(name, universe.integerType());

		ModelConfiguration.SIZEOF_VARS.add(result);
		if (!ModelConfiguration.RESERVE_NAMES.contains(name.getString()))
			ModelConfiguration.RESERVE_NAMES.add(name.getString());
		return result;
	}

	@Override
	public CIVLStateType stateType() {
		return this.stateType;
	}

	@Override
	public SymbolicType stateSymbolicType() {
		return this.stateSymbolicType;
	}

	@Override
	public SymbolicType voidSymbolicType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumericExpression sizeofDynamicType(SymbolicType dynamicType) {
		NumericExpression size;

		if (dynamicType.typeKind() == SymbolicTypeKind.ARRAY) {
			SymbolicArrayType arrayType = (SymbolicArrayType) dynamicType;

			assert arrayType.isComplete();
			size = sizeofDynamicType(arrayType.elementType());
			return universe.multiply(size,
					((SymbolicCompleteArrayType) arrayType).extent());
		}
		CIVLPrimitiveType civlType = primitiveTypeMap.get(dynamicType);

		if (civlType != null) {
			return civlType.getSizeof();
		}

		SymbolicExpression id = universe.integer(dynamicType.id());
		id = universe.tuple(dynamicSymbolicType,
				new Singleton<SymbolicExpression>(id));
		return (NumericExpression) universe.apply(sizeofFunction,
				Arrays.asList(id));
	}

	@Override
	public BooleanExpression sizeofNonPrimitiveTypesFact() {
		SymbolicConstant bv = universe.symbolicConstant(
				universe.stringObject("_t"), dynamicSymbolicType());

		return universe.forall(bv,
				universe.lessThan(universe.zeroInt(),
						(NumericExpression) universe.apply(sizeofFunction,
								Arrays.asList(bv))));
	}

	@Override
	public SymbolicExpression expressionOfType(CIVLType civlType,
			SymbolicType type) {
		SymbolicExpression id = universe.integer(type.id());
		SymbolicExpression result = universe.tuple(dynamicSymbolicType,
				new Singleton<SymbolicExpression>(id));

		dynamicTypeID2staticType.putIfAbsent(result, civlType);
		return result;
	}

	@Override
	public SymbolicType getType(SymbolicExpression dynamicTypeExpr) {
		assert dynamicTypeExpr.type() == dynamicSymbolicType;

		IntegerNumber id = (IntegerNumber) universe
				.extractNumber((NumericExpression) universe
						.tupleRead(dynamicTypeExpr, universe.intObject(0)));

		assert id != null;
		return (SymbolicType) universe.objectWithId(id.intValue());
	}

	@Override
	public CIVLType getStaticTypeOfDynamicType(SymbolicExpression typeId) {
		return dynamicTypeID2staticType.get(typeId);
	}

	@Override
	public CIVLMemType civlMemType() {
		return new CommonMemType(pointerType(voidType), dynamicMemType());
	}

	@Override
	public CIVLSetType civlSetType(CIVLType elementType) {
		if (elementType.typeKind() == TypeKind.SET)
			throw new CIVLInternalException(
					"Element type of a CIVLSetType shall not be CIVLSetType",
					this.systemSource);
		return new CommonSetType(elementType);
	}

	@Override
	public SymbolicTupleType dynamicMemType() {
		/**
		 * The dynamic type of $mem type is: <code>
		 * $mem<INT, $mem_member<INT, SCOPE, HeapID, MallocID, ValueSetTemplate>>
		 * </code>
		 */
		if (dynamicMemType == null) {
			// there is "$mem" type declared in CIVL-C library hence users will
			// not be allowed to declare a type with the same name:
			dynamicMemType = universe.tupleType(universe.stringObject("$mem"),
					Arrays.asList(universe.arrayType(universe.tupleType(
							universe.stringObject("$mem_member"),
							Arrays.asList(universe.integerType(),
									universe.integerType(),
									universe.integerType(), scopeSymbolicType(),
									universe.valueSetTemplateType())))));
		}
		return dynamicMemType;
	}
}
