package dev.civl.mc.model.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.AssignsOrReadsNode;
import dev.civl.abc.ast.node.IF.acsl.AssumesNode;
import dev.civl.abc.ast.node.IF.acsl.BehaviorNode;
import dev.civl.abc.ast.node.IF.acsl.CallEventNode;
import dev.civl.abc.ast.node.IF.acsl.CompositeEventNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.DependsEventNode;
import dev.civl.abc.ast.node.IF.acsl.DependsEventNode.DependsEventNodeKind;
import dev.civl.abc.ast.node.IF.acsl.DependsNode;
import dev.civl.abc.ast.node.IF.acsl.EnsuresNode;
import dev.civl.abc.ast.node.IF.acsl.GuardsNode;
import dev.civl.abc.ast.node.IF.acsl.MemoryEventNode;
import dev.civl.abc.ast.node.IF.acsl.RequiresNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLSyntaxException;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.contract.CallEvent;
import dev.civl.mc.model.IF.contract.CompositeEvent.CompositeEventOperator;
import dev.civl.mc.model.IF.contract.ContractFactory;
import dev.civl.mc.model.IF.contract.DependsEvent;
import dev.civl.mc.model.IF.contract.DependsEvent.DependsEventKind;
import dev.civl.mc.model.IF.contract.FunctionBehavior;
import dev.civl.mc.model.IF.contract.FunctionContract;
import dev.civl.mc.model.IF.contract.FunctionContract.ContractKind;
import dev.civl.mc.model.IF.contract.NamedFunctionBehavior;
import dev.civl.mc.model.IF.expression.BinaryExpression.BINARY_OPERATOR;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.Nothing;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.model.common.contract.CommonContractFactory;
import dev.civl.mc.util.IF.Pair;

public class FunctionContractTranslator extends FunctionTranslator {

	private CIVLFunction function;

	private ModelFactory modelFactory;

	private ModelBuilderWorker modelBuilder;

	private ContractFactory contractFactory = new CommonContractFactory();

	/**
	 * Current contract kind: {@link ContractKind} which informs the current
	 * contract kind during recursive parsing. {@link ContractKind} depends on a
	 * contract clause, which cannot be nested, so no need to use a stack.
	 */
	@SuppressWarnings("unused")
	private ContractKind currentContractKind;

	/******************** Constructor ********************/
	FunctionContractTranslator(ModelBuilderWorker modelBuilder, ModelFactory modelFactory, CIVLTypeFactory typeFactory,
			CIVLFunction function, CIVLConfiguration civlConfig) {
		super(modelBuilder, modelFactory, function, civlConfig);
		this.modelFactory = modelFactory;
		this.modelBuilder = modelBuilder;
		this.function = function;
	}

	public void translateFunctionContract(SequenceNode<ContractNode> contract) {
		Scope scope;

		// whatever scope S you choose, it must be the case that whenever this
		// function is called, the ancestor chain of the current dyscope
		// will contain a dyscope D whose
		// static scope is the parent of S. Then you can create a new dyscope D'
		// whose static scope is S and whose parent is D.

		// In other words, the parent of S must always be an ancestor
		// of the static scope of any current dyscope..
		//

		// a system function is different from an ordinary function in that
		// it doesn't have a definition, and so it could be called from
		// anywhere.
		// you must choose S to be some static scope that will always

		if (function.isSystemFunction()) {
			// make system function contracts get evaluated in the
			// context of the constant scope. Contract clauses can refer to
			// formal parameters and constants, and that's it.
			List<Variable> newParams = new LinkedList<>();

			for (Variable v : function.parameters()) {
				Variable w = modelFactory.variableAsParameter(v.getSource(), v.type(), v.name(), v.vid());

				newParams.add(w);
			}

			Scope rootScope = modelBuilder.rootScope;

			assert rootScope != null;
			scope = modelFactory.scope(modelFactory.sourceOf(contract), rootScope, newParams, function);
		} else {
			scope = function.outerScope();
		}

		FunctionContract result = contractFactory.newFunctionContract(modelFactory.sourceOf(contract), scope);

		for (ContractNode clause : contract) {
			this.translateContractNode(clause, result);
		}
		this.function.setFunctionContract(result);
	}

	private void translateContractNode(ContractNode contractNode, FunctionContract functionContract) {
		this.translateContractNodeWork(contractNode, functionContract, null);
	}

	/**
	 * Translates a {@link ContractNode} to a component of a
	 * {@link FunctionContract}.
	 * 
	 * The function takes at most three main components: {@link FunctionContract},
	 * {@link MPICollectiveBehavior} and {@link NamedFunctionBehavior}. According to
	 * the syntax:
	 * <p>
	 * <ol>
	 * <li>None of them can be nested.</li>
	 * <li>{@link NamedFunctionBehavior} can appear in {@link FunctionContract} or
	 * {@link MPICollectiveBehavior}</li>
	 * <li>{@link MPICollectiveBehavior} can only appear in
	 * {@link FunctionContract}</li>
	 * <li>{@link FunctionContract} denotes the whole group of function contracts
	 * for a function. For each function, it can has at most one
	 * {@link FunctionContract}.</li>
	 * </ol>
	 * </p>
	 * Thus, the specifications for different kind of contracts is as follows:
	 * <p>
	 * <ol>
	 * <li>A {@link NamedFunctionBehavior} will be added as a component of a
	 * {@link MPICollectiveBehavior} if it is non-null, else as of a
	 * {@link FunctionContract}.</li>
	 * <li>{@link ASSUMES} can only be added as a component of a
	 * {@link NamedFunctionBehavior}</li>
	 * <li>Other contract clauses will be added as a component of one of the three
	 * main blocks with such a precedence: <code>{@link NamedFunctionBehavior}
	 * higher than {@link MPICollectiveBehavior} high than
	 * {@link FunctionContract}<code></li>
	 * </ol>
	 * </p>
	 * 
	 * @param contractNode
	 * @return
	 */
	private void translateContractNodeWork(ContractNode contractNode, FunctionContract functionContract,
			NamedFunctionBehavior behavior) {
		CIVLSource source = modelFactory.sourceOf(contractNode);
		Scope scope = functionContract.scope();
		FunctionBehavior targetBehavior = behavior != null ? behavior : functionContract.defaultBehavior();

		switch (contractNode.contractKind()) {
		case ASSIGNS_READS: {
			AssignsOrReadsNode assignsOrReads = (AssignsOrReadsNode) contractNode;
			boolean isAssigns = assignsOrReads.isAssigns();
			SequenceNode<ExpressionNode> muNodes = assignsOrReads.getMemoryList();

			for (ExpressionNode muNode : muNodes) {
				Expression mu = this.translateExpressionNode(muNode, scope, true);

				if (mu instanceof Nothing) {
					if (isAssigns) {
						if (targetBehavior.numAssignsMemoryUnits() == 0)
							targetBehavior.setAssingsNothing();
						else
							throw new CIVLSyntaxException("assigns \\nothing conflicts with previous assigns clause",
									source);
					} else {
						if (targetBehavior.numReadsMemoryUnits() == 0)
							targetBehavior.setReadsNothing();
						else
							throw new CIVLSyntaxException("reads \\nothing conflicts with previous reads clause",
									source);
					}
				} else {
					if (isAssigns) {
						if (targetBehavior.assignsNothing())
							throw new CIVLSyntaxException("assigns clause conflicts with previous assigns \\nothing",
									source);
						targetBehavior.addAssignsMemoryUnit(mu);
					} else {
						if (targetBehavior.readsNothing())
							throw new CIVLSyntaxException("reads clause conflicts with previous reads \\nothing",
									source);
						targetBehavior.addReadsMemoryUnit(mu);
					}
				}
			}
			break;
		}
		case ASSUMES: {
			assert targetBehavior instanceof NamedFunctionBehavior;
			Expression expression = translateExpressionNode(((AssumesNode) contractNode).getPredicate(), scope, true);
			Expression existedAssumptions;

			if ((existedAssumptions = behavior.assumptions()) != null) {
				CIVLSource spanedSource = modelFactory.sourceOfSpan(existedAssumptions.getSource(),
						expression.getSource());

				expression = modelFactory.binaryExpression(spanedSource, BINARY_OPERATOR.AND, existedAssumptions,
						expression);
			}
			behavior.setAssumption(expression);
			break;
		}
		case BEHAVIOR: {
			assert behavior == null;
			BehaviorNode behaviorNode = (BehaviorNode) contractNode;
			NamedFunctionBehavior namedBehavior = this.contractFactory.newNamedFunctionBehavior(source,
					behaviorNode.getName().name());
			SequenceNode<ContractNode> body = behaviorNode.getBody();

			for (ContractNode item : body) {
				this.translateContractNodeWork(item, functionContract, namedBehavior);
			}
			functionContract.addNamedBehavior(namedBehavior);
			break;
		}

		case DEPENDS: {
			DependsNode dependsNode = (DependsNode) contractNode;
			SequenceNode<DependsEventNode> eventNodes = dependsNode.getEventList();

			for (DependsEventNode eventNode : eventNodes) {
				DependsEvent event = this.translateDependsEvent(eventNode, scope);

				if (event.dependsEventKind() == DependsEventKind.NOACT) {
					if (targetBehavior.numDependsEvents() > 0)
						throw new CIVLSyntaxException("depends \\noact conflicts with previous depends clause", source);
					targetBehavior.setDependsNoact();
				} else if (event.dependsEventKind() == DependsEventKind.ANYACT) {
					if (targetBehavior.dependsNoact())
						throw new CIVLSyntaxException("depends \\anyact conflicts with previous depends \\noact clause",
								source);
					targetBehavior.setDependsAnyact();
				} else
					targetBehavior.addDependsEvent(event);
			}
			if (targetBehavior.dependsAnyact())
				targetBehavior.clearDependsEvents();
			break;
		}
		case ENSURES: {
			currentContractKind = ContractKind.ENSURES;
			Expression expression = translateExpressionNode(((EnsuresNode) contractNode).getExpression(), scope, true);
			targetBehavior.addPostcondition(expression);
			currentContractKind = null;
			break;
		}
		case REQUIRES: {
			currentContractKind = ContractKind.REQUIRES;
			Expression expression = translateExpressionNode(((RequiresNode) contractNode).getExpression(), scope, true);
			targetBehavior.addPrecondition(expression);
			currentContractKind = null;
			break;
		}
		case GUARDS: {
			Expression guard = this.translateExpressionNode(((GuardsNode) contractNode).getExpression(), scope, true);

			functionContract.setGuard(modelFactory.booleanExpression(guard));
			break;
		}
		case PURE:
			functionContract.setPure(true);
			break;
		case COMPLETENESS:
		default:
			throw new CIVLUnimplementedFeatureException(
					"Translate Procedure ContractNode with " + contractNode.contractKind());
		}
	}

	private DependsEvent translateDependsEvent(DependsEventNode eventNode, Scope scope) {
		DependsEventNodeKind kind = eventNode.getEventKind();
		CIVLSource source = this.modelFactory.sourceOf(eventNode);

		switch (kind) {
		case MEMORY: {
			MemoryEventNode readWriteEvent = (MemoryEventNode) eventNode;
			Set<Expression> muSet = new HashSet<>();
			SequenceNode<ExpressionNode> muNodeSet = readWriteEvent.getMemoryList();
			DependsEventKind memoryKind;

			for (ExpressionNode muNode : muNodeSet) {
				muSet.add(this.translateExpressionNode(muNode, scope, true));
			}
			switch (readWriteEvent.memoryEventKind()) {
			case READ:
				memoryKind = DependsEventKind.READ;
				break;
			case WRITE:
				memoryKind = DependsEventKind.WRITE;
				break;
			default:// REACH
				memoryKind = DependsEventKind.REACH;
			}
			return this.contractFactory.newMemoryEvent(source, memoryKind, muSet);
		}
		case CALL: {
			CallEventNode callEvent = (CallEventNode) eventNode;
			Pair<Function, CIVLFunction> functionPair = this.getFunction(callEvent.getFunction());
			SequenceNode<ExpressionNode> argumentNodes = callEvent.arguments();
			List<Expression> arguments = new ArrayList<>();
			CallEvent call;

			for (ExpressionNode argNode : argumentNodes) {
				arguments.add(this.translateExpressionNode(argNode, scope, true));
			}
			call = this.contractFactory.newCallEvent(source, functionPair.right, arguments);
			if (functionPair.right == null)
				this.modelBuilder.callEvents.put(call, functionPair.left);
			return call;
		}
		case COMPOSITE: {
			CompositeEventNode compositeEvent = (CompositeEventNode) eventNode;
			CompositeEventOperator operator;
			DependsEvent left, right;

			switch (compositeEvent.eventOperator()) {
			case UNION:
				operator = CompositeEventOperator.UNION;
				break;
			case DIFFERENCE:
				operator = CompositeEventOperator.DIFFERENCE;
				break;
			case INTERSECT:
				operator = CompositeEventOperator.INTERSECT;
				break;
			default:
				throw new CIVLUnimplementedFeatureException(
						"unknown kind of composite event operatore: " + compositeEvent.eventOperator(), source);
			}
			left = this.translateDependsEvent(compositeEvent.getLeft(), scope);
			right = this.translateDependsEvent(compositeEvent.getRight(), scope);
			return this.contractFactory.newCompositeEvent(source, operator, left, right);
		}
		case ANYACT:
			return this.contractFactory.newAnyactEvent(source);
		case NOACT:
			return this.contractFactory.newNoactEvent(source);
		default:
			throw new CIVLUnimplementedFeatureException("unknown kind of depends event: " + kind, source);
		}

	}
}
