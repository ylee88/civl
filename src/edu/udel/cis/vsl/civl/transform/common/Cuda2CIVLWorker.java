package edu.udel.cis.vsl.civl.transform.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.IF.ASTFactory;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity.EntityKind;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.OrdinaryEntity;
import edu.udel.cis.vsl.abc.ast.entity.IF.ProgramEntity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.IdentifierNode;
import edu.udel.cis.vsl.abc.ast.node.IF.SequenceNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.FunctionDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.FunctionDefinitionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.CastNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.DotNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.FunctionCallNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IntegerConstantNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.AtomicNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CompoundStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ExpressionStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.IfNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.LoopNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode.StatementKind;
import edu.udel.cis.vsl.abc.ast.node.IF.type.ArrayTypeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.type.BasicTypeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.type.FunctionTypeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.type.TypeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.type.TypeNode.TypeNodeKind;
import edu.udel.cis.vsl.abc.ast.type.IF.FunctionType;
import edu.udel.cis.vsl.abc.ast.type.IF.ObjectType;
import edu.udel.cis.vsl.abc.ast.type.IF.PointerType;
import edu.udel.cis.vsl.abc.ast.type.IF.QualifiedObjectType;
import edu.udel.cis.vsl.abc.ast.type.IF.StandardBasicType;
import edu.udel.cis.vsl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import edu.udel.cis.vsl.abc.ast.type.IF.Type;
import edu.udel.cis.vsl.abc.ast.type.IF.Type.TypeKind;
import edu.udel.cis.vsl.abc.ast.type.common.CommonFunctionType;
import edu.udel.cis.vsl.abc.token.IF.CivlcToken;
import edu.udel.cis.vsl.abc.token.IF.Source;
import edu.udel.cis.vsl.abc.token.IF.SourceFile;
import edu.udel.cis.vsl.abc.token.IF.SyntaxException;
import edu.udel.cis.vsl.civl.transform.IF.Cuda2CIVLTransformer;

public class Cuda2CIVLWorker extends BaseWorker {

	private static String CUDA_HEADER = "cuda.h";
	private int tempVarNum;

	public Cuda2CIVLWorker(ASTFactory astFactory) {
		super(Cuda2CIVLTransformer.LONG_NAME, astFactory);
		this.identifierPrefix = "_cuda_";
	}

	@Override
	protected AST transformCore(AST ast) throws SyntaxException {
		if (!this.hasHeader(ast, CUDA_HEADER))
			return ast;

		SequenceNode<BlockItemNode> root = ast.getRootNode();
		AST newAST;

		ast.release();
		removeBuiltinDefinitions(root);
		translateCudaMallocCalls(root);
		translateKernelCalls(root);

		if (!this.has_gen_mainFunction(root)) {
			transformMainFunction(root);
			createNewMainFunction(root);
		}
		translateMainDefinition(root);
		translateKernelDefinitions(root);
		translateKernelDeclarations(root);
		newAST = astFactory.newAST(root, ast.getSourceFiles(),
				ast.isWholeProgram());
		// newAST.prettyPrint(System.out, false);
		return newAST;
	}

	/**
	 * Returns a new temporary variable each time it is called.
	 * 
	 * @return A generated temporary variable name
	 */
	protected String newTemporaryVariableName() {
		return this.identifierPrefix + "tmp" + tempVarNum++;
	}

	/**
	 * Finds the main function definition node underneath root and calls
	 * {@link Cuda2CIVLWorker#transformMainFunctionDefinition(FunctionDefinitionNode)}
	 * on it
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void translateMainDefinition(ASTNode root) {
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child.nodeKind() == NodeKind.FUNCTION_DEFINITION) {
				FunctionDefinitionNode definition = (FunctionDefinitionNode) child;

				if (definition.getName() != null
						&& definition.getName().equals("main")) {
					transformMainFunctionDefinition(definition);
					return;
				}
			}
		}
	}

	/**
	 * Transforms every kernel definition node under root using
	 * {@link Cuda2CIVLWorker#kernelDefinitionTransform(FunctionDefinitionNode)}.
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void translateKernelDefinitions(ASTNode root) {
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child.nodeKind() == NodeKind.FUNCTION_DEFINITION) {
				FunctionDefinitionNode definition = (FunctionDefinitionNode) child;

				if (definition.hasGlobalFunctionSpecifier()) {
					root.setChild(child.childIndex(),
							kernelDefinitionTransform(definition));
				}
			}
		}
	}

	/**
	 * Transforms every kernel declaration node under root using
	 * {@link Cuda2CIVLWorker#kernelDeclarationTransform(FunctionDeclarationNode)}.
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void translateKernelDeclarations(ASTNode root) {
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child.nodeKind() == NodeKind.FUNCTION_DECLARATION) {
				FunctionDeclarationNode declaration = (FunctionDeclarationNode) child;

				if (declaration.hasGlobalFunctionSpecifier() && declaration
						.getTypeNode() instanceof FunctionTypeNode) {
					root.setChild(child.childIndex(),
							kernelDeclarationTransform(declaration));
				}
			}
		}
	}

	/**
	 * Transforms every cuda malloc function call using
	 * {@link Cuda2CIVLWorker#cudaMallocTransform(FunctionCallNode)}.
	 * Cuda malloc calls are found by recursively searching through the
	 * AST for a matching function call.
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void translateCudaMallocCalls(ASTNode root) {
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child.nodeKind() == NodeKind.EXPRESSION) {
				ExpressionNode expression = (ExpressionNode) child;

				if (expression
						.expressionKind() == ExpressionKind.FUNCTION_CALL) {
					FunctionCallNode functionCall = (FunctionCallNode) expression;

					if (functionCall.getFunction()
							.expressionKind() == ExpressionKind.IDENTIFIER_EXPRESSION) {
						IdentifierExpressionNode identifierExpression = (IdentifierExpressionNode) functionCall
								.getFunction();

						if (identifierExpression.getIdentifier().name()
								.equals("cudaMalloc")) {
							int index = functionCall.childIndex();

							root.setChild(index,
									cudaMallocTransform(functionCall));
							continue;
						}
					}
				}
			}

			translateCudaMallocCalls(child);
		}
	}

	/**
	 * Transforms every kernel call using
	 * {@link Cuda2CIVLWorker#kernelCallTransform(FunctionCallNode)}.
	 * Kernel calls are found by recursively searching through the
	 * AST for a matching function call.
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void translateKernelCalls(ASTNode root) {
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child.nodeKind() == NodeKind.STATEMENT) {
				StatementNode statement = (StatementNode) child;

				if (statement.statementKind() == StatementKind.EXPRESSION) {
					ExpressionStatementNode expressionStatement = (ExpressionStatementNode) statement;
					ExpressionNode expression = expressionStatement
							.getExpression();
					if (expression
							.expressionKind() == ExpressionKind.FUNCTION_CALL) {
						FunctionCallNode functionCall = (FunctionCallNode) expression;
						if (functionCall.getNumberOfContextArguments() > 0) {
							root.setChild(statement.childIndex(),
									kernelCallTransform(functionCall));
							continue;
						}
					}
				}
			}

			translateKernelCalls(child);
		}
	}

	/**
	 * Translates "cudaMalloc( (void**) ptrPtr, size)" to "*ptrPtr =
	 * (type)$malloc($root, size), cudaSuccess" where "type" is the type of
	 * *ptrPtr
	 * 
	 * @param cudaMallocCall a FunctionCallNode which is a call to cuda malloc
	 * @return The translated cuda malloc call as an expression node
	 */
	protected ExpressionNode cudaMallocTransform(
			FunctionCallNode cudaMallocCall) {
		Source source = cudaMallocCall.getSource();
		// find the pointer
		ExpressionNode ptrPtr = cudaMallocCall.getArgument(0);
		while (ptrPtr instanceof CastNode) {
			ptrPtr = ((CastNode) ptrPtr).getArgument();
		}
		ExpressionNode size = cudaMallocCall.getArgument(1);
		// build lhs expression
		ExpressionNode assignLhs = nodeFactory.newOperatorNode(
				cudaMallocCall.getSource(), Operator.DEREFERENCE,
				Arrays.asList(ptrPtr.copy()));
		Type lhsType;
		if (ptrPtr.getInitialType().kind() == TypeKind.POINTER) {
			PointerType ptrType = (PointerType) ptrPtr.getInitialType();
			lhsType = ptrType.referencedType();
		} else {
			lhsType = ptrPtr.getInitialType();
		}
		// build rhs expression
		FunctionCallNode mallocCall = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$malloc"),
				Arrays.asList(nodeFactory.newHereNode(source), size.copy()),
				null);
		CastNode mallocCast = nodeFactory.newCastNode(source,
				this.typeNode(source, lhsType), mallocCall);
		// create assign node
		OperatorNode assignment = nodeFactory.newOperatorNode(
				cudaMallocCall.getSource(), Operator.ASSIGN,
				Arrays.asList(assignLhs, mallocCast));
		// create comma node
		ExpressionNode finalExpression = nodeFactory.newOperatorNode(source,
				Operator.COMMA,
				Arrays.asList(assignment,
						nodeFactory.newEnumerationConstantNode(nodeFactory
								.newIdentifierNode(source, "cudaSuccess"))));
		return finalExpression;
	}

	/**
	 * Inserts a call to $cuda_init at the beginning of main and a call to
	 * $cuda_finalize at the end of main
	 * 
	 * @param mainFunction the function definition node for the main function
	 */
	private void transformMainFunctionDefinition(
			FunctionDefinitionNode mainFunction) {
		Source source = mainFunction.getSource();
		FunctionCallNode cudaInitCall = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$cuda_init"),
				Collections.<ExpressionNode>emptyList(), null);
		FunctionCallNode cudaFinalizeCall = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$cuda_finalize"),
				Collections.<ExpressionNode>emptyList(), null);
		CompoundStatementNode body = mainFunction.getBody();

		body = this.insertToCompoundStatement(body,
				nodeFactory.newExpressionStatementNode(cudaInitCall), 0);
		body = this.insertToCompoundStatement(body,
				nodeFactory.newExpressionStatementNode(cudaFinalizeCall),
				body.numChildren());
		mainFunction.setBody(body);
	}

	/**
	 * Given a kernel name, returns a transformed version of it to distinguish
	 * it as a transformed version of the original kernel
	 * 
	 * @param name a string that is the name of the original kernel
	 * @return the transformed name
	 */
	private String transformKernelName(String name) {
		return "_cuda_" + name;
	}

	/**
	 * Given a kernel definition node, this method transforms the kernel name
	 * (see {@link Cuda2CIVLWorker#transformKernelName(String)}), prepends
	 * formal parameters for the context arguments of the kernel, builds and
	 * inserts the inner kernel definition from the kernel's body (see
	 * {@link Cuda2CIVLWorker#buildInnerKernelDefinition(CompoundStatementNode)}),
	 * and enqueues a call to the inner kernel definition using
	 * $cuda_enqueue_kernel.
	 * 
	 * @param oldDefinition a FunctionDefinitionNode which is the definition of the original kernel
	 * @return the transformed kernel definition
	 */
	protected FunctionDefinitionNode kernelDefinitionTransform(
			FunctionDefinitionNode oldDefinition) {
		// TODO: add execution configuration parameters as regular parameters
		Source source = oldDefinition.getSource();
		
		
		FunctionDefinitionNode innerKernelDefinition = this
				.buildInnerKernelDefinition(oldDefinition.getBody());
		String newKernelName = this
				.transformKernelName(oldDefinition.getIdentifier().name());
		FunctionCallNode enqueueKernelCall = nodeFactory.newFunctionCallNode(
				source,
				this.identifierExpression(source, "$cuda_enqueue_kernel"),
				Arrays.asList(this.identifierExpression(source, "_cuda_stream"),
						this.identifierExpression(source, "_cuda_kernel"),
						this.identifierExpression(source, "gridDim"),
						this.identifierExpression(source, "blockDim")),
				null);
		CompoundStatementNode newKernelBody = nodeFactory
				.newCompoundStatementNode(source,
						Arrays.asList(innerKernelDefinition,
								nodeFactory.newExpressionStatementNode(
										enqueueKernelCall)));
		
		List<VariableDeclarationNode> newKernelFormalsList = new ArrayList<>();

		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("gridDim"),
				nodeFactory.newTypedefNameNode(this.identifier("dim3"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("blockDim"),
				nodeFactory.newTypedefNameNode(this.identifier("dim3"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("_cuda_mem_size"), nodeFactory
						.newTypedefNameNode(this.identifier("size_t"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("_cuda_stream"), nodeFactory.newTypedefNameNode(
						this.identifier("cudaStream_t"), null)));
		for (VariableDeclarationNode decl : oldDefinition.getTypeNode()
				.getParameters()) {
			if (decl.getTypeNode().kind() != TypeNodeKind.VOID)
				newKernelFormalsList.add(decl.copy());
		}
		SequenceNode<VariableDeclarationNode> newKernelFormals = nodeFactory
				.newSequenceNode(source, "kernel formals",
						newKernelFormalsList);
		FunctionTypeNode newKernelType = nodeFactory.newFunctionTypeNode(source,
				oldDefinition.getTypeNode().getReturnType().copy(),
				newKernelFormals, true);
		FunctionDefinitionNode newKernel = nodeFactory
				.newFunctionDefinitionNode(source,
						nodeFactory.newIdentifierNode(source, newKernelName),
						newKernelType, null, newKernelBody);
		return newKernel;
	}

	/**
	 * Given a kernel declaration node, this method transforms the kernel name
	 * (see {@link Cuda2CIVLWorker#transformKernelName(String)}) and prepends
	 * formal parameters for the context arguments of the kernel.
	 * 
	 * @param oldDeclaration a FunctionDeclarationNode which is the declaration of the original kernel
	 * @return the transformed kernel declaration node
	 */
	protected FunctionDeclarationNode kernelDeclarationTransform(
			FunctionDeclarationNode oldDeclaration) {
		Source source = oldDeclaration.getSource();
		String newKernelName = this
				.transformKernelName(oldDeclaration.getIdentifier().name());
		List<VariableDeclarationNode> newKernelFormalsList = new ArrayList<>();

		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("gridDim"),
				nodeFactory.newTypedefNameNode(this.identifier("dim3"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("blockDim"),
				nodeFactory.newTypedefNameNode(this.identifier("dim3"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("_cuda_mem_size"), nodeFactory
						.newTypedefNameNode(this.identifier("size_t"), null)));
		newKernelFormalsList.add(nodeFactory.newVariableDeclarationNode(source,
				this.identifier("_cuda_stream"), nodeFactory.newTypedefNameNode(
						this.identifier("cudaStream_t"), null)));

		FunctionTypeNode oldDeclarationTypeNode = ((FunctionTypeNode) oldDeclaration
				.getTypeNode());
		for (VariableDeclarationNode decl : oldDeclarationTypeNode
				.getParameters()) {
			if (decl.getTypeNode().kind() != TypeNodeKind.VOID)
				newKernelFormalsList.add(decl.copy());
		}
		SequenceNode<VariableDeclarationNode> newKernelFormals = nodeFactory
				.newSequenceNode(source, "kernel formals",
						newKernelFormalsList);
		FunctionTypeNode newKernelType = nodeFactory.newFunctionTypeNode(source,
				oldDeclarationTypeNode.getReturnType().copy(), newKernelFormals,
				true);
		FunctionDeclarationNode newKernel = nodeFactory
				.newFunctionDeclarationNode(source,
						nodeFactory.newIdentifierNode(source, newKernelName),
						newKernelType, null);
		return newKernel;
	}

	/**
	 * Alters a body of code by removing any variable declaration 
	 * with the "__shared__" tag and returning a new list of those removed declarations
	 * without the "__shared__" tag.
	 * 
	 * @param statements a CompountStatementNode that is any section of code
	 * @return The list of removed variable declarations
	 */
	protected List<VariableDeclarationNode> extractSharedVariableDeclarations(
			CompoundStatementNode statements) {
		List<VariableDeclarationNode> declarations = new ArrayList<>();
		for (BlockItemNode item : statements) {
			if (item instanceof VariableDeclarationNode) {
				VariableDeclarationNode variableDeclaration = (VariableDeclarationNode) item.copy();
				if (variableDeclaration.hasSharedStorage()) {
					statements.removeChild(item.childIndex());
					variableDeclaration.setSharedStorage(false);
					declarations.add(variableDeclaration);
				}
			}
		}
		return declarations;
	}

	/**
	 * Given the body of a kernel definition, this method builds the 
	 * inner kernel for the transformed kernel, which aims to generate a grid of blocks
	 * and threads before running the body of the original kernel.
	 * 
	 * This method defines the inner kernel with formal parameters,
	 * inserts the block function (see {@link Cuda2CIVLWorker#buildBlockDefinition(CompoundStatementNode)}),
	 * and appends to that calls to $cuda_wait_in_queue,
	 * $cuda_run_procs (for block generation), and $cuda_kernel_finish.
	 * 
	 * @param body a CompoundStatementNode which is the body of the original kernel 
	 * @return The completed inner kernel definition
	 */
	protected FunctionDefinitionNode buildInnerKernelDefinition(
			CompoundStatementNode body) {
		Source source = body.getSource();
		VariableDeclarationNode thisDeclaration = nodeFactory
				.newVariableDeclarationNode(source,
						nodeFactory.newIdentifierNode(source, "_cuda_this"),
						nodeFactory.newPointerTypeNode(source,
								nodeFactory.newTypedefNameNode(
										nodeFactory.newIdentifierNode(source,
												"$cuda_kernel_instance_t"),
										null)));
		VariableDeclarationNode eDeclaration = nodeFactory
				.newVariableDeclarationNode(source,
						nodeFactory.newIdentifierNode(source, "_cuda_event"),
						nodeFactory.newTypedefNameNode(nodeFactory
								.newIdentifierNode(source, "cudaEvent_t"),
								null));
		SequenceNode<VariableDeclarationNode> innerKernelFormals = nodeFactory
				.newSequenceNode(source, "innerKernelFormals",
						Arrays.asList(thisDeclaration, eDeclaration));
		FunctionDefinitionNode blockDefinition = buildBlockDefinition(body);
		FunctionCallNode waitInQueueCall = nodeFactory.newFunctionCallNode(
				source,
				this.identifierExpression(source, "$cuda_wait_in_queue"),
				Arrays.asList(this.identifierExpression(source, "_cuda_this"),
						this.identifierExpression(source, "_cuda_event")),
				null);
		
		
		
		FunctionCallNode runProcsCall = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$cuda_run_procs"),
				Arrays.asList(this.identifierExpression(source, "gridDim"),
						this.identifierExpression(source, "_cuda_block")),
				null);
		FunctionCallNode kernelFinishCall = nodeFactory.newFunctionCallNode(
				source,
				this.identifierExpression(source, "$cuda_kernel_finish"),
				Arrays.asList(this.identifierExpression(source, "_cuda_this")),
				null);
		
		CompoundStatementNode innerKernelBody = nodeFactory
				.newCompoundStatementNode(source, Arrays.asList(blockDefinition,
						nodeFactory.newExpressionStatementNode(waitInQueueCall),
						nodeFactory.newExpressionStatementNode(runProcsCall),
						nodeFactory
								.newExpressionStatementNode(kernelFinishCall)));
		FunctionDefinitionNode innerKernelDefinition = nodeFactory
				.newFunctionDefinitionNode(source,
						nodeFactory.newIdentifierNode(source, "_cuda_kernel"),
						nodeFactory.newFunctionTypeNode(source,
								nodeFactory.newVoidTypeNode(source),
								innerKernelFormals, false),
						null, innerKernelBody);
		return innerKernelDefinition;
	}

	/**
	 * Given the body of a kernel definition, this method builds the 
	 * block function within the inner kernel, which aims to create threads within
	 * a block before running the body of the original kernel.
	 * 
	 * This method defines the block function with formal parameters,
	 * begins it with a barrier creation using $gbarrier_create, and appends to that
	 * the thread function (see {@link Cuda2CIVLWorker#buildThreadDefinition(CompoundStatementNode)},
	 * a call to $cuda_run_procs (for thread generation), and a call to $gbarrier_destroy.
	 * 
	 * @param body a CompoundStatementNode which is the body of the original kernel 
	 * @return The completed block function definition
	 */
	protected FunctionDefinitionNode buildBlockDefinition(
			CompoundStatementNode body) {
		Source source = body.getSource();
		DotNode blockDimX = nodeFactory.newDotNode(source,
				this.identifierExpression(source, "blockDim"),
				nodeFactory.newIdentifierNode(source, "x"));
		DotNode blockDimY = nodeFactory.newDotNode(source,
				this.identifierExpression(source, "blockDim"),
				nodeFactory.newIdentifierNode(source, "y"));
		DotNode blockDimZ = nodeFactory.newDotNode(source,
				this.identifierExpression(source, "blockDim"),
				nodeFactory.newIdentifierNode(source, "z"));
		OperatorNode totalThreads = nodeFactory
				.newOperatorNode(source, Operator.TIMES,
						Arrays.asList(
								nodeFactory
										.newOperatorNode(source, Operator.TIMES,
												Arrays.<ExpressionNode>asList(
														blockDimX, blockDimY)),
								blockDimZ));
		
		VariableDeclarationNode numThreads = nodeFactory.newVariableDeclarationNode(source,
				this.identifier("numThreads"), nodeFactory.newBasicTypeNode(source, BasicTypeKind.INT));
		numThreads.setInitializer(totalThreads);
		
		FunctionCallNode newGbarrier = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$gbarrier_create"),
				Arrays.asList(nodeFactory.newHereNode(source), this.identifierExpression("numThreads")),
				null);
		
		VariableDeclarationNode gCommCreate = nodeFactory.newVariableDeclarationNode(source, 
				this.identifier("gComm"), nodeFactory.newTypedefNameNode(this.identifier("$gcomm"), null));
		
		List<ExpressionNode> createArguments = new ArrayList<ExpressionNode>();
		createArguments.add(nodeFactory.newHereNode(source));
		createArguments.add(this.identifierExpression("numThreads"));
		
		gCommCreate.setInitializer(nodeFactory.newFunctionCallNode(source, this.identifierExpression("$gcomm_create"),
				createArguments, null));
		
		VariableDeclarationNode numWarps = nodeFactory.newVariableDeclarationNode(source,
				this.identifier("numWarps"), nodeFactory.newBasicTypeNode(source, BasicTypeKind.INT));
		
		OperatorNode totalWarps = nodeFactory.newOperatorNode(source, Operator.PLUS,
				Arrays.asList(
						nodeFactory.newOperatorNode(source, Operator.DIV, Arrays.asList(
								this.identifierExpression("numThreads"), nodeFactory.newIntConstantNode(source, 32))),
						nodeFactory.newOperatorNode(source, Operator.NEQ, Arrays.asList(
								nodeFactory.newOperatorNode(source, Operator.MOD, Arrays.asList(
										this.identifierExpression("numThreads"), nodeFactory.newIntConstantNode(source, 32)
										)),
								nodeFactory.newIntConstantNode(source, 0)))));
		
		numWarps.setInitializer(totalWarps);
		
		VariableDeclarationNode warpBarrierArray = nodeFactory.newVariableDeclarationNode(source,
				this.identifier("warpBarriers"), nodeFactory.newArrayTypeNode(source, 
						nodeFactory.newTypedefNameNode(this.identifier("$gbarrier"), null), this.identifierExpression("numWarps")));
		
		VariableDeclarationNode blockScope = nodeFactory.newVariableDeclarationNode(source,
				this.identifier("_block_root"), nodeFactory.newTypedefNameNode(this.identifier("$scope"), null));
		blockScope.setInitializer(nodeFactory.newHereNode(source));
		
		List<VariableDeclarationNode> creationLoopInitializerList = new ArrayList<VariableDeclarationNode>();
		VariableDeclarationNode forLoopInitializer = nodeFactory.newVariableDeclarationNode(source, 
				this.identifier("i"), nodeFactory.newBasicTypeNode(source, BasicTypeKind.INT));
		forLoopInitializer.setInitializer(nodeFactory.newIntConstantNode(source, 0));
		creationLoopInitializerList.add(forLoopInitializer);
		
		List<BlockItemNode> creationLoopItems = new ArrayList<BlockItemNode>();
		FunctionCallNode warpBarrierCreate = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression("$gbarrier_create"), 
				Arrays.asList(this.identifierExpression("_block_root"), nodeFactory.newIntConstantNode(source, 32)), null);
		
		OperatorNode warpBarrierAssign = nodeFactory.newOperatorNode(source, Operator.ASSIGN, 
				Arrays.asList(
						nodeFactory.newOperatorNode(source, Operator.SUBSCRIPT, 
								Arrays.asList(this.identifierExpression("warpBarriers"),
										this.identifierExpression("i"))),
						warpBarrierCreate));
		creationLoopItems.add(nodeFactory.newExpressionStatementNode(warpBarrierAssign));
		CompoundStatementNode creationLoopBody = nodeFactory.newCompoundStatementNode(source, creationLoopItems);
		
		OperatorNode numWarpsMinusOne = nodeFactory.newOperatorNode(source, Operator.MINUS, Arrays.asList(this.identifierExpression("numWarps"),
				nodeFactory.newIntConstantNode(source, 1)));
		
		LoopNode warpBarrierLoop = nodeFactory.newForLoopNode(source,
				nodeFactory.newForLoopInitializerNode(source, creationLoopInitializerList), 
				nodeFactory.newOperatorNode(source, Operator.LT, Arrays.asList(this.identifierExpression("i"), 
						numWarpsMinusOne.copy())),
				nodeFactory.newOperatorNode(source, Operator.POSTINCREMENT, this.identifierExpression("i")), 
				creationLoopBody, null);
		
		OperatorNode lastWarpNumThreads = nodeFactory.newOperatorNode(source, Operator.MINUS, Arrays.asList(
				this.identifierExpression("numThreads"),
				nodeFactory.newOperatorNode(source, Operator.TIMES, Arrays.asList(
						numWarpsMinusOne.copy(),
						nodeFactory.newIntConstantNode(source, 32)))));
		
		FunctionCallNode lastWarpBarrierCreate = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression("$gbarrier_create"), 
				Arrays.asList(this.identifierExpression("_block_root"), lastWarpNumThreads), null);
		
		OperatorNode LastWarpAssign = nodeFactory.newOperatorNode(source, Operator.ASSIGN,Arrays.asList(
				nodeFactory.newOperatorNode(source, Operator.SUBSCRIPT, Arrays.asList(
						this.identifierExpression("warpBarriers"),
						numWarpsMinusOne.copy())),
				lastWarpBarrierCreate));
		
		VariableDeclarationNode gbarrierCreation = nodeFactory
				.newVariableDeclarationNode(source,
						nodeFactory.newIdentifierNode(source,
								"_cuda_block_barrier"),
						nodeFactory.newTypedefNameNode(nodeFactory
								.newIdentifierNode(source, "$gbarrier"), null),
						newGbarrier);
		List<VariableDeclarationNode> sharedVars = this
				.extractSharedVariableDeclarations(body);
		completeSharedExternArrays(sharedVars);
		SequenceNode<VariableDeclarationNode> blockFormals = nodeFactory
				.newSequenceNode(source, "blockFormals",
						Arrays.asList(
								nodeFactory.newVariableDeclarationNode(source,
										nodeFactory.newIdentifierNode(source,
												"blockIdx"),
										nodeFactory.newTypedefNameNode(
												nodeFactory.newIdentifierNode(
														source, "uint3"),
												null))));
		FunctionDefinitionNode threadDefinition = this
				.buildThreadDefinition(body);
		FunctionCallNode runProcsCall = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$cuda_run_procs"),
				Arrays.asList(this.identifierExpression(source, "blockDim"),
						this.identifierExpression(source, "_cuda_thread")),
				null);
		
		List<VariableDeclarationNode> destructionLoopInitializerList = new ArrayList<VariableDeclarationNode>();
		destructionLoopInitializerList.add(forLoopInitializer.copy());
		
		List<BlockItemNode> destructionLoopItems = new ArrayList<BlockItemNode>();
		FunctionCallNode warpBarrierDestroy = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression("$gbarrier_destroy"), 
				Arrays.asList(nodeFactory.newOperatorNode(source, Operator.SUBSCRIPT, 
						Arrays.asList(this.identifierExpression("warpBarriers"), this.identifierExpression("i")))), null);
		destructionLoopItems.add(nodeFactory.newExpressionStatementNode(warpBarrierDestroy));
		CompoundStatementNode destructionLoopBody = nodeFactory.newCompoundStatementNode(source, destructionLoopItems);
		
		LoopNode warpBarrierDestruction = nodeFactory.newForLoopNode(source,
				nodeFactory.newForLoopInitializerNode(source, destructionLoopInitializerList), 
				nodeFactory.newOperatorNode(source, Operator.LT, Arrays.asList(this.identifierExpression("i"), this.identifierExpression("numWarps"))),
				nodeFactory.newOperatorNode(source, Operator.POSTINCREMENT, this.identifierExpression("i")), 
				destructionLoopBody, null);
		
		FunctionCallNode gbarrierDestruction = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$gbarrier_destroy"),
				Arrays.asList(this.identifierExpression(source,
						"_cuda_block_barrier")),
				null);
		
		List<ExpressionNode> destroyArguments = new ArrayList<ExpressionNode>();
		destroyArguments.add(this.identifierExpression("gComm"));
		IntegerConstantNode zero = nodeFactory.newIntConstantNode(source, 0);
		CastNode NULL = nodeFactory.newCastNode(source, nodeFactory.newPointerTypeNode(source, nodeFactory.newVoidTypeNode(source)), zero);
		destroyArguments.add(NULL);

		FunctionCallNode gCommDestroy = nodeFactory.newFunctionCallNode(source, this.identifierExpression("$gcomm_destroy"), 
				destroyArguments, null);
		
		List<BlockItemNode> blockBodyItems = new ArrayList<BlockItemNode>();
		blockBodyItems.add(numThreads);
		blockBodyItems.add(numWarps);
		blockBodyItems.add(gCommCreate);
		blockBodyItems.add(warpBarrierArray);
		blockBodyItems.add(blockScope);
		blockBodyItems.add(warpBarrierLoop);
		blockBodyItems.add(nodeFactory.newExpressionStatementNode(LastWarpAssign));
		blockBodyItems.add(gbarrierCreation);
		blockBodyItems.addAll(sharedVars);
		blockBodyItems.add(threadDefinition);
		blockBodyItems
				.add(nodeFactory.newExpressionStatementNode(runProcsCall));
		blockBodyItems.add(
				nodeFactory.newExpressionStatementNode(gbarrierDestruction));
		blockBodyItems.add(warpBarrierDestruction);
		blockBodyItems.add(nodeFactory.newExpressionStatementNode(gCommDestroy));
		CompoundStatementNode blockBody = nodeFactory
				.newCompoundStatementNode(source, blockBodyItems);
		FunctionDefinitionNode blockDefinition = nodeFactory
				.newFunctionDefinitionNode(source,
						nodeFactory.newIdentifierNode(source, "_cuda_block"),
						nodeFactory.newFunctionTypeNode(source,
								nodeFactory.newVoidTypeNode(source),
								blockFormals, false),
						null, blockBody);
		return blockDefinition;
	}

	/**
	 * 
	 * @param sharedVars a list of VariableDeclarationNodes
	 */
	protected void completeSharedExternArrays(
			List<VariableDeclarationNode> sharedVars) {
		for (VariableDeclarationNode node : sharedVars) {
			if (node.hasExternStorage()
					&& node.getTypeNode().kind() == TypeNodeKind.ARRAY) {
				ArrayTypeNode arrayType = (ArrayTypeNode) node.getTypeNode();
				if (arrayType.getExtent() == null) {
					arrayType.setExtent(
							this.identifierExpression("_cuda_mem_size"));
					node.setExternStorage(false);
				}
			}
		}
	}

	/**
	 * Given the body of a kernel definition, this method builds the 
	 * thread function within the block function of the inner kernel,
	 * which aims to create a barrier for the thread before running the body of the original kernel.
	 * 
	 * This method defines the thread function with formal parameters and
	 * inserts into it the body of the original kernel among other functions calls 
	 * for the creation/destruction of a barrier and for data race checking.
	 * 
	 * @param body a CompoundStatementNode which is the body of the original kernel 
	 * @return The completed thread function definition
	 */
	protected FunctionDefinitionNode buildThreadDefinition(
			CompoundStatementNode body) {
		translateShflCalls(body);
		CompoundStatementNode newBody = translateAtomicCalls(body);
		Source source = newBody.getSource();
		SequenceNode<VariableDeclarationNode> threadFormals = nodeFactory
				.newSequenceNode(source, "threadFormals",
						Arrays.asList(
								nodeFactory.newVariableDeclarationNode(source,
										nodeFactory.newIdentifierNode(source,
												"threadIdx"),
										nodeFactory.newTypedefNameNode(
												nodeFactory.newIdentifierNode(
														source, "uint3"),
												null))));
		
		FunctionCallNode localStart = nodeFactory.newFunctionCallNode(source, 
				this.identifierExpression(source, "$local_start"), 
				Arrays.asList(), null);
		
		VariableDeclarationNode tidDecl = nodeFactory
				.newVariableDeclarationNode(source,
						this.identifier("_cuda_tid"),
						nodeFactory.newBasicTypeNode(source, BasicTypeKind.INT),
						nodeFactory.newFunctionCallNode(source,
								this.identifierExpression(source,
										"$cuda_index"),
								Arrays.asList(
										this.identifierExpression(source,
												"blockDim"),
										this.identifierExpression(source,
												"threadIdx")),
								null));
		// Kernel_id
		VariableDeclarationNode kidDecl = nodeFactory
				.newVariableDeclarationNode(source,
						this.identifier("_cuda_kid"),
						nodeFactory.newBasicTypeNode(source, BasicTypeKind.INT),
						nodeFactory.newFunctionCallNode(source,
								this.identifierExpression(source,
										"$cuda_kernel_index"),
								Arrays.asList(
										this.identifierExpression(source,
												"gridDim"),
										this.identifierExpression(source,
												"blockDim"),
										this.identifierExpression(source,
												"blockIdx"),
										this.identifierExpression(source,
												"threadIdx")),
								null));
		
		VariableDeclarationNode commCreate = nodeFactory.newVariableDeclarationNode(source, 
				this.identifier("comm"), nodeFactory.newTypedefNameNode(this.identifier("$comm"), null));
		
		List<ExpressionNode> createArguments = new ArrayList<ExpressionNode>();
		createArguments.add(nodeFactory.newHereNode(source));
		createArguments.add(this.identifierExpression("gComm"));
		createArguments.add(this.identifierExpression("_cuda_tid"));
		
		commCreate.setInitializer(nodeFactory.newFunctionCallNode(source, this.identifierExpression("$comm_create"),
				createArguments, null));
		
		
		FunctionCallNode newBarrier = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$barrier_create"),
				Arrays.asList(nodeFactory.newHereNode(source),
						this.identifierExpression(source,
								"_cuda_block_barrier"),
						this.identifierExpression("_cuda_tid")),
				null);
		VariableDeclarationNode barrierCreation = nodeFactory
				.newVariableDeclarationNode(source,
						nodeFactory.newIdentifierNode(source,
								"_cuda_thread_barrier"),
						nodeFactory.newTypedefNameNode(nodeFactory
								.newIdentifierNode(source, "$barrier"), null),
						newBarrier);
		// FIXME: Not sure if this works with FunctionCallNode
		FunctionCallNode readPop = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$read_set_pop"),
				Arrays.asList(), null
				);
		FunctionCallNode writePop = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$write_set_pop"),
				Arrays.asList(), null
				);
		FunctionCallNode barrierDestruction = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$barrier_destroy"),
				Arrays.asList(this.identifierExpression(source,
						"_cuda_thread_barrier")),
				null);
		
		List<ExpressionNode> destroyArguments = new ArrayList<ExpressionNode>();
		destroyArguments.add(this.identifierExpression("comm"));
		FunctionCallNode commDestroy = nodeFactory.newFunctionCallNode(source, this.identifierExpression("$comm_destroy"), 
			    destroyArguments, null);
		
		FunctionCallNode localEnd = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$local_end"),
				Arrays.asList(), null);
		
		// FIXME: Not sure if this works
		FunctionCallNode readPush = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$read_set_push"),
				Arrays.asList(), null
				);
		FunctionCallNode writePush = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$write_set_push"),
				Arrays.asList(), null
				);
		// Node for check_data_race
		FunctionCallNode checkDataRace = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$check_data_race"),
				Arrays.asList(
						this.identifierExpression(source,
								"_cuda_this"),
						this.identifierExpression(source,
								"_cuda_kid")),
				null);
		List<BlockItemNode> threadBodyItems = new ArrayList<BlockItemNode>();
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(localStart));
		threadBodyItems.add(tidDecl);
		threadBodyItems.add(kidDecl);
		threadBodyItems.add(commCreate);
		threadBodyItems.add(barrierCreation);
		// threadBodyItems.add(Node for read/write set push)
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(readPush));
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(writePush));
		for (BlockItemNode child : newBody) {
			if (child != null)
				threadBodyItems.add(child.copy());
		}
		// check data race call (make Node)
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(checkDataRace));
		// threadBodyItems.add(Node for read/write set pop)
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(readPop));
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(writePop));
		threadBodyItems.add(
				nodeFactory.newExpressionStatementNode(barrierDestruction));
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(commDestroy));
		threadBodyItems.add(nodeFactory.newExpressionStatementNode(localEnd));
		CompoundStatementNode threadBody = nodeFactory
				.newCompoundStatementNode(source, threadBodyItems);
		
		FunctionDefinitionNode threadDefinition = nodeFactory
				.newFunctionDefinitionNode(source,
						nodeFactory.newIdentifierNode(source, "_cuda_thread"),
						nodeFactory.newFunctionTypeNode(source,
								nodeFactory.newVoidTypeNode(source),
								threadFormals, false),
						null, threadBody);

		//TODO Change into $cuda_barrier
		FunctionCallNode cudaBarrier = nodeFactory.newFunctionCallNode(source,
				this.identifierExpression(source, "$cuda_barrier"),
				Arrays.asList(
						this.identifierExpression(source,
								"_cuda_this"),
						this.identifierExpression(source,
								"_cuda_kid"),
						this.identifierExpression(source,
								"_cuda_thread_barrier")),
				null);
		
		
		replaceSyncThreadsCalls(threadDefinition, cudaBarrier);
		return threadDefinition;
	}

	/**
	 * Replaces all calls to "__synchthreads" with the replacement expression passed in.
	 * The AST is searched through recursively to find all function calls matching "__syncthreads".
	 * 
	 * @param root the root node of an Abstract Syntax Tree
	 * @param replacement an ExpressionNode which will replace all instances of "__synchthreads"
	 */
	protected void replaceSyncThreadsCalls(ASTNode root,
			ExpressionNode replacement) {

		for (ASTNode child : root.children()) {
			if (child == null)
				continue;

			if (child instanceof ExpressionNode) {
				ExpressionNode itemExpr = (ExpressionNode) child;
				if (itemExpr instanceof FunctionCallNode) {
					FunctionCallNode call = (FunctionCallNode) itemExpr;
					ExpressionNode function = call.getFunction();
					if (function instanceof IdentifierExpressionNode) {
						String functionName = ((IdentifierExpressionNode) function)
								.getIdentifier().name();
						if (functionName.equals("__syncthreads")) {
							root.setChild(child.childIndex(),
									replacement.copy());
							continue;
						}
					}
				}
			}
			replaceSyncThreadsCalls(child, replacement);
		}
	}

	/**
	 * Searches through the kernel and performs a transformation on all atomic function
	 * calls to ensure data races are accurately caught. This method acts on the statement
	 * level and copies transformed statements to build a new kernel.
	 * 
	 * @param root the root node of an Abstract Syntax Tree (root of a kernel on initial call of this method)
	 * @return A CompoundStatementNode with transformed statements (overall returns a new transformed kernel)
	 */
	protected CompoundStatementNode translateAtomicCalls(ASTNode root) {
		List<BlockItemNode> newKernelItems = new ArrayList<BlockItemNode>();
		
		for (Iterator<ASTNode> it = root.children().iterator(); it.hasNext();) {
			ASTNode child = it.next();
			if (child == null)
				continue;
			
			if (child.nodeKind() == NodeKind.STATEMENT) {
				StatementNode statement = (StatementNode) child;
				StatementKind statementKind = statement.statementKind();
				if (statementKind == StatementKind.COMPOUND) {
					newKernelItems.add(translateAtomicCalls(child));
					continue;
				}
			}
			replaceAtomicExpressions(child, newKernelItems);
			newKernelItems.add((BlockItemNode)child.copy());
		}
		CompoundStatementNode newKernelBody = nodeFactory.newCompoundStatementNode(root.getSource(), newKernelItems);
		return newKernelBody;
	}
	
	/**
	 * Takes in an ExpressionNode and determines if it is an atomic function call.
	 * If the expression is such a call, the matching FunctionDefinitionNode is returned,
	 * otherwise returns null.
	 * 
	 * @param expression an ExpressionNode
	 * @return the FunctionDefinitionNode of the atomic function call that is passed in. 
	 * If expression is not an atomic function call, null is returned.
	 */
	protected FunctionDefinitionNode findAtomicDefinition(ExpressionNode expression) {
		if(expression.expressionKind() == ExpressionKind.FUNCTION_CALL) {
			FunctionCallNode call = (FunctionCallNode) expression;
			ExpressionNode function = call.getFunction();
			if (function instanceof IdentifierExpressionNode) {
				IdentifierNode identifier = ((IdentifierExpressionNode) function).getIdentifier();
				String functionName = identifier.name();
				if (functionName.toLowerCase().contains("atomic")) {
					
					Function functionEntity = (Function) identifier.getEntity();
					FunctionDefinitionNode functionDefinition = functionEntity.getDefinition();
					
					Source source = functionDefinition.getSource();
					CivlcToken token = source.getFirstToken();
					SourceFile sourceFile = token.getSourceFile();
					String fileName = sourceFile.getName();
					
					if(fileName.equals("cuda.cvl"))
						return functionDefinition;
				}
			}
		}
		return null;
	}
	
	/**
	 * Extracts the expressions that are passed in as parameters to an atomic function call
	 * and stores them in temporary variables. Recurses if a parameter is also an atomic 
	 * function call. The temporary variable assignments are added to the new kernel.
	 * 
	 * @param functionCall the FunctionCallNode that is having its parameters transformed
	 * @param newKernelItems a list of BlockItemNodes that will be built into the new kernel
	 */
	protected void transformParameters(FunctionCallNode functionCall, List<BlockItemNode> newKernelItems) {
		for (ASTNode child: functionCall.getArguments()) {
			if (child == null)
				continue;
			if (child.nodeKind() == NodeKind.EXPRESSION) {
				ExpressionNode expression = (ExpressionNode) child;
				if (expression.expressionKind() == ExpressionKind.CONSTANT)
					continue;
				FunctionDefinitionNode functionDefinition = findAtomicDefinition(expression);
				if (functionDefinition != null) {
					transformParameters((FunctionCallNode) expression, newKernelItems);
					functionCall.setChild(child.childIndex(), atomicCallTransform((FunctionCallNode) expression, functionDefinition, newKernelItems));
					continue;
				}
				replaceAtomicExpressions(child, newKernelItems);
				Source source = expression.getSource();
				Type type = expression.getType();
				TypeNode typeNode;
				if (type instanceof CommonFunctionType) {
					FunctionType functionType = (FunctionType) type;
					ObjectType returnType = functionType.getReturnType();
					typeNode = this.typeNode(source, returnType);
				}
				else
					typeNode = this.typeNode(source, type);
				
				String tmpVariableName = "$" + this.newTemporaryVariableName();
				VariableDeclarationNode tmpDeclaration = nodeFactory.newVariableDeclarationNode(source, 
					nodeFactory.newIdentifierNode(source, tmpVariableName),
					typeNode.copy());
					
				tmpDeclaration.setInitializer(expression.copy());
				
				newKernelItems.add(tmpDeclaration);
					
				functionCall.setArgument(child.childIndex(), 
						nodeFactory.newIdentifierExpressionNode(source, 
								this.identifier(tmpVariableName)));
			}
			
		}
	}

	/**
	 * Searches through an AST in order to transform any atomic function calls. This method
	 * searches on the expression level, thus it only replaces expressions in the parent
	 * statements, and does not copy the statements into the new kernel.
	 * 
	 * @param root the root of an Abstract Syntax Tree
	 * @param newKernelItems a list of BlockItemNodes that will be built into the new kernel
	 */
	protected void replaceAtomicExpressions(ASTNode root, List<BlockItemNode> newKernelItems) {
		for (ASTNode child: root.children()) {
			if (child == null)
				continue;
			
			if(child.nodeKind() == NodeKind.STATEMENT) {
				StatementNode statement = (StatementNode) child;
				if (statement.statementKind() == StatementKind.COMPOUND) {
					root.setChild(child.childIndex(), translateAtomicCalls(statement));
					continue;
				}
				List<BlockItemNode> newStatementItems = new ArrayList<BlockItemNode>();
				replaceAtomicExpressions(child, newStatementItems);
				newStatementItems.add((BlockItemNode)child.copy());
				CompoundStatementNode newStatementBody = nodeFactory.newCompoundStatementNode(root.getSource(), newStatementItems);
				root.setChild(child.childIndex(), newStatementBody);
				continue;	
			}
			
			
			if (child.nodeKind() == NodeKind.EXPRESSION) {
				ExpressionNode expression = (ExpressionNode) child;
				FunctionDefinitionNode functionDefinition = findAtomicDefinition(expression);
				if (functionDefinition != null){
					transformParameters((FunctionCallNode) expression, newKernelItems);
					root.setChild(child.childIndex(), atomicCallTransform((FunctionCallNode) expression, functionDefinition, newKernelItems));
					continue;
				}
			}
			replaceAtomicExpressions(child, newKernelItems);
		}
	}
	
	/**
	 * Performs the transformation of an atomic function call. This includes creating a
	 * temporary variable to store the result of the function call, as well as checking
	 * for data races and clearing memory sets within an atomic block. The series of statements
	 * is added to the new kernel and the temporary variable identifier is returned.
	 * 
	 * @param atomicCall the FunctionCallNode that is an atomic function call
	 * @param functionDefinition the FunctionDefinitionNode of the atomic function call
	 * @param newKernelItems a list of BlockItemNodes that will be built into the new kernel
	 * @return an IdentifierExpressionNode that holds the identifier for the temporary variable created in
	 * the transformation
	 */
	protected IdentifierExpressionNode atomicCallTransform(FunctionCallNode atomicCall, FunctionDefinitionNode functionDefinition, List<BlockItemNode> newKernelItems) {
		Source source = atomicCall.getSource();
		
		FunctionCallNode publish = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$publish"),
				Arrays.asList(
						this.identifierExpression(source,
								"_cuda_this"),
						this.identifierExpression(source,
								"_cuda_kid")),
				null);
		
		
		
		// Node for check_data_race
		FunctionCallNode checkDataRace = nodeFactory.newFunctionCallNode(
				source, this.identifierExpression(source, "$check_data_race"),
				Arrays.asList(
						this.identifierExpression(source,
								"_cuda_this"),
						this.identifierExpression(source,
								"_cuda_kid")),
				null);
		
		FunctionCallNode clearMemSets = nodeFactory.newFunctionCallNode(source, 
				this.identifierExpression(source, "$clear_mem_sets"),
				Arrays.asList(
						this.identifierExpression(source, "_cuda_this"),
						this.identifierExpression(source, "_cuda_kid")),
				null);
		
		FunctionCallNode yeild = nodeFactory.newFunctionCallNode(source, 
				this.identifierExpression(source, "$yield"), 
				Arrays.asList(), null);
		
		FunctionTypeNode functionType = (FunctionTypeNode) functionDefinition.getTypeNode();
		TypeNode functionReturnType = functionType.getReturnType();
		
		String tmpVariableName = "$" + this.newTemporaryVariableName();
		VariableDeclarationNode tmpDeclaration = nodeFactory.newVariableDeclarationNode(source, 
				nodeFactory.newIdentifierNode(source, tmpVariableName),
				functionReturnType.copy());
		
		tmpDeclaration.setInitializer(atomicCall.copy());

		newKernelItems.add(nodeFactory.newExpressionStatementNode(publish));
		newKernelItems.add(nodeFactory.newExpressionStatementNode(yeild.copy()));
		newKernelItems.add(tmpDeclaration);
		newKernelItems.add(nodeFactory.newExpressionStatementNode(checkDataRace));
		newKernelItems.add(nodeFactory.newExpressionStatementNode(clearMemSets));

		return nodeFactory.newIdentifierExpressionNode(source, this.identifier(tmpVariableName));	
	}
	
	protected void translateShflCalls(ASTNode root) {
		for (ASTNode child: root.children()) {
			if (child == null) 
				continue;
			
			if (child.nodeKind() == NodeKind.EXPRESSION) {
				ExpressionNode expression = (ExpressionNode) child;
				if (expression.expressionKind() == ExpressionKind.FUNCTION_CALL) {
					FunctionCallNode functionCall = (FunctionCallNode) expression;
					ExpressionNode function = functionCall.getFunction();
					if (function instanceof IdentifierExpressionNode) {
						IdentifierNode identifier = ((IdentifierExpressionNode) function).getIdentifier();
						String functionName = identifier.name();
						if (functionName.toLowerCase().contains("__shfl") && functionName.toLowerCase().contains("_sync")) {
						
							Function functionEntity = (Function) identifier.getEntity();
							FunctionDeclarationNode functionDeclaration = (FunctionDeclarationNode) functionEntity.getDeclaration(0);
							
						
							Source source = functionDeclaration.getSource();
							CivlcToken token = source.getFirstToken();
							SourceFile sourceFile = token.getSourceFile();
							String fileName = sourceFile.getName();
						
							if(fileName.equals("cuda.h")) {
								shflCallTransform(functionCall);
							}
						}
					}
				}
			}
			translateShflCalls(child);
		}
	}
	
	protected void shflCallTransform(FunctionCallNode shflCall) {
		Source source = shflCall.getSource();
		SequenceNode<ExpressionNode> arguments = shflCall.getArguments();
		if (arguments.numChildren() < 4) {
			arguments.addSequenceChild(nodeFactory.newIntConstantNode(source, 32));
		}
		
		arguments.addSequenceChild(this.identifierExpression("numThreads"));
		arguments.addSequenceChild(this.identifierExpression("_cuda_tid"));
		arguments.addSequenceChild(this.identifierExpression("comm"));
		arguments.addSequenceChild(this.identifierExpression("warpBarriers"));
		
		ExpressionNode function = shflCall.getFunction();
		IdentifierNode identifier = ((IdentifierExpressionNode) function).getIdentifier();
		String functionName = identifier.name();
		String cudaFunctionName = "_cuda" + functionName;
		identifier.setName(cudaFunctionName);
	}
	
	/**
	 * Transforms the kernel call to instead use the kernel's transformed
	 * signature as transformed by
	 * {@link Cuda2CIVLWorker#kernelDeclarationTransform(FunctionDeclarationNode)}.
	 * 
	 * @param kernelCall a FunctionCallNode which is a kernel call
	 * @return The transformed kernel call
	 */
	protected StatementNode kernelCallTransform(FunctionCallNode kernelCall) {
		Source source = kernelCall.getSource();
		List<VariableDeclarationNode> tempVarDecls = new ArrayList<>();
		List<ExpressionNode> newArgumentList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			ExpressionNode arg = kernelCall.getContextArgument(i);
			Type argType = arg.getConvertedType();
			if (argType.kind() == TypeKind.QUALIFIED) {
				argType = ((QualifiedObjectType) argType).getBaseType();
			}
			if (argType.kind() == TypeKind.BASIC
					&& ((StandardBasicType) argType)
							.getBasicTypeKind() == BasicTypeKind.INT) {

				String tmpVar = newTemporaryVariableName();
				ExpressionNode intConvertedToDim3 = nodeFactory
						.newFunctionCallNode(source,
								this.identifierExpression("$cuda_to_dim3"),
								Arrays.asList(arg.copy()), null);
				tempVarDecls
						.add(nodeFactory.newVariableDeclarationNode(source,
								this.identifier(tmpVar),
								nodeFactory.newTypedefNameNode(
										this.identifier("dim3"), null),
								intConvertedToDim3));
				newArgumentList.add(this.identifierExpression(tmpVar));
			} else {
				newArgumentList.add(arg.copy());
			}
		}
		if (kernelCall.getNumberOfContextArguments() < 3) {
			try {
				newArgumentList
						.add(nodeFactory.newIntegerConstantNode(source, "0"));
			} catch (SyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			newArgumentList.add(kernelCall.getContextArgument(2).copy());
		}
		if (kernelCall.getNumberOfContextArguments() < 4) {
			try {
				newArgumentList
						.add(nodeFactory.newIntegerConstantNode(source, "0"));
			} catch (SyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			newArgumentList.add(kernelCall.getContextArgument(3).copy());
		}
		for (int i = 0; i < kernelCall.getNumberOfArguments(); i++) {
			newArgumentList.add(kernelCall.getArgument(i).copy());
		}
		ExpressionNode newFunction;
		if (kernelCall.getFunction() instanceof IdentifierExpressionNode) {
			IdentifierExpressionNode identifierExpression = (IdentifierExpressionNode) kernelCall
					.getFunction();
			newFunction = this.identifierExpression(transformKernelName(
					identifierExpression.getIdentifier().name()));
		} else {
			newFunction = kernelCall.getFunction().copy();
		}
		FunctionCallNode newFunctionCall = nodeFactory.newFunctionCallNode(
				source, newFunction, newArgumentList, null);
		List<BlockItemNode> blockItems = new ArrayList<>();
		blockItems.addAll(tempVarDecls);
		blockItems.add(nodeFactory.newExpressionStatementNode(newFunctionCall));
		CompoundStatementNode replacementNode = nodeFactory
				.newCompoundStatementNode(source, blockItems);
		return replacementNode;
	}

	/**
	 * Removes all definitions of the variables "threadIdx", "blockIdx", "gridDim", and "blockDim"
	 * that exist in the original CUDA code. The AST is searched recursively to find all variable
	 * declarations with a matching name.
	 *  
	 * @param root the root node of an Abstract Syntax Tree
	 */
	protected void removeBuiltinDefinitions(ASTNode root) {
		Set<String> builtinVariables = new HashSet<>(
				Arrays.asList("threadIdx", "blockIdx", "gridDim", "blockDim"));
		for (ASTNode child : root.children()) {
			if (child == null)
				continue;
			if (child.nodeKind() == NodeKind.VARIABLE_DECLARATION) {
				VariableDeclarationNode variableDeclaration = (VariableDeclarationNode) child;
				if (variableDeclaration.getIdentifier() != null
						&& variableDeclaration.getIdentifier().getSource()
								.getFirstToken().getSourceFile().getName()
								.equals("cuda.h")
						&& builtinVariables.contains(
								variableDeclaration.getIdentifier().name())) {
					root.removeChild(child.childIndex());
					continue;
				}
			}

			removeBuiltinDefinitions(child);
		}
	}
}

