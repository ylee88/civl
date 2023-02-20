package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.ast.node.IF.ASTNode;

/**
 * This represents an event of the <code>depends</code> clause.
 * 
 * @author Manchun Zheng
 *
 */
public interface DependsEventNode extends ASTNode {
	public enum DependsEventNodeKind {
		MEMORY, CALL, COMPOSITE, ANYACT, NOACT
	}

	DependsEventNodeKind getEventKind();

	@Override
	DependsEventNode copy();
}
