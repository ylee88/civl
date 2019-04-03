package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.entity.IF.Scope;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode.ExpressionKind;
import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;
import edu.udel.cis.vsl.sarl.SARL;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;

public class CommonFlowInsensePointsToAnalyzer
		implements
			FlowInsensePointsToAnalyzer {

	/**
	 * The program associated with this analyzer
	 */
	private AST program;

	/**
	 * the root node of the invocation graph
	 */
	private InvocationGraphNode rootNode;

	/**
	 * a reference to {@link AssignmentFactory} for creating
	 * {@link AssignmentSequence}s for analysis
	 */
	private AssignmentFactory factory;

	/**
	 * a reference to {@link InvocationGraphFactory} for creating
	 * {@link InvocationGraphNode} for analysis
	 */
	private InvocationGraphFactory igFactory;

	/**
	 * a table maps {@link Function}s to {@link AssignmentSequence}s that
	 * represent the points-to abstraction of their function bodies.
	 */
	private Map<Function, AssignmentSequence> table;

	/**
	 * a table maps {@link InvocationGraphNode}s to their {@link PointsToGraph}s
	 */
	private Map<InvocationGraphNode, PointsToGraph> pointsToTable; // TODO:make
																	// local

	/**
	 * a table maps functions to all the nodes representing lexical calls to
	 * them:
	 */
	private Map<Function, List<InvocationGraphNode>> funcCallsTable;

	private SymbolicUniverse universe;

	public CommonFlowInsensePointsToAnalyzer(AST ast, AssignmentFactory factory,
			InvocationGraphFactory igFactory) {
		this.program = ast;
		this.factory = factory;
		this.igFactory = igFactory;
		this.table = new HashMap<>();
		this.pointsToTable = new HashMap<>();
		this.funcCallsTable = new HashMap<>();
		this.universe = SARL.newStandardUniverse();
		this.rootNode = abstractProgram(ast);

		Map<Function, PointsToGraph> basePtGraphs = new HashMap<>();

		intraProceduralAnalysis(basePtGraphs);
		interProceduralAnalysis(basePtGraphs);
		// build funcCallsTable:
		for (InvocationGraphNode node : pointsToTable.keySet()) {
			List<InvocationGraphNode> calls = funcCallsTable
					.get(node.function());

			if (calls == null)
				calls = new LinkedList<>();
			calls.add(node);
			funcCallsTable.put(node.function(), calls);
		}
	}

	@Override
	public List<AssignExprIF> mayPointsTo(Function func, Entity ptr) {
		return this.mayPointsToWorker(func, ptr);
	}

	@Override
	public List<AssignExprIF> mayPointsTo(Function func, ExpressionNode ptr) {
		return this.mayPointsToWorker(func, ptr);
	}

	private List<AssignExprIF> mayPointsToWorker(Function func, Object ptr) {

		Iterable<InvocationGraphNode> calls = funcCallsTable.get(func);
		List<AssignExprIF> result = new LinkedList<>();

		assert calls != null;
		for (InvocationGraphNode node : calls) {
			PointsToGraph ptGraph = this.pointsToTable.get(node);
			Iterable<AssignExprIF> pts;

			if (ptr instanceof Entity)
				pts = ptGraph.mayPointsTo((Entity) ptr);
			else
				pts = ptGraph.mayPointsTo((ExpressionNode) ptr);
			for (AssignExprIF pt : pts)
				result.add(pt);
		}
		return result;
	}

	private InvocationGraphNode abstractProgram(AST ast) {
		/*
		 * A function can corresponds to multiple invocation graph nodes since a
		 * function can be called multiple times. This map saves only one node
		 * for every processed function. Only one node needs to be saved because
		 * before the points-to analysis, nodes that are associated with the
		 * same function will have same value. Hence, if a different call to a
		 * processed function is reached, the new node that is associated with
		 * the reached call can be obtained by copy from the saved node.
		 */
		Map<Function, InvocationGraphNode> funcs2Nodes = new HashMap<>();
		// start from "main" function, builds an abstraction of the program,
		// which is a table that maps functions to their assignment sequences
		// and an invocation graph:
		Function mainFunc = (Function) ast.getInternalOrExternalEntity("main");
		InvocationGraphNode mainNode = igFactory.newNode(mainFunc, null, null);
		AssignmentSequence mainSeq = factory.assignmentSequence(mainFunc,
				mainNode);

		System.out.println(mainNode.function().getName() + ":\n"
				+ mainSeq.toString() + "\n\n");
		funcs2Nodes.put(mainFunc, mainNode);
		table.put(mainFunc, mainSeq);
		for (InvocationGraphNode child : mainNode.children())
			abstractInvocation(child, funcs2Nodes);
		return mainNode;
	}

	private void abstractInvocation(InvocationGraphNode node,
			Map<Function, InvocationGraphNode> funcs2Nodes) {
		InvocationGraphNode sameValNode = funcs2Nodes.get(node.function());

		if (sameValNode != null)
			sameValNode.share(node);
		else {
			AssignmentSequence seq = factory.assignmentSequence(node.function(),
					node);

			table.put(node.function(), seq);
			funcs2Nodes.put(node.function(), node);
			System.out.println(
					node.function().getName() + ":\n" + seq.toString());
		}
		for (InvocationGraphNode child : node.children())
			abstractInvocation(child, funcs2Nodes);
	}

	private void intraProceduralAnalysis(
			Map<Function, PointsToGraph> basePtGraphs) {
		for (Entry<Function, AssignmentSequence> entry : table.entrySet()) {
			PointsToGraph ptGraph = SimplePointsToAnalysis
					.newPointsToGraph(entry.getValue(), universe);

			basePtGraphs.put(entry.getKey(), ptGraph);
		}
	}

	private void interProceduralAnalysis(
			Map<Function, PointsToGraph> basePtGraphs) {
		Map<InvocationGraphNode, FunctionCallInputs> nodeInputsTable = new HashMap<>();

		// traverse the invocation graph :
		analyzeInvocationGraphNode(rootNode, nodeInputsTable, basePtGraphs);
	}

	private void analyzeInvocationGraphNode(InvocationGraphNode node,
			Map<InvocationGraphNode, FunctionCallInputs> nodeInputsTable,
			Map<Function, PointsToGraph> funcDefTable) {
		PointsToGraph nodePtGraph = pointsToTable.get(node);
		boolean undone;

		if (nodePtGraph == null) {
			nodePtGraph = funcDefTable.get(node.function()).clone();
			pointsToTable.put(node, nodePtGraph);
		}
		do {
			undone = false;
			// process children (calls in this function):
			for (InvocationGraphNode child : node.children()) {
				switch (child.kind()) {
					case APPROXIMATE :
						analyzeInvocationGraphNode(child, nodeInputsTable,
								funcDefTable);
						undone |= analyzeApproximateNode(child, nodePtGraph,
								nodeInputsTable);
						break;
					case ORDINARY :
						analyzeInvocationGraphNode(child, nodeInputsTable,
								funcDefTable);
						undone |= analyzeOrdinaryNode(child, nodePtGraph);
						break;
					case RECURSIVE :
						analyzeInvocationGraphNode(child, nodeInputsTable,
								funcDefTable);
						undone |= analyzeRecursiveNode(child, nodePtGraph,
								nodeInputsTable);
						break;
					default :
						throw new CIVLInternalException(
								"unknown invocation graph node kind "
										+ child.kind(),
								node.function().getDefinition().getSource());
				}
			}
			if (!undone)
				for (InvocationGraphNode child : node.children()) {
					PointsToGraph childPtGraph = pointsToTable.get(child);

					undone |= computesConstraintsForCall(child, childPtGraph,
							nodePtGraph);
				}
		} while (undone);
	}

	/**
	 * 
	 * Test inputs, return test result
	 */
	private boolean analyzeApproximateNode(InvocationGraphNode node,
			PointsToGraph callerPtGraph,
			Map<InvocationGraphNode, FunctionCallInputs> inputsTable) {
		InvocationGraphNode recurNode = node.getRecursive();
		FunctionCallInputs inputs = inputsTable.get(recurNode);
		boolean covered = false;

		if (inputs == null) {
			inputs = new FunctionCallInputs(node);
			inputsTable.put(recurNode, inputs);
		} else
			covered = coveredByInput(node, callerPtGraph, inputs);
		if (covered) {
			// recursive node must be processed before approximate node, so its
			// pointsToGraph != null
			PointsToGraph recurPtGraph = pointsToTable.get(recurNode);

			// computes the constraints:
			return computesConstraintsForCall(node, recurPtGraph,
					callerPtGraph);
		} else {
			// add inputs computed from caller graph to saved inputs:
			addToInputs(node, callerPtGraph, inputs);
			inputsTable.put(recurNode, inputs);
			return true;
		}
	}

	/**
	 * 
	 * computes constraints for the caller pt graph
	 */
	private boolean analyzeOrdinaryNode(InvocationGraphNode node,
			PointsToGraph callerPtGraph) {
		PointsToGraph nodePtGraph = pointsToTable.get(node);
		int i = 0;

		// map actual parameter points-to set to formal parameters:
		for (AssignExprIF actualParam : node.actualParams())
			nodePtGraph.addPointsTo(node.formalParams()[i++],
					callerPtGraph.mayPointsTo(actualParam));

		return computesConstraintsForCall(node, nodePtGraph, callerPtGraph);
	}

	private boolean analyzeRecursiveNode(InvocationGraphNode node,
			PointsToGraph callerPtGraph,
			Map<InvocationGraphNode, FunctionCallInputs> inputsTable) {
		FunctionCallInputs inputs = inputsTable.get(node);
		PointsToGraph nodePtGraph = pointsToTable.get(node);

		// approximate shall have been recursively processed hence inputs shall
		// not be null
		assert inputs != null;
		boolean covered = coveredByInput(node, callerPtGraph, inputs);

		if (covered) {
			// computes the constraints:
			return computesConstraintsForCall(node, nodePtGraph, callerPtGraph);
		}
		addToInputs(node, callerPtGraph, inputs);
		//
		// List<InvocationGraphNode> pendings = pendingLists.get(node);
		//
		// if (pendings != null)
		// for (InvocationGraphNode pending : pendings)
		// inputs.addInputs(inputsTable.get(pending));
		return !covered || recomputePointsToGraph(node, nodePtGraph, inputs);
	}

	/**
	 * Given a invocation graph node, a points-to graph of the function
	 * associated with the node and the information of the inputs of the
	 * function call represented by the node. Add the inputs information to the
	 * points-to graph.
	 * 
	 * @param node
	 *            a invocation node
	 * @param nodeGraph
	 *            the points-to graph of the function associated with the given
	 *            node
	 * @param inputs
	 *            inputs information of the call represented by the given node,
	 *            i.e., the points-to set of every input including parameters
	 *            and global accesses
	 */
	private boolean recomputePointsToGraph(InvocationGraphNode node,
			PointsToGraph nodeGraph, FunctionCallInputs inputs) {
		int i = 0;
		boolean changed = false;

		// formals:
		for (AssignExprIF formal : node.formalParams())
			changed |= nodeGraph.addPointsTo(formal,
					inputs.getParamInputs(i++));
		// globals:
		for (AssignExprIF global : node.accessedGlobals())
			changed |= nodeGraph.addPointsTo(global,
					inputs.getGlobalInputs(global));
		return changed;
	}

	/**
	 * Given a invocation graph node, a points-to graph of the function
	 * associated with the node, computes the impact of the call represented by
	 * the node and saves the impact in the points-to graph of the caller
	 * function.
	 * 
	 * @param node
	 *            a invocation node
	 * @param nodeGraph
	 *            the points-to graph of the function associated with the given
	 *            node
	 * @param callerGraph
	 *            the points-to graph of the function that calls the function
	 *            associated with the given node
	 * @return true iff at least one of the computed constraints is new to the
	 *         caller's points-to graph
	 */
	private boolean computesConstraintsForCall(InvocationGraphNode node,
			PointsToGraph nodeGraph, PointsToGraph callerGraph) {
		int i = 0;
		boolean changed = false;
		Iterable<AssignExprIF> pts;

		// parameters:
		for (AssignExprIF formal : node.formalParams()) {
			pts = filterOutLocal(nodeGraph.mayPointsTo(formal), node);
			changed |= callerGraph.addPointsTo(node.actualParams()[i++], pts);
		}
		// globals:
		for (AssignExprIF global : node.accessedGlobals()) {
			pts = filterOutLocal(nodeGraph.mayPointsTo(global), node);
			changed |= callerGraph.addPointsTo(global,
					nodeGraph.mayPointsTo(global));
		}
		// returnings
		for (AssignExprIF returning : node.returnings()) {
			pts = filterOutLocal(nodeGraph.mayPointsTo(returning), node);
			changed |= callerGraph.addPointsTo(node.returnTo(),
					nodeGraph.mayPointsTo(returning));
		}
		return changed;
	}

	/**
	 * Test if the inputs (i.e., global objects and actual parameters) of a node
	 * are contained by the saved inputs of the node.
	 * 
	 * @param node
	 *            a invocation node
	 * @param callerGraph
	 *            the points-to graph of the caller function which contains the
	 *            function call represented by the given node
	 * @param inputs
	 *            the saved inputs of the given node
	 * @return true iff the inputs (computed by the caller graph) of the given
	 *         node are covered by the saved inputs.
	 */
	private boolean coveredByInput(InvocationGraphNode node,
			PointsToGraph callerGraph, FunctionCallInputs inputs) {
		int i = 0;

		// test parameters:
		for (AssignExprIF actualParam : node.actualParams())
			if (!inputs.paramCovers(i++, callerGraph.mayPointsTo(actualParam)))
				return false;
		// test global objects:
		for (AssignExprIF global : node.accessedGlobals())
			if (!inputs.globalConvers(global, callerGraph.mayPointsTo(global)))
				return false;
		return true;
	}

	/**
	 * Add the inputs, computed by the points-to graph of the caller function,
	 * of a node to its saved input
	 * 
	 * @param node
	 *            a invocation node
	 * @param callerGraph
	 *            the points-to graph of the caller function which contains the
	 *            function call represented by the given node
	 * @param inputs
	 *            the saved inputs of the given node
	 */
	private void addToInputs(InvocationGraphNode node,
			PointsToGraph callerGraph, FunctionCallInputs inputs) {
		int i = 0;

		for (AssignExprIF actualParam : node.actualParams())
			inputs.addParamInputs(i++, callerGraph.mayPointsTo(actualParam));
		for (AssignExprIF global : node.accessedGlobals())
			inputs.addGlabalInputs(global, callerGraph.mayPointsTo(global));
	}

	/**
	 * filter out local objects
	 *
	 * @return
	 */
	private Iterable<AssignExprIF> filterOutLocal(Iterable<AssignExprIF> pts,
			InvocationGraphNode node) {
		List<AssignExprIF> results = new LinkedList<>();

		for (AssignExprIF pt : pts) {
			Entity entity = pt.source();

			if (entity != null) {
				Scope outerScope = node.function().getScope().getParentScope();
				Entity outerVisible = outerScope.getLexicalOrdinaryEntity(false,
						entity.getName());

				if (entity == outerVisible)
					results.add(pt);
			} else {
				ExpressionNode expr = pt.nonEntitySource();

				expr.expressionKind();
				expr.expressionKind();
				assert expr.expressionKind() == ExpressionKind.FUNCTION_CALL
						|| expr.expressionKind() == ExpressionKind.CONSTANT : "either"
								+ " allocation or string literal";
				results.add(pt);
			}
		}
		return results;
	}

	/* ****************** sub-classes ******************* */
	private class FunctionCallInputs {
		/**
		 * points-to set of each formal parameter of a function:
		 */
		Map<AssignExprIF, Set<AssignExprIF>> globalInputsMap;

		ArrayList<Set<AssignExprIF>> formalInputsMap;

		FunctionCallInputs(InvocationGraphNode node) {
			globalInputsMap = new HashMap<>();
			formalInputsMap = new ArrayList<>(node.formalParams().length);
			for (int i = 0; i < node.formalParams().length; i++)
				formalInputsMap.add(new HashSet<>());
			for (AssignExprIF global : node.accessedGlobals())
				globalInputsMap.put(global, new HashSet<>());
		}

		boolean globalConvers(AssignExprIF global,
				Iterable<AssignExprIF> ptSet) {
			Set<AssignExprIF> inputs = this.globalInputsMap.get(global);

			for (AssignExprIF pt : ptSet)
				if (!inputs.contains(pt))
					return false;
			return true;
		}

		boolean paramCovers(int index, Iterable<AssignExprIF> ptSet) {
			Set<AssignExprIF> inputs = this.formalInputsMap.get(index);

			for (AssignExprIF pt : ptSet)
				if (!inputs.contains(pt))
					return false;
			return true;
		}

		Set<AssignExprIF> getParamInputs(int idx) {
			return this.formalInputsMap.get(idx);
		}

		Set<AssignExprIF> getGlobalInputs(AssignExprIF global) {
			return this.globalInputsMap.get(global);
		}

		void addParamInputs(int idx, Iterable<AssignExprIF> inputs) {
			for (AssignExprIF input : inputs)
				this.formalInputsMap.get(idx).add(input);
		}

		void addGlabalInputs(AssignExprIF global,
				Iterable<AssignExprIF> inputs) {
			for (AssignExprIF input : inputs)
				this.globalInputsMap.get(global).add(input);
		}

		void addInputs(FunctionCallInputs inputs) {
			for (Entry<AssignExprIF, Set<AssignExprIF>> entry : inputs.globalInputsMap
					.entrySet()) {
				Set<AssignExprIF> myVal = globalInputsMap.get(entry.getKey());

				if (myVal == null)
					myVal = entry.getValue();
				else
					myVal.addAll(entry.getValue());
				globalInputsMap.put(entry.getKey(), myVal);
			}

			int i = 0;

			for (Set<AssignExprIF> paramPtSet : inputs.formalInputsMap)
				formalInputsMap.get(i++).addAll(paramPtSet);
		}
	}
}
