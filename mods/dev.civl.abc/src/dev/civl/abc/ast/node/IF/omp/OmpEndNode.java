package dev.civl.abc.ast.node.IF.omp;

import dev.civl.abc.ast.node.IF.statement.StatementNode;

public interface OmpEndNode extends OmpNode, StatementNode{

	public enum OmpEndType {
		PARALLEL, SECTIONS, SECTION, SINGLE, DO
	}
	
	public OmpEndType ompEndType();
}
