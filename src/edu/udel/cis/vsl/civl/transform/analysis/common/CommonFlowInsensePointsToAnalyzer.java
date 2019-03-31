package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.HashMap;
import java.util.Map;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentSequence;
import edu.udel.cis.vsl.civl.transform.analysisIF.FlowInsensePointsToAnalyzer;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.PointsToGraph;

public class CommonFlowInsensePointsToAnalyzer
		implements
			FlowInsensePointsToAnalyzer {

	/**
	 * The program associated with this analyzer
	 */
	//private AST program;

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

	CommonFlowInsensePointsToAnalyzer(AST ast) {
		//this.program = ast;
		abstractProgram(ast);
	}

	@Override
	public PointsToGraph getGraph(AssignmentSequence stmts) {
		// TODO Auto-generated method stub
		return null;
	}

	private void abstractProgram(AST ast) {
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

		funcs2Nodes.put(mainFunc, mainNode);
		table.put(mainFunc, mainSeq);
		for (InvocationGraphNode child : mainNode.children())
			abstractInvocation(child, funcs2Nodes);
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
		}
		for (InvocationGraphNode child : node.children())
			abstractInvocation(child, funcs2Nodes);
	}
}
