package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;

public class CommonInvocationGraphNode implements InvocationGraphNode {
	/* ***** fields set by initialization and shall not be modified ***** */
	/**
	 * the parent node , the caller
	 */
	private InvocationGraphNode parent;

	/**
	 * the {@link Function} associated with this node
	 */
	private Function function;

	/**
	 * actual parameters (associated with the caller) of the call associated
	 * with this node:
	 */
	private AssignExprIF[] actualParams;

	/**
	 * the expression (in caller) that receives the returned value of this call
	 */
	private AssignExprIF returnTo;

	/**
	 * a non-null reference to its RECURSIVE node iff this node is an
	 * APPROXIMATE node
	 */
	private InvocationGraphNode recursive = null;

	/* ********* fields set by initialization ********** */
	/* ********* and can be modified before static completion *******/
	/**
	 * node kind may change from ORDINARY to RECURSIVE after initialization
	 */
	private IGNodeKind kind;

	/**
	 * abstract representation of the formal parameters of the function
	 * associated with this node
	 */
	private AssignExprIF[] formalParams;

	/**
	 * abstract representation of global accesses in the function body
	 */
	private List<AssignExprIF> globals;

	/**
	 * abstract representation of returning expressions in the function body
	 */
	private List<AssignExprIF> returnings;

	private List<InvocationGraphNode> children;

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

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String toString() {
		return this.function.getName() + " " + this.kind();
	}
}
