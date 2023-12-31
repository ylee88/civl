package dev.civl.abc.ast.node.common;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.*;
import dev.civl.abc.ast.node.IF.acsl.*;
import dev.civl.abc.ast.node.IF.acsl.CompositeEventNode.EventOperator;
import dev.civl.abc.ast.node.IF.acsl.ExtendedQuantifiedExpressionNode.ExtendedQuantifier;
import dev.civl.abc.ast.node.IF.acsl.MPICollectiveBlockNode.MPICommunicatorMode;
import dev.civl.abc.ast.node.IF.acsl.MPIContractAbsentEventNode.MPIAbsentEventKind;
import dev.civl.abc.ast.node.IF.acsl.MPIContractConstantNode.MPIConstantKind;
import dev.civl.abc.ast.node.IF.acsl.MPIContractExpressionNode.MPIContractExpressionKind;
import dev.civl.abc.ast.node.IF.acsl.MemoryEventNode.MemoryEventNodeKind;
import dev.civl.abc.ast.node.IF.compound.*;
import dev.civl.abc.ast.node.IF.declaration.*;
import dev.civl.abc.ast.node.IF.expression.*;
import dev.civl.abc.ast.node.IF.expression.ConstantNode.ConstantKind;
import dev.civl.abc.ast.node.IF.expression.OperatorNode.Operator;
import dev.civl.abc.ast.node.IF.expression.QuantifiedExpressionNode.Quantifier;
import dev.civl.abc.ast.node.IF.label.LabelNode;
import dev.civl.abc.ast.node.IF.label.OrdinaryLabelNode;
import dev.civl.abc.ast.node.IF.label.SwitchLabelNode;
import dev.civl.abc.ast.node.IF.omp.OmpAtomicNode.OmpAtomicClause;
import dev.civl.abc.ast.node.IF.omp.*;
import dev.civl.abc.ast.node.IF.omp.OmpEndNode.OmpEndType;
import dev.civl.abc.ast.node.IF.omp.OmpReductionNode.OmpReductionOperator;
import dev.civl.abc.ast.node.IF.omp.OmpSyncNode.OmpSyncNodeKind;
import dev.civl.abc.ast.node.IF.omp.OmpWorksharingNode.OmpWorksharingNodeKind;
import dev.civl.abc.ast.node.IF.statement.*;
import dev.civl.abc.ast.node.IF.statement.JumpNode.JumpKind;
import dev.civl.abc.ast.node.IF.statement.LoopNode.LoopKind;
import dev.civl.abc.ast.node.IF.type.*;
import dev.civl.abc.ast.node.common.acsl.*;
import dev.civl.abc.ast.node.common.compound.CommonArrayDesignatorNode;
import dev.civl.abc.ast.node.common.compound.CommonCompoundInitializerNode;
import dev.civl.abc.ast.node.common.compound.CommonDesignationNode;
import dev.civl.abc.ast.node.common.compound.CommonFieldDesignatorNode;
import dev.civl.abc.ast.node.common.declaration.*;
import dev.civl.abc.ast.node.common.expression.*;
import dev.civl.abc.ast.node.common.label.CommonOrdinaryLabelNode;
import dev.civl.abc.ast.node.common.label.CommonSwitchLabelNode;
import dev.civl.abc.ast.node.common.omp.*;
import dev.civl.abc.ast.node.common.statement.*;
import dev.civl.abc.ast.node.common.type.*;
import dev.civl.abc.ast.type.IF.IntegerType;
import dev.civl.abc.ast.type.IF.ObjectType;
import dev.civl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import dev.civl.abc.ast.type.IF.StandardSignedIntegerType.SignedIntKind;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType;
import dev.civl.abc.ast.type.IF.StandardUnsignedIntegerType.UnsignedIntKind;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.IF.*;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.err.IF.ABCRuntimeException;
import dev.civl.abc.front.c.parse.CivlCParser;
import dev.civl.abc.token.IF.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static dev.civl.abc.ast.node.IF.acsl.MPIContractAbsentEventNode.MPIAbsentEventKind.SENDFROM;
import static dev.civl.abc.ast.node.IF.acsl.MPIContractAbsentEventNode.MPIAbsentEventKind.SENDTO;
import static dev.civl.abc.ast.node.IF.acsl.MPIContractExpressionNode.MPIContractExpressionKind.MPI_ABSENT;
import static dev.civl.abc.ast.node.IF.acsl.MPIContractExpressionNode.MPIContractExpressionKind.MPI_ABSENT_EVENT;

public class CommonNodeFactory implements NodeFactory {

	private int attributeCount = 0;

	private LiteralInterpreter literalInterpreter;

	private ValueFactory valueFactory;

	private TypeFactory typeFactory;

	private StandardUnsignedIntegerType booleanType;

	private ObjectType processType;

	private ObjectType scopeType;

	private Configuration configuration;
	
	private AttributeKey tempCountKey;

	public CommonNodeFactory(Configuration configuration,
			TypeFactory typeFactory, ValueFactory valueFactory) {
		this.literalInterpreter = new LiteralInterpreter(typeFactory,
				valueFactory);
		this.typeFactory = typeFactory;
		this.valueFactory = valueFactory;
		this.booleanType = typeFactory
				.unsignedIntegerType(UnsignedIntKind.BOOL);
		this.processType = typeFactory.processType();
		this.scopeType = typeFactory.scopeType();
		this.configuration = configuration;
		this.tempCountKey = newAttribute("tempCount", Integer.class);
	}

	@Override
	public ValueFactory getValueFactory() {
		return valueFactory;
	}
	
	@Override
	public AttributeKey getTempCountKey() {
		return tempCountKey;
	}

	@Override
	public AttributeKey newAttribute(String attributeName,
			Class<? extends Object> attributeClass) {
		AttributeKey key = new CommonAttributeKey(attributeCount, attributeName,
				attributeClass);

		attributeCount++;
		return key;
	}

	@Override
	public <T extends ASTNode> SequenceNode<T> newSequenceNode(Source source,
			String name, List<T> nodes) {
		return new CommonSequenceNode<T>(source, name, nodes);
	}

	@Override
	public <S extends ASTNode, T extends ASTNode> PairNode<S, T> newPairNode(
			Source source, S node1, T node2) {
		return new CommonPairNode<S, T>(source, node1, node2);
	}

	@Override
	public IdentifierNode newIdentifierNode(Source source, String name) {
		return new CommonIdentifierNode(source, name);
	}

	@Override
	public BasicTypeNode newBasicTypeNode(Source source, BasicTypeKind kind) {
		return new CommonBasicTypeNode(source, kind);
	}

	@Override
	public TypeNode newVoidTypeNode(Source source) {
		return new CommonVoidTypeNode(source);
	}

	@Override
	public EnumerationTypeNode newEnumerationTypeNode(Source source,
			IdentifierNode tag,
			SequenceNode<EnumeratorDeclarationNode> enumerators) {
		return new CommonEnumerationTypeNode(source, tag, enumerators);
	}

	@Override
	public ArrayTypeNode newArrayTypeNode(Source source, TypeNode elementType,
			ExpressionNode extent) {
		return new CommonArrayTypeNode(source, elementType, extent);
	}

	@Override
	public AtomicTypeNode newAtomicTypeNode(Source source, TypeNode baseType) {
		return new CommonAtomicTypeNode(source, baseType);
	}

	@Override
	public PointerTypeNode newPointerTypeNode(Source source,
			TypeNode referencedType) {
		return new CommonPointerTypeNode(source, referencedType);
	}

	@Override
	public StructureOrUnionTypeNode newStructOrUnionTypeNode(Source source,
			boolean isStruct, IdentifierNode tag,
			SequenceNode<FieldDeclarationNode> structDeclList) {
		return new CommonStructureOrUnionTypeNode(source, isStruct, tag,
				structDeclList);
	}

	@Override
	public FunctionTypeNode newFunctionTypeNode(Source source,
			TypeNode returnType, SequenceNode<VariableDeclarationNode> formals,
			boolean hasIdentifierList) {
		return new CommonFunctionTypeNode(source, returnType, formals,
				hasIdentifierList);
	}

	@Override
	public TypeNode newScopeTypeNode(Source source) {
		return new CommonScopeTypeNode(source);
	}

	@Override
	public TypeNode newRangeTypeNode(Source source) {
		return new CommonRangeTypeNode(source);
	}

	@Override
	public DomainTypeNode newDomainTypeNode(Source source) {
		return new CommonDomainTypeNode(source, null);
	}

	@Override
	public DomainTypeNode newDomainTypeNode(Source source,
			ExpressionNode dimension) {
		return new CommonDomainTypeNode(source, dimension);
	}

	@Override
	public TypedefNameNode newTypedefNameNode(IdentifierNode name,
			SequenceNode<ExpressionNode> scopeList) {
		return new CommonTypedefNameNode(name.getSource(), name, scopeList);
	}

	@Override
	public CharacterConstantNode newCharacterConstantNode(Source source,
			String representation, ExecutionCharacter character) {
		CharacterValue constant = valueFactory.characterValue(character);

		return new CommonCharacterConstantNode(source, representation,
				constant);
	}

	@Override
	public StringLiteralNode newStringLiteralNode(Source source,
			String representation, StringLiteral literal) {
		StringValue stringValue = valueFactory.stringValue(literal);

		return new CommonStringLiteralNode(source, representation, stringValue);
	}

	@Override
	public IntegerConstantNode newIntegerConstantNode(Source source,
			String representation) throws SyntaxException {
		return literalInterpreter.integerConstant(source, representation);
	}

	@Override
	public IntegerConstantNode newIntConstantNode(Source source, int value) {
		IntegerType type = typeFactory.signedIntegerType(SignedIntKind.INT);
		IntegerValue intValue = valueFactory.integerValue(type,
				BigInteger.valueOf(value));

		return new CommonIntegerConstantNode(source, String.valueOf(value),
				intValue);
	}

	@Override
	public FloatingConstantNode newFloatingConstantNode(Source source,
			String representation) throws SyntaxException {
		return literalInterpreter.floatingConstant(source, representation);
	}

	@Override
	public EnumerationConstantNode newEnumerationConstantNode(
			IdentifierNode name) {
		return new CommonEnumerationConstantNode(name.getSource(), name);
	}

	@Override
	public CompoundLiteralNode newCompoundLiteralNode(Source source,
			TypeNode typeNode, CompoundInitializerNode initializerList) {
		return new CommonCompoundLiteralNode(source, typeNode, initializerList);
	}

	@Override
	public IdentifierExpressionNode newIdentifierExpressionNode(Source source,
			IdentifierNode identifier) {
		return new CommonIdentifierExpressionNode(source, identifier);
	}

	@Override
	public AlignOfNode newAlignOfNode(Source source, TypeNode type) {
		return new CommonAlignOfNode(source, type);
	}

	@Override
	public CastNode newCastNode(Source source, TypeNode type,
			ExpressionNode argument) {
		return new CommonCastNode(source, type, argument);
	}

	@Override
	public FunctionCallNode newFunctionCallNode(Source source,
			ExpressionNode function, List<ExpressionNode> arguments,
			SequenceNode<ExpressionNode> scopeList) {
		SequenceNode<ExpressionNode> argumentSequenceNode = newSequenceNode(
				source, "ActualParameterList", arguments);

		return new CommonFunctionCallNode(source, function, null,
				argumentSequenceNode, scopeList);
	}

	@Override
	public GenericSelectionNode newGenericSelectionNode(Source source,
			ExpressionNode controllingExpression,
			ExpressionNode defaultExpression,
			SequenceNode<GenericAssociationNode> genericAssociationList) {
		return new CommonGenericSelectionNode(source, controllingExpression,
				defaultExpression, genericAssociationList);
	}

	@Override
	public GenericAssociationNode newGenericAssociationNode(Source source,
			TypeNode typeLabel, ExpressionNode associatedExpression) {
		return new CommonGenericAssociationNode(source, typeLabel,
				associatedExpression);
	}

	@Override
	public FunctionCallNode newFunctionCallNode(Source source,
			ExpressionNode function, List<ExpressionNode> contextArguments,
			List<ExpressionNode> arguments,
			SequenceNode<ExpressionNode> scopeList) {
		SequenceNode<ExpressionNode> contextArgumentSequenceNode = newSequenceNode(
				source, "ActualContextParameterList", contextArguments);
		SequenceNode<ExpressionNode> argumentSequenceNode = newSequenceNode(
				source, "ActualParameterList", arguments);

		return new CommonFunctionCallNode(source, function,
				contextArgumentSequenceNode, argumentSequenceNode, scopeList);
	}

	@Override
	public DotNode newDotNode(Source source, ExpressionNode structure,
			IdentifierNode fieldName) {
		return new CommonDotNode(source, structure, fieldName);
	}

	@Override
	public ArrowNode newArrowNode(Source source,
			ExpressionNode structurePointer, IdentifierNode fieldName) {
		return new CommonArrowNode(source, structurePointer, fieldName);
	}

	@Override
	public OperatorNode newOperatorNode(Source source, Operator operator,
			List<ExpressionNode> arguments) {
		return new CommonOperatorNode(source, operator, arguments);
	}

	@Override
	public SizeofNode newSizeofNode(Source source, SizeableNode argument) {
		return new CommonSizeofNode(source, argument);
	}

	@Override
	public ScopeOfNode newScopeOfNode(Source source, ExpressionNode argument) {
		return new CommonScopeOfNode(source, argument);
	}

	@Override
	public VariableDeclarationNode newVariableDeclarationNode(Source source,
			IdentifierNode name, TypeNode type) {
		return new CommonVariableDeclarationNode(source, name, type);
	}

	@Override
	public VariableDeclarationNode newVariableDeclarationNode(Source source,
			IdentifierNode name, TypeNode type, InitializerNode initializer) {
		return new CommonVariableDeclarationNode(source, name, type,
				initializer);
	}

	@Override
	public FunctionDeclarationNode newFunctionDeclarationNode(Source source,
			IdentifierNode name, TypeNode type,
			SequenceNode<ContractNode> contract) {
		return new CommonFunctionDeclarationNode(source, name, type, contract);
	}

	@Override
	public EnumeratorDeclarationNode newEnumeratorDeclarationNode(Source source,
			IdentifierNode name, ExpressionNode value) {
		return new CommonEnumeratorDeclarationNode(source, name, value);
	}

	@Override
	public FieldDeclarationNode newFieldDeclarationNode(Source source,
			IdentifierNode name, TypeNode type) {
		return new CommonFieldDeclarationNode(source, name, type);
	}

	@Override
	public FieldDeclarationNode newFieldDeclarationNode(Source source,
			IdentifierNode name, TypeNode type, ExpressionNode bitFieldWidth) {
		return new CommonFieldDeclarationNode(source, name, type,
				bitFieldWidth);
	}

	@Override
	public OrdinaryLabelNode newStandardLabelDeclarationNode(Source source,
			IdentifierNode name, StatementNode statement) {
		CommonOrdinaryLabelNode label = new CommonOrdinaryLabelNode(source,
				name);

		label.setStatement(statement);
		return label;
	}

	@Override
	public SwitchLabelNode newCaseLabelDeclarationNode(Source source,
			ExpressionNode constantExpression, StatementNode statement) {
		CommonSwitchLabelNode label = new CommonSwitchLabelNode(source,
				constantExpression);

		label.setStatement(statement);
		return label;
	}

	@Override
	public SwitchLabelNode newDefaultLabelDeclarationNode(Source source,
			StatementNode statement) {
		CommonSwitchLabelNode label = new CommonSwitchLabelNode(source);

		label.setStatement(statement);
		return label;
	}

	@Override
	public TypedefDeclarationNode newTypedefDeclarationNode(Source source,
			IdentifierNode name, TypeNode type) {
		return new CommonTypedefDeclarationNode(source, name, type);
	}

	@Override
	public CompoundInitializerNode newCompoundInitializerNode(Source source,
			List<PairNode<DesignationNode, InitializerNode>> initList) {
		return new CommonCompoundInitializerNode(source, initList);
	}

	@Override
	public DesignationNode newDesignationNode(Source source,
			List<DesignatorNode> designators) {
		return new CommonDesignationNode(source, designators);
	}

	@Override
	public FieldDesignatorNode newFieldDesignatorNode(Source source,
			IdentifierNode name) {
		return new CommonFieldDesignatorNode(source, name);
	}

	@Override
	public ArrayDesignatorNode newArrayDesignatorNode(Source source,
			ExpressionNode index) {
		return new CommonArrayDesignatorNode(source, index);
	}

	@Override
	public CompoundStatementNode newCompoundStatementNode(Source source,
			List<BlockItemNode> items) {
		return new CommonCompoundStatementNode(source, items);
	}

	@Override
	public ExpressionStatementNode newExpressionStatementNode(
			ExpressionNode expression) {
		return new CommonExpressionStatementNode(expression.getSource(),
				expression);
	}

	@Override
	public StatementNode newNullStatementNode(Source source) {
		return new CommonNullStatementNode(source);
	}

	@Override
	public ForLoopNode newForLoopNode(Source source,
			ForLoopInitializerNode initializer, ExpressionNode condition,
			ExpressionNode incrementer, StatementNode body,
			SequenceNode<ContractNode> contracts) {
		return new CommonForLoopNode(source, condition, body, initializer,
				incrementer, contracts);
	}

	@Override
	public DeclarationListNode newForLoopInitializerNode(Source source,
			List<VariableDeclarationNode> declarations) {
		return new CommonDeclarationListNode(source, declarations);
	}

	@Override
	public LoopNode newWhileLoopNode(Source source, ExpressionNode condition,
			StatementNode body, SequenceNode<ContractNode> contracts) {
		return new CommonLoopNode(source, LoopKind.WHILE, condition, body,
				contracts);
	}

	@Override
	public LoopNode newDoLoopNode(Source source, ExpressionNode condition,
			StatementNode body, SequenceNode<ContractNode> contracts) {
		return new CommonLoopNode(source, LoopKind.DO_WHILE, condition, body,
				contracts);
	}

	@Override
	public GotoNode newGotoNode(Source source, IdentifierNode label) {
		return new CommonGotoNode(source, label);
	}

	@Override
	public IfNode newIfNode(Source source, ExpressionNode condition,
			StatementNode trueBranch) {
		return new CommonIfNode(source, condition, trueBranch);
	}

	@Override
	public IfNode newIfNode(Source source, ExpressionNode condition,
			StatementNode trueBranch, StatementNode falseBranch) {
		return new CommonIfNode(source, condition, trueBranch, falseBranch);
	}

	@Override
	public JumpNode newContinueNode(Source source) {
		return new CommonJumpNode(source, JumpKind.CONTINUE);
	}

	@Override
	public JumpNode newBreakNode(Source source) {
		return new CommonJumpNode(source, JumpKind.BREAK);
	}

	@Override
	public ReturnNode newReturnNode(Source source, ExpressionNode argument) {
		return new CommonReturnNode(source, argument);
	}

	@Override
	public LabeledStatementNode newLabeledStatementNode(Source source,
			LabelNode label, StatementNode statement) {
		return new CommonLabeledStatementNode(source, label, statement);
	}

	@Override
	public SwitchNode newSwitchNode(Source source, ExpressionNode condition,
			StatementNode body) {
		CommonSwitchNode switchNode = new CommonSwitchNode(source, condition,
				body);

		return switchNode;
	}

	@Override
	public CivlForNode newCivlForNode(Source source, boolean isParallel,
			DeclarationListNode variables, ExpressionNode domain,
			StatementNode body, SequenceNode<ContractNode> loopContracts) {
		return new CommonCivlForNode(source, isParallel, variables, domain,
				body, loopContracts);
	}

	@Override
	public StaticAssertionNode newStaticAssertionNode(Source source,
			ExpressionNode expression, StringLiteralNode message) {
		return new CommonStaticAssertionNode(source, expression, message);
	}

	@Override
	public PragmaNode newPragmaNode(Source source, IdentifierNode identifier,
			CivlcTokenSequence producer, CivlcToken newlineToken) {
		newlineToken.setType(CivlCParser.EOF);
		return new CommonPragmaNode(source, identifier, producer, newlineToken);
	}

	@Override
	public FunctionDefinitionNode newFunctionDefinitionNode(Source source,
			IdentifierNode name, FunctionTypeNode type,
			SequenceNode<ContractNode> contract, CompoundStatementNode body) {
		return new CommonFunctionDefinitionNode(source, name, type, contract,
				body);
	}

	@Override
	public AbstractFunctionDefinitionNode newAbstractFunctionDefinitionNode(
			Source source, IdentifierNode name, TypeNode type,
			SequenceNode<ContractNode> contract, int continuity,
			SequenceNode<PairNode<ExpressionNode, ExpressionNode>> intervals,
			StringLiteralNode attr) {
		return new CommonAbstractFunctionDefinitionNode(source, name, type,
				contract, continuity, intervals, attr);
	}

	@Override
	public SequenceNode<BlockItemNode> newTranslationUnitNode(Source source,
			List<BlockItemNode> definitions) {
		return newSequenceNode(source, "TranslationUnit", definitions);
	}

	@Override
	public SequenceNode<BlockItemNode> newProgramNode(Source source,
			List<BlockItemNode> definitions) {
		return newSequenceNode(source, "Program", definitions);
	}

	@Override
	public Value getConstantValue(ExpressionNode expression)
			throws SyntaxException {
		CommonExpressionNode commonNode = (CommonExpressionNode) expression;

		if (commonNode.constantComputed()) {
			return commonNode.getConstantValue();
		} else {
			Value value = valueFactory.evaluate(expression);

			commonNode.setConstantValue(value);
			return value;
		}
	}

	@Override
	public SpawnNode newSpawnNode(Source source, FunctionCallNode callNode) {
		return new CommonSpawnNode(source, callNode);
	}

	@Override
	public RemoteOnExpressionNode newRemoteOnExpressionNode(Source source,
			ExpressionNode left, ExpressionNode right) {
		return new CommonRemoteExpressionNode(source, left, right);
	}

	@Override
	public ScopeOfNode newScopeOfNode(Source source,
			IdentifierExpressionNode variableExpression) {
		return new CommonScopeOfNode(source, variableExpression);
	}

	// @Override
	// public AssumeNode newAssumeNode(Source source, ExpressionNode expression)
	// {
	// return new CommonAssumeNode(source, expression);
	// }
	//
	// @Override
	// public AssertNode newAssertNode(Source source, ExpressionNode expression,
	// SequenceNode<ExpressionNode> explanation) {
	// return new CommonAssertNode(source, expression, explanation);
	// }

	@Override
	public WhenNode newWhenNode(Source source, ExpressionNode guard,
			StatementNode body) {
		return new CommonWhenNode(source, guard, body);
	}

	@Override
	public ChooseStatementNode newChooseStatementNode(Source source,
			List<StatementNode> statements) {
		return new CommonChooseStatementNode(source, statements);
	}

	@Override
	public ConstantNode newBooleanConstantNode(Source source, boolean value) {
		IntegerValue theValue;
		String representation;

		if (value) {
			representation = "\\true";
			theValue = valueFactory.integerValue(booleanType, 1);
		} else {
			representation = "\\false";
			theValue = valueFactory.integerValue(booleanType, 0);
		}
		return new CommonIntegerConstantNode(source, representation, theValue);
	}

	@Override
	public ExpressionNode newSelfNode(Source source) {
		ExpressionNode result = new CommonSelfNode(source, processType);

		result.setInitialType(processType);
		return result;
	}

	@Override
	public ExpressionNode newHereNode(Source source) {
		ExpressionNode result = new CommonHereOrRootNode(source, "$here",
				scopeType);

		result.setInitialType(scopeType);
		return result;
	}

	@Override
	public ExpressionNode newRootNode(Source source) {
		ExpressionNode result = new CommonHereOrRootNode(source, "$root",
				scopeType);

		result.setInitialType(scopeType);
		return result;
	}

	@Override
	public ExpressionNode newResultNode(Source source) {
		return new CommonResultNode(source);
	}

	@Override
	public RequiresNode newRequiresNode(Source source,
			ExpressionNode expression) {
		return new CommonRequiresNode(source, expression);
	}

	@Override
	public EnsuresNode newEnsuresNode(Source source,
			ExpressionNode expression) {
		return new CommonEnsuresNode(source, expression);
	}

	@Override
	public DerivativeExpressionNode newDerivativeExpressionNode(Source source,
			ExpressionNode function,
			SequenceNode<PairNode<IdentifierExpressionNode, IntegerConstantNode>> partials,
			SequenceNode<ExpressionNode> arguments) {
		return new CommonDerivativeExpressionNode(source, function, partials,
				arguments);
	}

	@Override
	public void setConstantValue(ExpressionNode expression, Value value) {
		((CommonExpressionNode) expression).setConstantValue(value);
	}

	@Override
	public AtomicNode newAtomicStatementNode(Source statementSource,
			StatementNode body) {
		return new CommonAtomicNode(statementSource, body);
	}

	@Override
	public ExpressionNode newProcnullNode(Source source) {
		ExpressionNode result = new CommonProcnullNode(source, processType);

		result.setInitialType(processType);
		return result;
	}

	@Override
	public ExpressionNode newStatenullNode(Source source) {
		ExpressionNode result = new CommonStatenullNode(source,
				typeFactory.stateType());

		result.setInitialType(typeFactory.stateType());
		return result;
	}

	/* *************************** OpenMP Section ************************** */

	@Override
	public OmpParallelNode newOmpParallelNode(Source source,
			StatementNode statement) {
		return new CommonOmpParallelNode(source, statement);
	}

	@Override
	public OmpForNode newOmpForNode(Source source, StatementNode statement) {
		return new CommonOmpForNode(source, statement);
	}

	@Override
	public OmpSyncNode newOmpMasterNode(Source source,
			StatementNode statement) {
		return new CommonOmpSyncNode(source, OmpSyncNodeKind.MASTER, statement);
	}

	@Override
	public OmpSyncNode newOmpAtomicNode(Source source, StatementNode statement,
			OmpAtomicClause clause, boolean seqConsistent) {
		return new CommomOmpAtomicNode(source, statement, clause,
				seqConsistent);
	}

	@Override
	public OmpSyncNode newOmpBarrierNode(Source source) {
		return new CommonOmpSyncNode(source, OmpSyncNodeKind.BARRIER, null);
	}

	@Override
	public OmpWorksharingNode newOmpSectionsNode(Source source,
			StatementNode statement) {
		return new CommonOmpWorkshareNode(source,
				OmpWorksharingNodeKind.SECTIONS, statement);
	}

	@Override
	public OmpWorksharingNode newOmpSectionNode(Source source,
			StatementNode statement) {
		return new CommonOmpWorkshareNode(source,
				OmpWorksharingNodeKind.SECTION, statement);
	}

	@Override
	public OmpDeclarativeNode newOmpThreadprivateNode(Source source,
			SequenceNode<IdentifierExpressionNode> variables) {
		return new CommonOmpDeclarativeNode(source, variables);
	}

	@Override
	public OmpSymbolReductionNode newOmpSymbolReductionNode(Source source,
			OmpReductionOperator operator,
			SequenceNode<IdentifierExpressionNode> variables) {
		return new CommonOmpSymbolReductionNode(source, operator, variables);
	}

	@Override
	public OmpSyncNode newOmpCriticalNode(Source source, IdentifierNode name,
			StatementNode statement) {
		OmpSyncNode criticalNode = new CommonOmpSyncNode(source,
				OmpSyncNodeKind.CRITICAL, statement);

		criticalNode.setCriticalName(name);
		return criticalNode;
	}

	@Override
	public OmpSyncNode newOmpFlushNode(Source source,
			SequenceNode<IdentifierExpressionNode> variables) {
		OmpSyncNode flushNode = new CommonOmpSyncNode(source,
				OmpSyncNodeKind.FLUSH, null);

		flushNode.setFlushedList(variables);
		return flushNode;
	}

	@Override
	public OmpSyncNode newOmpFlushNode(Source source) {
		return new CommonOmpSyncNode(source, OmpSyncNodeKind.FLUSH, null);
	}

	@Override
	public OmpSyncNode newOmpOrederedNode(Source source,
			StatementNode statement) {
		return new CommonOmpSyncNode(source, OmpSyncNodeKind.ORDERED,
				statement);
	}

	@Override
	public OmpWorksharingNode newOmpSingleNode(Source source,
			StatementNode statement) {
		return new CommonOmpWorkshareNode(source, OmpWorksharingNodeKind.SINGLE,
				statement);
	}

	@Override
	public OmpSimdNode newOmpSimdNode(Source source, StatementNode statement) {
		return new CommonOmpSimdNode(source, statement);
	}

	@Override
	public OmpFunctionReductionNode newOmpFunctionReductionNode(Source source,
			IdentifierExpressionNode function,
			SequenceNode<IdentifierExpressionNode> variables) {
		return new CommonOmpFunctionReductionNode(source, function, variables);
	}

	@Override
	public OmpWorksharingNode newWorksharingNode(Source source,
			OmpWorksharingNodeKind kind) {
		return new CommonOmpWorkshareNode(source, kind, null);
	}

	@Override
	public OmpEndNode newOmpFortranEndNode(Source source, OmpEndType endType) {
		return new CommonOmpEndNode(source, endType);
	}

	@Override
	public RegularRangeNode newRegularRangeNode(Source source,
			ExpressionNode low, ExpressionNode high) {
		return new CommonRegularRangeNode(source, low, high);
	}

	@Override
	public RegularRangeNode newRegularRangeNode(Source source,
			ExpressionNode low, ExpressionNode high, ExpressionNode step) {
		return new CommonRegularRangeNode(source, low, high, step);
	}

	@Override
	public OperatorNode newOperatorNode(Source source, Operator operator,
			ExpressionNode argument) {
		return new CommonOperatorNode(source, operator,
				Arrays.asList(argument));
	}

	@Override
	public OperatorNode newOperatorNode(Source source, Operator operator,
			ExpressionNode arg0, ExpressionNode arg1) {
		return new CommonOperatorNode(source, operator,
				Arrays.asList(arg0, arg1));
	}

	@Override
	public OperatorNode newOperatorNode(Source source, Operator operator,
			ExpressionNode arg0, ExpressionNode arg1, ExpressionNode arg2) {
		return new CommonOperatorNode(source, operator,
				Arrays.asList(arg0, arg1, arg2));
	}

	@Override
	public DependsNode newDependsNode(Source source, ExpressionNode condition,
			SequenceNode<DependsEventNode> eventList) {
		return new CommonDependsNode(source, eventList);
	}

	@Override
	public GuardsNode newGuardNode(Source source, ExpressionNode expression) {
		return new CommonGuardNode(source, expression);
	}

	@Override
	public AssignsOrReadsNode newAssignsNode(Source source,
			SequenceNode<ExpressionNode> expressionList) {
		return new CommonAssignsOrReadsNode(source, true, expressionList);
	}

	@Override
	public AssignsOrReadsNode newReadsNode(Source source,
			SequenceNode<ExpressionNode> expressionList) {
		return new CommonAssignsOrReadsNode(source, false, expressionList);
	}

	@Override
	public WildcardNode newWildcardNode(Source source) {
		return new CommonWildcardNode(source);
	}

	@Override
	public Configuration configuration() {
		return this.configuration;
	}

	@Override
	public StatementExpressionNode newStatementExpressionNode(Source source,
			CompoundStatementNode statement) {
		return new CommonStatementExpressionNode(source, statement);
	}

	@Override
	public TypeofNode newTypeofNode(Source source, ExpressionNode expression) {
		return new CommonTypeofNode(source, expression);
	}

	@Override
	public MemoryEventNode newMemoryEventNode(Source source,
			MemoryEventNodeKind kind, SequenceNode<ExpressionNode> memoryList) {
		return new CommonMemoryEventNode(source, kind, memoryList);
	}

	@Override
	public CompositeEventNode newOperatorEventNode(Source source,
			EventOperator op, DependsEventNode left, DependsEventNode right) {
		return new CommonCompositeEventNode(source, op, left, right);
	}

	@Override
	public NothingNode newNothingNode(Source source) {
		return new CommonNothingNode(source);
	}

	@Override
	public BehaviorNode newBehaviorNode(Source source, IdentifierNode name,
			SequenceNode<ContractNode> body) {
		return new CommonBehaviorNode(source, name, body);
	}

	@Override
	public CompletenessNode newCompletenessNode(Source source,
			boolean isComplete, SequenceNode<IdentifierNode> idList) {
		return new CommonCompletenessNode(source, isComplete, idList);
	}

	@Override
	public AssumesNode newAssumesNode(Source source, ExpressionNode predicate) {
		return new CommonAssumesNode(source, predicate);
	}

	@Override
	public NoactNode newNoactNode(Source source) {
		return new CommonNoactNode(source);
	}

	@Override
	public AnyactNode newAnyactNode(Source source) {
		return new CommonAnyactNode(source);
	}

	@Override
	public CallEventNode newCallEventNode(Source source,
			IdentifierExpressionNode function,
			SequenceNode<ExpressionNode> args) {
		return new CommonCallEventNode(source, function, args);
	}

	@Override
	public MPICollectiveBlockNode newMPICollectiveBlockNode(Source source,
			ExpressionNode mpiComm, MPICommunicatorMode kind,
			SequenceNode<ContractNode> body) {
		return new CommonMPICollectiveBlockNode(source, mpiComm, kind, body);
	}

	@Override
	public MPIContractConstantNode newMPIConstantNode(Source source,
			String stringRepresentation, MPIConstantKind kind,
			ConstantKind constKind) {
		return new CommonMPIConstantNode(source, stringRepresentation, kind,
				constKind);
	}

	@Override
	public MPIContractExpressionNode newMPIExpressionNode(Source source,
			List<ASTNode> arguments, MPIContractExpressionKind kind,
			String exprName) {
		if (kind == MPI_ABSENT_EVENT)
			throw new ABCRuntimeException("use newMPIAbsentEventNode method to "
					+ "create MPIContractAbsentEventNode.");

		List<ExpressionNode> exprs = new LinkedList<>();

		for (ASTNode arg : arguments) {
			if (arg instanceof ExpressionNode)
				exprs.add((ExpressionNode) arg);
			else
				throw new ASTException("For " + kind + " kind MPI Contract "
						+ "expression, arguments shall be "
						+ "instances of ExpressionNode.");
		}
		if (kind == MPI_ABSENT)
			return new CommonMPIContractAbsentNode(source, exprs);
		else
			return new CommonMPIContractExpressionNode(source, exprs, kind,
					exprName);
	}

	@Override
	public MPIContractAbsentEventNode newMPIAbsentEventNode(Source source,
			List<ExpressionNode> arguments, MPIAbsentEventKind kind) {
		if (kind == SENDTO || kind == SENDFROM) {
			if (arguments.size() != 2)
				throw new ASTException("MPIContractAbsentEventNode of kind "
						+ kind + " takes two arguments.");
		} else if (arguments.size() > 1)
			throw new ASTException("MPIContractAbsentEventNode of kind " + kind
					+ " takes no more than one " + "argument.");
		return new CommonMPIContractAbsentEventNode(source, kind, arguments);
	}

	@Override
	public TypeFactory typeFactory() {
		return typeFactory;
	}

	@Override
	public InvariantNode newInvariantNode(Source source,
			boolean isLoopInvariant, ExpressionNode expression) {
		return new CommonInvariantNode(source, isLoopInvariant, expression);
	}

	@Override
	public ArrayTypeNode newArrayTypeNode(Source source, TypeNode elementType,
			ExpressionNode extent, ExpressionNode startIndex) {
		return new CommonArrayTypeNode(source, elementType, extent, startIndex);
	}

	@Override
	public WaitsforNode newWaitsforNode(Source source,
			SequenceNode<ExpressionNode> arguments) {
		return new CommonWaitsforNode(source, arguments);
	}

	@Override
	public QuantifiedExpressionNode newQuantifiedExpressionNode(Source source,
			Quantifier quantifier,
			SequenceNode<PairNode<SequenceNode<VariableDeclarationNode>, ExpressionNode>> boundVariableDeclarationList,
			ExpressionNode restriction, ExpressionNode expression,
			SequenceNode<PairNode<ExpressionNode, ExpressionNode>> intervalSequence) {
		return new CommonQuantifiedExpressionNode(source, quantifier,
				boundVariableDeclarationList, restriction, expression,
				intervalSequence);
	}

	@Override
	public ArrayLambdaNode newArrayLambdaNode(Source source, TypeNode type,
			SequenceNode<PairNode<SequenceNode<VariableDeclarationNode>, ExpressionNode>> boundVariableDeclarationList,
			ExpressionNode restriction, ExpressionNode expression) {
		return new CommonArrayLambdaNode(source, type,
				boundVariableDeclarationList, restriction, expression);
	}

	@Override
	public ArrayLambdaNode newArrayLambdaNode(Source source, TypeNode type,
			List<VariableDeclarationNode> boundVariableDeclarationList,
			ExpressionNode restriction, ExpressionNode expression) {
		List<PairNode<SequenceNode<VariableDeclarationNode>, ExpressionNode>> variableList = new LinkedList<>();

		variableList.add(newPairNode(source, newSequenceNode(source,
				"bound variable sub-list", boundVariableDeclarationList),
				null));
		return new CommonArrayLambdaNode(source, type,
				newSequenceNode(source, "bound variable list", variableList),
				restriction, expression);
	}

	@Override
	public LambdaNode newLambdaNode(Source source,
			VariableDeclarationNode freeVariableDeclaration,
			ExpressionNode expression) {
		return new CommonLambdaNode(source, freeVariableDeclaration,
				expression);
	}

	@Override
	public LambdaNode newLambdaNode(Source source,
			VariableDeclarationNode boundVariableDeclaration,
			ExpressionNode restriction, ExpressionNode expression) {
		return new CommonLambdaNode(source, boundVariableDeclaration,
				restriction, expression);
	}

	@Override
	public UpdateNode newUpdateNode(Source source, ExpressionNode collator,
			FunctionCallNode call) {
		return new CommonUpdateNode(source, collator, call);
	}

	@Override
	public WithNode newWithNode(Source source, ExpressionNode stateRef,
			StatementNode statement) {
		return new CommonWithNode(source, stateRef, statement);
	}

	@Override
	public WithNode newWithNode(Source source, ExpressionNode stateRef,
			StatementNode statement, boolean isParallel) {
		return new CommonWithNode(source, stateRef, statement, isParallel);
	}

	@Override
	public RunNode newRunNode(Source source, StatementNode statement) {
		return new CommonRunNode(source, statement);
	}

	@Override
	public ObjectOrRegionOfNode newObjectofNode(Source source,
			ExpressionNode operand) {
		return new CommonObjectOrRegionOfNode(source, true, operand);
	}

	@Override
	public ObjectOrRegionOfNode newRegionofNode(Source source,
			ExpressionNode operand) {
		return new CommonObjectOrRegionOfNode(source, false, operand);
	}

	@Override
	public AllocationNode newAllocationNode(Source source, boolean isAllocates,
			SequenceNode<ExpressionNode> memoryList) {
		return new CommonAllocationNode(source, isAllocates, memoryList);
	}

	@Override
	public TypeNode newStateTypeNode(Source source) {
		return new CommonStateTypeNode(source);
	}

	@Override
	public TypeNode newMemTypeNode(Source source) {
		return new CommonMemTypeNode(source);
	}

	@Override
	public ExtendedQuantifiedExpressionNode newExtendedQuantifiedExpressionNode(
			Source source, ExtendedQuantifier quant, ExpressionNode lo,
			ExpressionNode hi, ExpressionNode function) {
		return new CommonExtendedQuantifiedExpressionNode(source, quant, lo, hi,
				function);
	}

	@Override
	public ValueAtNode newValueAtNode(Source source, ExpressionNode state,
			ExpressionNode pid, ExpressionNode expression) {
		return new CommonValueAtNode(source, state, pid, expression);
	}

	@Override
	public LambdaTypeNode newLambdaTypeNode(Source source,
			TypeNode freeVariableType, TypeNode lambdaFunctionType) {
		return new CommonLambdaTypeNode(source, freeVariableType,
				lambdaFunctionType);
	}

	@Override
	public PredicateNode newPredicateNode(Source source,
			IdentifierNode identifier,
			SequenceNode<VariableDeclarationNode> parameters,
			ExpressionNode body) {
		TypeNode boolType = newBasicTypeNode(source, BasicTypeKind.BOOL);
		FunctionTypeNode predicateTypeNode = newFunctionTypeNode(source,
				boolType, parameters.copy(), false);
		CompoundStatementNode wrappedBody = newCompoundStatementNode(source,
				Arrays.asList(newReturnNode(source, body)));

		return new CommonPredicateNode(source, predicateTypeNode, identifier,
				wrappedBody);
	}
}
