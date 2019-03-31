package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;

public class CommonInvocationGraphNode implements InvocationGraphNode {
	/* ***** fields set by initialization and shall not be modified ***** */

	private InvocationGraphNode parent;

	private Function function;

	private AssignExprIF[] actualParams;

	private AssignExprIF returnTo;

	private InvocationGraphNode recursive = null;

	/* ********* fields set by initialization ********** */
	/* ********* and can be modified before static completion *******/

	private IGNodeKind kind;

	/* ********* fields shall not modified AFTER static completion *******/
	private AssignExprIF[] formalParams;

	private List<InvocationGraphNode> children;

	private List<AssignExprIF> globals;

	private List<AssignExprIF> returnings;

	CommonInvocationGraphNode(InvocationGraphNode parent, Function function,
			IGNodeKind kind, AssignExprIF returnTo,
			AssignExprIF... actualParams) {
		init(parent, function, kind, returnTo, actualParams);
	}

	CommonInvocationGraphNode(InvocationGraphNode parent,
			InvocationGraphNode recursive, Function function, IGNodeKind kind,
			AssignExprIF returnTo, AssignExprIF... actualParams) {
		init(parent, function, kind, returnTo, actualParams);
		this.recursive = recursive;
	}

	private void init(InvocationGraphNode parent, Function function,
			IGNodeKind kind, AssignExprIF returnTo,
			AssignExprIF... actualParams) {
		this.parent = parent;
		this.function = function;
		this.globals = new LinkedList<>();
		this.returnings = new LinkedList<>();
		this.kind = kind;
		this.children = new LinkedList<>();
		this.actualParams = actualParams;
		this.returnTo = returnTo;
	}

	@Override
	public Iterable<InvocationGraphNode> children() {
		return children;
	}

	@Override
	public InvocationGraphNode parent() {
		return parent;
	}

	@Override
	public Function function() {
		return function;
	}

	@Override
	public Iterable<AssignExprIF> accessedGlobals() {
		return globals;
	}

	@Override
	public AssignExprIF[] actualParams() {
		return this.actualParams;
	}

	@Override
	public AssignExprIF[] formalParams() {
		return formalParams;
	}

	@Override
	public AssignExprIF returnTo() {
		return returnTo;
	}

	@Override
	public Iterable<AssignExprIF> returnings() {
		return returnings;
	}

	@Override
	public void markRecursive() {
		this.kind = IGNodeKind.RECURSIVE;
	}

	@Override
	public void addChild(InvocationGraphNode child) {
		this.children.add(child);
	}

	@Override
	public IGNodeKind kind() {
		return kind;
	}

	@Override
	public void setFormalParameters(AssignExprIF[] formals) {
		this.formalParams = formals;
	}

	@Override
	public void addReturnValue(AssignExprIF returnValue) {
		this.returnings.add(returnValue);
	}

	@Override
	public InvocationGraphNode getRecursive() {
		return recursive;
	}

	@Override
	public void addGlobalAccess(AssignExprIF globalAccess) {
		this.globals.add(globalAccess);
	}

	@Override
	public void share(InvocationGraphNode node) {
		node.setFormalParameters(formalParams);
		for (AssignExprIF globalAccess : accessedGlobals())
			node.addGlobalAccess(globalAccess);
		for (AssignExprIF returning : returnings())
			node.addReturnValue(returning);
	}
}
