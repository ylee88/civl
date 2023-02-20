package dev.civl.abc.ast.node.IF.acsl;

/**
 * This represents the no-act event <code>\noact</code>, which is an event of
 * <code>depends</code> clauses.
 * 
 * @author Manchun Zheng
 *
 */
public interface NoactNode extends DependsEventNode {

	@Override
	NoactNode copy();
}
