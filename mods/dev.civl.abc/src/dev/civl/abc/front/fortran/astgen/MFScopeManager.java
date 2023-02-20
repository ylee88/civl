package dev.civl.abc.front.fortran.astgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import dev.civl.abc.ast.node.IF.type.FunctionTypeNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.ast.node.IF.type.TypeNode.TypeNodeKind;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.token.IF.Source;

public class MFScopeManager {
	static int NUM_ATTR_ALL = 0;
	static final int ATTR_FUNC_ELEMENTAL = NUM_ATTR_ALL++;
	static final int ATTR_FUNC_IMPURE = NUM_ATTR_ALL++;
	static final int ATTR_FUNC_MODULE = NUM_ATTR_ALL++;
	static final int ATTR_FUNC_NON_RECURSIVE = NUM_ATTR_ALL++;
	static final int ATTR_FUNC_PURE = NUM_ATTR_ALL++;
	static final int ATTR_FUNC_RECURSIVE = NUM_ATTR_ALL++;
	static final int ATTR_MODULE_PROTECTED = NUM_ATTR_ALL++;
	static final int ATTR_PROC_INTRINSIC = NUM_ATTR_ALL++;
	/* ==== All FUNC/MODULE/PROC attribute indices shall be placed above ==== */
	final int NUM_ATTR_PROCEDURE = NUM_ATTR_ALL;
	static final int ATTR_ARG_INTENT_IN = NUM_ATTR_ALL++;
	static final int ATTR_ARG_INTENT_OUT = NUM_ATTR_ALL++;
	static final int ATTR_ARG_OPTIONAL = NUM_ATTR_ALL++;
	static final int ATTR_ARG_VALUE = NUM_ATTR_ALL++;
	static final int ATTR_IDENT_PRIVATE = NUM_ATTR_ALL++;
	static final int ATTR_IDENT_PUBLIC = NUM_ATTR_ALL++;
	static final int ATTR_VAR_ALLOCATABLE = NUM_ATTR_ALL++;
	static final int ATTR_VAR_CONTIGUOUS = NUM_ATTR_ALL++;
	static final int ATTR_VAR_CODIMENSION = NUM_ATTR_ALL++;
	static final int ATTR_VAR_DIMENSION = NUM_ATTR_ALL++;
	static final int ATTR_VAR_EXTERNAL = NUM_ATTR_ALL++;
	static final int ATTR_VAR_POINTER = NUM_ATTR_ALL++;
	static final int ATTR_VAR_SAVE = NUM_ATTR_ALL++;
	static final int ATTR_VAR_TARGET = NUM_ATTR_ALL++;
	static final int ATTR_VAR_VOLATILE = NUM_ATTR_ALL++;

	private static final int DEFAULT_IMPLICIT_INTEGER_START = 'I' - 'A';
	private static final int DEFAULT_IMPLICIT_INTEGER_END = 'N' - 'A';
	private static final int NUM_LETTERS = 26;

	private NodeFactory nodeFactory = null;
	private List<HashSet<String>> globalAttrIdentSets = new ArrayList<HashSet<String>>();
	private MFScopeRecord gSRecord = new MFScopeRecord();
	private MFScopeRecord cSRecord = gSRecord;

	/**
	 * Constructs a {@code MFScopeManager} to store and analyze scope-based
	 * information.
	 * 
	 * @param nodeFactory
	 *                        is a non-null instance used for creating implicit
	 *                        {@link TypeNode}s
	 */
	MFScopeManager(NodeFactory nodeFactory) {
		assert nodeFactory != null;
		this.nodeFactory = nodeFactory;
		for (int i = 0; i < NUM_ATTR_PROCEDURE; i++) {
			this.globalAttrIdentSets.add(new HashSet<String>());
		}
	}

	private int indexOfScopedImplicitType(char c) {
		return c - 'A';
	}

	/**
	 * Collects array type declaration information (i.e.,
	 * {@code arrayBaseType}), which is associated with {@code arrayIdent}.
	 * 
	 * @param arrayIdent
	 *                          is a non-null and non-empty array variable
	 *                          identifier/name
	 * @param arrayBaseType
	 *                          is a non-null array element type
	 */
	void addDeclArray(String arrayIdent, TypeNode arrayBaseType) {
		assert arrayIdent != null && !arrayIdent.isEmpty()
				&& arrayBaseType != null;
		this.cSRecord.varIdentToArrayBaseTypes.put(arrayIdent, arrayBaseType);
	}

	/**
	 * Collects parameter type declaration information, which is associated with
	 * {@code parIdent}.
	 * 
	 * @param parIdent
	 *                        is a non-null and non-empty parameter
	 *                        identifier/name
	 * @param parDeclNode
	 *                        is a non-null {@link TypeNode}
	 */
	void addDeclParameter(String parIdent,
			VariableDeclarationNode parDeclNode) {
		assert parIdent != null && !parIdent.isEmpty() && parDeclNode != null;
		this.cSRecord.parIdentToDecls.put(parIdent, parDeclNode);
	}

	/**
	 * Collects variable type declaration information, which is associated with
	 * {@code parIdent}.
	 * 
	 * @param varIdent
	 *                        is a non-null and non-empty variable
	 *                        identifier/name
	 * @param varDeclNode
	 *                        is a non-null {@link TypeNode}
	 */
	void addDeclVariable(String varIdent, VariableDeclarationNode varDeclNode) {
		assert varIdent != null && !varIdent.isEmpty() && varDeclNode != null;
		this.cSRecord.varIdentToDecls.put(varIdent, varDeclNode);
	}

	/**
	 * Collects the given derived type identifier/name.
	 * 
	 * @param derivedTypeIdent
	 *                             is a non-null and non-empty derived type
	 *                             identifier/name
	 */
	void addDerivedType(String derivedTypeIdent) {
		assert derivedTypeIdent != null && !derivedTypeIdent.isEmpty();
		this.cSRecord.derivedTypeIdents.add(derivedTypeIdent);
	}

	/**
	 * Collects the identifier/name requiring dereference operation for
	 * retrieving its value.
	 * <p>
	 * Note: Recorded variables either have {@code POINTER} attribute or are
	 * created/transformed by {@link MFASTBuilderWorker}
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     whose initial character shall be a letter in
	 *                     upper-case.
	 */
	void addPointerType(String varIdent) {
		assert varIdent != null && !varIdent.isEmpty();
		this.cSRecord.ptrIdents.add(varIdent);
	}

	/**
	 * Collects the given label identifier/name.
	 * 
	 * @param lblIdent
	 *                     is a non-null and non-empty label identifier/name
	 */
	void addLabel(String lblIdent) {
		assert lblIdent != null && !lblIdent.isEmpty();
		this.cSRecord.labels.add(lblIdent);
	}

	/**
	 * Collects the given undeclared identifier/name
	 * 
	 * @param ident
	 *                  is a non-null and non-empty undeclared identifier/name
	 */
	void addUndeclaredIdent(String ident) {
		assert ident != null && !ident.isEmpty();
		this.cSRecord.undeclIdents.add(ident);
	}

	/**
	 * Enters a program unit {@code Scope}.
	 * 
	 * @param scope
	 *                      is a non-null lexical/static scope of a Fortran
	 *                      program unit.
	 * @param unitIdent
	 *                      is a non-null and non-empty identifier/name of the
	 *                      program unit associated with {@code scope}
	 * @param unitType
	 *                      is the type of the program unit associated with
	 *                      {@code scope}
	 */
	void enterProgramUnitScope(String unitIdent, FunctionTypeNode unitType) {
		assert unitIdent != null && !unitIdent.isEmpty();
		this.cSRecord = new MFScopeRecord(this.gSRecord, unitIdent, unitType);
	}

	/**
	 * Exits the current/innermost scope
	 * <p>
	 * All local variable (in the current scope) type, declaration, attribute
	 * records shall be cleaned up.
	 */
	void exitProgramUnitScope() {
		assert this.cSRecord != null;
		// Clean up the current scope record
		this.cSRecord = cSRecord.parent();
	}

	/**
	 * Returns the array element type for a given array variable identifier/name
	 * 
	 * @param arrayIdent
	 *                       is a non-null and non-empty array variable
	 *                       identifier/name
	 * @return {@link TypeNode}, which is the array element type declared for
	 *         {@code varIdent}.
	 */
	TypeNode getArrayBaseTypeByIdent(String arrayIdent) {
		// assert arrayIdent != null && !arrayIdent.isEmpty();
		// is asserted in `hasArrayType`
		assert this.hasArrayType(arrayIdent);
		return this.cSRecord.varIdentToArrayBaseTypes.get(arrayIdent);
	}

	/**
	 * Returns the type declaration for a given variable identifier/name.
	 * <p>
	 * For a declared identifier, its {@link VariableDeclarationNode} is
	 * returned.
	 * <p>
	 * For an undeclared identifier, its {@link VariableDeclarationNode} is
	 * returned iff {@code IMPLICIT NONE} is not specified in the current
	 * program unit scope; else {@code null} shall be returned.
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     which starts with a letter in upper-case (or an
	 *                     underscore for identifiers imported from C/C++ code).
	 * @return a {@link VariableDeclarationNode}, which is the type declaration
	 *         of the given variable identifier/name {@code varIdent}
	 */
	VariableDeclarationNode getDeclByVarIdent(String varIdent) {
		// assert varIdent != null && !varIdent.isEmpty();
		// is asserted in 'isDeclaredVar'
		if (this.isDeclaredVar(varIdent)) {
			return this.cSRecord.varIdentToDecls.get(varIdent);
		} else {
			return null;
		}
	}

	/**
	 * Returns the type declaration for a given parameter identifier/name.
	 * 
	 * <p>
	 * For any explicitly type-declared parameter , its
	 * {@link VariableDeclarationNode} is returned.
	 * <p>
	 * For any undeclared parameter, its {@link VariableDeclarationNode} is
	 * returned iff {@code IMPLICIT NONE} is NOT specified in the current
	 * program unit scope; else {@code null} shall be returned.
	 * 
	 * @param parIdent
	 *                     is a non-null and non-empty parameter
	 *                     identifier/name, which starts with a letter in
	 *                     upper-case or a underscore symbol.
	 * @return a {@link VariableDeclarationNode}, which is the type declaration
	 *         of the given parameter identifier/name {@code parIdent}
	 */
	VariableDeclarationNode getDeclByParIdent(String parIdent) {
		if (this.isParameterVar(parIdent)) {
			return this.cSRecord.parIdentToDecls.get(parIdent);
		} else {
			return null;
		}
	}

	/**
	 * Returns the implicit type for a given variable identifier/name.
	 * <p>
	 * {@code null} shall be returned iff {@code IMPLICIT NONE} statement is
	 * specified; else a non-null {@link TypeNode} shall be returned.
	 * <p>
	 * The implicit type is determined by the initial character of
	 * {@code varIdent} and {@code IMPLICIT} statement used in the current unit
	 * scope.
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     which starts with a letter in upper-case (or an
	 *                     underscore for identifiers imported from C/C++ code).
	 * @param src
	 *                     is the {@link Source} associated with
	 *                     {@code varIdent}.
	 * @return {@link TypeNode}, which is possibly associated with
	 *         {@code varIdent}.
	 */
	TypeNode getImplicitType(String varIdent, Source src) {
		assert varIdent != null && !varIdent.isEmpty();
		if (this.cSRecord.implicitNone)
			return null;

		int init = indexOfScopedImplicitType(varIdent.charAt(0));
		TypeNode type = null;

		assert init >= 0;
		if (init < NUM_LETTERS) {
			/*
			 * Try to get an implicit type for a regular Fortran variable name.
			 * The type is assigned according to rules specified by IMPLICIT
			 * statement in the current program unit scope.
			 */
			type = this.cSRecord.scopeImplicitTypes[init];
		}
		// type is null if any of following condition holds:
		// 1. the varIdent does not start with a letter in upper-case
		// 2. there is no specific implicit rules specified
		if (type == null) {
			// Case 1: varIdent may be an imported C/C++
			// variable, which shall be declared explicitly.
			// Case 2: default implicit type rules are used.
			if (DEFAULT_IMPLICIT_INTEGER_START <= init && //
					init <= DEFAULT_IMPLICIT_INTEGER_END) {
				// if varIdent starts with any of: I,J,K,L,M,N
				type = nodeFactory.newBasicTypeNode(src, BasicTypeKind.INT);
			} else {
				type = nodeFactory.newBasicTypeNode(src, BasicTypeKind.FLOAT);
			}
		} else {
			type = type.copy();
		}
		return type;
	}

	/**
	 * Returns the type for a given parameter identifier/name.
	 * <p>
	 * For a parameter with its type declared, it is returned according to
	 * recorded parameter type declarations.
	 * <p>
	 * For a parameter with no type declared, its implicit type is returned iff
	 * {@code IMPLICIT NONE} is not specified in the current program unit scope;
	 * else {@code null} shall be returned.
	 * 
	 * @param parIdent
	 *                     is a non-null and non-empty parameter
	 *                     identifier/name, which shall start with a letter in
	 *                     upper-case or a underscore symbol.
	 * @param src
	 *                     is the {@link Source} associated with
	 *                     {@code varIdent}.
	 * @return {@link TypeNode}, which is possibly associated with
	 *         {@code parIdent}.
	 */
	TypeNode getTypeByParIdent(String parIdent, Source src) {
		if (this.isParameterVar(parIdent)) {
			return this.cSRecord.parIdentToDecls.get(parIdent).getTypeNode();
		} else {
			return this.getImplicitType(parIdent, src);
		}

	}

	/**
	 * Returns the type for a given variable identifier/name.
	 * <p>
	 * For a declared identifier, its {@link TypeNode} is returned according to
	 * recorded type declarations.
	 * <p>
	 * For an undeclared identifier, an implicit type is returned iff
	 * {@code IMPLICIT NONE} is not specified in the current program unit scope;
	 * else {@code null} shall be returned.
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     which starts with a letter in upper-case (or an
	 *                     underscore for identifiers imported from C/C++ code).
	 * @param src
	 *                     is the {@link Source} associated with
	 *                     {@code varIdent}.
	 * @return {@link TypeNode}, which is possibly associated with
	 *         {@code varIdent}.
	 */
	TypeNode getTypeByVarIdent(String varIdent, Source src) {
		// assert varIdent != null && !varIdent.isEmpty();
		// is asserted in 'isDeclaredVar'
		if (this.isDeclaredVar(varIdent)) {
			return this.cSRecord.varIdentToDecls.get(varIdent).getTypeNode();
		} else {
			return this.getImplicitType(varIdent, src);
		}
	}

	/** @return the identifier/name of the current program unit. */
	String getProgramUnitIdent() {
		return this.cSRecord.name();
	}

	/** @return the type of the current program unit */
	TypeNode getProgramUnitType() {
		return this.cSRecord.scopeType;
	}

	/**
	 * @return a non-null set of all undeclared identifiers in the current
	 *         program unit scope.
	 */
	Set<String> getUndeclaredIdents() {
		assert this.cSRecord.undeclIdents != null;
		return this.cSRecord.undeclIdents;
	}

	/**
	 * Returns {@code true} iff the given {@code arrayIdent} is declared as an
	 * array in the current scope unit.
	 * 
	 * @param arrayIdent
	 *                       is a non-null and non-empty array variable
	 *                       identifier/name
	 * @return see above
	 */
	boolean hasArrayType(String arrayIdent) {
		assert arrayIdent != null && !arrayIdent.isEmpty();
		return this.cSRecord.varIdentToArrayBaseTypes.containsKey(arrayIdent);
	}

	/**
	 * Returns {@code true} iff a given identifier/name has a specific attribute
	 * associated; else {@code false} is returned.
	 * 
	 * @param ident
	 *                     is a non-null and non-empty identifier/name
	 * @param kindAttr
	 *                     is a non-negative integer value representing a
	 *                     specific kind of attribute
	 * @return see above;
	 */
	boolean hasAttr(String ident, int kindAttr) {
		assert ident != null && !ident.isEmpty() && kindAttr >= 0;
		return this.cSRecord.scopedAttrIdentSets.get(kindAttr).contains(ident);
	}

	/**
	 * Returns {@code true} iff a given program unit identifier/name has a
	 * specific attribute associated; else {@code false} is returned.
	 * 
	 * @param puIdent
	 *                     is a non-null and non-empty program unit
	 *                     identifier/name
	 * @param kindAttr
	 *                     is a non-negative integer value representing a
	 *                     specific kind of attribute
	 * @return
	 */
	boolean hasAttrForPrograumUnit(String puIdent, int kindAttr) {
		assert puIdent != null && !puIdent.isEmpty() && kindAttr >= 0;
		return this.gSRecord.scopedAttrIdentSets.get(kindAttr)
				.contains(puIdent);
	}

	/**
	 * Returns {@code true} iff the given {@code lblIdent} is declared in the
	 * current program unit scope; else {@code false}.
	 * 
	 * @param lblIdent
	 *                     is a non-null and non-empty label identifier/name
	 * @return see above
	 */
	boolean hasLabel(String lblIdent) {
		assert lblIdent != null && !lblIdent.isEmpty();
		// No global lables from Fortran source code.
		return this.cSRecord.labels.contains(lblIdent);
	}

	/**
	 * Returns {@code true} iff the given {@code varIdent} is declared in the
	 * current program unit scope (which is NOT the global one).
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name
	 * @return see above
	 */
	boolean isDeclaredVar(String varIdent) {
		assert varIdent != null && !varIdent.isEmpty();
		return this.cSRecord.varIdentToDecls.containsKey(varIdent);
	}

	/**
	 * Returns {@code true} iff the given {@code derivedTypeIdent} is declared
	 * as a derived type; else {@code false}.
	 * 
	 * @param derivedTypeIdent
	 *                             is a non-null and non-empty identifier/name
	 * @return see above
	 */
	boolean isDerivedType(String derivedTypeIdent) {
		assert derivedTypeIdent != null && !derivedTypeIdent.isEmpty();
		return this.cSRecord.derivedTypeIdents.contains(derivedTypeIdent) || //
				this.gSRecord.derivedTypeIdents.contains(derivedTypeIdent);
	}

	/** @return {@code true} iff {@code IMPLICIT NONE} statement is specified */
	boolean isImplicitNone() {
		return this.cSRecord.implicitNone;
	}

	/**
	 * @return true iff the current scope is the global one; else (the current
	 *         scope is associated with a Fortran program unit) false.
	 */
	boolean isInGlobalScope() {
		return this.cSRecord == this.gSRecord;
	}

	/**
	 * Returns {@code true} iff the given {@code varIdent} is declared as a
	 * parameter of the current program unit.
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     whose initial character shall be a letter in
	 *                     upper-case or a underscore symbol
	 * @return see above
	 */
	boolean isParameterVar(String varIdent) {
		assert varIdent != null && !varIdent.isEmpty();
		assert '_' == varIdent.charAt(0)
				|| ('A' <= varIdent.charAt(0) && varIdent.charAt(0) <= 'Z');
		return this.cSRecord.parIdentToDecls.containsKey(varIdent);
	}

	/**
	 * Returns {@code true} iff the given {@code varIdent} is a pointer to a
	 * memory storage. The pointer is either a variable with {@code POINTER}
	 * attribute or one created/transformed by {@link MFASTBuilderWorker}
	 * 
	 * @param varIdent
	 *                     is a non-null and non-empty variable identifier/name,
	 *                     whose initial character shall be a letter in
	 *                     upper-case.
	 * @return see above
	 */
	boolean isReference(String varIdent) {
		assert varIdent != null && !varIdent.isEmpty();
		return this.cSRecord.ptrIdents.contains(varIdent);
	}

	/**
	 * Set the a given attribute to a given identifier.
	 * 
	 * @param ident
	 *                     is a non-null and non-empty identifier/name
	 * @param kindAttr
	 *                     is a non-negative integer value representing a
	 *                     specific kind of attribute
	 */
	void setAttrByIdent(String ident, int kindAttr) {
		assert ident != null && !ident.isEmpty() && kindAttr >= 0;
		this.cSRecord.scopedAttrIdentSets.get(kindAttr).add(ident);
		if (kindAttr < this.NUM_ATTR_PROCEDURE) {
			this.globalAttrIdentSets.get(kindAttr).add(ident);
		}
	}

	/**
	 * Sets the record of the declaration scope for the given array variable
	 * {@code arrayIdent} from the current scope to the global scope.
	 * 
	 * @param arrayIdent
	 *                       is a non-null and non-empty identifier/name of the
	 *                       array variable, which has been declared as an
	 *                       array.
	 */
	void setDeclArrayGlobal(String arrayIdent) {
		assert this.hasArrayType(arrayIdent);
		this.gSRecord.varIdentToArrayBaseTypes.put(arrayIdent, //
				this.cSRecord.varIdentToArrayBaseTypes.get(arrayIdent));
		this.cSRecord.varIdentToArrayBaseTypes.remove(arrayIdent);
	}

	/**
	 * Sets the record of the declaration scope for the given {@code ident} from
	 * the current scope to the global scope.
	 * 
	 * @param ident
	 *                  is a non-null and non-empty identifier/name, which has
	 *                  had its type declared.
	 */
	void setDeclGlobal(String ident) {
		assert this.isDeclaredVar(ident);
		this.gSRecord.varIdentToDecls.put(ident, //
				this.cSRecord.varIdentToDecls.get(ident));
		this.cSRecord.varIdentToDecls.remove(ident);
	}

	/** Forbids implicit declaration when {@code IMPLICIT NONE} is specified. */
	void setImplicitNone() {
		this.cSRecord.implicitNone = true;
	}

	/**
	 * Sets implicit types for a specific range of initial characters based on
	 * given starting and ending characters, which is specified by Fortran
	 * {@code IMPLICIT} statement.
	 * 
	 * @param charStart
	 *                            is the starting initial character in
	 *                            upper-case.
	 * @param charEnd
	 *                            is the ending initial character in upper-case;
	 *                            its integer value shall be greater than or
	 *                            equal to {@code charStart}
	 * @param newImplicitType
	 *                            is the implicit type shall be assigned to
	 *                            variables, which are implicitly declared.
	 */
	void setImplicitType(char charStart, char charEnd,
			TypeNode newImplicitType) {
		assert !this.cSRecord.implicitNone;

		int start = indexOfScopedImplicitType(charStart);
		int end = indexOfScopedImplicitType(charEnd);

		// 'A' <= start <= end <= 'Z'-'A'
		assert 0 <= start && start <= end && //
				end <= indexOfScopedImplicitType('Z');
		// Update scoped implicit type records
		for (int i = start; i <= end; i++) {
			this.cSRecord.scopeImplicitTypes[i] = newImplicitType;
		}
		// Update parameter list
		for (VariableDeclarationNode parDeclNode : this.cSRecord.parIdentToDecls
				.values()) {
			char initParIdent = parDeclNode.getIdentifier().name().charAt(0);

			if (charStart <= initParIdent && initParIdent <= charEnd) {
				TypeNode parImplicitTypeNode = newImplicitType.copy();

				// Scalar type is updated as pointer-to-scalar type.
				if (newImplicitType.kind() == TypeNodeKind.BASIC) {
					parImplicitTypeNode = nodeFactory.newPointerTypeNode(
							parImplicitTypeNode.getSource(),
							parImplicitTypeNode);
				}
				parDeclNode.setTypeNode(parImplicitTypeNode);
			}
		}
	}

	void updateParameterIdentfier(String oldParId, String newParId) {
		Map<String, VariableDeclarationNode> parId2Decls = cSRecord.parIdentToDecls;
		VariableDeclarationNode parDeclNode = parId2Decls.get(oldParId);
		IdentifierNode oldParIdNode = parDeclNode.getIdentifier();
		IdentifierNode newParIdNode = nodeFactory
				.newIdentifierNode(oldParIdNode.getSource(), newParId);

		this.cSRecord.outParIdents.add(newParId);
		parId2Decls.remove(oldParId, parDeclNode);
		oldParIdNode.remove();
		parDeclNode.setIdentifier(newParIdNode);
		parId2Decls.put(newParId, parDeclNode);
	}

	Set<String> getAllIntentOutParameterIdentifiers() {
		return this.cSRecord.outParIdents;
	}

	/**
	 * Sets the feasible type of the current program unit.
	 * 
	 * @param scopeType
	 *                      a non-null {@link TypeNode}
	 */
	void setProgramUnitType(TypeNode scopeType) {
		assert scopeType != null;
		// Currently a scope type of a prpgram unit shall be
		// any of PROGRAM, SUBROUTINE, and FUNCTION, whose
		// scope types shall be a {@link FunctionTypeNode}.
		// If other types are possible, the following assertions
		// shall be deleted or changed.
		assert scopeType instanceof FunctionTypeNode;
		this.cSRecord.scopeType = scopeType;
	}

	/**
	 * {@code MFScopeRecord} is mainly used for recording variable type,
	 * declaration, and attribute information; label identifers are also
	 * recorded.
	 * <p>
	 * The record can be created for either a global scope or a program unit
	 * scope.
	 * <p>
	 * A program unit is one of following:
	 * <ul>
	 * <li>A main program declared with {@code PROGRAM},
	 * <li>A function subprogram declared with {@code FUNCTION},
	 * <li>A subroutine subprogram declared with {@code SUBROUTINE},
	 * <li>A module declared with {@code MODULE},
	 * <li>A submodule declared with {@code SUBMODULE}, or
	 * <li>A data-block declared with {@code BLOCK DATA}.
	 * </ul>
	 * 
	 * @author Wenhao Wu (wuwenhao@udel.edu)
	 */
	private class MFScopeRecord {
		/** The unique name of global scope record is defined here. */
		static final String SCOPE_GLOBAL = "";

		private String name = null;
		private MFScopeRecord parent = null;
		boolean implicitNone = false;
		TypeNode scopeType = null;
		TypeNode[] scopeImplicitTypes = new TypeNode[NUM_LETTERS];
		Set<String> derivedTypeIdents = new HashSet<>();
		Set<String> ptrIdents = new HashSet<>();
		Set<String> labels = new HashSet<>();
		Set<String> undeclIdents = new HashSet<>();
		Set<String> outParIdents = new HashSet<>();
		Map<String, TypeNode> varIdentToArrayBaseTypes = new HashMap<>();
		Map<String, VariableDeclarationNode> varIdentToDecls = new HashMap<>();
		Map<String, VariableDeclarationNode> parIdentToDecls = new HashMap<>();
		List<HashSet<String>> scopedAttrIdentSets = new ArrayList<HashSet<String>>();

		/**
		 * Constructs global scope record.
		 * <ul>
		 * <li>The value returned by calling {@code parent()} is {@code null}.
		 * <li>The value returned by calling {@code name} is
		 * {@value #SCOPE_GLOBAL}
		 * </ul>
		 */
		MFScopeRecord() {
			this.name = SCOPE_GLOBAL;
		}

		/**
		 * Constructs program unit scope record.
		 * 
		 * @param parentRecord
		 *                            is the parent scope record
		 * @param scopeRecordName
		 *                            is the identifier/name of the associated
		 *                            program unit
		 * @param scopeType
		 *                            is the type of the associated program unit
		 */
		MFScopeRecord(MFScopeRecord parentRecord, String scopeRecordName,
				TypeNode scopeType) {
			assert parentRecord != null && scopeRecordName != null;
			this.name = scopeRecordName;
			this.parent = parentRecord;
			this.scopeType = scopeType;
			for (int i = 0; i < NUM_ATTR_ALL; i++) {
				this.scopedAttrIdentSets.add(new HashSet<String>());
			}
		}

		/** @return the name of the associated program unit */
		String name() {
			return this.name;
		}

		/** @return the parent scope record of {@code this} one. */
		MFScopeRecord parent() {
			return this.parent;
		}

	}

}