package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.udel.cis.vsl.abc.ast.entity.IF.Function;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.FunctionDefinitionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.InvocationGraphNode;

public class CommonInvocationGraphNode implements InvocationGraphNode {

	private List<InvocationGraphNode> children;

	private InvocationGraphNode parent;

	private InvocationGraphNode recursive = null;

	private Function function;

	private Set<AssignExprIF> funcInputs;

	private Map<AssignExprIF, AssignExprIF> unmapping;

	private IGNodeKind kind;

	private AssignExprIF[] acutalParams;

	private AssignExprIF returnTo;

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
		this.funcInputs = new HashSet<>();
		this.kind = kind;
		this.unmapping = new HashMap<>();
		this.children = new LinkedList<>();
		this.funcInputs.addAll(Arrays.asList(actualParams));
		this.acutalParams = actualParams;
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
	public Map<AssignExprIF, AssignExprIF> unmapping() {
		return unmapping;
	}

	@Override
	public void markRecursive() {
		this.kind = IGNodeKind.RECURSIVE;
	}

	@Override
	public void addUnmapping(AssignExprIF umFrom, AssignExprIF umTo) {
		this.unmapping.putIfAbsent(umFrom, umTo);
		this.funcInputs.add(umTo);
	}

	@Override
	public Set<AssignExprIF> functionInputs() {
		return funcInputs;
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
		for (int i = 0; i < formals.length; i++) {
			this.unmapping.putIfAbsent(formals[i], acutalParams[i]);
		}
	}

	@Override
	public void addReturnValue(AssignExprIF returnValue) {
		this.unmapping.putIfAbsent(returnValue, returnTo);
	}

	@Override
	public InvocationGraphNode getRecursive() {
		return recursive;
	}

	@Override
	public void completeSelf(FunctionDefinitionNode function) {
		return; // TODO: impl me!
	}
}
